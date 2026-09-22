CREATE TABLE user_profile (
      id BIGSERIAL PRIMARY KEY,
      supabase_user_id VARCHAR(255) NOT NULL UNIQUE,
      nom VARCHAR(100) NOT NULL,
      email VARCHAR(255) NOT NULL UNIQUE,
      role VARCHAR(30) NOT NULL,
      created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT chk_user_role
          CHECK (role IN ('ADMIN', 'ORGANISATEUR', 'PARTICIPANT'))
);

CREATE TABLE salle (
   id BIGSERIAL PRIMARY KEY,
   nom VARCHAR(100) NOT NULL,
   adresse VARCHAR(200) NOT NULL,
   capacite INTEGER NOT NULL,
   created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

   CONSTRAINT chk_salle_capacite
       CHECK (capacite > 0)
);

CREATE TABLE place (
    id BIGSERIAL PRIMARY KEY,
   numero VARCHAR(20) NOT NULL,
   salle_id BIGINT NOT NULL,

   CONSTRAINT fk_place_salle
       FOREIGN KEY (salle_id)
           REFERENCES salle(id),

   CONSTRAINT uk_place_salle_numero
       UNIQUE (salle_id, numero)
);

CREATE TABLE event (
   id BIGSERIAL PRIMARY KEY,
   titre VARCHAR(100) NOT NULL,
   description VARCHAR(1000) NOT NULL,
   date_heure TIMESTAMPTZ NOT NULL,
   image_url VARCHAR(1000),
   statut VARCHAR(30) NOT NULL,
   organisateur_id BIGINT NOT NULL,
   salle_id BIGINT NOT NULL,
   created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

   CONSTRAINT fk_event_organisateur
       FOREIGN KEY (organisateur_id)
           REFERENCES user_profile(id),

   CONSTRAINT fk_event_salle
       FOREIGN KEY (salle_id)
           REFERENCES salle(id),

   CONSTRAINT chk_event_statut
       CHECK (statut IN ('BROUILLON', 'PUBLIE', 'ANNULE'))
);

CREATE TABLE reservation (
     id BIGSERIAL PRIMARY KEY,
     participant_id BIGINT NOT NULL,
     event_id BIGINT NOT NULL,
     place_id BIGINT NOT NULL,
     statut VARCHAR(30) NOT NULL,
     created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

     CONSTRAINT fk_reservation_participant
         FOREIGN KEY (participant_id)
             REFERENCES user_profile(id),

     CONSTRAINT fk_reservation_event
         FOREIGN KEY (event_id)
             REFERENCES event(id),

     CONSTRAINT fk_reservation_place
         FOREIGN KEY (place_id)
             REFERENCES place(id),

     CONSTRAINT chk_reservation_statut
         CHECK (statut IN ('EN_ATTENTE', 'CONFIRMEE', 'ANNULEE'))
);

CREATE UNIQUE INDEX uk_reservation_event_place_active
    ON reservation(event_id, place_id)
    WHERE statut IN ('EN_ATTENTE', 'CONFIRMEE');