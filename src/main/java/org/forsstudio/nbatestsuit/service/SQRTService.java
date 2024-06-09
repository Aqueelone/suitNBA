package org.forsstudio.nbatestsuit.service;

import reactor.core.publisher.Mono;

public interface SQRTService {
    <T> Mono<String> getReferenceData(T object);
}
