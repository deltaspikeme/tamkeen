package com.tamkeen.backoffice.web.rest;

import static com.tamkeen.backoffice.domain.UserResponseAsserts.*;
import static com.tamkeen.backoffice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamkeen.backoffice.IntegrationTest;
import com.tamkeen.backoffice.domain.UserResponse;
import com.tamkeen.backoffice.repository.UserRepository;
import com.tamkeen.backoffice.repository.UserResponseRepository;
import com.tamkeen.backoffice.repository.search.UserResponseSearchRepository;
import com.tamkeen.backoffice.service.dto.UserResponseDTO;
import com.tamkeen.backoffice.service.mapper.UserResponseMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link UserResponseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserResponseResourceIT {

    private static final Instant DEFAULT_RESPONSE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESPONSE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/user-responses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/user-responses/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserResponseRepository userResponseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserResponseMapper userResponseMapper;

    @Autowired
    private UserResponseSearchRepository userResponseSearchRepository;

    @Autowired
    private MockMvc restUserResponseMockMvc;

    private UserResponse userResponse;

    private UserResponse insertedUserResponse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserResponse createEntity() {
        return new UserResponse().responseDate(DEFAULT_RESPONSE_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserResponse createUpdatedEntity() {
        return new UserResponse().responseDate(UPDATED_RESPONSE_DATE);
    }

    @BeforeEach
    public void initTest() {
        userResponse = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedUserResponse != null) {
            userResponseRepository.delete(insertedUserResponse);
            userResponseSearchRepository.delete(insertedUserResponse);
            insertedUserResponse = null;
        }
        userRepository.deleteAll();
    }

    @Test
    void createUserResponse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        // Create the UserResponse
        UserResponseDTO userResponseDTO = userResponseMapper.toDto(userResponse);
        var returnedUserResponseDTO = om.readValue(
            restUserResponseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userResponseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserResponseDTO.class
        );

        // Validate the UserResponse in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserResponse = userResponseMapper.toEntity(returnedUserResponseDTO);
        assertUserResponseUpdatableFieldsEquals(returnedUserResponse, getPersistedUserResponse(returnedUserResponse));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedUserResponse = returnedUserResponse;
    }

    @Test
    void createUserResponseWithExistingId() throws Exception {
        // Create the UserResponse with an existing ID
        userResponse.setId("existing_id");
        UserResponseDTO userResponseDTO = userResponseMapper.toDto(userResponse);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userResponseSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserResponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userResponseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkResponseDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        // set the field null
        userResponse.setResponseDate(null);

        // Create the UserResponse, which fails.
        UserResponseDTO userResponseDTO = userResponseMapper.toDto(userResponse);

        restUserResponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userResponseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllUserResponses() throws Exception {
        // Initialize the database
        insertedUserResponse = userResponseRepository.save(userResponse);

        // Get all the userResponseList
        restUserResponseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userResponse.getId())))
            .andExpect(jsonPath("$.[*].responseDate").value(hasItem(DEFAULT_RESPONSE_DATE.toString())));
    }

    @Test
    void getUserResponse() throws Exception {
        // Initialize the database
        insertedUserResponse = userResponseRepository.save(userResponse);

        // Get the userResponse
        restUserResponseMockMvc
            .perform(get(ENTITY_API_URL_ID, userResponse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userResponse.getId()))
            .andExpect(jsonPath("$.responseDate").value(DEFAULT_RESPONSE_DATE.toString()));
    }

    @Test
    void getNonExistingUserResponse() throws Exception {
        // Get the userResponse
        restUserResponseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingUserResponse() throws Exception {
        // Initialize the database
        insertedUserResponse = userResponseRepository.save(userResponse);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        userResponseSearchRepository.save(userResponse);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userResponseSearchRepository.findAll());

        // Update the userResponse
        UserResponse updatedUserResponse = userResponseRepository.findById(userResponse.getId()).orElseThrow();
        updatedUserResponse.responseDate(UPDATED_RESPONSE_DATE);
        UserResponseDTO userResponseDTO = userResponseMapper.toDto(updatedUserResponse);

        restUserResponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userResponseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userResponseDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserResponseToMatchAllProperties(updatedUserResponse);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UserResponse> userResponseSearchList = Streamable.of(userResponseSearchRepository.findAll()).toList();
                UserResponse testUserResponseSearch = userResponseSearchList.get(searchDatabaseSizeAfter - 1);

                assertUserResponseAllPropertiesEquals(testUserResponseSearch, updatedUserResponse);
            });
    }

    @Test
    void putNonExistingUserResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        userResponse.setId(UUID.randomUUID().toString());

        // Create the UserResponse
        UserResponseDTO userResponseDTO = userResponseMapper.toDto(userResponse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserResponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userResponseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userResponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchUserResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        userResponse.setId(UUID.randomUUID().toString());

        // Create the UserResponse
        UserResponseDTO userResponseDTO = userResponseMapper.toDto(userResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserResponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userResponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamUserResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        userResponse.setId(UUID.randomUUID().toString());

        // Create the UserResponse
        UserResponseDTO userResponseDTO = userResponseMapper.toDto(userResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserResponseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userResponseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateUserResponseWithPatch() throws Exception {
        // Initialize the database
        insertedUserResponse = userResponseRepository.save(userResponse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userResponse using partial update
        UserResponse partialUpdatedUserResponse = new UserResponse();
        partialUpdatedUserResponse.setId(userResponse.getId());

        restUserResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserResponse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserResponse))
            )
            .andExpect(status().isOk());

        // Validate the UserResponse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserResponseUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserResponse, userResponse),
            getPersistedUserResponse(userResponse)
        );
    }

    @Test
    void fullUpdateUserResponseWithPatch() throws Exception {
        // Initialize the database
        insertedUserResponse = userResponseRepository.save(userResponse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userResponse using partial update
        UserResponse partialUpdatedUserResponse = new UserResponse();
        partialUpdatedUserResponse.setId(userResponse.getId());

        partialUpdatedUserResponse.responseDate(UPDATED_RESPONSE_DATE);

        restUserResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserResponse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserResponse))
            )
            .andExpect(status().isOk());

        // Validate the UserResponse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserResponseUpdatableFieldsEquals(partialUpdatedUserResponse, getPersistedUserResponse(partialUpdatedUserResponse));
    }

    @Test
    void patchNonExistingUserResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        userResponse.setId(UUID.randomUUID().toString());

        // Create the UserResponse
        UserResponseDTO userResponseDTO = userResponseMapper.toDto(userResponse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userResponseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userResponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchUserResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        userResponse.setId(UUID.randomUUID().toString());

        // Create the UserResponse
        UserResponseDTO userResponseDTO = userResponseMapper.toDto(userResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userResponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamUserResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        userResponse.setId(UUID.randomUUID().toString());

        // Create the UserResponse
        UserResponseDTO userResponseDTO = userResponseMapper.toDto(userResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserResponseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userResponseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteUserResponse() throws Exception {
        // Initialize the database
        insertedUserResponse = userResponseRepository.save(userResponse);
        userResponseRepository.save(userResponse);
        userResponseSearchRepository.save(userResponse);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the userResponse
        restUserResponseMockMvc
            .perform(delete(ENTITY_API_URL_ID, userResponse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userResponseSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchUserResponse() throws Exception {
        // Initialize the database
        insertedUserResponse = userResponseRepository.save(userResponse);
        userResponseSearchRepository.save(userResponse);

        // Search the userResponse
        restUserResponseMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userResponse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userResponse.getId())))
            .andExpect(jsonPath("$.[*].responseDate").value(hasItem(DEFAULT_RESPONSE_DATE.toString())));
    }

    protected long getRepositoryCount() {
        return userResponseRepository.count();
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

    protected UserResponse getPersistedUserResponse(UserResponse userResponse) {
        return userResponseRepository.findById(userResponse.getId()).orElseThrow();
    }

    protected void assertPersistedUserResponseToMatchAllProperties(UserResponse expectedUserResponse) {
        assertUserResponseAllPropertiesEquals(expectedUserResponse, getPersistedUserResponse(expectedUserResponse));
    }

    protected void assertPersistedUserResponseToMatchUpdatableProperties(UserResponse expectedUserResponse) {
        assertUserResponseAllUpdatablePropertiesEquals(expectedUserResponse, getPersistedUserResponse(expectedUserResponse));
    }
}
