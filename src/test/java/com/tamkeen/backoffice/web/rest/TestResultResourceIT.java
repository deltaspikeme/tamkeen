package com.tamkeen.backoffice.web.rest;

import static com.tamkeen.backoffice.domain.TestResultAsserts.*;
import static com.tamkeen.backoffice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamkeen.backoffice.IntegrationTest;
import com.tamkeen.backoffice.domain.TestResult;
import com.tamkeen.backoffice.repository.TestResultRepository;
import com.tamkeen.backoffice.repository.UserRepository;
import com.tamkeen.backoffice.repository.search.TestResultSearchRepository;
import com.tamkeen.backoffice.service.dto.TestResultDTO;
import com.tamkeen.backoffice.service.mapper.TestResultMapper;
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
 * Integration tests for the {@link TestResultResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TestResultResourceIT {

    private static final String DEFAULT_ANALYSIS = "AAAAAAAAAA";
    private static final String UPDATED_ANALYSIS = "BBBBBBBBBB";

    private static final Instant DEFAULT_RESULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESULT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/test-results";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/test-results/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestResultMapper testResultMapper;

    @Autowired
    private TestResultSearchRepository testResultSearchRepository;

    @Autowired
    private MockMvc restTestResultMockMvc;

    private TestResult testResult;

    private TestResult insertedTestResult;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TestResult createEntity() {
        return new TestResult().analysis(DEFAULT_ANALYSIS).resultDate(DEFAULT_RESULT_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TestResult createUpdatedEntity() {
        return new TestResult().analysis(UPDATED_ANALYSIS).resultDate(UPDATED_RESULT_DATE);
    }

    @BeforeEach
    public void initTest() {
        testResult = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTestResult != null) {
            testResultRepository.delete(insertedTestResult);
            testResultSearchRepository.delete(insertedTestResult);
            insertedTestResult = null;
        }
        userRepository.deleteAll();
    }

    @Test
    void createTestResult() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        // Create the TestResult
        TestResultDTO testResultDTO = testResultMapper.toDto(testResult);
        var returnedTestResultDTO = om.readValue(
            restTestResultMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testResultDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TestResultDTO.class
        );

        // Validate the TestResult in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTestResult = testResultMapper.toEntity(returnedTestResultDTO);
        assertTestResultUpdatableFieldsEquals(returnedTestResult, getPersistedTestResult(returnedTestResult));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(testResultSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTestResult = returnedTestResult;
    }

    @Test
    void createTestResultWithExistingId() throws Exception {
        // Create the TestResult with an existing ID
        testResult.setId("existing_id");
        TestResultDTO testResultDTO = testResultMapper.toDto(testResult);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testResultSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTestResultMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testResultDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TestResult in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkResultDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        // set the field null
        testResult.setResultDate(null);

        // Create the TestResult, which fails.
        TestResultDTO testResultDTO = testResultMapper.toDto(testResult);

        restTestResultMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testResultDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTestResults() throws Exception {
        // Initialize the database
        insertedTestResult = testResultRepository.save(testResult);

        // Get all the testResultList
        restTestResultMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(testResult.getId())))
            .andExpect(jsonPath("$.[*].analysis").value(hasItem(DEFAULT_ANALYSIS.toString())))
            .andExpect(jsonPath("$.[*].resultDate").value(hasItem(DEFAULT_RESULT_DATE.toString())));
    }

    @Test
    void getTestResult() throws Exception {
        // Initialize the database
        insertedTestResult = testResultRepository.save(testResult);

        // Get the testResult
        restTestResultMockMvc
            .perform(get(ENTITY_API_URL_ID, testResult.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(testResult.getId()))
            .andExpect(jsonPath("$.analysis").value(DEFAULT_ANALYSIS.toString()))
            .andExpect(jsonPath("$.resultDate").value(DEFAULT_RESULT_DATE.toString()));
    }

    @Test
    void getNonExistingTestResult() throws Exception {
        // Get the testResult
        restTestResultMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingTestResult() throws Exception {
        // Initialize the database
        insertedTestResult = testResultRepository.save(testResult);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        testResultSearchRepository.save(testResult);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testResultSearchRepository.findAll());

        // Update the testResult
        TestResult updatedTestResult = testResultRepository.findById(testResult.getId()).orElseThrow();
        updatedTestResult.analysis(UPDATED_ANALYSIS).resultDate(UPDATED_RESULT_DATE);
        TestResultDTO testResultDTO = testResultMapper.toDto(updatedTestResult);

        restTestResultMockMvc
            .perform(
                put(ENTITY_API_URL_ID, testResultDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(testResultDTO))
            )
            .andExpect(status().isOk());

        // Validate the TestResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTestResultToMatchAllProperties(updatedTestResult);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(testResultSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TestResult> testResultSearchList = Streamable.of(testResultSearchRepository.findAll()).toList();
                TestResult testTestResultSearch = testResultSearchList.get(searchDatabaseSizeAfter - 1);

                assertTestResultAllPropertiesEquals(testTestResultSearch, updatedTestResult);
            });
    }

    @Test
    void putNonExistingTestResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        testResult.setId(UUID.randomUUID().toString());

        // Create the TestResult
        TestResultDTO testResultDTO = testResultMapper.toDto(testResult);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTestResultMockMvc
            .perform(
                put(ENTITY_API_URL_ID, testResultDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(testResultDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTestResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        testResult.setId(UUID.randomUUID().toString());

        // Create the TestResult
        TestResultDTO testResultDTO = testResultMapper.toDto(testResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestResultMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(testResultDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTestResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        testResult.setId(UUID.randomUUID().toString());

        // Create the TestResult
        TestResultDTO testResultDTO = testResultMapper.toDto(testResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestResultMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(testResultDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TestResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTestResultWithPatch() throws Exception {
        // Initialize the database
        insertedTestResult = testResultRepository.save(testResult);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the testResult using partial update
        TestResult partialUpdatedTestResult = new TestResult();
        partialUpdatedTestResult.setId(testResult.getId());

        partialUpdatedTestResult.analysis(UPDATED_ANALYSIS);

        restTestResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTestResult.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTestResult))
            )
            .andExpect(status().isOk());

        // Validate the TestResult in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTestResultUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTestResult, testResult),
            getPersistedTestResult(testResult)
        );
    }

    @Test
    void fullUpdateTestResultWithPatch() throws Exception {
        // Initialize the database
        insertedTestResult = testResultRepository.save(testResult);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the testResult using partial update
        TestResult partialUpdatedTestResult = new TestResult();
        partialUpdatedTestResult.setId(testResult.getId());

        partialUpdatedTestResult.analysis(UPDATED_ANALYSIS).resultDate(UPDATED_RESULT_DATE);

        restTestResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTestResult.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTestResult))
            )
            .andExpect(status().isOk());

        // Validate the TestResult in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTestResultUpdatableFieldsEquals(partialUpdatedTestResult, getPersistedTestResult(partialUpdatedTestResult));
    }

    @Test
    void patchNonExistingTestResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        testResult.setId(UUID.randomUUID().toString());

        // Create the TestResult
        TestResultDTO testResultDTO = testResultMapper.toDto(testResult);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTestResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, testResultDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(testResultDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTestResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        testResult.setId(UUID.randomUUID().toString());

        // Create the TestResult
        TestResultDTO testResultDTO = testResultMapper.toDto(testResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestResultMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(testResultDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TestResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTestResult() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        testResult.setId(UUID.randomUUID().toString());

        // Create the TestResult
        TestResultDTO testResultDTO = testResultMapper.toDto(testResult);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTestResultMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(testResultDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TestResult in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTestResult() throws Exception {
        // Initialize the database
        insertedTestResult = testResultRepository.save(testResult);
        testResultRepository.save(testResult);
        testResultSearchRepository.save(testResult);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the testResult
        restTestResultMockMvc
            .perform(delete(ENTITY_API_URL_ID, testResult.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(testResultSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTestResult() throws Exception {
        // Initialize the database
        insertedTestResult = testResultRepository.save(testResult);
        testResultSearchRepository.save(testResult);

        // Search the testResult
        restTestResultMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + testResult.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(testResult.getId())))
            .andExpect(jsonPath("$.[*].analysis").value(hasItem(DEFAULT_ANALYSIS.toString())))
            .andExpect(jsonPath("$.[*].resultDate").value(hasItem(DEFAULT_RESULT_DATE.toString())));
    }

    protected long getRepositoryCount() {
        return testResultRepository.count();
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

    protected TestResult getPersistedTestResult(TestResult testResult) {
        return testResultRepository.findById(testResult.getId()).orElseThrow();
    }

    protected void assertPersistedTestResultToMatchAllProperties(TestResult expectedTestResult) {
        assertTestResultAllPropertiesEquals(expectedTestResult, getPersistedTestResult(expectedTestResult));
    }

    protected void assertPersistedTestResultToMatchUpdatableProperties(TestResult expectedTestResult) {
        assertTestResultAllUpdatablePropertiesEquals(expectedTestResult, getPersistedTestResult(expectedTestResult));
    }
}
