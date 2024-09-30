package com.tamkeen.backoffice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.tamkeen.backoffice.domain.PersonalityType;
import com.tamkeen.backoffice.repository.PersonalityTypeRepository;
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
 * Spring Data Elasticsearch repository for the {@link PersonalityType} entity.
 */
public interface PersonalityTypeSearchRepository
    extends ElasticsearchRepository<PersonalityType, String>, PersonalityTypeSearchRepositoryInternal {}

interface PersonalityTypeSearchRepositoryInternal {
    Page<PersonalityType> search(String query, Pageable pageable);

    Page<PersonalityType> search(Query query);

    @Async
    void index(PersonalityType entity);

    @Async
    void deleteFromIndexById(String id);
}

class PersonalityTypeSearchRepositoryInternalImpl implements PersonalityTypeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PersonalityTypeRepository repository;

    PersonalityTypeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PersonalityTypeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<PersonalityType> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<PersonalityType> search(Query query) {
        SearchHits<PersonalityType> searchHits = elasticsearchTemplate.search(query, PersonalityType.class);
        List<PersonalityType> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(PersonalityType entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), PersonalityType.class);
    }
}
