package com.orange.ops.sbire.domain;

import com.orange.ops.sbire.tags.ListServiceBindings;
import com.orange.ops.sbire.tags.v1_0_0_RELEASE;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;

/**
 * @author Sebastien Bortolussi
 */
@ListServiceBindings
@v1_0_0_RELEASE
public class ListServiceBindingsTest extends SimpleScenarioTest<ListServiceBindingsStage> {

    private static final ImmutableServiceBindingSummary[] ALL_SERVICE_BINDINGS = new ImmutableServiceBindingSummary[]{
            ImmutableServiceBindingSummary.builder()
                    .id("aaa")
                    .serviceInstance("my-redis-111")
                    .servicePlan("shared-vm")
                    .service("p-redis")
                    .space("space11")
                    .organization("org1")
                    .application("my-app")
                    .build(),
            ImmutableServiceBindingSummary.builder()
                    .id("bbb")
                    .serviceInstance("my-redis-211")
                    .servicePlan("dedicated-vm")
                    .service("p-redis")
                    .space("space11")
                    .organization("org1")
                    .application("my-app")
                    .build(),
            ImmutableServiceBindingSummary.builder()
                    .id("ccc")
                    .serviceInstance("my-redis-112")
                    .servicePlan("shared-vm")
                    .service("p-redis")
                    .space("space12")
                    .organization("org2")
                    .application("my-app")
                    .build(),
            ImmutableServiceBindingSummary.builder()
                    .id("ddd")
                    .serviceInstance("my-redis-122")
                    .servicePlan("shared-vm")
                    .service("p-redis")
                    .space("space22")
                    .organization("org2")
                    .application("my-app")
                    .build()
    };


    @Test
    public void paas_ops_lists_all_service_broker_service_bindings() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings("p-redis");
        then().she_should_get_service_bindings(ALL_SERVICE_BINDINGS);
    }

    @Test
    public void paas_ops_lists_service_bindings_for_unknown_service_broker() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings("dummy");
        then().she_should_get_error("Service Broker dummy does not exist");
    }

    @Test
    public void paas_ops_lists_service_broker_service_bindings_for_an_org() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings_for_org("p-redis", "org1");
        then().she_should_get_service_bindings
                (
                        ImmutableServiceBindingSummary.builder()
                                .id("aaa")
                                .serviceInstance("my-redis-111")
                                .servicePlan("shared-vm")
                                .service("p-redis")
                                .space("space11")
                                .organization("org1")
                                .application("my-app")
                                .build(),
                        ImmutableServiceBindingSummary.builder()
                                .id("bbb")
                                .serviceInstance("my-redis-211")
                                .servicePlan("dedicated-vm")
                                .service("p-redis")
                                .space("space11")
                                .organization("org1")
                                .application("my-app")
                                .build()
                );
    }

    @Test
    public void paas_ops_lists_service_broker_service_bindings_for_an_unknown_org() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings_for_org("p-redis", "dummy");
        then().she_should_get_no_service_binding();
    }

    @Test
    public void paas_ops_lists_service_broker_service_bindings_for_a_space() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings_for_org_$_and_space("p-redis", "org2", "space12");
        then().she_should_get_service_bindings
                (
                        ImmutableServiceBindingSummary.builder()
                                .id("ccc")
                                .serviceInstance("my-redis-112")
                                .servicePlan("shared-vm")
                                .service("p-redis")
                                .space("space12")
                                .organization("org2")
                                .application("my-app")
                                .build()
                );
    }

    @Test
    public void paas_ops_lists_service_broker_service_bindings_for_an_unknown_space() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings_for_org_$_and_space("p-redis", "org2", "dummy");
        then().she_should_get_no_service_binding();
    }

    @Test
    public void paas_ops_lists_service_broker_service_bindings_for_a_service_label() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings_for_service_label("p-redis", "p-redis");
        then().she_should_get_service_bindings(ALL_SERVICE_BINDINGS);
    }

    @Test
    public void paas_ops_lists_service_broker_service_bindings_for_an_unknown_service_label() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings_for_service_label("p-redis", "dummy");
        then().she_should_get_no_service_binding();
    }


    @Test
    public void paas_ops_lists_service_broker_service_bindings_for_a_service_plan() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings_for_service_plan_name("p-redis", "dedicated-vm");
        then().she_should_get_service_bindings
                (
                        ImmutableServiceBindingSummary.builder()
                                .id("bbb")
                                .serviceInstance("my-redis-211")
                                .servicePlan("dedicated-vm")
                                .service("p-redis")
                                .space("space11")
                                .organization("org1")
                                .application("my-app")
                                .build()
                );
    }

    @Test
    public void paas_ops_lists_service_broker_service_bindings_for_an_unknown_service_plan() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings_for_service_plan_name("p-redis", "dummy");
        then().she_should_get_no_service_binding();
    }

    @Test
    public void paas_ops_lists_service_broker_service_bindings_for_a_service_instance() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings_for_service_instance("p-redis", "my-redis-112");
        then().she_should_get_service_bindings
                (
                        ImmutableServiceBindingSummary.builder()
                                .id("ccc")
                                .serviceInstance("my-redis-112")
                                .servicePlan("shared-vm")
                                .service("p-redis")
                                .space("space12")
                                .organization("org2")
                                .application("my-app")
                                .build()
                );
    }

    @Test
    public void paas_ops_lists_service_broker_service_bindings_for_an_unknown_service_instance() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings_for_service_instance("p-redis", "dummy");
        then().she_should_get_no_service_binding();
    }

}