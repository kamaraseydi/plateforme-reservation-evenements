package com.seydi.plateformereservationevenements.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateSalleRequestTest {

    private static Validator validator;
    private CreateSalleRequest request;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void initRequest() {
        request = new CreateSalleRequest();
        request.setNom("Salle Teranga");
        request.setAdresse("Dakar, Sénégal");
        request.setPlaces(List.of("A1", "A2", "A3", "A4"));
    }

    // ── CAS VALIDE ───────────────────────────────────────────────────────────

    @Test
    void requestValide_aucuneViolation() {
        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    // ── NOM ──────────────────────────────────────────────────────────────────

    @Test
    void nom_null_violation() {
        request.setNom(null);

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("nom"));
    }

    @Test
    void nom_vide_violation() {
        request.setNom("");

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("nom"));
    }

    @Test
    void nom_espacesSeulement_violation() {
        request.setNom("   ");

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("nom"));
    }

    @Test
    void nom_tropCourt_violation() {
        request.setNom("A");

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("nom"));
    }

    @Test
    void nom_tropLong_violation() {
        request.setNom("A".repeat(101));

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("nom"));
    }

    // ── ADRESSE ──────────────────────────────────────────────────────────────

    @Test
    void adresse_null_violation() {
        request.setAdresse(null);

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("adresse"));
    }

    @Test
    void adresse_vide_violation() {
        request.setAdresse("");

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("adresse"));
    }

    @Test
    void adresse_espacesSeulement_violation() {
        request.setAdresse("   ");

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("adresse"));
    }

    @Test
    void adresse_tropCourte_violation() {
        request.setAdresse("A");

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("adresse"));
    }

    @Test
    void adresse_tropLongue_violation() {
        request.setAdresse("A".repeat(201));

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("adresse"));
    }

    // ── PLACES ───────────────────────────────────────────────────────────────

    @Test
    void places_null_violation() {
        request.setPlaces(null);

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("places"));
    }

    @Test
    void places_vide_violation() {
        request.setPlaces(List.of());

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("places"));
    }

    @Test
    void place_vide_violation() {
        request.setPlaces(List.of("A1", ""));

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("places"));
    }

    @Test
    void place_espacesSeulement_violation() {
        request.setPlaces(List.of("A1", "   "));

        Set<ConstraintViolation<CreateSalleRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().contains("places"));
    }
}