package com.tamkeen.backoffice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.tamkeen.backoffice.domain.UserResponse;
import com.tamkeen.backoffice.repository.UserResponseRepository;
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
 * Spring Data Elasticsearch repository for the {@link UserResponse} entity.
 */
public interface UserResponseSearchRepository extends ElasticsearchRepository<UserResponse, String>, UserResponseSearchRepositoryInternal {}

interface UserResponseSearchRepositoryInternal {
    Page<UserResponse> search(String query, Pageable pageable);

    Page<UserResponse> search(Query query);

    @Async
    void index(UserResponse entity);

    @Async
    void deleteFromIndexById(String id);
}

class UserResponseSearchRepositoryInternalImpl implements UserResponseSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UserResponseRepository repository;

    UserResponseSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, UserResponseRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<UserResponse> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<UserResponse> search(Query query) {
        SearchHits<UserResponse> searchHits = elasticsearchTemplate.search(query, UserResponse.class);
        List<UserResponse> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(UserResponse entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), UserResponse.class);
    }
}
