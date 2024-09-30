package com.tamkeen.backoffice.web.rest;

import static com.tamkeen.backoffice.domain.PersonalityTypeAsserts.*;
import static com.tamkeen.backoffice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamkeen.backoffice.IntegrationTest;
import com.tamkeen.backoffice.domain.PersonalityType;
import com.tamkeen.backoffice.repository.PersonalityTypeRepository;
import com.tamkeen.backoffice.repository.search.PersonalityTypeSearchRepository;
import com.tamkeen.backoffice.service.dto.PersonalityTypeDTO;
import com.tamkeen.backoffice.service.mapper.PersonalityTypeMapper;
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
 * Integration tests for the {@link PersonalityTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PersonalityTypeResourceIT {

    private static final String DEFAULT_TYPE_CODE = "AAAA";
    private static final String UPDATED_TYPE_CODE = "BBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_STRENGTHS = "AAAAAAAAAA";
    private static final String UPDATED_STRENGTHS = "BBBBBBBBBB";

    private static final String DEFAULT_WEAKNESSES = "AAAAAAAAAA";
    private static final String UPDATED_WEAKNESSES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/personality-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/personality-types/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PersonalityTypeRepository personalityTypeRepository;

    @Autowired
    private PersonalityTypeMapper personalityTypeMapper;

    @Autowired
    private PersonalityTypeSearchRepository personalityTypeSearchRepository;

    @Autowired
    private MockMvc restPersonalityTypeMockMvc;

    private PersonalityType personalityType;

    private PersonalityType insertedPersonalityType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PersonalityType createEntity() {
        return new PersonalityType()
            .typeCode(DEFAULT_TYPE_CODE)
            .description(DEFAULT_DESCRIPTION)
            .strengths(DEFAULT_STRENGTHS)
            .weaknesses(DEFAULT_WEAKNESSES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PersonalityType createUpdatedEntity() {
        return new PersonalityType()
            .typeCode(UPDATED_TYPE_CODE)
            .description(UPDATED_DESCRIPTION)
            .strengths(UPDATED_STRENGTHS)
            .weaknesses(UPDATED_WEAKNESSES);
    }

    @BeforeEach
    public void initTest() {
        personalityType = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPersonalityType != null) {
            personalityTypeRepository.delete(insertedPersonalityType);
            personalityTypeSearchRepository.delete(insertedPersonalityType);
            insertedPersonalityType = null;
        }
    }

    @Test
    void createPersonalityType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        // Create the PersonalityType
        PersonalityTypeDTO personalityTypeDTO = personalityTypeMapper.toDto(personalityType);
        var returnedPersonalityTypeDTO = om.readValue(
            restPersonalityTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personalityTypeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PersonalityTypeDTO.class
        );

        // Validate the PersonalityType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPersonalityType = personalityTypeMapper.toEntity(returnedPersonalityTypeDTO);
        assertPersonalityTypeUpdatableFieldsEquals(returnedPersonalityType, getPersistedPersonalityType(returnedPersonalityType));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedPersonalityType = returnedPersonalityType;
    }

    @Test
    void createPersonalityTypeWithExistingId() throws Exception {
        // Create the PersonalityType with an existing ID
        personalityType.setId("existing_id");
        PersonalityTypeDTO personalityTypeDTO = personalityTypeMapper.toDto(personalityType);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPersonalityTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personalityTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PersonalityType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTypeCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        // set the field null
        personalityType.setTypeCode(null);

        // Create the PersonalityType, which fails.
        PersonalityTypeDTO personalityTypeDTO = personalityTypeMapper.toDto(personalityType);

        restPersonalityTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personalityTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllPersonalityTypes() throws Exception {
        // Initialize the database
        insertedPersonalityType = personalityTypeRepository.save(personalityType);

        // Get all the personalityTypeList
        restPersonalityTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(personalityType.getId())))
            .andExpect(jsonPath("$.[*].typeCode").value(hasItem(DEFAULT_TYPE_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].strengths").value(hasItem(DEFAULT_STRENGTHS.toString())))
            .andExpect(jsonPath("$.[*].weaknesses").value(hasItem(DEFAULT_WEAKNESSES.toString())));
    }

    @Test
    void getPersonalityType() throws Exception {
        // Initialize the database
        insertedPersonalityType = personalityTypeRepository.save(personalityType);

        // Get the personalityType
        restPersonalityTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, personalityType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(personalityType.getId()))
            .andExpect(jsonPath("$.typeCode").value(DEFAULT_TYPE_CODE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.strengths").value(DEFAULT_STRENGTHS.toString()))
            .andExpect(jsonPath("$.weaknesses").value(DEFAULT_WEAKNESSES.toString()));
    }

    @Test
    void getNonExistingPersonalityType() throws Exception {
        // Get the personalityType
        restPersonalityTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingPersonalityType() throws Exception {
        // Initialize the database
        insertedPersonalityType = personalityTypeRepository.save(personalityType);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        personalityTypeSearchRepository.save(personalityType);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());

        // Update the personalityType
        PersonalityType updatedPersonalityType = personalityTypeRepository.findById(personalityType.getId()).orElseThrow();
        updatedPersonalityType
            .typeCode(UPDATED_TYPE_CODE)
            .description(UPDATED_DESCRIPTION)
            .strengths(UPDATED_STRENGTHS)
            .weaknesses(UPDATED_WEAKNESSES);
        PersonalityTypeDTO personalityTypeDTO = personalityTypeMapper.toDto(updatedPersonalityType);

        restPersonalityTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, personalityTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(personalityTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the PersonalityType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPersonalityTypeToMatchAllProperties(updatedPersonalityType);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<PersonalityType> personalityTypeSearchList = Streamable.of(personalityTypeSearchRepository.findAll()).toList();
                PersonalityType testPersonalityTypeSearch = personalityTypeSearchList.get(searchDatabaseSizeAfter - 1);

                assertPersonalityTypeAllPropertiesEquals(testPersonalityTypeSearch, updatedPersonalityType);
            });
    }

    @Test
    void putNonExistingPersonalityType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        personalityType.setId(UUID.randomUUID().toString());

        // Create the PersonalityType
        PersonalityTypeDTO personalityTypeDTO = personalityTypeMapper.toDto(personalityType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonalityTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, personalityTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(personalityTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalityType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchPersonalityType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        personalityType.setId(UUID.randomUUID().toString());

        // Create the PersonalityType
        PersonalityTypeDTO personalityTypeDTO = personalityTypeMapper.toDto(personalityType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalityTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(personalityTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalityType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamPersonalityType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        personalityType.setId(UUID.randomUUID().toString());

        // Create the PersonalityType
        PersonalityTypeDTO personalityTypeDTO = personalityTypeMapper.toDto(personalityType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalityTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personalityTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PersonalityType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdatePersonalityTypeWithPatch() throws Exception {
        // Initialize the database
        insertedPersonalityType = personalityTypeRepository.save(personalityType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the personalityType using partial update
        PersonalityType partialUpdatedPersonalityType = new PersonalityType();
        partialUpdatedPersonalityType.setId(personalityType.getId());

        partialUpdatedPersonalityType.strengths(UPDATED_STRENGTHS);

        restPersonalityTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPersonalityType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPersonalityType))
            )
            .andExpect(status().isOk());

        // Validate the PersonalityType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersonalityTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPersonalityType, personalityType),
            getPersistedPersonalityType(personalityType)
        );
    }

    @Test
    void fullUpdatePersonalityTypeWithPatch() throws Exception {
        // Initialize the database
        insertedPersonalityType = personalityTypeRepository.save(personalityType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the personalityType using partial update
        PersonalityType partialUpdatedPersonalityType = new PersonalityType();
        partialUpdatedPersonalityType.setId(personalityType.getId());

        partialUpdatedPersonalityType
            .typeCode(UPDATED_TYPE_CODE)
            .description(UPDATED_DESCRIPTION)
            .strengths(UPDATED_STRENGTHS)
            .weaknesses(UPDATED_WEAKNESSES);

        restPersonalityTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPersonalityType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPersonalityType))
            )
            .andExpect(status().isOk());

        // Validate the PersonalityType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersonalityTypeUpdatableFieldsEquals(
            partialUpdatedPersonalityType,
            getPersistedPersonalityType(partialUpdatedPersonalityType)
        );
    }

    @Test
    void patchNonExistingPersonalityType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        personalityType.setId(UUID.randomUUID().toString());

        // Create the PersonalityType
        PersonalityTypeDTO personalityTypeDTO = personalityTypeMapper.toDto(personalityType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonalityTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, personalityTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(personalityTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalityType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchPersonalityType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        personalityType.setId(UUID.randomUUID().toString());

        // Create the PersonalityType
        PersonalityTypeDTO personalityTypeDTO = personalityTypeMapper.toDto(personalityType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalityTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(personalityTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalityType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamPersonalityType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        personalityType.setId(UUID.randomUUID().toString());

        // Create the PersonalityType
        PersonalityTypeDTO personalityTypeDTO = personalityTypeMapper.toDto(personalityType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalityTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(personalityTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PersonalityType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deletePersonalityType() throws Exception {
        // Initialize the database
        insertedPersonalityType = personalityTypeRepository.save(personalityType);
        personalityTypeRepository.save(personalityType);
        personalityTypeSearchRepository.save(personalityType);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the personalityType
        restPersonalityTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, personalityType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTypeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchPersonalityType() throws Exception {
        // Initialize the database
        insertedPersonalityType = personalityTypeRepository.save(personalityType);
        personalityTypeSearchRepository.save(personalityType);

        // Search the personalityType
        restPersonalityTypeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + personalityType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(personalityType.getId())))
            .andExpect(jsonPath("$.[*].typeCode").value(hasItem(DEFAULT_TYPE_CODE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].strengths").value(hasItem(DEFAULT_STRENGTHS.toString())))
            .andExpect(jsonPath("$.[*].weaknesses").value(hasItem(DEFAULT_WEAKNESSES.toString())));
    }

    protected long getRepositoryCount() {
        return personalityTypeRepository.count();
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

    protected PersonalityType getPersistedPersonalityType(PersonalityType personalityType) {
        return personalityTypeRepository.findById(personalityType.getId()).orElseThrow();
    }

    protected void assertPersistedPersonalityTypeToMatchAllProperties(PersonalityType expectedPersonalityType) {
        assertPersonalityTypeAllPropertiesEquals(expectedPersonalityType, getPersistedPersonalityType(expectedPersonalityType));
    }

    protected void assertPersistedPersonalityTypeToMatchUpdatableProperties(PersonalityType expectedPersonalityType) {
        assertPersonalityTypeAllUpdatablePropertiesEquals(expectedPersonalityType, getPersistedPersonalityType(expectedPersonalityType));
    }
}
