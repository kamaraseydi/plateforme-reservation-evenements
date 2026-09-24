package com.seydi.plateformereservationevenements.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateEventRequestTest {

    private static Validator validator;
    private CreateEventRequest request;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void initRequest() {
        request = new CreateEventRequest();
        request.setTitre("Concert Dakar Music Festival");
        request.setDescription("Un concert de musique sénégalaise réunissant les meilleurs artistes du pays");
        request.setDateHeure(OffsetDateTime.now().plusDays(30));
        request.setSalleId(1L);
    }

    // ── CAS VALIDE ───────────────────────────────────────────────────────────

    @Test
    void requestValide_aucuneViolation() {
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    // ── TITRE ────────────────────────────────────────────────────────────────

    @Test
    void titre_null_violation() {
        request.setTitre(null);
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("titre"));
    }

    @Test
    void titre_vide_violation() {
        request.setTitre("");
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("titre"));
    }

    @Test
    void titre_espacesSeulement_violation() {
        request.setTitre("   ");
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("titre"));
    }

    @Test
    void titre_tropCourt_violation() {
        request.setTitre("A"); // 1 caractère, min = 2
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("titre"));
    }

    @Test
    void titre_tropLong_violation() {
        request.setTitre("A".repeat(101)); // max = 100
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("titre"));
    }

    @Test
    void titre_longueurLimite_valide() {
        request.setTitre("AB"); // exactement min = 2
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .noneMatch(v -> v.getPropertyPath().toString().equals("titre"));
    }

    // ── DESCRIPTION ──────────────────────────────────────────────────────────

    @Test
    void description_null_violation() {
        request.setDescription(null);
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    @Test
    void description_vide_violation() {
        request.setDescription("");
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    @Test
    void description_tropCourte_violation() {
        request.setDescription("A".repeat(9)); // min = 10
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    @Test
    void description_tropLongue_violation() {
        request.setDescription("A".repeat(1001)); // max = 1000
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    // ── DATE HEURE ───────────────────────────────────────────────────────────

    @Test
    void dateHeure_null_violation() {
        request.setDateHeure(null);
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("dateHeure"));
    }

    @Test
    void dateHeure_dansLePassé_violation() {
        request.setDateHeure(OffsetDateTime.now().minusDays(1));
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("dateHeure"));
    }

    // ── SALLE ID ─────────────────────────────────────────────────────────────

    @Test
    void salleId_null_violation() {
        request.setSalleId(null);
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("salleId"));
    }

    @Test
    void salleId_zero_violation() {
        request.setSalleId(0L);
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("salleId"));
    }

    @Test
    void salleId_negatif_violation() {
        request.setSalleId(-1L);
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("salleId"));
    }

    @Test
    void salleId_positif_valide() {
        request.setSalleId(1L);
        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);
        assertThat(violations)
                .noneMatch(v -> v.getPropertyPath().toString().equals("salleId"));
    }
}