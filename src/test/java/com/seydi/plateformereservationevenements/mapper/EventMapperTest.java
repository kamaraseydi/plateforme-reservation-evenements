package com.seydi.plateformereservationevenements.mapper;

import com.seydi.plateformereservationevenements.dto.request.CreateEventRequest;
import com.seydi.plateformereservationevenements.dto.response.EventResponse;
import com.seydi.plateformereservationevenements.model.Event;
import com.seydi.plateformereservationevenements.model.Role;
import com.seydi.plateformereservationevenements.model.Salle;
import com.seydi.plateformereservationevenements.model.StatutEvent;
import com.seydi.plateformereservationevenements.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EventMapperTest {

    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        eventMapper = new EventMapper();
    }

    @Test
    void toEntity_doitConvertirRequestEnEvent() {

        CreateEventRequest request = new CreateEventRequest();

        OffsetDateTime dateHeure =
                OffsetDateTime.now().plusDays(30);

        request.setTitre("Concert Dakar Music Festival");
        request.setDescription(
                "Un grand concert de musique sénégalaise"
        );
        request.setDateHeure(dateHeure);
        request.setSalleId(1L);

        Event event = eventMapper.toEntity(request);

        assertThat(event.getTitre())
                .isEqualTo(request.getTitre());

        assertThat(event.getDescription())
                .isEqualTo(request.getDescription());

        assertThat(event.getDateHeure())
                .isEqualTo(request.getDateHeure());
    }

    @Test
    void toResponse_doitConvertirEventEnResponse() {

        User organisateur = new User();
        organisateur.setId(1L);
        organisateur.setNom("Seydi Productions");
        organisateur.setEmail("contact@seydi.com");
        organisateur.setSupabaseUserId("supabase-123");
        organisateur.setRole(Role.ORGANISATEUR);

        Salle salle = new Salle();
        salle.setId(2L);
        salle.setNom("Salle Teranga");
        salle.setAdresse("Dakar, Sénégal");
        salle.setCapacite(500);

        OffsetDateTime dateHeure =
                OffsetDateTime.now().plusDays(30);

        OffsetDateTime createdAt =
                OffsetDateTime.now();

        Event event = new Event();

        event.setId(10L);
        event.setTitre("Concert Dakar Music Festival");
        event.setDescription(
                "Un grand concert de musique sénégalaise"
        );
        event.setDateHeure(dateHeure);
        event.setImageUrl("https://example.com/image.jpg");
        event.setStatut(StatutEvent.PUBLIE);
        event.setOrganisateur(organisateur);
        event.setSalle(salle);
        event.setCreatedAt(createdAt);

        EventResponse response = eventMapper.toResponse(event);

        assertThat(response.getId())
                .isEqualTo(10L);

        assertThat(response.getTitre())
                .isEqualTo("Concert Dakar Music Festival");

        assertThat(response.getDescription())
                .isEqualTo(
                        "Un grand concert de musique sénégalaise"
                );

        assertThat(response.getDateHeure())
                .isEqualTo(dateHeure);

        assertThat(response.getImageUrl())
                .isEqualTo("https://example.com/image.jpg");

        assertThat(response.getStatut())
                .isEqualTo(StatutEvent.PUBLIE);

        assertThat(response.getOrganisateur())
                .isEqualTo("Seydi Productions");

        assertThat(response.getSalle())
                .isEqualTo("Salle Teranga");

        assertThat(response.getAdresse())
                .isEqualTo("Dakar, Sénégal");

        assertThat(response.getCreatedAt())
                .isEqualTo(createdAt);
    }
}