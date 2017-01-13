package com.orange.ops.sbire.domain;

import reactor.core.publisher.Flux;

/**
 * @author Sebastien Bortolussi
 */
public interface ServiceBindingsService {

    Flux<ServiceBindingDetail> list(ListServiceBindingsRequest request);

    Flux<ServiceBindingDetail> rebind(ImmutableRebindServiceBindingsRequest request);
}
