package org.forsstudio.nbatestsuit.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import org.forsstudio.nbatestsuit.domain.PlayerInTeam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link PlayerInTeam} entity.
 */
public interface PlayerInTeamSearchRepository
    extends ReactiveElasticsearchRepository<PlayerInTeam, Long>, PlayerInTeamSearchRepositoryInternal {}

interface PlayerInTeamSearchRepositoryInternal {
    Flux<PlayerInTeam> search(String query, Pageable pageable);

    Flux<PlayerInTeam> search(Query query);
}

class PlayerInTeamSearchRepositoryInternalImpl implements PlayerInTeamSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    PlayerInTeamSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<PlayerInTeam> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<PlayerInTeam> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, PlayerInTeam.class).map(SearchHit::getContent);
    }
}
