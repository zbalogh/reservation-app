package com.zbalogh.reservation.awsinfra.stack;

import java.util.List;
import java.util.Map;

import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.servicediscovery.NamespaceType;

public class AwsPublicStack extends Stack {

  public AwsPublicStack(final App scope, final String id, final StackProps props)
  {
      super(scope, id, props);

      // --- VPC (public subnet only, no NAT) ---
      Vpc vpc = Vpc.Builder.create(this, "ReservationVpc")
              .maxAzs(2)
              .natGateways(0)
              .subnetConfiguration(List.of(
                      SubnetConfiguration.builder()
                              .name("ReservationPublicSubnet")
                              .subnetType(SubnetType.PUBLIC)
                              .cidrMask(24)
                              .build()
              ))
              .build();

      // --- ECS Cluster ---
      Cluster cluster = Cluster.Builder.create(this, "ReservationCluster")
              .vpc(vpc)
              .clusterName("ReservationCluster")
              .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                      .name("reservation.local")
                      .type(NamespaceType.DNS_PRIVATE)
                      .build())
              .build();



      // ALB Security Group: allows HTTP from anywhere
      SecurityGroup albSecurityGroup = SecurityGroup.Builder.create(this, "ReservationAlbSecurityGroup")
              .vpc(vpc)
              .allowAllOutbound(true)
              .build();
      albSecurityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(80), "Allow HTTP from anywhere to access ALB");

      // API Gateway Security Group: allows traffic from ALB only
      SecurityGroup gatewaySecurityGroup = SecurityGroup.Builder.create(this, "ReservationGatewaySecurityGroup")
              .vpc(vpc)
              .allowAllOutbound(true)
              .build();
      gatewaySecurityGroup.addIngressRule(albSecurityGroup, Port.tcp(8000), "Allow ALB to call API Gateway");

      // API Service Security Group: allows traffic from API Gateway only
      SecurityGroup apiServerSecurityGroup = SecurityGroup.Builder.create(this, "ReservationApiServerSecurityGroup")
              .vpc(vpc)
              .allowAllOutbound(true)
              .build();
      apiServerSecurityGroup.addIngressRule(gatewaySecurityGroup, Port.tcp(8080), "Allow API Gateway to call API Service");

      // Auth Service Security Group: allows traffic from API Gateway only
      SecurityGroup authServerSecurityGroup = SecurityGroup.Builder.create(this, "ReservationAuthServerSecurityGroup")
              .vpc(vpc)
              .allowAllOutbound(true)
              .build();
      authServerSecurityGroup.addIngressRule(gatewaySecurityGroup, Port.tcp(5000), "Allow API Gateway to call Auth Service");

      // Web GUI Security Group: allows traffic from API Gateway only
      SecurityGroup webGuiSecurityGroup = SecurityGroup.Builder.create(this, "ReservationWebGuiSecurityGroup")
              .vpc(vpc)
              .allowAllOutbound(true)
              .build();
      webGuiSecurityGroup.addIngressRule(gatewaySecurityGroup, Port.tcp(8050), "Allow API Gateway to call Web GUI Service");



      // Create a security group for RDS
      SecurityGroup rdsSecurityGroup = SecurityGroup.Builder.create(this, "ReservationRdsSecurityGroup")
              .vpc(vpc)
              .allowAllOutbound(true)
              .build();

      // Allow API Server Fargate tasks to connect to RDS on port 5432
      //rdsSecurityGroup.addIngressRule(apiServerSecurityGroup, Port.tcp(5432),
      //        "Allow API Server Fargate services to access RDS"
      //);

      // Allow all resources in the VPC to connect to RDS on port 5432
      rdsSecurityGroup.addIngressRule(Peer.ipv4(vpc.getVpcCidrBlock()), Port.tcp(5432),
              "Allow all VPC resources to access RDS"
      );

      // --- Secrets for PostgreSQL (master user) ---
      DatabaseSecret dbSecret = DatabaseSecret.Builder.create(this, "ReservationPostgresSecret")
              .username("reservationuser")
              .secretName("reservation-postgres-secret")
              .build();

      // --- RDS PostgreSQL ---
      DatabaseInstance postgres = DatabaseInstance.Builder.create(this, "ReservationPostgresDB")
              .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                      .version(PostgresEngineVersion.VER_15_10)
                      .build()))
              .vpc(vpc)
              .securityGroups(List.of(rdsSecurityGroup))
              .instanceType(InstanceType.of(InstanceClass.BURSTABLE3, InstanceSize.MICRO))
              .allocatedStorage(20)
              .vpcSubnets(software.amazon.awscdk.services.ec2.SubnetSelection.builder()
                      .subnetType(SubnetType.PUBLIC)
                      .build())
              .credentials(Credentials.fromSecret(dbSecret))
              .multiAz(false)
              .databaseName("zbaloghspringdemo") // creates the DB with this name, owned by master user
              .port(5432)
              .removalPolicy(RemovalPolicy.DESTROY)
              .deletionProtection(false)
              .build();


      // --- API Service (Spring Boot) ---
      TaskDefinition apiTaskDef = TaskDefinition.Builder.create(this, "ReservationApiTaskDef")
              .compatibility(Compatibility.FARGATE)
              .cpu("512")
              .memoryMiB("1024")
              .build();

      ContainerDefinition apiContainer = apiTaskDef.addContainer("ReservationApiContainer", ContainerDefinitionOptions.builder()
              .image(ContainerImage.fromRegistry("zbalogh/reservation-api-server:latest"))
              .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                      .streamPrefix("api-service")
                      .build()))
              .environment(Map.of(
                      "DB_HOSTNAME", postgres.getDbInstanceEndpointAddress(),
                      "DB_PORT", String.valueOf(5432),
                      "DB_NAME", "zbaloghspringdemo",
                      "DB_USERNAME", "reservationuser",
                      "DB_PASSWORD", dbSecret.secretValueFromJson("password").toString()
              ))
              .build());

      apiContainer.addPortMappings(PortMapping.builder().containerPort(8080).build());

      FargateService apiService = FargateService.Builder.create(this, "ReservationApiService")
              .cluster(cluster)
              .taskDefinition(apiTaskDef)
              .desiredCount(1)
              .cloudMapOptions(CloudMapOptions.builder()
                      .name("api-server")
                      .cloudMapNamespace(cluster.getDefaultCloudMapNamespace())
                      .build())
              .assignPublicIp(true)
              .securityGroups(List.of(apiServerSecurityGroup))
              .build();


      // --- Auth Service (.NET) ---
      TaskDefinition authTaskDef = TaskDefinition.Builder.create(this, "ReservationAuthTaskDef")
              .compatibility(Compatibility.FARGATE)
              .cpu("512")
              .memoryMiB("1024")
              .build();

      ContainerDefinition authContainer = authTaskDef.addContainer("ReservationAuthContainer", ContainerDefinitionOptions.builder()
              .image(ContainerImage.fromRegistry("zbalogh/reservation-dotnet-auth-server:latest"))
              .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                      .streamPrefix("auth-service")
                      .build()))
              .environment(Map.of(
                      // if auth needs DB or other env, add here
              ))
              .build());

      authContainer.addPortMappings(PortMapping.builder().containerPort(5000).build());

      FargateService authService = FargateService.Builder.create(this, "ReservationAuthService")
              .cluster(cluster)
              .taskDefinition(authTaskDef)
              .desiredCount(1)
              .cloudMapOptions(CloudMapOptions.builder()
                      .name("auth-server")
                      .cloudMapNamespace(cluster.getDefaultCloudMapNamespace())
                      .build())
              .assignPublicIp(true)
              .securityGroups(List.of(authServerSecurityGroup))
              .build();


      // --- Web GUI Service (Angular) ---
      TaskDefinition webTaskDef = TaskDefinition.Builder.create(this, "ReservationWebGuiTaskDef")
              .compatibility(Compatibility.FARGATE)
              .cpu("512")
              .memoryMiB("1024")
              .build();

      ContainerDefinition webContainer = webTaskDef.addContainer("ReservationWebGuiContainer", ContainerDefinitionOptions.builder()
              .image(ContainerImage.fromRegistry("zbalogh/reservation-angular-ui:latest"))
              .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                      .streamPrefix("webgui-service")
                      .build()))
              .environment(Map.of(
                      // add env for frontend if needed
              ))
              .build());

      webContainer.addPortMappings(PortMapping.builder().containerPort(8050).build());

      FargateService webGuiService = FargateService.Builder.create(this, "ReservationWebGuiService")
              .cluster(cluster)
              .taskDefinition(webTaskDef)
              .desiredCount(1)
              .cloudMapOptions(CloudMapOptions.builder()
                      .name("webgui-server")
                      .cloudMapNamespace(cluster.getDefaultCloudMapNamespace())
                      .build())
              .assignPublicIp(true)
              .securityGroups(List.of(webGuiSecurityGroup))
              .build();


      // --- API Gateway + ALB (ApplicationLoadBalancedFargateService) ---
      ApplicationLoadBalancedFargateService gatewayAlbService = ApplicationLoadBalancedFargateService.Builder.create(this, "ReservationALBService")
              .cluster(cluster)
              .taskImageOptions(ApplicationLoadBalancedTaskImageOptions.builder()
                      .image(ContainerImage.fromRegistry("zbalogh/reservation-spring-api-gateway:latest"))
                      .containerPort(8000)
                      .environment(Map.of(
                              "API_SERVER_NAME", "api-server.reservation.local",
                              "AUTH_SERVER_NAME", "auth-server.reservation.local",
                              "WEBGUI_SERVER_NAME", "webgui-server.reservation.local"
                      ))
                      .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                              .streamPrefix("gateway-service")
                              .build()))
                      .build())
              .publicLoadBalancer(true)  // ALB internet facing (HTTP on port 80)
              .desiredCount(1)
              .assignPublicIp(true)
              .cpu(512)
              .memoryLimitMiB(1024)
              .securityGroups(List.of(gatewaySecurityGroup))
              .build();

      gatewayAlbService.getLoadBalancer().addSecurityGroup(albSecurityGroup);

      // Optionally you can reference the ALB DNS name:
      // String gatewayUrl = gatewayAlbService.getLoadBalancer().getLoadBalancerDnsName();
  }


  public static void main(final String[] args)
  {
    App app = new App(AppProps.builder().outdir("./cdk.out/aws-public-nets").build());

    StackProps props = StackProps.builder()
        .synthesizer(new BootstraplessSynthesizer())
        .build();

    System.out.println("App synthesizing in progress...");

    new AwsPublicStack(app, "resappawspublicstack", props);
    app.synth();

    System.out.println("App synthesizing completed.");
  }

}
