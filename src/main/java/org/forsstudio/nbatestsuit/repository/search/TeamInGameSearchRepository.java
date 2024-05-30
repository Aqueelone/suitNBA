package org.forsstudio.nbatestsuit.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import org.forsstudio.nbatestsuit.domain.TeamInGame;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link TeamInGame} entity.
 */
public interface TeamInGameSearchRepository extends ReactiveElasticsearchRepository<TeamInGame, Long>, TeamInGameSearchRepositoryInternal {}

interface TeamInGameSearchRepositoryInternal {
    Flux<TeamInGame> search(String query, Pageable pageable);

    Flux<TeamInGame> search(Query query);
}

class TeamInGameSearchRepositoryInternalImpl implements TeamInGameSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TeamInGameSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<TeamInGame> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<TeamInGame> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, TeamInGame.class).map(SearchHit::getContent);
    }
}
