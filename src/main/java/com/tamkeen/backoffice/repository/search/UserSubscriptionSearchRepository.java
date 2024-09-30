package com.tamkeen.backoffice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.tamkeen.backoffice.domain.UserSubscription;
import com.tamkeen.backoffice.repository.UserSubscriptionRepository;
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
 * Spring Data Elasticsearch repository for the {@link UserSubscription} entity.
 */
public interface UserSubscriptionSearchRepository
    extends ElasticsearchRepository<UserSubscription, String>, UserSubscriptionSearchRepositoryInternal {}

interface UserSubscriptionSearchRepositoryInternal {
    Page<UserSubscription> search(String query, Pageable pageable);

    Page<UserSubscription> search(Query query);

    @Async
    void index(UserSubscription entity);

    @Async
    void deleteFromIndexById(String id);
}

class UserSubscriptionSearchRepositoryInternalImpl implements UserSubscriptionSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UserSubscriptionRepository repository;

    UserSubscriptionSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, UserSubscriptionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<UserSubscription> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<UserSubscription> search(Query query) {
        SearchHits<UserSubscription> searchHits = elasticsearchTemplate.search(query, UserSubscription.class);
        List<UserSubscription> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(UserSubscription entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(String id) {
        elasticsearchTemplate.delete(String.valueOf(id), UserSubscription.class);
    }
}
