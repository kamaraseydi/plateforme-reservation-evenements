package com.seydi.plateformereservationevenements.controller;

import com.seydi.plateformereservationevenements.config.SecurityConfig;
import com.seydi.plateformereservationevenements.dto.request.CreateReservationRequest;
import com.seydi.plateformereservationevenements.dto.response.ReservationResponse;
import com.seydi.plateformereservationevenements.exception.EventNotFoundException;
import com.seydi.plateformereservationevenements.exception.ReservationException;
import com.seydi.plateformereservationevenements.exception.RoleInvalideException;
import com.seydi.plateformereservationevenements.exception.UserNotFoundException;
import com.seydi.plateformereservationevenements.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@Import(SecurityConfig.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private ReservationService reservationService;


    @Test
    void creerReservation_devraitRetourner201() throws Exception {

        CreateReservationRequest request =
                new CreateReservationRequest();

        request.setPlaceId(30L);

        ReservationResponse response =
                new ReservationResponse();

        response.setId(100L);
        response.setEventId(20L);
        response.setEventTitre("Concert Dakar");
        response.setPlaceId(30L);
        response.setNumeroPlace("A1");
        response.setStatut("EN_ATTENTE");

        when(reservationService.creerReservation(
                eq(20L),
                any(CreateReservationRequest.class),
                eq("user-123")
        )).thenReturn(response);

        mockMvc.perform(
                        post("/api/events/20/reservations")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject("user-123")
                                                )
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        jsonMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.eventId").value(20))
                .andExpect(jsonPath("$.eventTitre")
                        .value("Concert Dakar"))
                .andExpect(jsonPath("$.placeId").value(30))
                .andExpect(jsonPath("$.numeroPlace").value("A1"))
                .andExpect(jsonPath("$.statut")
                        .value("EN_ATTENTE"));

        verify(reservationService).creerReservation(
                eq(20L),
                any(CreateReservationRequest.class),
                eq("user-123")
        );
    }


    @Test
    void creerReservation_sansAuthentification_devraitRetourner401()
            throws Exception {

        CreateReservationRequest request =
                new CreateReservationRequest();

        request.setPlaceId(30L);

        mockMvc.perform(
                        post("/api/events/20/reservations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        jsonMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(reservationService);
    }


    @Test
    void creerReservation_avecPlaceInvalide_devraitRetourner400()
            throws Exception {

        CreateReservationRequest request =
                new CreateReservationRequest();

        request.setPlaceId(null);

        mockMvc.perform(
                        post("/api/events/20/reservations")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject("user-123")
                                                )
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        jsonMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reservationService);
    }


    @Test
    void creerReservation_evenementInexistant_devraitRetourner404()
            throws Exception {

        CreateReservationRequest request =
                new CreateReservationRequest();

        request.setPlaceId(30L);

        when(reservationService.creerReservation(
                eq(20L),
                any(CreateReservationRequest.class),
                eq("user-123")
        )).thenThrow(
                new EventNotFoundException(
                        "Événement introuvable"
                )
        );

        mockMvc.perform(
                        post("/api/events/20/reservations")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject("user-123")
                                                )
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        jsonMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void creerReservation_roleInvalide_devraitRetourner409()
            throws Exception {

        CreateReservationRequest request =
                new CreateReservationRequest();

        request.setPlaceId(30L);

        when(reservationService.creerReservation(
                eq(20L),
                any(CreateReservationRequest.class),
                eq("user-123")
        )).thenThrow(
                new RoleInvalideException(
                        "Seul un participant peut effectuer une réservation"
                )
        );

        mockMvc.perform(
                        post("/api/events/20/reservations")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject("user-123")
                                                )
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        jsonMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isConflict());
    }


    @Test
    void listerMesReservations_devraitRetourner200()
            throws Exception {

        ReservationResponse reservation1 =
                new ReservationResponse();

        reservation1.setId(100L);
        reservation1.setEventId(20L);
        reservation1.setEventTitre("Concert Dakar");
        reservation1.setPlaceId(30L);
        reservation1.setNumeroPlace("A1");
        reservation1.setStatut("EN_ATTENTE");

        ReservationResponse reservation2 =
                new ReservationResponse();

        reservation2.setId(101L);
        reservation2.setEventId(21L);
        reservation2.setEventTitre("Festival Dakar");
        reservation2.setPlaceId(40L);
        reservation2.setNumeroPlace("B2");
        reservation2.setStatut("CONFIRMEE");

        when(reservationService.listerMesReservations("user-123"))
                .thenReturn(List.of(
                        reservation1,
                        reservation2
                ));

        mockMvc.perform(
                        get("/api/reservations/me")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject("user-123")
                                                )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].eventId").value(20))
                .andExpect(jsonPath("$[0].statut")
                        .value("EN_ATTENTE"))
                .andExpect(jsonPath("$[1].id").value(101))
                .andExpect(jsonPath("$[1].eventId").value(21))
                .andExpect(jsonPath("$[1].statut")
                        .value("CONFIRMEE"));

        verify(reservationService)
                .listerMesReservations("user-123");
    }


    @Test
    void listerMesReservations_sansAuthentification_devraitRetourner401()
            throws Exception {

        mockMvc.perform(
                        get("/api/reservations/me")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(reservationService);
    }


    @Test
    void listerMesReservations_utilisateurInexistant_devraitRetourner404()
            throws Exception {

        when(reservationService.listerMesReservations("unknown"))
                .thenThrow(
                        new UserNotFoundException(
                                "Utilisateur introuvable"
                        )
                );

        mockMvc.perform(
                        get("/api/reservations/me")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject("unknown")
                                                )
                                )
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void annulerReservation_devraitRetourner204()
            throws Exception {

        doNothing().when(reservationService)
                .annulerReservation(100L, "user-123");

        mockMvc.perform(
                        patch("/api/reservations/100/cancel")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject("user-123")
                                                )
                                )
                )
                .andExpect(status().isNoContent());

        verify(reservationService)
                .annulerReservation(100L, "user-123");
    }


    @Test
    void annulerReservation_sansAuthentification_devraitRetourner401()
            throws Exception {

        mockMvc.perform(
                        patch("/api/reservations/100/cancel")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(reservationService);
    }


    @Test
    void annulerReservation_inexistante_devraitRetourner409()
            throws Exception {

        doThrow(
                new ReservationException(
                        "Réservation introuvable"
                )
        ).when(reservationService)
                .annulerReservation(999L, "user-123");

        mockMvc.perform(
                        patch("/api/reservations/999/cancel")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject("user-123")
                                                )
                                )
                )
                .andExpect(status().isConflict());
    }


    @Test
    void annulerReservation_autreParticipant_devraitRetourner409()
            throws Exception {

        doThrow(
                new ReservationException(
                        "Vous ne pouvez pas annuler cette réservation"
                )
        ).when(reservationService)
                .annulerReservation(100L, "user-123");

        mockMvc.perform(
                        patch("/api/reservations/100/cancel")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject("user-123")
                                                )
                                )
                )
                .andExpect(status().isConflict());
    }
}