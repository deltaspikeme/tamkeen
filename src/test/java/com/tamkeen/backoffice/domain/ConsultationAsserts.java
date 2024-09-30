package com.tamkeen.backoffice.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ConsultationAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertConsultationAllPropertiesEquals(Consultation expected, Consultation actual) {
        assertConsultationAutoGeneratedPropertiesEquals(expected, actual);
        assertConsultationAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertConsultationAllUpdatablePropertiesEquals(Consultation expected, Consultation actual) {
        assertConsultationUpdatableFieldsEquals(expected, actual);
        assertConsultationUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertConsultationAutoGeneratedPropertiesEquals(Consultation expected, Consultation actual) {
        assertThat(expected)
            .as("Verify Consultation auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertConsultationUpdatableFieldsEquals(Consultation expected, Consultation actual) {
        assertThat(expected)
            .as("Verify Consultation relevant properties")
            .satisfies(e -> assertThat(e.getConsultantName()).as("check consultantName").isEqualTo(actual.getConsultantName()))
            .satisfies(e -> assertThat(e.getConsultationDate()).as("check consultationDate").isEqualTo(actual.getConsultationDate()))
            .satisfies(e -> assertThat(e.getNotes()).as("check notes").isEqualTo(actual.getNotes()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertConsultationUpdatableRelationshipsEquals(Consultation expected, Consultation actual) {
        assertThat(expected)
            .as("Verify Consultation relationships")
            .satisfies(e -> assertThat(e.getConsultant()).as("check consultant").isEqualTo(actual.getConsultant()));
    }
}
