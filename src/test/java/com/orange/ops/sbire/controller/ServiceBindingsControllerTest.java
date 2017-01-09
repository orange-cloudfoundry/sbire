package com.orange.ops.sbire.controller;

import com.orange.ops.sbire.domain.ImmutableServiceBindingSummary;
import com.orange.ops.sbire.tags.HttpAPI;
import com.orange.ops.sbire.tags.ListServiceBindings;
import com.orange.ops.sbire.tags.v1_0_0_RELEASE;
import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringScenarioTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static org.cloudfoundry.util.test.TestObjects.fill;

/**
 * @author Sebastien Bortolussi
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = ServiceBindingsController.class, secure = false)
@EnableJGiven
@ComponentScan
@ListServiceBindings
@HttpAPI
@v1_0_0_RELEASE
public class ServiceBindingsControllerTest extends SimpleSpringScenarioTest<ServiceBindingsControllerStage> {

    @Test
    public void no_filter() throws Exception {
        given().service_broker_service_bindings(fill(ImmutableServiceBindingSummary.builder())
                .build());
        when().paas_ops_lists_service_broker_service_bindings("/v1/service_brokers/p-redis/service_bindings");
        then().she_should_get_HTTP_$_response(HttpStatus.OK)
                .she_should_get_json_response("[{\"id\": \"test-id\",\"serviceInstance\": \"test-serviceInstance\",\"application\": \"test-application\",\"servicePlan\": \"test-servicePlan\",\"service\": \"test-service\",\"space\": \"test-space\",\"organization\": \"test-organization\"}]");
    }

    @Test
    public void filter_by_organization_name() throws Exception {
        given().service_broker_service_bindings_for_org1(fill(ImmutableServiceBindingSummary.builder())
                .organization("org1")
                .build());
        when().paas_ops_lists_service_broker_service_bindings("/v1/service_brokers/p-redis/service_bindings?org_name=org1");
        then().she_should_get_HTTP_$_response(HttpStatus.OK)
                .she_should_get_json_response("[{\"id\": \"test-id\",\"serviceInstance\": \"test-serviceInstance\",\"application\": \"test-application\",\"servicePlan\": \"test-servicePlan\",\"service\": \"test-service\",\"space\": \"test-space\",\"organization\": \"org1\"}]");
    }

    @Test
    public void filter_by_space_name() throws Exception {
        given().service_broker_service_bindings_for_org1_space12(fill(ImmutableServiceBindingSummary.builder())
                .organization("org1")
                .space("space12")
                .build());
        when().paas_ops_lists_service_broker_service_bindings("/v1/service_brokers/p-redis/service_bindings?org_name=org1&space_name=space12");
        then().she_should_get_HTTP_$_response(HttpStatus.OK)
                .she_should_get_json_response("[{\"id\": \"test-id\",\"serviceInstance\": \"test-serviceInstance\",\"application\": \"test-application\",\"servicePlan\": \"test-servicePlan\",\"service\": \"test-service\",\"space\": \"space12\",\"organization\": \"org1\"}]");

    }

    @Test
    public void filter_by_service_label() throws Exception {
        given().service_broker_service_bindings_for_service_label_p_redis(fill(ImmutableServiceBindingSummary.builder())
                .service("p-redis")
                .build());
        when().paas_ops_lists_service_broker_service_bindings("/v1/service_brokers/p-redis/service_bindings?service_label=p-redis");
        then().she_should_get_HTTP_$_response(HttpStatus.OK)
                .she_should_get_json_response("[{\"id\": \"test-id\",\"serviceInstance\": \"test-serviceInstance\",\"application\": \"test-application\",\"servicePlan\": \"test-servicePlan\",\"service\": \"p-redis\",\"space\": \"test-space\",\"organization\": \"test-organization\"}]");
    }

    @Test
    public void filter_by_service_plan() throws Exception {
        given().service_broker_service_bindings_for_service_plan_dedicated_vm(fill(ImmutableServiceBindingSummary.builder())
                .servicePlan("dedicated-vm")
                .build());
        when().paas_ops_lists_service_broker_service_bindings("/v1/service_brokers/p-redis/service_bindings?plan_name=dedicated-vm");
        then().she_should_get_HTTP_$_response(HttpStatus.OK)
                .she_should_get_json_response("[{\"id\": \"test-id\",\"serviceInstance\": \"test-serviceInstance\",\"application\": \"test-application\",\"servicePlan\": \"dedicated-vm\",\"service\": \"test-service\",\"space\": \"test-space\",\"organization\": \"test-organization\"}]");

    }

    @Test
    public void filter_by_service_instance() throws Exception {
        given().service_broker_service_bindings_for_service_instance_my_redis_112(fill(ImmutableServiceBindingSummary.builder())
                .serviceInstance("my-redis-112")
                .build());
        when().paas_ops_lists_service_broker_service_bindings("/v1/service_brokers/p-redis/service_bindings?instance_name=my-redis-112");
        then().she_should_get_HTTP_$_response(HttpStatus.OK)
                .she_should_get_json_response("[{\"id\": \"test-id\",\"serviceInstance\": \"my-redis-112\",\"application\": \"test-application\",\"servicePlan\": \"test-servicePlan\",\"service\": \"test-service\",\"space\": \"test-space\",\"organization\": \"test-organization\"}]");
    }

}