package com.orange.ops.sbire.domain;

import com.orange.ops.sbire.tags.ListServiceBindings;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;

import static com.orange.ops.sbire.domain.ServiceBindingFixture.*;


/**
 * @author Sebastien Bortolussi
 */
@ListServiceBindings
public class ListServiceBindingsTest extends SimpleScenarioTest<ServiceBindingsStage> {


    @Test
    public void paas_ops_lists_all_service_broker_service_bindings() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings("p-redis");
        then().she_should_get_$_service_bindings(4, P_REDIS_SERVICE_BROKER_SERVICE_BINDINGS);
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
        then().she_should_get_$_service_bindings(2, ORG1_SERVICE_BINDINGS);
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
        then().she_should_get_$_service_bindings(1, SERVICE_BINDING_CCC);
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
        then().she_should_get_$_service_bindings(4, P_REDIS_SERVICE_SERVICE_BINDINGS);
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
        then().she_should_get_$_service_bindings(1, SERVICE_BINDING_BBB);
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
        then().she_should_get_$_service_bindings(1, SERVICE_BINDING_CCC);
    }

    @Test
    public void paas_ops_lists_service_broker_service_bindings_for_an_unknown_service_instance() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_service_broker_$_service_bindings_for_service_instance("p-redis", "dummy");
        then().she_should_get_no_service_binding();
    }

}