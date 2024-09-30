package com.tamkeen.backoffice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.tamkeen.backoffice.domain.Consultant;
import com.tamkeen.backoffice.repository.ConsultantRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Consultant} entity.
 */
public interface ConsultantSearchRepository extends ElasticsearchRepository<Consultant, String>, ConsultantSearchRepositoryInternal {}

interface ConsultantSearchRepositoryInternal {
    Page<Consultant> search(String query, Pageable pageable);

    Page<Consultant> search(Query query);

    @Async
    void index(Consultant entity);

    @Async
    void deleteFromIndexById(String id);
}

class ConsultantSearchRepositoryInternalImpl implements ConsultantSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ConsultantRepository repository;

    ConsultantSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ConsultantRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Consultant> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Consultant> search(Query query) {
        SearchHits<Consultant> searchHits = elasticsearchTemplate.search(query, Consultant.class);
        List<Consultant> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Consultant entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), Consultant.class);
    }
}
