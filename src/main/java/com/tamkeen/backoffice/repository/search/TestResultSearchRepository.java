package com.tamkeen.backoffice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.tamkeen.backoffice.domain.TestResult;
import com.tamkeen.backoffice.repository.TestResultRepository;
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
 * Spring Data Elasticsearch repository for the {@link TestResult} entity.
 */
public interface TestResultSearchRepository extends ElasticsearchRepository<TestResult, String>, TestResultSearchRepositoryInternal {}

interface TestResultSearchRepositoryInternal {
    Page<TestResult> search(String query, Pageable pageable);

    Page<TestResult> search(Query query);

    @Async
    void index(TestResult entity);

    @Async
    void deleteFromIndexById(String id);
}

class TestResultSearchRepositoryInternalImpl implements TestResultSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TestResultRepository repository;

    TestResultSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TestResultRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TestResult> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<TestResult> search(Query query) {
        SearchHits<TestResult> searchHits = elasticsearchTemplate.search(query, TestResult.class);
        List<TestResult> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TestResult entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), TestResult.class);
    }
}
