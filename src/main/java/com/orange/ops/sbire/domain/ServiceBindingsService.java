package com.orange.ops.sbire.domain;

import reactor.core.publisher.Flux;

/**
 * @author Sebastien Bortolussi
 */
public interface ServiceBindingsService {

    Flux<ServiceBindingSummary> list(ListServiceBindingsRequest request);

}
