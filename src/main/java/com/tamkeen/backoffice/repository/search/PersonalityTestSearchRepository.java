package com.tamkeen.backoffice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.tamkeen.backoffice.domain.PersonalityTest;
import com.tamkeen.backoffice.repository.PersonalityTestRepository;
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
 * Spring Data Elasticsearch repository for the {@link PersonalityTest} entity.
 */
public interface PersonalityTestSearchRepository
    extends ElasticsearchRepository<PersonalityTest, String>, PersonalityTestSearchRepositoryInternal {}

interface PersonalityTestSearchRepositoryInternal {
    Page<PersonalityTest> search(String query, Pageable pageable);

    Page<PersonalityTest> search(Query query);

    @Async
    void index(PersonalityTest entity);

    @Async
    void deleteFromIndexById(String id);
}

class PersonalityTestSearchRepositoryInternalImpl implements PersonalityTestSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PersonalityTestRepository repository;

    PersonalityTestSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PersonalityTestRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<PersonalityTest> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<PersonalityTest> search(Query query) {
        SearchHits<PersonalityTest> searchHits = elasticsearchTemplate.search(query, PersonalityTest.class);
        List<PersonalityTest> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(PersonalityTest entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), PersonalityTest.class);
    }
}
