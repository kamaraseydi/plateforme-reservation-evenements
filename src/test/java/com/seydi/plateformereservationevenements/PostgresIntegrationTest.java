package com.seydi.plateformereservationevenements;

import com.seydi.plateformereservationevenements.dto.request.CreateReservationRequest;
import com.seydi.plateformereservationevenements.dto.response.ReservationResponse;
import com.seydi.plateformereservationevenements.exception.ReservationException;
import com.seydi.plateformereservationevenements.model.Event;
import com.seydi.plateformereservationevenements.model.Place;
import com.seydi.plateformereservationevenements.model.Reservation;
import com.seydi.plateformereservationevenements.model.Role;
import com.seydi.plateformereservationevenements.model.Salle;
import com.seydi.plateformereservationevenements.model.StatutEvent;
import com.seydi.plateformereservationevenements.model.User;
import com.seydi.plateformereservationevenements.repository.EventRepository;
import com.seydi.plateformereservationevenements.repository.PlaceRepository;
import com.seydi.plateformereservationevenements.repository.ReservationRepository;
import com.seydi.plateformereservationevenements.repository.UserRepository;
import com.seydi.plateformereservationevenements.repository.SalleRepository;
import com.seydi.plateformereservationevenements.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class PostgresIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void nettoyerBase() {
        reservationRepository.deleteAll();
        eventRepository.deleteAll();
        placeRepository.deleteAll();
        salleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void applicationContextShouldStart() {
    }

    @Test
    void deuxReservationsSimultanees_surMemePlace_uneSeuleDoitReussir()
            throws Exception {

        // 1. Création des utilisateurs
        User organisateur = creerUtilisateur(
                "organisateur-123",
                "organisateur@test.com",
                "Organisateur",
                Role.ORGANISATEUR
        );

        User participant1 = creerUtilisateur(
                "participant-1",
                "participant1@test.com",
                "Participant 1",
                Role.PARTICIPANT
        );

        User participant2 = creerUtilisateur(
                "participant-2",
                "participant2@test.com",
                "Participant 2",
                Role.PARTICIPANT
        );

        // 2. Création de la salle
        Salle salle = new Salle();
        salle.setNom("Salle Test");
        salle.setAdresse("Dakar");
        salle.setCapacite(1);
        salle.setCreatedAt(OffsetDateTime.now());

        salle = salleRepository.saveAndFlush(salle);

        // 3. Création d'une seule place
        Place place = new Place();
        place.setNumero("A1");
        place.setSalle(salle);

        place = placeRepository.saveAndFlush(place);

        // 4. Création de l'événement
        Event event = new Event();
        event.setTitre("Concert Test");
        event.setDescription("Événement de test");
        event.setDateHeure(OffsetDateTime.now().plusDays(10));
        event.setStatut(StatutEvent.PUBLIE);
        event.setOrganisateur(organisateur);
        event.setSalle(salle);
        event.setCreatedAt(OffsetDateTime.now());

        event = eventRepository.saveAndFlush(event);

        // 5. Création des requêtes
        CreateReservationRequest request1 =
                new CreateReservationRequest();

        request1.setPlaceId(place.getId());

        CreateReservationRequest request2 =
                new CreateReservationRequest();

        request2.setPlaceId(place.getId());

        Long eventId = event.getId();

        // 6. Deux threads prêts à réserver simultanément
        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch startSignal =
                new CountDownLatch(1);

        Callable<Object> reservation1 = () -> {

            startSignal.await();

            try {
                return reservationService.creerReservation(
                        eventId,
                        request1,
                        participant1.getSupabaseUserId()
                );
            } catch (ReservationException e) {
                return e;
            }
        };

        Callable<Object> reservation2 = () -> {

            startSignal.await();

            try {
                return reservationService.creerReservation(
                        eventId,
                        request2,
                        participant2.getSupabaseUserId()
                );
            } catch (ReservationException e) {
                return e;
            }
        };

        Future<Object> future1 = executor.submit(reservation1);
        Future<Object> future2 = executor.submit(reservation2);

        // 7. Libérer les deux threads en même temps
        startSignal.countDown();

        Object resultat1 = future1.get(10, TimeUnit.SECONDS);
        Object resultat2 = future2.get(10, TimeUnit.SECONDS);

        executor.shutdown();

        // 8. Une seule réservation doit réussir
        long succes = List.of(resultat1, resultat2)
                .stream()
                .filter(resultat -> resultat instanceof ReservationResponse)
                .count();

        long echecs = List.of(resultat1, resultat2)
                .stream()
                .filter(resultat -> resultat instanceof ReservationException)
                .count();

        assertEquals(1, succes);
        assertEquals(1, echecs);

        // 9. Vérifier directement la base
        List<Reservation> reservations =
                reservationRepository.findAll();

        assertEquals(1, reservations.size());

        assertEquals(
                place.getId(),
                reservations.get(0).getPlace().getId()
        );

        assertEquals(
                event.getId(),
                reservations.get(0).getEvent().getId()
        );

        executor.shutdownNow();
    }

    private User creerUtilisateur(
            String supabaseUserId,
            String email,
            String nom,
            Role role
    ) {
        User user = new User();

        user.setSupabaseUserId(supabaseUserId);
        user.setEmail(email);
        user.setNom(nom);
        user.setRole(role);
        user.setCreatedAt(OffsetDateTime.now());

        return userRepository.saveAndFlush(user);
    }

    @Test
    void annulerReservation_doitLibererLaPlacePourUneNouvelleReservation() {

        User organisateur = creerUtilisateur(
                "organisateur-456",
                "organisateur2@test.com",
                "Organisateur",
                Role.ORGANISATEUR
        );

        User participant1 = creerUtilisateur(
                "participant-3",
                "participant3@test.com",
                "Participant 3",
                Role.PARTICIPANT
        );

        User participant2 = creerUtilisateur(
                "participant-4",
                "participant4@test.com",
                "Participant 4",
                Role.PARTICIPANT
        );

        Salle salle = new Salle();
        salle.setNom("Salle Annulation");
        salle.setAdresse("Dakar");
        salle.setCapacite(1);
        salle.setCreatedAt(OffsetDateTime.now());
        salle = salleRepository.saveAndFlush(salle);

        Place place = new Place();
        place.setNumero("A1");
        place.setSalle(salle);
        place = placeRepository.saveAndFlush(place);

        Event event = new Event();
        event.setTitre("Concert Annulation");
        event.setDescription("Test libération place");
        event.setDateHeure(OffsetDateTime.now().plusDays(10));
        event.setStatut(StatutEvent.PUBLIE);
        event.setOrganisateur(organisateur);
        event.setSalle(salle);
        event.setCreatedAt(OffsetDateTime.now());
        event = eventRepository.saveAndFlush(event);

        // Première réservation
        CreateReservationRequest request1 = new CreateReservationRequest();
        request1.setPlaceId(place.getId());

        ReservationResponse premiereReservation =
                reservationService.creerReservation(
                        event.getId(),
                        request1,
                        participant1.getSupabaseUserId()
                );

        assertNotNull(premiereReservation);
        assertEquals("EN_ATTENTE", premiereReservation.getStatut());

        // Annulation
        reservationService.annulerReservation(
                premiereReservation.getId(),
                participant1.getSupabaseUserId()
        );

        Reservation reservationAnnulee =
                reservationRepository.findById(premiereReservation.getId())
                        .orElseThrow();

        assertEquals(
                "ANNULEE",
                reservationAnnulee.getStatut().name()
        );

        // Deuxième réservation de la même place
        CreateReservationRequest request2 = new CreateReservationRequest();
        request2.setPlaceId(place.getId());

        ReservationResponse deuxiemeReservation =
                reservationService.creerReservation(
                        event.getId(),
                        request2,
                        participant2.getSupabaseUserId()
                );

        assertNotNull(deuxiemeReservation);
        assertEquals("EN_ATTENTE", deuxiemeReservation.getStatut());

        // Vérification de l'historique
        List<Reservation> reservations =
                reservationRepository.findAll();

        assertEquals(2, reservations.size());

        long reservationsActives = reservations.stream()
                .filter(reservation ->
                        reservation.getStatut().name().equals("EN_ATTENTE")
                                || reservation.getStatut().name().equals("CONFIRMEE"))
                .count();

        assertEquals(1, reservationsActives);
    }
}