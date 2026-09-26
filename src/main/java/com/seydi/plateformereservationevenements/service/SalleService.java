package com.seydi.plateformereservationevenements.service;

import com.seydi.plateformereservationevenements.dto.request.CreateSalleRequest;
import com.seydi.plateformereservationevenements.dto.request.UpdateSalleRequest;
import com.seydi.plateformereservationevenements.dto.response.SalleResponse;
import com.seydi.plateformereservationevenements.mapper.SalleMapper;
import com.seydi.plateformereservationevenements.model.Place;
import com.seydi.plateformereservationevenements.model.Role;
import com.seydi.plateformereservationevenements.model.Salle;
import com.seydi.plateformereservationevenements.model.User;
import com.seydi.plateformereservationevenements.repository.PlaceRepository;
import com.seydi.plateformereservationevenements.repository.SalleRepository;
import com.seydi.plateformereservationevenements.repository.UserRepository;
import com.seydi.plateformereservationevenements.exception.RoleInvalideException;
import com.seydi.plateformereservationevenements.exception.SalleNotFoundException;
import com.seydi.plateformereservationevenements.exception.UserNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalleService {

    private final SalleRepository salleRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final SalleMapper salleMapper;

    public SalleService(
            SalleRepository salleRepository,
            PlaceRepository placeRepository,
            UserRepository userRepository,
            SalleMapper salleMapper
    ) {
        this.salleRepository = salleRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
        this.salleMapper = salleMapper;
    }

    public List<SalleResponse> listerSalles() {

        return salleRepository.findAll()
                .stream()
                .map(salleMapper::toResponse)
                .toList();
    }

    private User trouverUserOuLeverException(String supabaseUserId){
        return userRepository.findBySupabaseUserId(supabaseUserId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable : " + supabaseUserId));
    }

    private Salle trouverSalleOuLeverException(Long id){
        return salleRepository.findById(id)
                .orElseThrow(() -> new SalleNotFoundException("Salle introuvable"));
    }

    @Transactional
    public SalleResponse creerSalle(
            CreateSalleRequest request,
            String supabaseUserId
    ) {

        User user = trouverUserOuLeverException(supabaseUserId);

        if (user.getRole() != Role.ADMIN) {
            throw new RoleInvalideException(
                    "Seul un administrateur peut créer une salle"
            );
        }

        Salle salle = salleMapper.toEntity(request);

        salle.setCapacite(request.getPlaces().size());

        Salle salleSauvegardee = salleRepository.save(salle);

        for (String numero : request.getPlaces()) {

            Place place = new Place();

            place.setNumero(numero);
            place.setSalle(salleSauvegardee);

            placeRepository.save(place);
        }

        return salleMapper.toResponse(salleSauvegardee);
    }

    @Transactional
    public SalleResponse modifierSalle(
            Long salleId,
            UpdateSalleRequest request,
            String supabaseUserId
    ) {

        User user = trouverUserOuLeverException(supabaseUserId);

        if (user.getRole() != Role.ADMIN) {
            throw new RoleInvalideException(
                    "Seul un administrateur peut modifier une salle"
            );
        }

        Salle salle = trouverSalleOuLeverException(salleId);

        salleMapper.updateEntity(request, salle);

        Salle salleModifiee = salleRepository.save(salle);

        return salleMapper.toResponse(salleModifiee);
    }

    @Transactional
    public void supprimerSalle(
            Long salleId,
            String supabaseUserId
    ) {

        User user = trouverUserOuLeverException(supabaseUserId);

        if (user.getRole() != Role.ADMIN) {
            throw new RoleInvalideException(
                    "Seul un administrateur peut supprimer une salle"
            );
        }

        Salle salle = trouverSalleOuLeverException(salleId);

        salleRepository.delete(salle);
    }
}