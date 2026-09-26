package com.seydi.plateformereservationevenements.controller;

import com.seydi.plateformereservationevenements.config.SecurityConfig;
import com.seydi.plateformereservationevenements.dto.request.CreateSalleRequest;
import com.seydi.plateformereservationevenements.dto.request.UpdateSalleRequest;
import com.seydi.plateformereservationevenements.dto.response.SalleResponse;
import com.seydi.plateformereservationevenements.exception.RoleInvalideException;
import com.seydi.plateformereservationevenements.exception.SalleNotFoundException;
import com.seydi.plateformereservationevenements.service.SalleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SalleController.class)
@Import(SecurityConfig.class)
class SalleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private SalleService salleService;


    @Test
    void listerSalles_devraitRetourner200() throws Exception {

        SalleResponse salle = new SalleResponse();
        salle.setId(1L);
        salle.setNom("Salle Dakar Arena");
        salle.setAdresse("Dakar");
        salle.setCapacite(3);
        salle.setPlaces(List.of("A1", "A2", "A3"));
        salle.setCreatedAt(OffsetDateTime.now());

        when(salleService.listerSalles())
                .thenReturn(List.of(salle));

        mockMvc.perform(
                        get("/api/salles")
                                .with(SecurityMockMvcRequestPostProcessors.jwt())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nom").value("Salle Dakar Arena"))
                .andExpect(jsonPath("$[0].adresse").value("Dakar"))
                .andExpect(jsonPath("$[0].capacite").value(3))
                .andExpect(jsonPath("$[0].places[0]").value("A1"));

        verify(salleService).listerSalles();
    }


    @Test
    void listerSalles_sansAuthentification_devraitRetourner401()
            throws Exception {

        mockMvc.perform(
                        get("/api/salles")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(salleService);
    }


    @Test
    void creerSalle_devraitRetourner201() throws Exception {

        CreateSalleRequest request = new CreateSalleRequest();
        request.setNom("Salle Dakar Arena");
        request.setAdresse("Dakar");
        request.setPlaces(List.of("A1", "A2", "A3"));

        SalleResponse response = new SalleResponse();
        response.setId(1L);
        response.setNom("Salle Dakar Arena");
        response.setAdresse("Dakar");
        response.setCapacite(3);
        response.setPlaces(List.of("A1", "A2", "A3"));
        response.setCreatedAt(OffsetDateTime.now());

        when(salleService.creerSalle(any(CreateSalleRequest.class), eq("user-123")))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/salles")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.subject("user-123")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Salle Dakar Arena"))
                .andExpect(jsonPath("$.capacite").value(3));

        verify(salleService)
                .creerSalle(any(CreateSalleRequest.class), eq("user-123"));
    }


    @Test
    void creerSalle_sansAuthentification_devraitRetourner401()
            throws Exception {

        CreateSalleRequest request = new CreateSalleRequest();
        request.setNom("Salle Dakar Arena");
        request.setAdresse("Dakar");
        request.setPlaces(List.of("A1"));

        mockMvc.perform(
                        post("/api/salles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(salleService);
    }


    @Test
    void creerSalle_avecDonneesInvalides_devraitRetourner400()
            throws Exception {

        CreateSalleRequest request = new CreateSalleRequest();
        request.setNom("");
        request.setAdresse("");
        request.setPlaces(List.of());

        mockMvc.perform(
                        post("/api/salles")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.subject("user-123")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(salleService);
    }


    @Test
    void modifierSalle_devraitRetourner200() throws Exception {

        UpdateSalleRequest request = new UpdateSalleRequest();
        request.setNom("Nouvelle Salle");
        request.setAdresse("Dakar Plateau");

        SalleResponse response = new SalleResponse();
        response.setId(1L);
        response.setNom("Nouvelle Salle");
        response.setAdresse("Dakar Plateau");
        response.setCapacite(3);
        response.setPlaces(List.of("A1", "A2", "A3"));
        response.setCreatedAt(OffsetDateTime.now());

        when(salleService.modifierSalle(
                eq(1L),
                any(UpdateSalleRequest.class),
                eq("user-123")
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/salles/1")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.subject("user-123")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nom").value("Nouvelle Salle"))
                .andExpect(jsonPath("$.adresse").value("Dakar Plateau"));

        verify(salleService).modifierSalle(
                eq(1L),
                any(UpdateSalleRequest.class),
                eq("user-123")
        );
    }


    @Test
    void modifierSalle_sansAuthentification_devraitRetourner401()
            throws Exception {

        UpdateSalleRequest request = new UpdateSalleRequest();
        request.setNom("Nouvelle Salle");
        request.setAdresse("Dakar");

        mockMvc.perform(
                        put("/api/salles/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(salleService);
    }


    @Test
    void modifierSalle_avecDonneesInvalides_devraitRetourner400()
            throws Exception {

        UpdateSalleRequest request = new UpdateSalleRequest();
        request.setNom("");
        request.setAdresse("");

        mockMvc.perform(
                        put("/api/salles/1")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.subject("user-123")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(salleService);
    }


    @Test
    void supprimerSalle_devraitRetourner204() throws Exception {

        doNothing().when(salleService)
                .supprimerSalle(1L, "user-123");

        mockMvc.perform(
                        delete("/api/salles/1")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.subject("user-123")))
                )
                .andExpect(status().isNoContent());

        verify(salleService)
                .supprimerSalle(1L, "user-123");
    }


    @Test
    void supprimerSalle_sansAuthentification_devraitRetourner401()
            throws Exception {

        mockMvc.perform(
                        delete("/api/salles/1")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(salleService);
    }


    @Test
    void modifierSalle_salleInexistante_devraitRetourner404()
            throws Exception {

        UpdateSalleRequest request = new UpdateSalleRequest();
        request.setNom("Nouvelle Salle");
        request.setAdresse("Dakar");

        when(salleService.modifierSalle(
                eq(99L),
                any(UpdateSalleRequest.class),
                eq("user-123")
        )).thenThrow(
                new SalleNotFoundException("Salle introuvable")
        );

        mockMvc.perform(
                        put("/api/salles/99")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.subject("user-123")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void supprimerSalle_salleInexistante_devraitRetourner404()
            throws Exception {

        doThrow(
                new SalleNotFoundException("Salle introuvable")
        ).when(salleService)
                .supprimerSalle(99L, "user-123");

        mockMvc.perform(
                        delete("/api/salles/99")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.subject("user-123")))
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void creerSalle_roleInvalide_devraitRetourner409()
            throws Exception {

        CreateSalleRequest request = new CreateSalleRequest();
        request.setNom("Salle Dakar Arena");
        request.setAdresse("Dakar");
        request.setPlaces(List.of("A1"));

        when(salleService.creerSalle(
                any(CreateSalleRequest.class),
                eq("user-123")
        )).thenThrow(
                new RoleInvalideException(
                        "Seul un administrateur peut créer une salle"
                )
        );

        mockMvc.perform(
                        post("/api/salles")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.subject("user-123")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());
    }
}