package com.tamkeen.backoffice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.tamkeen.backoffice.domain.Answer;
import com.tamkeen.backoffice.repository.AnswerRepository;
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
 * Spring Data Elasticsearch repository for the {@link Answer} entity.
 */
public interface AnswerSearchRepository extends ElasticsearchRepository<Answer, String>, AnswerSearchRepositoryInternal {}

interface AnswerSearchRepositoryInternal {
    Page<Answer> search(String query, Pageable pageable);

    Page<Answer> search(Query query);

    @Async
    void index(Answer entity);

    @Async
    void deleteFromIndexById(String id);
}

class AnswerSearchRepositoryInternalImpl implements AnswerSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AnswerRepository repository;

    AnswerSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AnswerRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Answer> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Answer> search(Query query) {
        SearchHits<Answer> searchHits = elasticsearchTemplate.search(query, Answer.class);
        List<Answer> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Answer entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), Answer.class);
    }
}
