package com.orange.ops.sbire.domain;

import reactor.core.publisher.Mono;

/**
 * This interface is implemented to process requests to list and rebind service bindings.
 *
 * @author Sebastien Bortolussi
 */

public interface ServiceBindingsService {

    /**
     * List service bindings for a service broker.
     * Results can be filtered by :
     * <ul>
     * <li> org name</li>
     * <li> space name</li>
     * <li> service label</li>
     * <li> plan name</li>
     * <li> instance name</li>
     * </ul>
     * <p>
     * See {@link ListServiceBindingsRequest} for details.
     *
     * @param request containing parameters sent from PaaS Operator
     * @return a ListServiceBindingsResponse
     */
    Mono<ListServiceBindingsResponse> list(ListServiceBindingsRequest request);

    /**
     * Rebind (Delete and then Re-create) existing service bindings for a service broker.
     * Rebind can be filtered by :
     * <ul>
     * <li> org name</li>
     * <li> space name</li>
     * <li> service label</li>
     * <li> plan name</li>
     * <li> instance name</li>
     * </ul>
     * <p>
     * See {@link RebindServiceBindingsRequest} for details.
     *
     * @param request containing parameters sent from PaaS Operator
     * @return a RebindServiceBindingsResponse
     */
    Mono<RebindServiceBindingsResponse> rebind(RebindServiceBindingsRequest request);

}
