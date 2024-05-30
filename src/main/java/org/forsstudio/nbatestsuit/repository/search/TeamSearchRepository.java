package org.forsstudio.nbatestsuit.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import org.forsstudio.nbatestsuit.domain.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Team} entity.
 */
public interface TeamSearchRepository extends ReactiveElasticsearchRepository<Team, Long>, TeamSearchRepositoryInternal {}

interface TeamSearchRepositoryInternal {
    Flux<Team> search(String query, Pageable pageable);

    Flux<Team> search(Query query);
}

class TeamSearchRepositoryInternalImpl implements TeamSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    TeamSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Team> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<Team> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Team.class).map(SearchHit::getContent);
    }
}
