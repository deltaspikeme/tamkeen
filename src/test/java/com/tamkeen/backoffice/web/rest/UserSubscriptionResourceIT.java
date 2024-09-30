package com.tamkeen.backoffice.web.rest;

import static com.tamkeen.backoffice.domain.UserSubscriptionAsserts.*;
import static com.tamkeen.backoffice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamkeen.backoffice.IntegrationTest;
import com.tamkeen.backoffice.domain.UserSubscription;
import com.tamkeen.backoffice.domain.enumeration.SubscriptionType;
import com.tamkeen.backoffice.repository.UserRepository;
import com.tamkeen.backoffice.repository.UserSubscriptionRepository;
import com.tamkeen.backoffice.repository.search.UserSubscriptionSearchRepository;
import com.tamkeen.backoffice.service.dto.UserSubscriptionDTO;
import com.tamkeen.backoffice.service.mapper.UserSubscriptionMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link UserSubscriptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserSubscriptionResourceIT {

    private static final SubscriptionType DEFAULT_SUBSCRIPTION_TYPE = SubscriptionType.MONTHLY;
    private static final SubscriptionType UPDATED_SUBSCRIPTION_TYPE = SubscriptionType.YEARLY;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/user-subscriptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/user-subscriptions/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSubscriptionMapper userSubscriptionMapper;

    @Autowired
    private UserSubscriptionSearchRepository userSubscriptionSearchRepository;

    @Autowired
    private MockMvc restUserSubscriptionMockMvc;

    private UserSubscription userSubscription;

    private UserSubscription insertedUserSubscription;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserSubscription createEntity() {
        return new UserSubscription().subscriptionType(DEFAULT_SUBSCRIPTION_TYPE).startDate(DEFAULT_START_DATE).endDate(DEFAULT_END_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserSubscription createUpdatedEntity() {
        return new UserSubscription().subscriptionType(UPDATED_SUBSCRIPTION_TYPE).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);
    }

    @BeforeEach
    public void initTest() {
        userSubscription = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedUserSubscription != null) {
            userSubscriptionRepository.delete(insertedUserSubscription);
            userSubscriptionSearchRepository.delete(insertedUserSubscription);
            insertedUserSubscription = null;
        }
        userRepository.deleteAll();
    }

    @Test
    void createUserSubscription() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        // Create the UserSubscription
        UserSubscriptionDTO userSubscriptionDTO = userSubscriptionMapper.toDto(userSubscription);
        var returnedUserSubscriptionDTO = om.readValue(
            restUserSubscriptionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userSubscriptionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserSubscriptionDTO.class
        );

        // Validate the UserSubscription in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserSubscription = userSubscriptionMapper.toEntity(returnedUserSubscriptionDTO);
        assertUserSubscriptionUpdatableFieldsEquals(returnedUserSubscription, getPersistedUserSubscription(returnedUserSubscription));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedUserSubscription = returnedUserSubscription;
    }

    @Test
    void createUserSubscriptionWithExistingId() throws Exception {
        // Create the UserSubscription with an existing ID
        userSubscription.setId("existing_id");
        UserSubscriptionDTO userSubscriptionDTO = userSubscriptionMapper.toDto(userSubscription);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userSubscriptionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSubscriptionTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        // set the field null
        userSubscription.setSubscriptionType(null);

        // Create the UserSubscription, which fails.
        UserSubscriptionDTO userSubscriptionDTO = userSubscriptionMapper.toDto(userSubscription);

        restUserSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userSubscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        // set the field null
        userSubscription.setStartDate(null);

        // Create the UserSubscription, which fails.
        UserSubscriptionDTO userSubscriptionDTO = userSubscriptionMapper.toDto(userSubscription);

        restUserSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userSubscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        // set the field null
        userSubscription.setEndDate(null);

        // Create the UserSubscription, which fails.
        UserSubscriptionDTO userSubscriptionDTO = userSubscriptionMapper.toDto(userSubscription);

        restUserSubscriptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userSubscriptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllUserSubscriptions() throws Exception {
        // Initialize the database
        insertedUserSubscription = userSubscriptionRepository.save(userSubscription);

        // Get all the userSubscriptionList
        restUserSubscriptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userSubscription.getId())))
            .andExpect(jsonPath("$.[*].subscriptionType").value(hasItem(DEFAULT_SUBSCRIPTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @Test
    void getUserSubscription() throws Exception {
        // Initialize the database
        insertedUserSubscription = userSubscriptionRepository.save(userSubscription);

        // Get the userSubscription
        restUserSubscriptionMockMvc
            .perform(get(ENTITY_API_URL_ID, userSubscription.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userSubscription.getId()))
            .andExpect(jsonPath("$.subscriptionType").value(DEFAULT_SUBSCRIPTION_TYPE.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    void getNonExistingUserSubscription() throws Exception {
        // Get the userSubscription
        restUserSubscriptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingUserSubscription() throws Exception {
        // Initialize the database
        insertedUserSubscription = userSubscriptionRepository.save(userSubscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        userSubscriptionSearchRepository.save(userSubscription);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());

        // Update the userSubscription
        UserSubscription updatedUserSubscription = userSubscriptionRepository.findById(userSubscription.getId()).orElseThrow();
        updatedUserSubscription.subscriptionType(UPDATED_SUBSCRIPTION_TYPE).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);
        UserSubscriptionDTO userSubscriptionDTO = userSubscriptionMapper.toDto(updatedUserSubscription);

        restUserSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userSubscriptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userSubscriptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserSubscriptionToMatchAllProperties(updatedUserSubscription);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UserSubscription> userSubscriptionSearchList = Streamable.of(userSubscriptionSearchRepository.findAll()).toList();
                UserSubscription testUserSubscriptionSearch = userSubscriptionSearchList.get(searchDatabaseSizeAfter - 1);

                assertUserSubscriptionAllPropertiesEquals(testUserSubscriptionSearch, updatedUserSubscription);
            });
    }

    @Test
    void putNonExistingUserSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        userSubscription.setId(UUID.randomUUID().toString());

        // Create the UserSubscription
        UserSubscriptionDTO userSubscriptionDTO = userSubscriptionMapper.toDto(userSubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userSubscriptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userSubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchUserSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        userSubscription.setId(UUID.randomUUID().toString());

        // Create the UserSubscription
        UserSubscriptionDTO userSubscriptionDTO = userSubscriptionMapper.toDto(userSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserSubscriptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userSubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamUserSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        userSubscription.setId(UUID.randomUUID().toString());

        // Create the UserSubscription
        UserSubscriptionDTO userSubscriptionDTO = userSubscriptionMapper.toDto(userSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserSubscriptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userSubscriptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateUserSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedUserSubscription = userSubscriptionRepository.save(userSubscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userSubscription using partial update
        UserSubscription partialUpdatedUserSubscription = new UserSubscription();
        partialUpdatedUserSubscription.setId(userSubscription.getId());

        partialUpdatedUserSubscription.subscriptionType(UPDATED_SUBSCRIPTION_TYPE).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restUserSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserSubscription.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserSubscription))
            )
            .andExpect(status().isOk());

        // Validate the UserSubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserSubscriptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserSubscription, userSubscription),
            getPersistedUserSubscription(userSubscription)
        );
    }

    @Test
    void fullUpdateUserSubscriptionWithPatch() throws Exception {
        // Initialize the database
        insertedUserSubscription = userSubscriptionRepository.save(userSubscription);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userSubscription using partial update
        UserSubscription partialUpdatedUserSubscription = new UserSubscription();
        partialUpdatedUserSubscription.setId(userSubscription.getId());

        partialUpdatedUserSubscription.subscriptionType(UPDATED_SUBSCRIPTION_TYPE).startDate(UPDATED_START_DATE).endDate(UPDATED_END_DATE);

        restUserSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserSubscription.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserSubscription))
            )
            .andExpect(status().isOk());

        // Validate the UserSubscription in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserSubscriptionUpdatableFieldsEquals(
            partialUpdatedUserSubscription,
            getPersistedUserSubscription(partialUpdatedUserSubscription)
        );
    }

    @Test
    void patchNonExistingUserSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        userSubscription.setId(UUID.randomUUID().toString());

        // Create the UserSubscription
        UserSubscriptionDTO userSubscriptionDTO = userSubscriptionMapper.toDto(userSubscription);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userSubscriptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userSubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchUserSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        userSubscription.setId(UUID.randomUUID().toString());

        // Create the UserSubscription
        UserSubscriptionDTO userSubscriptionDTO = userSubscriptionMapper.toDto(userSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserSubscriptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userSubscriptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamUserSubscription() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        userSubscription.setId(UUID.randomUUID().toString());

        // Create the UserSubscription
        UserSubscriptionDTO userSubscriptionDTO = userSubscriptionMapper.toDto(userSubscription);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserSubscriptionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userSubscriptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserSubscription in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteUserSubscription() throws Exception {
        // Initialize the database
        insertedUserSubscription = userSubscriptionRepository.save(userSubscription);
        userSubscriptionRepository.save(userSubscription);
        userSubscriptionSearchRepository.save(userSubscription);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the userSubscription
        restUserSubscriptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, userSubscription.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userSubscriptionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchUserSubscription() throws Exception {
        // Initialize the database
        insertedUserSubscription = userSubscriptionRepository.save(userSubscription);
        userSubscriptionSearchRepository.save(userSubscription);

        // Search the userSubscription
        restUserSubscriptionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userSubscription.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userSubscription.getId())))
            .andExpect(jsonPath("$.[*].subscriptionType").value(hasItem(DEFAULT_SUBSCRIPTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    protected long getRepositoryCount() {
        return userSubscriptionRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected UserSubscription getPersistedUserSubscription(UserSubscription userSubscription) {
        return userSubscriptionRepository.findById(userSubscription.getId()).orElseThrow();
    }

    protected void assertPersistedUserSubscriptionToMatchAllProperties(UserSubscription expectedUserSubscription) {
        assertUserSubscriptionAllPropertiesEquals(expectedUserSubscription, getPersistedUserSubscription(expectedUserSubscription));
    }

    protected void assertPersistedUserSubscriptionToMatchUpdatableProperties(UserSubscription expectedUserSubscription) {
        assertUserSubscriptionAllUpdatablePropertiesEquals(
            expectedUserSubscription,
            getPersistedUserSubscription(expectedUserSubscription)
        );
    }
}
