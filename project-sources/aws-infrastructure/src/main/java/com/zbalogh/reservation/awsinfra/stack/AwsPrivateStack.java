package com.zbalogh.reservation.awsinfra.stack;

import java.util.List;
import java.util.Map;
import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.Duration;
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
import software.amazon.awscdk.services.servicediscovery.PrivateDnsNamespace;

public class AwsPrivateStack extends Stack {

    public AwsPrivateStack(final App scope, final String id, final StackProps props)
    {
        super(scope, id, props);

        // --- VPC with public and private subnets, 1 NAT Gateway ---
        Vpc vpc = Vpc.Builder.create(this, "ReservationPrivateVpc")
                .maxAzs(2)
                .natGateways(2)
                .subnetConfiguration(List.of(
                        SubnetConfiguration.builder()
                                .name("ReservationPublicSubnet")
                                .subnetType(SubnetType.PUBLIC)
                                .cidrMask(24)
                                .build(),
                        SubnetConfiguration.builder()
                                .name("ReservationPrivateSubnet")
                                // --- it is deprecated, but still works ---
                                //.subnetType(SubnetType.PRIVATE_WITH_NAT)
                                // --- Egress is the new name for PRIVATE_WITH_NAT ---
                                .subnetType(SubnetType.PRIVATE_WITH_EGRESS)
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

        // --- Security Groups (same as AwsStack) ---
        SecurityGroup albSecurityGroup = SecurityGroup.Builder.create(this, "ReservationAlbSecurityGroup")
                .vpc(vpc)
                .allowAllOutbound(true)
                .build();

        albSecurityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(80), "Allow HTTP from anywhere to access ALB");


        SecurityGroup gatewaySecurityGroup = SecurityGroup.Builder.create(this, "ReservationGatewaySecurityGroup")
                .vpc(vpc)
                .allowAllOutbound(true)
                .build();

        gatewaySecurityGroup.addIngressRule(albSecurityGroup, Port.tcp(8000), "Allow ALB to call API Gateway");


        SecurityGroup apiServerSecurityGroup = SecurityGroup.Builder.create(this, "ReservationApiServerSecurityGroup")
                .vpc(vpc)
                .allowAllOutbound(true)
                .build();

        apiServerSecurityGroup.addIngressRule(gatewaySecurityGroup, Port.tcp(8080), "Allow API Gateway to call API Service");


        SecurityGroup authServerSecurityGroup = SecurityGroup.Builder.create(this, "ReservationAuthServerSecurityGroup")
                .vpc(vpc)
                .allowAllOutbound(true)
                .build();

        authServerSecurityGroup.addIngressRule(gatewaySecurityGroup, Port.tcp(5000), "Allow API Gateway to call Auth Service");


        SecurityGroup webGuiSecurityGroup = SecurityGroup.Builder.create(this, "ReservationWebGuiSecurityGroup")
                .vpc(vpc)
                .allowAllOutbound(true)
                .build();

        webGuiSecurityGroup.addIngressRule(gatewaySecurityGroup, Port.tcp(8050), "Allow API Gateway to call Web GUI Service");


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


        // --- RDS PostgreSQL in private subnet ---
        DatabaseInstance postgres = DatabaseInstance.Builder.create(this, "ReservationPostgresDB")
                .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                        .version(PostgresEngineVersion.VER_15_10)
                        .build()))
                .vpc(vpc)
                .securityGroups(List.of(rdsSecurityGroup))
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE3, InstanceSize.MICRO))
                .allocatedStorage(20)
                .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PRIVATE_WITH_EGRESS).build())
                .credentials(Credentials.fromSecret(dbSecret))
                .publiclyAccessible(false)
                .multiAz(false)
                .databaseName("zbaloghspringdemo")
                .port(5432)
                .removalPolicy(RemovalPolicy.DESTROY)
                .deletionProtection(false)
                .build();


        // --- API Service (Spring Boot) in private subnet ---
        TaskDefinition apiTaskDef = TaskDefinition.Builder.create(this, "ReservationApiTaskDef")
                .compatibility(Compatibility.FARGATE)
                .cpu("512")
                .memoryMiB("1024")
                .build();

        ContainerDefinition apiContainer = apiTaskDef.addContainer("ReservationApiContainer", ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry("zbalogh/reservation-api-server:latest"))
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder().streamPrefix("api-service").build()))
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
                .assignPublicIp(false)
                .securityGroups(List.of(apiServerSecurityGroup))
                .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PRIVATE_WITH_EGRESS).build())
                .build();


        // --- Auth Service (.NET) in private subnet ---
        TaskDefinition authTaskDef = TaskDefinition.Builder.create(this, "ReservationAuthTaskDef")
                .compatibility(Compatibility.FARGATE)
                .cpu("512")
                .memoryMiB("1024")
                .build();

        ContainerDefinition authContainer = authTaskDef.addContainer("ReservationAuthContainer", ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry("zbalogh/reservation-dotnet-auth-server:latest"))
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder().streamPrefix("auth-service").build()))
                .environment(Map.of())
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
                .assignPublicIp(false)
                .securityGroups(List.of(authServerSecurityGroup))
                .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PRIVATE_WITH_EGRESS).build())
                .build();


        // --- Web GUI Service (Angular) in private subnet ---
        TaskDefinition webTaskDef = TaskDefinition.Builder.create(this, "ReservationWebGuiTaskDef")
                .compatibility(Compatibility.FARGATE)
                .cpu("512")
                .memoryMiB("1024")
                .build();

        ContainerDefinition webContainer = webTaskDef.addContainer("ReservationWebGuiContainer", ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry("zbalogh/reservation-angular-ui:latest"))
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder().streamPrefix("webgui-service").build()))
                .environment(Map.of())
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
                .assignPublicIp(false)
                .securityGroups(List.of(webGuiSecurityGroup))
                .vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PRIVATE_WITH_EGRESS).build())
                .build();


        // --- API Gateway + ALB (ALB in public subnet, targets in private) ---
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
                        .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder().streamPrefix("gateway-service").build()))
                        .build())
                .publicLoadBalancer(true)
                .assignPublicIp(false)
                .desiredCount(1)
                .cpu(512)
                .memoryLimitMiB(1024)
                .securityGroups(List.of(gatewaySecurityGroup))
                .taskSubnets(SubnetSelection.builder().subnetType(SubnetType.PRIVATE_WITH_EGRESS).build())
                //.vpcSubnets(SubnetSelection.builder().subnetType(SubnetType.PRIVATE_WITH_EGRESS).build())
                .build();

        gatewayAlbService.getLoadBalancer().addSecurityGroup(albSecurityGroup);
    }


    public static void main(final String[] args)
    {
        App app = new App(AppProps.builder().outdir("./cdk.out/aws-private-nets").build());
        StackProps props = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer())
                .build();
        new AwsPrivateStack(app, "resappawsprivatestack", props);
        System.out.println("App synthesizing in progress...");
        app.synth();
    }

}
