package com.orange.ops.sbire.domain;

import org.immutables.value.Value;

/**
 * @author Sebastien Bortolussi
 */
@Value.Immutable
public abstract class SpaceSummary {

    public abstract String getId();

    public abstract String getName();

    public abstract String getOrganizationName();

    public abstract String getOrganizationId();


}
