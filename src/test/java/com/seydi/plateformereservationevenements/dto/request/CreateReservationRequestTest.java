package com.seydi.plateformereservationevenements.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateReservationRequestTest {

    private static Validator validator;
    private CreateReservationRequest request;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void initRequest() {
        request = new CreateReservationRequest();
        request.setPlaceId(1L);
    }

    // ── CAS VALIDE ───────────────────────────────────────────────────────────

    @Test
    void requestValide_aucuneViolation() {
        Set<ConstraintViolation<CreateReservationRequest>> violations =
                validator.validate(request);

        assertThat(violations).isEmpty();
    }

    // ── PLACE ID ─────────────────────────────────────────────────────────────

    @Test
    void placeId_null_violation() {
        request.setPlaceId(null);

        Set<ConstraintViolation<CreateReservationRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("placeId"));
    }

    @Test
    void placeId_zero_violation() {
        request.setPlaceId(0L);

        Set<ConstraintViolation<CreateReservationRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("placeId"));
    }

    @Test
    void placeId_negatif_violation() {
        request.setPlaceId(-1L);

        Set<ConstraintViolation<CreateReservationRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("placeId"));
    }

    @Test
    void placeId_positif_valide() {
        request.setPlaceId(1L);

        Set<ConstraintViolation<CreateReservationRequest>> violations =
                validator.validate(request);

        assertThat(violations)
                .noneMatch(v -> v.getPropertyPath().toString().equals("placeId"));
    }
}