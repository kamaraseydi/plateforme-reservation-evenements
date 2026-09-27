package com.seydi.plateformereservationevenements.controller;

import com.seydi.plateformereservationevenements.config.SecurityConfig;
import com.seydi.plateformereservationevenements.dto.response.PlaceResponse;
import com.seydi.plateformereservationevenements.exception.EventNotFoundException;
import com.seydi.plateformereservationevenements.service.PlaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlaceController.class)
@Import(SecurityConfig.class)
class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceService placeService;

    @Test
    @WithMockUser
    void listerPlacesDisponibles_devraitRetourner200() throws Exception {

        PlaceResponse place1 =
                new PlaceResponse(1L, "A1", true);

        PlaceResponse place2 =
                new PlaceResponse(2L, "A2", false);

        when(placeService.listerPlacesDisponibles(10L))
                .thenReturn(List.of(place1, place2));

        mockMvc.perform(
                        get("/api/events/10/seats")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].numero").value("A1"))
                .andExpect(jsonPath("$[0].disponible").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].numero").value("A2"))
                .andExpect(jsonPath("$[1].disponible").value(false));

        verify(placeService)
                .listerPlacesDisponibles(10L);
    }

    @Test
    @WithMockUser
    void listerPlacesDisponibles_evenementInexistant_devraitRetourner404()
            throws Exception {

        when(placeService.listerPlacesDisponibles(99L))
                .thenThrow(
                        new EventNotFoundException(
                                "Evenement introuvable : 99"
                        )
                );

        mockMvc.perform(
                        get("/api/events/99/seats")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

        verify(placeService)
                .listerPlacesDisponibles(99L);
    }

    @Test
    void listerPlacesDisponibles_sansAuthentification_devraitRetourner401()
            throws Exception {

        mockMvc.perform(
                        get("/api/events/10/seats")
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(placeService);
    }
}