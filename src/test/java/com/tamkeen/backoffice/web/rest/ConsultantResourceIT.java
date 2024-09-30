package com.tamkeen.backoffice.web.rest;

import static com.tamkeen.backoffice.domain.ConsultantAsserts.*;
import static com.tamkeen.backoffice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamkeen.backoffice.IntegrationTest;
import com.tamkeen.backoffice.domain.Consultant;
import com.tamkeen.backoffice.repository.ConsultantRepository;
import com.tamkeen.backoffice.repository.UserRepository;
import com.tamkeen.backoffice.repository.search.ConsultantSearchRepository;
import com.tamkeen.backoffice.service.dto.ConsultantDTO;
import com.tamkeen.backoffice.service.mapper.ConsultantMapper;
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
 * Integration tests for the {@link ConsultantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ConsultantResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EXPERTISE = "AAAAAAAAAA";
    private static final String UPDATED_EXPERTISE = "BBBBBBBBBB";

    private static final String DEFAULT_BIO = "AAAAAAAAAA";
    private static final String UPDATED_BIO = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "Gql@kFIa.s.uP";
    private static final String UPDATED_EMAIL = "4@tc2at.cKu";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_SERVICES_OFFERED = "AAAAAAAAAA";
    private static final String UPDATED_SERVICES_OFFERED = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/consultants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/consultants/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsultantMapper consultantMapper;

    @Autowired
    private ConsultantSearchRepository consultantSearchRepository;

    @Autowired
    private MockMvc restConsultantMockMvc;

    private Consultant consultant;

    private Consultant insertedConsultant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Consultant createEntity() {
        return new Consultant()
            .name(DEFAULT_NAME)
            .expertise(DEFAULT_EXPERTISE)
            .bio(DEFAULT_BIO)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .servicesOffered(DEFAULT_SERVICES_OFFERED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Consultant createUpdatedEntity() {
        return new Consultant()
            .name(UPDATED_NAME)
            .expertise(UPDATED_EXPERTISE)
            .bio(UPDATED_BIO)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .servicesOffered(UPDATED_SERVICES_OFFERED);
    }

    @BeforeEach
    public void initTest() {
        consultant = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedConsultant != null) {
            consultantRepository.delete(insertedConsultant);
            consultantSearchRepository.delete(insertedConsultant);
            insertedConsultant = null;
        }
        userRepository.deleteAll();
    }

    @Test
    void createConsultant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        // Create the Consultant
        ConsultantDTO consultantDTO = consultantMapper.toDto(consultant);
        var returnedConsultantDTO = om.readValue(
            restConsultantMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consultantDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ConsultantDTO.class
        );

        // Validate the Consultant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedConsultant = consultantMapper.toEntity(returnedConsultantDTO);
        assertConsultantUpdatableFieldsEquals(returnedConsultant, getPersistedConsultant(returnedConsultant));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedConsultant = returnedConsultant;
    }

    @Test
    void createConsultantWithExistingId() throws Exception {
        // Create the Consultant with an existing ID
        consultant.setId("existing_id");
        ConsultantDTO consultantDTO = consultantMapper.toDto(consultant);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restConsultantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consultantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Consultant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        // set the field null
        consultant.setName(null);

        // Create the Consultant, which fails.
        ConsultantDTO consultantDTO = consultantMapper.toDto(consultant);

        restConsultantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consultantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkExpertiseIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        // set the field null
        consultant.setExpertise(null);

        // Create the Consultant, which fails.
        ConsultantDTO consultantDTO = consultantMapper.toDto(consultant);

        restConsultantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consultantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        // set the field null
        consultant.setEmail(null);

        // Create the Consultant, which fails.
        ConsultantDTO consultantDTO = consultantMapper.toDto(consultant);

        restConsultantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consultantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllConsultants() throws Exception {
        // Initialize the database
        insertedConsultant = consultantRepository.save(consultant);

        // Get all the consultantList
        restConsultantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(consultant.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].expertise").value(hasItem(DEFAULT_EXPERTISE)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].servicesOffered").value(hasItem(DEFAULT_SERVICES_OFFERED.toString())));
    }

    @Test
    void getConsultant() throws Exception {
        // Initialize the database
        insertedConsultant = consultantRepository.save(consultant);

        // Get the consultant
        restConsultantMockMvc
            .perform(get(ENTITY_API_URL_ID, consultant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(consultant.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.expertise").value(DEFAULT_EXPERTISE))
            .andExpect(jsonPath("$.bio").value(DEFAULT_BIO.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.servicesOffered").value(DEFAULT_SERVICES_OFFERED.toString()));
    }

    @Test
    void getNonExistingConsultant() throws Exception {
        // Get the consultant
        restConsultantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingConsultant() throws Exception {
        // Initialize the database
        insertedConsultant = consultantRepository.save(consultant);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        consultantSearchRepository.save(consultant);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());

        // Update the consultant
        Consultant updatedConsultant = consultantRepository.findById(consultant.getId()).orElseThrow();
        updatedConsultant
            .name(UPDATED_NAME)
            .expertise(UPDATED_EXPERTISE)
            .bio(UPDATED_BIO)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .servicesOffered(UPDATED_SERVICES_OFFERED);
        ConsultantDTO consultantDTO = consultantMapper.toDto(updatedConsultant);

        restConsultantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, consultantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(consultantDTO))
            )
            .andExpect(status().isOk());

        // Validate the Consultant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedConsultantToMatchAllProperties(updatedConsultant);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Consultant> consultantSearchList = Streamable.of(consultantSearchRepository.findAll()).toList();
                Consultant testConsultantSearch = consultantSearchList.get(searchDatabaseSizeAfter - 1);

                assertConsultantAllPropertiesEquals(testConsultantSearch, updatedConsultant);
            });
    }

    @Test
    void putNonExistingConsultant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        consultant.setId(UUID.randomUUID().toString());

        // Create the Consultant
        ConsultantDTO consultantDTO = consultantMapper.toDto(consultant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConsultantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, consultantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(consultantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Consultant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchConsultant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        consultant.setId(UUID.randomUUID().toString());

        // Create the Consultant
        ConsultantDTO consultantDTO = consultantMapper.toDto(consultant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsultantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(consultantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Consultant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamConsultant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        consultant.setId(UUID.randomUUID().toString());

        // Create the Consultant
        ConsultantDTO consultantDTO = consultantMapper.toDto(consultant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsultantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consultantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Consultant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateConsultantWithPatch() throws Exception {
        // Initialize the database
        insertedConsultant = consultantRepository.save(consultant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the consultant using partial update
        Consultant partialUpdatedConsultant = new Consultant();
        partialUpdatedConsultant.setId(consultant.getId());

        partialUpdatedConsultant.bio(UPDATED_BIO).phone(UPDATED_PHONE);

        restConsultantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConsultant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConsultant))
            )
            .andExpect(status().isOk());

        // Validate the Consultant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConsultantUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedConsultant, consultant),
            getPersistedConsultant(consultant)
        );
    }

    @Test
    void fullUpdateConsultantWithPatch() throws Exception {
        // Initialize the database
        insertedConsultant = consultantRepository.save(consultant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the consultant using partial update
        Consultant partialUpdatedConsultant = new Consultant();
        partialUpdatedConsultant.setId(consultant.getId());

        partialUpdatedConsultant
            .name(UPDATED_NAME)
            .expertise(UPDATED_EXPERTISE)
            .bio(UPDATED_BIO)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .servicesOffered(UPDATED_SERVICES_OFFERED);

        restConsultantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConsultant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConsultant))
            )
            .andExpect(status().isOk());

        // Validate the Consultant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConsultantUpdatableFieldsEquals(partialUpdatedConsultant, getPersistedConsultant(partialUpdatedConsultant));
    }

    @Test
    void patchNonExistingConsultant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        consultant.setId(UUID.randomUUID().toString());

        // Create the Consultant
        ConsultantDTO consultantDTO = consultantMapper.toDto(consultant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConsultantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, consultantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(consultantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Consultant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchConsultant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        consultant.setId(UUID.randomUUID().toString());

        // Create the Consultant
        ConsultantDTO consultantDTO = consultantMapper.toDto(consultant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsultantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(consultantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Consultant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamConsultant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        consultant.setId(UUID.randomUUID().toString());

        // Create the Consultant
        ConsultantDTO consultantDTO = consultantMapper.toDto(consultant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsultantMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(consultantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Consultant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteConsultant() throws Exception {
        // Initialize the database
        insertedConsultant = consultantRepository.save(consultant);
        consultantRepository.save(consultant);
        consultantSearchRepository.save(consultant);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the consultant
        restConsultantMockMvc
            .perform(delete(ENTITY_API_URL_ID, consultant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(consultantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchConsultant() throws Exception {
        // Initialize the database
        insertedConsultant = consultantRepository.save(consultant);
        consultantSearchRepository.save(consultant);

        // Search the consultant
        restConsultantMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + consultant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(consultant.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].expertise").value(hasItem(DEFAULT_EXPERTISE)))
            .andExpect(jsonPath("$.[*].bio").value(hasItem(DEFAULT_BIO.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].servicesOffered").value(hasItem(DEFAULT_SERVICES_OFFERED.toString())));
    }

    protected long getRepositoryCount() {
        return consultantRepository.count();
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

    protected Consultant getPersistedConsultant(Consultant consultant) {
        return consultantRepository.findById(consultant.getId()).orElseThrow();
    }

    protected void assertPersistedConsultantToMatchAllProperties(Consultant expectedConsultant) {
        assertConsultantAllPropertiesEquals(expectedConsultant, getPersistedConsultant(expectedConsultant));
    }

    protected void assertPersistedConsultantToMatchUpdatableProperties(Consultant expectedConsultant) {
        assertConsultantAllUpdatablePropertiesEquals(expectedConsultant, getPersistedConsultant(expectedConsultant));
    }
}
