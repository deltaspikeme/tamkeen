package com.tamkeen.backoffice.web.rest;

import static com.tamkeen.backoffice.domain.ConsultationAsserts.*;
import static com.tamkeen.backoffice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamkeen.backoffice.IntegrationTest;
import com.tamkeen.backoffice.domain.Consultation;
import com.tamkeen.backoffice.repository.ConsultationRepository;
import com.tamkeen.backoffice.repository.search.ConsultationSearchRepository;
import com.tamkeen.backoffice.service.dto.ConsultationDTO;
import com.tamkeen.backoffice.service.mapper.ConsultationMapper;
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
 * Integration tests for the {@link ConsultationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ConsultationResourceIT {

    private static final String DEFAULT_CONSULTANT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CONSULTANT_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_CONSULTATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CONSULTATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/consultations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/consultations/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private ConsultationSearchRepository consultationSearchRepository;

    @Autowired
    private MockMvc restConsultationMockMvc;

    private Consultation consultation;

    private Consultation insertedConsultation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Consultation createEntity() {
        return new Consultation().consultantName(DEFAULT_CONSULTANT_NAME).consultationDate(DEFAULT_CONSULTATION_DATE).notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Consultation createUpdatedEntity() {
        return new Consultation().consultantName(UPDATED_CONSULTANT_NAME).consultationDate(UPDATED_CONSULTATION_DATE).notes(UPDATED_NOTES);
    }

    @BeforeEach
    public void initTest() {
        consultation = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedConsultation != null) {
            consultationRepository.delete(insertedConsultation);
            consultationSearchRepository.delete(insertedConsultation);
            insertedConsultation = null;
        }
    }

    @Test
    void createConsultation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        // Create the Consultation
        ConsultationDTO consultationDTO = consultationMapper.toDto(consultation);
        var returnedConsultationDTO = om.readValue(
            restConsultationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consultationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ConsultationDTO.class
        );

        // Validate the Consultation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedConsultation = consultationMapper.toEntity(returnedConsultationDTO);
        assertConsultationUpdatableFieldsEquals(returnedConsultation, getPersistedConsultation(returnedConsultation));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedConsultation = returnedConsultation;
    }

    @Test
    void createConsultationWithExistingId() throws Exception {
        // Create the Consultation with an existing ID
        consultation.setId("existing_id");
        ConsultationDTO consultationDTO = consultationMapper.toDto(consultation);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restConsultationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consultationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Consultation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkConsultantNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        // set the field null
        consultation.setConsultantName(null);

        // Create the Consultation, which fails.
        ConsultationDTO consultationDTO = consultationMapper.toDto(consultation);

        restConsultationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consultationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkConsultationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        // set the field null
        consultation.setConsultationDate(null);

        // Create the Consultation, which fails.
        ConsultationDTO consultationDTO = consultationMapper.toDto(consultation);

        restConsultationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consultationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllConsultations() throws Exception {
        // Initialize the database
        insertedConsultation = consultationRepository.save(consultation);

        // Get all the consultationList
        restConsultationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(consultation.getId())))
            .andExpect(jsonPath("$.[*].consultantName").value(hasItem(DEFAULT_CONSULTANT_NAME)))
            .andExpect(jsonPath("$.[*].consultationDate").value(hasItem(DEFAULT_CONSULTATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES.toString())));
    }

    @Test
    void getConsultation() throws Exception {
        // Initialize the database
        insertedConsultation = consultationRepository.save(consultation);

        // Get the consultation
        restConsultationMockMvc
            .perform(get(ENTITY_API_URL_ID, consultation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(consultation.getId()))
            .andExpect(jsonPath("$.consultantName").value(DEFAULT_CONSULTANT_NAME))
            .andExpect(jsonPath("$.consultationDate").value(DEFAULT_CONSULTATION_DATE.toString()))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES.toString()));
    }

    @Test
    void getNonExistingConsultation() throws Exception {
        // Get the consultation
        restConsultationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingConsultation() throws Exception {
        // Initialize the database
        insertedConsultation = consultationRepository.save(consultation);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        consultationSearchRepository.save(consultation);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultationSearchRepository.findAll());

        // Update the consultation
        Consultation updatedConsultation = consultationRepository.findById(consultation.getId()).orElseThrow();
        updatedConsultation.consultantName(UPDATED_CONSULTANT_NAME).consultationDate(UPDATED_CONSULTATION_DATE).notes(UPDATED_NOTES);
        ConsultationDTO consultationDTO = consultationMapper.toDto(updatedConsultation);

        restConsultationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, consultationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(consultationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Consultation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedConsultationToMatchAllProperties(updatedConsultation);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Consultation> consultationSearchList = Streamable.of(consultationSearchRepository.findAll()).toList();
                Consultation testConsultationSearch = consultationSearchList.get(searchDatabaseSizeAfter - 1);

                assertConsultationAllPropertiesEquals(testConsultationSearch, updatedConsultation);
            });
    }

    @Test
    void putNonExistingConsultation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        consultation.setId(UUID.randomUUID().toString());

        // Create the Consultation
        ConsultationDTO consultationDTO = consultationMapper.toDto(consultation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConsultationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, consultationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(consultationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Consultation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchConsultation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        consultation.setId(UUID.randomUUID().toString());

        // Create the Consultation
        ConsultationDTO consultationDTO = consultationMapper.toDto(consultation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsultationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(consultationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Consultation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamConsultation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        consultation.setId(UUID.randomUUID().toString());

        // Create the Consultation
        ConsultationDTO consultationDTO = consultationMapper.toDto(consultation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsultationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consultationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Consultation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateConsultationWithPatch() throws Exception {
        // Initialize the database
        insertedConsultation = consultationRepository.save(consultation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the consultation using partial update
        Consultation partialUpdatedConsultation = new Consultation();
        partialUpdatedConsultation.setId(consultation.getId());

        restConsultationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConsultation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConsultation))
            )
            .andExpect(status().isOk());

        // Validate the Consultation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConsultationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedConsultation, consultation),
            getPersistedConsultation(consultation)
        );
    }

    @Test
    void fullUpdateConsultationWithPatch() throws Exception {
        // Initialize the database
        insertedConsultation = consultationRepository.save(consultation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the consultation using partial update
        Consultation partialUpdatedConsultation = new Consultation();
        partialUpdatedConsultation.setId(consultation.getId());

        partialUpdatedConsultation.consultantName(UPDATED_CONSULTANT_NAME).consultationDate(UPDATED_CONSULTATION_DATE).notes(UPDATED_NOTES);

        restConsultationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConsultation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConsultation))
            )
            .andExpect(status().isOk());

        // Validate the Consultation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConsultationUpdatableFieldsEquals(partialUpdatedConsultation, getPersistedConsultation(partialUpdatedConsultation));
    }

    @Test
    void patchNonExistingConsultation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        consultation.setId(UUID.randomUUID().toString());

        // Create the Consultation
        ConsultationDTO consultationDTO = consultationMapper.toDto(consultation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConsultationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, consultationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(consultationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Consultation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchConsultation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        consultation.setId(UUID.randomUUID().toString());

        // Create the Consultation
        ConsultationDTO consultationDTO = consultationMapper.toDto(consultation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsultationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(consultationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Consultation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamConsultation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        consultation.setId(UUID.randomUUID().toString());

        // Create the Consultation
        ConsultationDTO consultationDTO = consultationMapper.toDto(consultation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsultationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(consultationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Consultation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteConsultation() throws Exception {
        // Initialize the database
        insertedConsultation = consultationRepository.save(consultation);
        consultationRepository.save(consultation);
        consultationSearchRepository.save(consultation);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the consultation
        restConsultationMockMvc
            .perform(delete(ENTITY_API_URL_ID, consultation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchConsultation() throws Exception {
        // Initialize the database
        insertedConsultation = consultationRepository.save(consultation);
        consultationSearchRepository.save(consultation);

        // Search the consultation
        restConsultationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + consultation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(consultation.getId())))
            .andExpect(jsonPath("$.[*].consultantName").value(hasItem(DEFAULT_CONSULTANT_NAME)))
            .andExpect(jsonPath("$.[*].consultationDate").value(hasItem(DEFAULT_CONSULTATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES.toString())));
    }

    protected long getRepositoryCount() {
        return consultationRepository.count();
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

    protected Consultation getPersistedConsultation(Consultation consultation) {
        return consultationRepository.findById(consultation.getId()).orElseThrow();
    }

    protected void assertPersistedConsultationToMatchAllProperties(Consultation expectedConsultation) {
        assertConsultationAllPropertiesEquals(expectedConsultation, getPersistedConsultation(expectedConsultation));
    }

    protected void assertPersistedConsultationToMatchUpdatableProperties(Consultation expectedConsultation) {
        assertConsultationAllUpdatablePropertiesEquals(expectedConsultation, getPersistedConsultation(expectedConsultation));
    }
}
