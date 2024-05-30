package org.forsstudio.nbatestsuit.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import org.forsstudio.nbatestsuit.domain.PlayerInGame;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link PlayerInGame} entity.
 */
public interface PlayerInGameSearchRepository
    extends ReactiveElasticsearchRepository<PlayerInGame, Long>, PlayerInGameSearchRepositoryInternal {}

interface PlayerInGameSearchRepositoryInternal {
    Flux<PlayerInGame> search(String query, Pageable pageable);

    Flux<PlayerInGame> search(Query query);
}

class PlayerInGameSearchRepositoryInternalImpl implements PlayerInGameSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    PlayerInGameSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<PlayerInGame> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<PlayerInGame> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, PlayerInGame.class).map(SearchHit::getContent);
    }
}
