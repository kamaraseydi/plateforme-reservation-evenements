package com.seydi.plateformereservationevenements.controller;

import com.seydi.plateformereservationevenements.dto.request.CreateEventRequest;
import com.seydi.plateformereservationevenements.dto.request.UpdateEventRequest;
import com.seydi.plateformereservationevenements.dto.response.EventResponse;
import com.seydi.plateformereservationevenements.model.StatutEvent;
import com.seydi.plateformereservationevenements.service.EventService;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.context.annotation.Import;
import com.seydi.plateformereservationevenements.config.SecurityConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.seydi.plateformereservationevenements.exception.EventAccessDeniedException;
import com.seydi.plateformereservationevenements.exception.EventModificationException;
import com.seydi.plateformereservationevenements.exception.EventNotFoundException;

@Import(SecurityConfig.class)
@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    private final String supabaseUserId = "supabase-user-123";


    // ==========================================
    // GET /api/events
    // ==========================================

    @Test
    void listerEvenementsPublies_devraitRetourner200() throws Exception {

        EventResponse event = new EventResponse();

        event.setId(1L);
        event.setTitre("Concert de Dakar");
        event.setDescription("Grand concert");
        event.setStatut(StatutEvent.PUBLIE);

        when(eventService.listerEvenementsPublies())
                .thenReturn(List.of(event));

        mockMvc.perform(
                        get("/api/events")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titre")
                        .value("Concert de Dakar"))
                .andExpect(jsonPath("$[0].statut")
                        .value("PUBLIE"));

        verify(eventService)
                .listerEvenementsPublies();
    }


    // ==========================================
    // GET /api/events/{id}
    // ==========================================

    @Test
    void trouverEvent_devraitRetourner200() throws Exception {

        EventResponse event = new EventResponse();

        event.setId(1L);
        event.setTitre("Concert de Dakar");
        event.setDescription("Grand concert");
        event.setStatut(StatutEvent.PUBLIE);

        when(eventService.trouverEvent(1L))
                .thenReturn(event);

        mockMvc.perform(
                        get("/api/events/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titre")
                        .value("Concert de Dakar"))
                .andExpect(jsonPath("$.statut")
                        .value("PUBLIE"));

        verify(eventService)
                .trouverEvent(1L);
    }


    // ==========================================
    // POST /api/events
    // ==========================================

    @Test
    void creerEvent_devraitRetourner201() throws Exception {

        CreateEventRequest request = new CreateEventRequest();

        request.setTitre("Concert de Dakar");
        request.setDescription("Grand concert à Dakar");
        request.setDateHeure(
                OffsetDateTime.now().plusDays(10)
        );
        request.setSalleId(1L);

        EventResponse response = new EventResponse();

        response.setId(1L);
        response.setTitre("Concert de Dakar");
        response.setDescription("Grand concert à Dakar");
        response.setStatut(StatutEvent.BROUILLON);

        when(eventService.creerEvent(
                any(CreateEventRequest.class),
                eq(supabaseUserId)
        )).thenReturn(response);

        mockMvc.perform(
                        post("/api/events")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titre")
                        .value("Concert de Dakar"))
                .andExpect(jsonPath("$.statut")
                        .value("BROUILLON"));

        verify(eventService).creerEvent(
                any(CreateEventRequest.class),
                eq(supabaseUserId)
        );
    }


    // ==========================================
    // PUT /api/events/{id}
    // ==========================================

    @Test
    void modifierEvent_devraitRetourner200() throws Exception {

        UpdateEventRequest request = new UpdateEventRequest();

        request.setTitre("Concert de Dakar modifié");
        request.setDescription("Nouvelle description");
        request.setDateHeure(
                OffsetDateTime.now().plusDays(15)
        );
        request.setSalleId(2L);

        EventResponse response = new EventResponse();

        response.setId(1L);
        response.setTitre("Concert de Dakar modifié");
        response.setDescription("Nouvelle description");
        response.setStatut(StatutEvent.BROUILLON);

        when(eventService.modifierEvent(
                eq(1L),
                any(UpdateEventRequest.class),
                eq(supabaseUserId)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/events/1")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titre")
                        .value("Concert de Dakar modifié"))
                .andExpect(jsonPath("$.statut")
                        .value("BROUILLON"));

        verify(eventService).modifierEvent(
                eq(1L),
                any(UpdateEventRequest.class),
                eq(supabaseUserId)
        );
    }


    // ==========================================
    // PATCH /api/events/{id}/publish
    // ==========================================

    @Test
    void publierEvent_devraitRetourner200() throws Exception {

        EventResponse response = new EventResponse();

        response.setId(1L);
        response.setTitre("Concert de Dakar");
        response.setStatut(StatutEvent.PUBLIE);

        when(eventService.publierEvent(1L, supabaseUserId))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/api/events/1/publish")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statut")
                        .value("PUBLIE"));

        verify(eventService)
                .publierEvent(1L, supabaseUserId);
    }


    // ==========================================
    // PATCH /api/events/{id}/cancel
    // ==========================================

    @Test
    void annulerEvent_devraitRetourner200() throws Exception {

        EventResponse response = new EventResponse();

        response.setId(1L);
        response.setTitre("Concert de Dakar");
        response.setStatut(StatutEvent.ANNULE);

        when(eventService.annulerEvent(1L, supabaseUserId))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/api/events/1/cancel")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statut")
                        .value("ANNULE"));

        verify(eventService)
                .annulerEvent(1L, supabaseUserId);
    }

    @Test
    void creerEvent_avecDonneesInvalides_devraitRetourner400() throws Exception {

        CreateEventRequest request = new CreateEventRequest();

        request.setTitre("");
        request.setDescription("");
        request.setDateHeure(OffsetDateTime.now().minusDays(1));
        request.setSalleId(null);

        mockMvc.perform(
                        post("/api/events")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void creerEvent_sansAuthentification_devraitRetourner401() throws Exception {

        CreateEventRequest request = new CreateEventRequest();

        request.setTitre("Concert de Dakar");
        request.setDescription("Grand concert à Dakar");
        request.setDateHeure(
                OffsetDateTime.now().plusDays(10)
        );
        request.setSalleId(1L);

        mockMvc.perform(
                        post("/api/events")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void modifierEvent_sansAuthentification_devraitRetourner401() throws Exception {

        UpdateEventRequest request = new UpdateEventRequest();

        request.setTitre("Concert modifié");
        request.setDescription("Nouvelle description du concert");
        request.setDateHeure(
                OffsetDateTime.now().plusDays(15)
        );
        request.setSalleId(1L);

        mockMvc.perform(
                        put("/api/events/1")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publierEvent_sansAuthentification_devraitRetourner401() throws Exception {

        mockMvc.perform(
                        patch("/api/events/1/publish")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void annulerEvent_sansAuthentification_devraitRetourner401() throws Exception {

        mockMvc.perform(
                        patch("/api/events/1/cancel")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void trouverEvent_evenementInexistant_devraitRetourner404() throws Exception {

        when(eventService.trouverEvent(999L))
                .thenThrow(new EventNotFoundException("Événement introuvable"));

        mockMvc.perform(
                        get("/api/events/999")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void modifierEvent_evenementInexistant_devraitRetourner404() throws Exception {

        UpdateEventRequest request = new UpdateEventRequest();

        request.setTitre("Concert modifié");
        request.setDescription("Nouvelle description");
        request.setDateHeure(
                OffsetDateTime.now().plusDays(15)
        );
        request.setSalleId(1L);

        when(eventService.modifierEvent(
                eq(999L),
                any(UpdateEventRequest.class),
                eq(supabaseUserId)
        )).thenThrow(
                new EventNotFoundException("Événement introuvable")
        );

        mockMvc.perform(
                        put("/api/events/999")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void publierEvent_evenementInexistant_devraitRetourner404() throws Exception {

        when(eventService.publierEvent(999L, supabaseUserId))
                .thenThrow(
                        new EventNotFoundException("Événement introuvable")
                );

        mockMvc.perform(
                        patch("/api/events/999/publish")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void annulerEvent_evenementInexistant_devraitRetourner404() throws Exception {

        when(eventService.annulerEvent(999L, supabaseUserId))
                .thenThrow(
                        new EventNotFoundException("Événement introuvable")
                );

        mockMvc.perform(
                        patch("/api/events/999/cancel")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void modifierEvent_utilisateurNonAutorise_devraitRetourner403() throws Exception {

        UpdateEventRequest request = new UpdateEventRequest();

        request.setTitre("Concert modifié");
        request.setDescription("Nouvelle description");
        request.setDateHeure(
                OffsetDateTime.now().plusDays(15)
        );
        request.setSalleId(1L);

        when(eventService.modifierEvent(
                eq(1L),
                any(UpdateEventRequest.class),
                eq(supabaseUserId)
        )).thenThrow(
                new EventAccessDeniedException("Accès refusé")
        );

        mockMvc.perform(
                        put("/api/events/1")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void publierEvent_utilisateurNonAutorise_devraitRetourner403() throws Exception {

        when(eventService.publierEvent(1L, supabaseUserId))
                .thenThrow(
                        new EventAccessDeniedException("Accès refusé")
                );

        mockMvc.perform(
                        patch("/api/events/1/publish")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void annulerEvent_utilisateurNonAutorise_devraitRetourner403() throws Exception {

        when(eventService.annulerEvent(1L, supabaseUserId))
                .thenThrow(
                        new EventAccessDeniedException("Accès refusé")
                );

        mockMvc.perform(
                        patch("/api/events/1/cancel")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void modifierEvent_modificationInterdite_devraitRetourner409() throws Exception {

        UpdateEventRequest request = new UpdateEventRequest();

        request.setTitre("Concert modifié");
        request.setDescription("Nouvelle description");
        request.setDateHeure(
                OffsetDateTime.now().plusDays(15)
        );
        request.setSalleId(1L);

        when(eventService.modifierEvent(
                eq(1L),
                any(UpdateEventRequest.class),
                eq(supabaseUserId)
        )).thenThrow(
                new EventModificationException("Modification interdite")
        );

        mockMvc.perform(
                        put("/api/events/1")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isConflict());
    }

    @Test
    void publierEvent_publicationInterdite_devraitRetourner409() throws Exception {

        when(eventService.publierEvent(1L, supabaseUserId))
                .thenThrow(
                        new EventModificationException("Publication interdite")
                );

        mockMvc.perform(
                        patch("/api/events/1/publish")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                )
                .andExpect(status().isConflict());
    }

    @Test
    void annulerEvent_annulationInterdite_devraitRetourner409() throws Exception {

        when(eventService.annulerEvent(1L, supabaseUserId))
                .thenThrow(
                        new EventModificationException("Annulation interdite")
                );

        mockMvc.perform(
                        patch("/api/events/1/cancel")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(jwt ->
                                                        jwt.subject(supabaseUserId)
                                                )
                                )
                )
                .andExpect(status().isConflict());
    }


}