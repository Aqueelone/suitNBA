package org.forsstudio.nbatestsuit.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import org.forsstudio.nbatestsuit.domain.TeamInSeason;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link TeamInSeason} entity.
 */
public interface TeamInSeasonSearchRepository
    extends ReactiveElasticsearchRepository<TeamInSeason, Long>, TeamInSeasonSearchRepositoryInternal {}

interface TeamInSeasonSearchRepositoryInternal {
    Flux<TeamInSeason> search(String query, Pageable pageable);

    Flux<TeamInSeason> search(Query query);
}

class TeamInSeasonSearchRepositoryInternalImpl implements TeamInSeasonSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TeamInSeasonSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<TeamInSeason> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<TeamInSeason> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, TeamInSeason.class).map(SearchHit::getContent);
    }
}
