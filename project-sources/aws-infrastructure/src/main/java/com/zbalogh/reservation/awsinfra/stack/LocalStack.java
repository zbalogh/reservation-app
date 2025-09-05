package com.zbalogh.reservation.awsinfra.stack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Token;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;
import software.amazon.awscdk.services.servicediscovery.NamespaceType;

public class LocalStack extends Stack
{

  public LocalStack(final App scope, final String id, final StackProps props)
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
              .build();

      // --- Web GUI Service (Angular) ---
      TaskDefinition webTaskDef = TaskDefinition.Builder.create(this, "ReservationWebGuiTaskDef")
              .compatibility(Compatibility.FARGATE)
              .cpu("256")
              .memoryMiB("512")
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
              .build();

      // ALB DNS név LocalStack-ben nem publikus, de a konténer belsőként elérhető
      // String gatewayUrl = gatewayAlbService.getLoadBalancer().getLoadBalancerDnsName();
  }


  public static void main(final String[] args)
  {
    App app = new App(AppProps.builder().outdir("./cdk.out/local").build());

    StackProps props = StackProps.builder()
        .synthesizer(new BootstraplessSynthesizer())
        .build();

    System.out.println("App synthesizing in progress...");

    new LocalStack(app, "resapplocalstack", props);
    app.synth();

    System.out.println("App synthesizing completed.");
  }

}
