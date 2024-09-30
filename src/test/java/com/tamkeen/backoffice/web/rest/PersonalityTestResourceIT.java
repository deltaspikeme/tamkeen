package com.tamkeen.backoffice.web.rest;

import static com.tamkeen.backoffice.domain.PersonalityTestAsserts.*;
import static com.tamkeen.backoffice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamkeen.backoffice.IntegrationTest;
import com.tamkeen.backoffice.domain.PersonalityTest;
import com.tamkeen.backoffice.repository.PersonalityTestRepository;
import com.tamkeen.backoffice.repository.search.PersonalityTestSearchRepository;
import com.tamkeen.backoffice.service.dto.PersonalityTestDTO;
import com.tamkeen.backoffice.service.mapper.PersonalityTestMapper;
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
 * Integration tests for the {@link PersonalityTestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PersonalityTestResourceIT {

    private static final String DEFAULT_TEST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TEST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/personality-tests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/personality-tests/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PersonalityTestRepository personalityTestRepository;

    @Autowired
    private PersonalityTestMapper personalityTestMapper;

    @Autowired
    private PersonalityTestSearchRepository personalityTestSearchRepository;

    @Autowired
    private MockMvc restPersonalityTestMockMvc;

    private PersonalityTest personalityTest;

    private PersonalityTest insertedPersonalityTest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PersonalityTest createEntity() {
        return new PersonalityTest().testName(DEFAULT_TEST_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PersonalityTest createUpdatedEntity() {
        return new PersonalityTest().testName(UPDATED_TEST_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        personalityTest = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPersonalityTest != null) {
            personalityTestRepository.delete(insertedPersonalityTest);
            personalityTestSearchRepository.delete(insertedPersonalityTest);
            insertedPersonalityTest = null;
        }
    }

    @Test
    void createPersonalityTest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        // Create the PersonalityTest
        PersonalityTestDTO personalityTestDTO = personalityTestMapper.toDto(personalityTest);
        var returnedPersonalityTestDTO = om.readValue(
            restPersonalityTestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personalityTestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PersonalityTestDTO.class
        );

        // Validate the PersonalityTest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPersonalityTest = personalityTestMapper.toEntity(returnedPersonalityTestDTO);
        assertPersonalityTestUpdatableFieldsEquals(returnedPersonalityTest, getPersistedPersonalityTest(returnedPersonalityTest));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedPersonalityTest = returnedPersonalityTest;
    }

    @Test
    void createPersonalityTestWithExistingId() throws Exception {
        // Create the PersonalityTest with an existing ID
        personalityTest.setId("existing_id");
        PersonalityTestDTO personalityTestDTO = personalityTestMapper.toDto(personalityTest);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPersonalityTestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personalityTestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PersonalityTest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTestNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        // set the field null
        personalityTest.setTestName(null);

        // Create the PersonalityTest, which fails.
        PersonalityTestDTO personalityTestDTO = personalityTestMapper.toDto(personalityTest);

        restPersonalityTestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personalityTestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllPersonalityTests() throws Exception {
        // Initialize the database
        insertedPersonalityTest = personalityTestRepository.save(personalityTest);

        // Get all the personalityTestList
        restPersonalityTestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(personalityTest.getId())))
            .andExpect(jsonPath("$.[*].testName").value(hasItem(DEFAULT_TEST_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    void getPersonalityTest() throws Exception {
        // Initialize the database
        insertedPersonalityTest = personalityTestRepository.save(personalityTest);

        // Get the personalityTest
        restPersonalityTestMockMvc
            .perform(get(ENTITY_API_URL_ID, personalityTest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(personalityTest.getId()))
            .andExpect(jsonPath("$.testName").value(DEFAULT_TEST_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingPersonalityTest() throws Exception {
        // Get the personalityTest
        restPersonalityTestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingPersonalityTest() throws Exception {
        // Initialize the database
        insertedPersonalityTest = personalityTestRepository.save(personalityTest);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        personalityTestSearchRepository.save(personalityTest);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());

        // Update the personalityTest
        PersonalityTest updatedPersonalityTest = personalityTestRepository.findById(personalityTest.getId()).orElseThrow();
        updatedPersonalityTest.testName(UPDATED_TEST_NAME).description(UPDATED_DESCRIPTION);
        PersonalityTestDTO personalityTestDTO = personalityTestMapper.toDto(updatedPersonalityTest);

        restPersonalityTestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, personalityTestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(personalityTestDTO))
            )
            .andExpect(status().isOk());

        // Validate the PersonalityTest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPersonalityTestToMatchAllProperties(updatedPersonalityTest);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<PersonalityTest> personalityTestSearchList = Streamable.of(personalityTestSearchRepository.findAll()).toList();
                PersonalityTest testPersonalityTestSearch = personalityTestSearchList.get(searchDatabaseSizeAfter - 1);

                assertPersonalityTestAllPropertiesEquals(testPersonalityTestSearch, updatedPersonalityTest);
            });
    }

    @Test
    void putNonExistingPersonalityTest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        personalityTest.setId(UUID.randomUUID().toString());

        // Create the PersonalityTest
        PersonalityTestDTO personalityTestDTO = personalityTestMapper.toDto(personalityTest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonalityTestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, personalityTestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(personalityTestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalityTest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchPersonalityTest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        personalityTest.setId(UUID.randomUUID().toString());

        // Create the PersonalityTest
        PersonalityTestDTO personalityTestDTO = personalityTestMapper.toDto(personalityTest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalityTestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(personalityTestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalityTest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamPersonalityTest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        personalityTest.setId(UUID.randomUUID().toString());

        // Create the PersonalityTest
        PersonalityTestDTO personalityTestDTO = personalityTestMapper.toDto(personalityTest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalityTestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(personalityTestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PersonalityTest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdatePersonalityTestWithPatch() throws Exception {
        // Initialize the database
        insertedPersonalityTest = personalityTestRepository.save(personalityTest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the personalityTest using partial update
        PersonalityTest partialUpdatedPersonalityTest = new PersonalityTest();
        partialUpdatedPersonalityTest.setId(personalityTest.getId());

        partialUpdatedPersonalityTest.description(UPDATED_DESCRIPTION);

        restPersonalityTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPersonalityTest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPersonalityTest))
            )
            .andExpect(status().isOk());

        // Validate the PersonalityTest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersonalityTestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPersonalityTest, personalityTest),
            getPersistedPersonalityTest(personalityTest)
        );
    }

    @Test
    void fullUpdatePersonalityTestWithPatch() throws Exception {
        // Initialize the database
        insertedPersonalityTest = personalityTestRepository.save(personalityTest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the personalityTest using partial update
        PersonalityTest partialUpdatedPersonalityTest = new PersonalityTest();
        partialUpdatedPersonalityTest.setId(personalityTest.getId());

        partialUpdatedPersonalityTest.testName(UPDATED_TEST_NAME).description(UPDATED_DESCRIPTION);

        restPersonalityTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPersonalityTest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPersonalityTest))
            )
            .andExpect(status().isOk());

        // Validate the PersonalityTest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersonalityTestUpdatableFieldsEquals(
            partialUpdatedPersonalityTest,
            getPersistedPersonalityTest(partialUpdatedPersonalityTest)
        );
    }

    @Test
    void patchNonExistingPersonalityTest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        personalityTest.setId(UUID.randomUUID().toString());

        // Create the PersonalityTest
        PersonalityTestDTO personalityTestDTO = personalityTestMapper.toDto(personalityTest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonalityTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, personalityTestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(personalityTestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalityTest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchPersonalityTest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        personalityTest.setId(UUID.randomUUID().toString());

        // Create the PersonalityTest
        PersonalityTestDTO personalityTestDTO = personalityTestMapper.toDto(personalityTest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalityTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(personalityTestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PersonalityTest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamPersonalityTest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        personalityTest.setId(UUID.randomUUID().toString());

        // Create the PersonalityTest
        PersonalityTestDTO personalityTestDTO = personalityTestMapper.toDto(personalityTest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonalityTestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(personalityTestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PersonalityTest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deletePersonalityTest() throws Exception {
        // Initialize the database
        insertedPersonalityTest = personalityTestRepository.save(personalityTest);
        personalityTestRepository.save(personalityTest);
        personalityTestSearchRepository.save(personalityTest);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the personalityTest
        restPersonalityTestMockMvc
            .perform(delete(ENTITY_API_URL_ID, personalityTest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personalityTestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchPersonalityTest() throws Exception {
        // Initialize the database
        insertedPersonalityTest = personalityTestRepository.save(personalityTest);
        personalityTestSearchRepository.save(personalityTest);

        // Search the personalityTest
        restPersonalityTestMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + personalityTest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(personalityTest.getId())))
            .andExpect(jsonPath("$.[*].testName").value(hasItem(DEFAULT_TEST_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    protected long getRepositoryCount() {
        return personalityTestRepository.count();
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

    protected PersonalityTest getPersistedPersonalityTest(PersonalityTest personalityTest) {
        return personalityTestRepository.findById(personalityTest.getId()).orElseThrow();
    }

    protected void assertPersistedPersonalityTestToMatchAllProperties(PersonalityTest expectedPersonalityTest) {
        assertPersonalityTestAllPropertiesEquals(expectedPersonalityTest, getPersistedPersonalityTest(expectedPersonalityTest));
    }

    protected void assertPersistedPersonalityTestToMatchUpdatableProperties(PersonalityTest expectedPersonalityTest) {
        assertPersonalityTestAllUpdatablePropertiesEquals(expectedPersonalityTest, getPersistedPersonalityTest(expectedPersonalityTest));
    }
}
