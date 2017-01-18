package com.orange.ops.sbire.domain;

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Details of a request to rebind (delete and then re-create) existing service bindings.
 *
 * @author Sebastien Bortolussi
 */
@Value.Immutable
public abstract class RebindServiceBindingsRequest {

    /**
     * The name of the service broker whose service bindings need to be rebound.
     */
    public abstract String getServiceBrokerName();

    /**
     * The organization name of service bindings that need to be rebound.
     */
    public abstract Optional<String> getOrgName();

    /**
     * The space name of service bindings that need to be rebound.
     */
    public abstract Optional<String> getSpaceName();

    /**
     * The service label of service bindings that need to be rebound.
     */
    public abstract Optional<String> getServiceLabel();

    /**
     * The service plan name of service bindings that need to be rebound.
     */
    public abstract Optional<String> getServicePlanName();

    /**
     * The service instance name of service bindings that need to be rebound.
     */
    public abstract Optional<String> getServiceInstanceName();
}
