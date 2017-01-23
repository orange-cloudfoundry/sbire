package com.orange.ops.sbire.domain;

/**
 * @author Sebastien Bortolussi
 */
public class ServiceBindingFixture {

    public static final ImmutableServiceBindingDetail SERVICE_BINDING_AAA = ImmutableServiceBindingDetail.builder()
            .id("aaa")
            .serviceInstanceId("my-redis-111-id")
            .serviceInstanceName("my-redis-111")
            .applicationId("my-app-id")
            .applicationName("my-app")
            .servicePlan("shared-vm")
            .service("p-redis")
            .serviceBroker("p-redis")
            .space("space11")
            .organization("org1")
            .build();
    public static final ImmutableServiceBindingDetail SERVICE_BINDING_BBB = ImmutableServiceBindingDetail.builder()
            .id("bbb")
            .serviceInstanceId("my-redis-211-id")
            .serviceInstanceName("my-redis-211")
            .applicationName("my-app")
            .applicationId("my-app-id")
            .servicePlan("dedicated-vm")
            .service("p-redis")
            .serviceBroker("p-redis")
            .space("space11")
            .organization("org1")
            .build();
    public static final ImmutableServiceBindingDetail SERVICE_BINDING_CCC = ImmutableServiceBindingDetail.builder()
            .id("ccc")
            .serviceInstanceId("my-redis-112-id")
            .serviceInstanceName("my-redis-112")
            .applicationName("my-app")
            .applicationId("my-app-id")
            .servicePlan("shared-vm")
            .service("p-redis")
            .serviceBroker("p-redis")
            .space("space12")
            .organization("org2")
            .build();
    public static final ImmutableServiceBindingDetail SERVICE_BINDING_DDD = ImmutableServiceBindingDetail.builder()
            .id("ddd")
            .serviceInstanceId("my-redis-122-id")
            .serviceInstanceName("my-redis-122")
            .applicationName("my-app")
            .applicationId("my-app-id")
            .servicePlan("shared-vm")
            .service("p-redis")
            .serviceBroker("p-redis")
            .space("space22")
            .organization("org2")
            .build();
    public static final ImmutableServiceBindingDetail SERVICE_BINDING_EEE = ImmutableServiceBindingDetail.builder()
            .id("eee")
            .serviceInstanceId("my-sql-122-id")
            .serviceInstanceName("my-sql-122")
            .applicationName("my-app")
            .applicationId("my-app-id")
            .servicePlan("1GB")
            .service("p-mysql")
            .serviceBroker("p-mysql")
            .space("space22")
            .organization("org2")
            .build();

    public static final ImmutableServiceBindingDetail NEW_SERVICE_BINDING_MMM = ImmutableServiceBindingDetail.builder()
            .id("mmm")
            .serviceInstanceId("my-redis-111-id")
            .serviceInstanceName("my-redis-111")
            .applicationId("my-app-id")
            .applicationName("my-app")
            .servicePlan("shared-vm")
            .service("p-redis")
            .serviceBroker("p-redis")
            .space("space11")
            .organization("org1")
            .build();
    public static final ImmutableServiceBindingDetail NEW_SERVICE_BINDING_NNN = ImmutableServiceBindingDetail.builder()
            .id("nnn")
            .serviceInstanceId("my-redis-211-id")
            .serviceInstanceName("my-redis-211")
            .applicationName("my-app")
            .applicationId("my-app-id")
            .servicePlan("dedicated-vm")
            .service("p-redis")
            .serviceBroker("p-redis")
            .space("space11")
            .organization("org1")
            .build();
    public static final ImmutableServiceBindingDetail NEW_SERVICE_BINDING_OOO = ImmutableServiceBindingDetail.builder()
            .id("ooo")
            .serviceInstanceId("my-redis-112-id")
            .serviceInstanceName("my-redis-112")
            .applicationName("my-app")
            .applicationId("my-app-id")
            .servicePlan("shared-vm")
            .service("p-redis")
            .serviceBroker("p-redis")
            .space("space12")
            .organization("org2")
            .build();
    public static final ImmutableServiceBindingDetail NEW_SERVICE_BINDING_PPP = ImmutableServiceBindingDetail.builder()
            .id("ppp")
            .serviceInstanceId("my-redis-122-id")
            .serviceInstanceName("my-redis-122")
            .applicationName("my-app")
            .applicationId("my-app-id")
            .servicePlan("shared-vm")
            .service("p-redis")
            .serviceBroker("p-redis")
            .space("space22")
            .organization("org2")
            .build();

    public static final ImmutableServiceBindingDetail[] ALL_SERVICE_BINDINGS = new ImmutableServiceBindingDetail[]{
            SERVICE_BINDING_AAA,
            SERVICE_BINDING_BBB,
            SERVICE_BINDING_CCC,
            SERVICE_BINDING_DDD,
            SERVICE_BINDING_EEE
    };

    public static final ImmutableServiceBindingDetail[] P_REDIS_SERVICE_BROKER_SERVICE_BINDINGS = new ImmutableServiceBindingDetail[]{
            SERVICE_BINDING_AAA,
            SERVICE_BINDING_BBB,
            SERVICE_BINDING_CCC,
            SERVICE_BINDING_DDD
    };

    public static final ImmutableServiceBindingDetail[] NEW_P_REDIS_SERVICE_BROKER_SERVICE_BINDINGS = new ImmutableServiceBindingDetail[]{
            NEW_SERVICE_BINDING_MMM,
            NEW_SERVICE_BINDING_NNN,
            NEW_SERVICE_BINDING_OOO,
            NEW_SERVICE_BINDING_PPP
    };

    public static final ImmutableServiceBindingDetail[] ORG1_SERVICE_BINDINGS = new ImmutableServiceBindingDetail[]{
            SERVICE_BINDING_AAA,
            SERVICE_BINDING_BBB
    };

    public static final ImmutableServiceBindingDetail[] NEW_ORG1_SERVICE_BINDINGS = new ImmutableServiceBindingDetail[]{
            NEW_SERVICE_BINDING_MMM,
            NEW_SERVICE_BINDING_NNN
    };

    public static final ImmutableServiceBindingDetail[] P_REDIS_SERVICE_SERVICE_BINDINGS = new ImmutableServiceBindingDetail[]{
            SERVICE_BINDING_AAA,
            SERVICE_BINDING_BBB,
            SERVICE_BINDING_CCC,
            SERVICE_BINDING_DDD
    };

    public static final ImmutableServiceBindingDetail[] NEW_P_REDIS_SERVICE_SERVICE_BINDINGS = new ImmutableServiceBindingDetail[]{
            NEW_SERVICE_BINDING_MMM,
            NEW_SERVICE_BINDING_NNN,
            NEW_SERVICE_BINDING_OOO,
            NEW_SERVICE_BINDING_PPP
    };

}
