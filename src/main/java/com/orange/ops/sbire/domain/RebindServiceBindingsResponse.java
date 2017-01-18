package com.orange.ops.sbire.domain;

import org.cloudfoundry.client.v2.PaginatedResponse;
import org.immutables.value.Value;

/**
 * Details of a response to a request to rebind (delete and then re-create) existing service bindings.
 *
 * @author Sebastien Bortolussi
 */
@Value.Immutable
public abstract class RebindServiceBindingsResponse extends PaginatedResponse<ServiceBindingDetailResource> {

}
