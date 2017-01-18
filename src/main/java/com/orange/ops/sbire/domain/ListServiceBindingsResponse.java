package com.orange.ops.sbire.domain;

import org.cloudfoundry.client.v2.PaginatedResponse;
import org.immutables.value.Value;

/**
 * Details of a response to a request to list service bindings.
 *
 * @author Sebastien Bortolussi
 */
@Value.Immutable
public abstract class ListServiceBindingsResponse extends PaginatedResponse<ServiceBindingDetailResource> {

}
