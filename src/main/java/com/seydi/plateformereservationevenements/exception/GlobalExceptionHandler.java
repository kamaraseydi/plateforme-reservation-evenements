package com.seydi.plateformereservationevenements.exception;

import com.seydi.plateformereservationevenements.dto.response.ApiError;
import com.seydi.plateformereservationevenements.dto.response.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Erreur de Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> gererValidation(MethodArgumentNotValidException ex) {

        //La clé (String) est le nom du champ, et la valeur (String) est le message d'erreur.
        Map<String, String> erreurs = new HashMap<>();

        // getFieldErrors() renvoie uniquement les erreurs liées aux champs (nom, email, etc.).
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erreurs.put(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        ValidationErrorResponse erreur = new ValidationErrorResponse(
                "Erreur de validation",
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                erreurs
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.BAD_REQUEST
        );
    }

    //Erreur métier si l'utilisateur n'éxiste pas
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> gererUserIntrouvable(UserNotFoundException ex){

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.NOT_FOUND
        );
    }

    //Erreur métier si la salle n'éxiste pas
    @ExceptionHandler(SalleNotFoundException.class)
    public ResponseEntity<ApiError> gererSalleIntrouvable(SalleNotFoundException ex){

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.NOT_FOUND
        );
    }

    //SI le role est incorrect
    @ExceptionHandler(RoleInvalideException.class)
    public ResponseEntity<ApiError> gererEventIntrouvable(RoleInvalideException ex){

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.NOT_FOUND
        );
    }

    //Si l'evenement est introuvable
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ApiError> gererRoleIncorrect(EventNotFoundException ex){

        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                erreur,
                HttpStatus.CONFLICT
        );
    }

    //ACCES INTERDIT
    @ExceptionHandler(EventAccessDeniedException.class)
    public ResponseEntity<ApiError> gererAccesEventRefuse(
            EventAccessDeniedException ex
    ) {
        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(erreur, HttpStatus.FORBIDDEN);
    }

    //si l'evenement est daja publie erreur de le modifier
    @ExceptionHandler(EventModificationException.class)
    public ResponseEntity<ApiError> gererModificationEventImpossible(
            EventModificationException ex
    ) {
        ApiError erreur = new ApiError(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(erreur, HttpStatus.CONFLICT);
    }
}
