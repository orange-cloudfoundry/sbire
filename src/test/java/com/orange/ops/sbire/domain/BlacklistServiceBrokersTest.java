package com.orange.ops.sbire.domain;

import com.orange.ops.sbire.tags.BlacklistServiceBrokers;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;

import static com.orange.ops.sbire.domain.ServiceBindingFixture.ALL_SERVICE_BINDINGS;

/**
 * @author Sebastien Bortolussi
 */
@BlacklistServiceBrokers
public class BlacklistServiceBrokersTest extends SimpleScenarioTest<ServiceBindingsStage> {

    @Test
    public void paas_ops_lists_all_service_bindings_for_blacklisted_service_broker() throws Exception {
        given().service_bindings(ALL_SERVICE_BINDINGS);
        when().paas_ops_lists_blacklisted_service_broker_$_service_bindings("p-redis");
        then().she_should_get_no_service_binding();
    }

}