package com.seydi.plateformereservationevenements.service;

import com.seydi.plateformereservationevenements.dto.request.CreateSalleRequest;
import com.seydi.plateformereservationevenements.dto.request.UpdateSalleRequest;
import com.seydi.plateformereservationevenements.dto.response.SalleResponse;
import com.seydi.plateformereservationevenements.exception.RoleInvalideException;
import com.seydi.plateformereservationevenements.exception.SalleNotFoundException;
import com.seydi.plateformereservationevenements.exception.UserNotFoundException;
import com.seydi.plateformereservationevenements.mapper.SalleMapper;
import com.seydi.plateformereservationevenements.model.Place;
import com.seydi.plateformereservationevenements.model.Role;
import com.seydi.plateformereservationevenements.model.Salle;
import com.seydi.plateformereservationevenements.model.User;
import com.seydi.plateformereservationevenements.repository.PlaceRepository;
import com.seydi.plateformereservationevenements.repository.SalleRepository;
import com.seydi.plateformereservationevenements.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalleServiceTest {

    @Mock
    private SalleRepository salleRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SalleMapper salleMapper;

    @InjectMocks
    private SalleService salleService;

    private User admin;
    private User participant;
    private Salle salle;
    private SalleResponse salleResponse;

    private final String supabaseUserId = "admin-user-123";

    @BeforeEach
    void setUp() {

        admin = new User();
        admin.setId(1L);
        admin.setSupabaseUserId(supabaseUserId);
        admin.setRole(Role.ADMIN);

        participant = new User();
        participant.setId(2L);
        participant.setSupabaseUserId("participant-user-123");
        participant.setRole(Role.PARTICIPANT);

        salle = new Salle();
        salle.setId(1L);
        salle.setNom("Salle Sorano");
        salle.setAdresse("Dakar");
        salle.setCapacite(3);

        salleResponse = new SalleResponse();
        salleResponse.setId(1L);
        salleResponse.setNom("Salle Sorano");
        salleResponse.setAdresse("Dakar");
        salleResponse.setCapacite(3);
        salleResponse.setPlaces(List.of("A1", "A2", "A3"));
    }

    // =====================================================
    // LISTER
    // =====================================================

    @Test
    void listerSalles_devraitRetournerLesSalles() {

        when(salleRepository.findAll())
                .thenReturn(List.of(salle));

        when(salleMapper.toResponse(salle))
                .thenReturn(salleResponse);

        List<SalleResponse> result =
                salleService.listerSalles();

        assertEquals(1, result.size());
        assertEquals("Salle Sorano", result.get(0).getNom());
        assertEquals(3, result.get(0).getCapacite());

        verify(salleRepository).findAll();
        verify(salleMapper).toResponse(salle);
    }

    // =====================================================
    // CREER
    // =====================================================

    @Test
    void creerSalle_admin_devraitCreerSalleEtPlaces() {

        CreateSalleRequest request = new CreateSalleRequest();

        request.setNom("Salle Sorano");
        request.setAdresse("Dakar");
        request.setPlaces(
                List.of("A1", "A2", "A3")
        );

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(admin));

        when(salleMapper.toEntity(request))
                .thenReturn(salle);

        when(salleRepository.save(salle))
                .thenReturn(salle);

        when(salleMapper.toResponse(salle))
                .thenReturn(salleResponse);

        SalleResponse result =
                salleService.creerSalle(
                        request,
                        supabaseUserId
                );

        assertNotNull(result);
        assertEquals("Salle Sorano", result.getNom());
        assertEquals(3, result.getCapacite());

        verify(salleRepository).save(salle);

        verify(placeRepository, times(3))
                .save(any(Place.class));

        verify(salleMapper).toResponse(salle);
    }

    @Test
    void creerSalle_utilisateurInexistant_devraitLeverException() {

        CreateSalleRequest request = new CreateSalleRequest();

        request.setNom("Salle Sorano");
        request.setAdresse("Dakar");
        request.setPlaces(List.of("A1"));

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> salleService.creerSalle(
                        request,
                        supabaseUserId
                )
        );

        verify(salleRepository, never())
                .save(any(Salle.class));
    }

    @Test
    void creerSalle_utilisateurNonAdmin_devraitLeverException() {

        CreateSalleRequest request = new CreateSalleRequest();

        request.setNom("Salle Sorano");
        request.setAdresse("Dakar");
        request.setPlaces(List.of("A1"));

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(participant));

        assertThrows(
                RoleInvalideException.class,
                () -> salleService.creerSalle(
                        request,
                        supabaseUserId
                )
        );

        verify(salleRepository, never())
                .save(any(Salle.class));
    }

    // =====================================================
    // MODIFIER
    // =====================================================

    @Test
    void modifierSalle_admin_devraitModifierSalle() {

        UpdateSalleRequest request = new UpdateSalleRequest();

        request.setNom("Nouvelle Salle");
        request.setAdresse("Nouvelle adresse");

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(admin));

        when(salleRepository.findById(1L))
                .thenReturn(Optional.of(salle));

        when(salleRepository.save(salle))
                .thenReturn(salle);

        when(salleMapper.toResponse(salle))
                .thenReturn(salleResponse);

        SalleResponse result =
                salleService.modifierSalle(
                        1L,
                        request,
                        supabaseUserId
                );

        assertNotNull(result);

        verify(salleMapper)
                .updateEntity(request, salle);

        verify(salleRepository)
                .save(salle);
    }

    @Test
    void modifierSalle_salleInexistante_devraitLeverException() {

        UpdateSalleRequest request = new UpdateSalleRequest();

        request.setNom("Nouvelle Salle");
        request.setAdresse("Dakar");

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(admin));

        when(salleRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                SalleNotFoundException.class,
                () -> salleService.modifierSalle(
                        999L,
                        request,
                        supabaseUserId
                )
        );

        verify(salleRepository, never())
                .save(any(Salle.class));
    }

    @Test
    void modifierSalle_utilisateurNonAdmin_devraitLeverException() {

        UpdateSalleRequest request = new UpdateSalleRequest();

        request.setNom("Nouvelle Salle");
        request.setAdresse("Dakar");

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(participant));

        assertThrows(
                RoleInvalideException.class,
                () -> salleService.modifierSalle(
                        1L,
                        request,
                        supabaseUserId
                )
        );

        verify(salleRepository, never())
                .save(any(Salle.class));
    }

    // =====================================================
    // SUPPRIMER
    // =====================================================

    @Test
    void supprimerSalle_admin_devraitSupprimerSalle() {

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(admin));

        when(salleRepository.findById(1L))
                .thenReturn(Optional.of(salle));

        salleService.supprimerSalle(
                1L,
                supabaseUserId
        );

        verify(salleRepository)
                .delete(salle);
    }

    @Test
    void supprimerSalle_salleInexistante_devraitLeverException() {

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(admin));

        when(salleRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                SalleNotFoundException.class,
                () -> salleService.supprimerSalle(
                        999L,
                        supabaseUserId
                )
        );

        verify(salleRepository, never())
                .delete(any(Salle.class));
    }

    @Test
    void supprimerSalle_utilisateurNonAdmin_devraitLeverException() {

        when(userRepository.findBySupabaseUserId(supabaseUserId))
                .thenReturn(Optional.of(participant));

        assertThrows(
                RoleInvalideException.class,
                () -> salleService.supprimerSalle(
                        1L,
                        supabaseUserId
                )
        );

        verify(salleRepository, never())
                .delete(any(Salle.class));
    }
}