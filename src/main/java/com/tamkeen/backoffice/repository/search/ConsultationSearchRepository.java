package com.tamkeen.backoffice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.tamkeen.backoffice.domain.Consultation;
import com.tamkeen.backoffice.repository.ConsultationRepository;
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
 * Spring Data Elasticsearch repository for the {@link Consultation} entity.
 */
public interface ConsultationSearchRepository extends ElasticsearchRepository<Consultation, String>, ConsultationSearchRepositoryInternal {}

interface ConsultationSearchRepositoryInternal {
    Page<Consultation> search(String query, Pageable pageable);

    Page<Consultation> search(Query query);

    @Async
    void index(Consultation entity);

    @Async
    void deleteFromIndexById(String id);
}

class ConsultationSearchRepositoryInternalImpl implements ConsultationSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ConsultationRepository repository;

    ConsultationSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ConsultationRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Consultation> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Consultation> search(Query query) {
        SearchHits<Consultation> searchHits = elasticsearchTemplate.search(query, Consultation.class);
        List<Consultation> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Consultation entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), Consultation.class);
    }
}
