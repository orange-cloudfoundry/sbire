package com.orange.ops.sbire.domain;

import org.immutables.value.Value;

/**
 * @author Sebastien Bortolussi
 */
@Value.Immutable
public abstract class ServiceBindingSummary {

    public abstract String getId();

    public abstract String getServiceInstance();

    public abstract String getApplication();

    public abstract String getServicePlan();

    public abstract String getService();

    public abstract String getSpace();

    public abstract String getOrganization();

}
