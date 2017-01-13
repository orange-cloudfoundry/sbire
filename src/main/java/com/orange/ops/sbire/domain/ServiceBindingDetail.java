package com.orange.ops.sbire.domain;

import org.immutables.value.Value;

/**
 * @author Sebastien Bortolussi
 */
@Value.Immutable
public abstract class ServiceBindingDetail {

    public abstract String getId();

    public abstract String getServiceInstanceId();

    public abstract String getServiceInstanceName();

    public abstract String getApplicationId();

    public abstract String getApplicationName();

    public abstract String getServicePlan();

    public abstract String getService();

    public abstract String getSpace();

    public abstract String getOrganization();

}
