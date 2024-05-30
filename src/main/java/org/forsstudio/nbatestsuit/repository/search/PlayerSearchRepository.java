package org.forsstudio.nbatestsuit.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import org.forsstudio.nbatestsuit.domain.Player;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Player} entity.
 */
public interface PlayerSearchRepository extends ReactiveElasticsearchRepository<Player, Long>, PlayerSearchRepositoryInternal {}

interface PlayerSearchRepositoryInternal {
    Flux<Player> search(String query, Pageable pageable);

    Flux<Player> search(Query query);
}

class PlayerSearchRepositoryInternalImpl implements PlayerSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    PlayerSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Player> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<Player> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Player.class).map(SearchHit::getContent);
    }
}
