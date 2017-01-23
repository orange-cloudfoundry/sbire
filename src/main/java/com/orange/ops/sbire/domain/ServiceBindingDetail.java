package com.orange.ops.sbire.domain;

import org.immutables.value.Value;

/**
 * Service binding details.
 *
 * @author Sebastien Bortolussi
 */
@Value.Immutable
public abstract class ServiceBindingDetail {

    /**
     * The id of the service service binding.
     */
    public abstract String getId();

    /**
     * The service instance id of the service service binding.
     */
    public abstract String getServiceInstanceId();

    /**
     * The service instance name of the service service binding.
     */
    public abstract String getServiceInstanceName();

    /**
     * The service application id of the service service binding.
     */
    public abstract String getApplicationId();

    /**
     * The service application name of the service service binding.
     */
    public abstract String getApplicationName();

    /**
     * The service plan name of the service service binding.
     */
    public abstract String getServicePlan();

    /**
     * The service name of the service service binding.
     */
    public abstract String getService();

    /**
     * The service broker name of the service service binding.
     */
    public abstract String getServiceBroker();

    /**
     * The space name of the service service binding.
     */
    public abstract String getSpace();

    /**
     * The organization name of the service service binding.
     */
    public abstract String getOrganization();

}
