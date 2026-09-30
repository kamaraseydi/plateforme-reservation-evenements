# 🎫 Plateforme de Réservation d'Événements

API REST backend de gestion d'événements culturels développée avec **Java 21** et **Spring Boot 4.1**.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker)
![Tests](https://img.shields.io/badge/Tests-145%20passing-success?logo=junit5)
![CI](https://github.com/kamaraseydi/plateforme-reservation-evenements/actions/workflows/ci.yml/badge.svg)

---

## 🚀 Démarrage rapide

```bash
git clone https://github.com/kamaraseydi/plateforme-reservation-evenements.git
cd plateforme-reservation-evenements
cp .env.example .env        # Renseigner les variables
docker compose up --build
```

API disponible sur **http://localhost:8080**
Documentation Swagger : **http://localhost:8080/swagger-ui/index.html**

---

## 📌 Présentation

La plateforme permet à trois types d'utilisateurs d'interagir selon leurs responsabilités :

- aux **administrateurs** de gérer les salles et leurs places
- aux **organisateurs** de créer, publier et gérer leurs événements
- aux **participants** de consulter les événements publiés et de réserver des places

Le système garantit qu'une même place ne peut pas être réservée deux fois pour un même événement, y compris lorsque plusieurs réservations arrivent simultanément. Cette protection est assurée à la fois par la logique applicative et par une contrainte d'unicité partielle PostgreSQL.

---

## ✨ Fonctionnalités

### 👤 Utilisateurs et authentification
- Authentification avec Supabase Auth (JWT)
- Trois rôles applicatifs : `ADMIN`, `ORGANISATEUR`, `PARTICIPANT`
- Synchronisation automatique via trigger PostgreSQL entre `auth.users` et `user_profile`

### 🏢 Salles et places
- Création d'une salle avec toutes ses places en une seule opération
- Modification et suppression de salles
- Unicité du numéro de place au sein d'une salle

### 🎫 Événements
- Création, modification, publication et annulation d'un événement
- Consultation publique des événements publiés
- Contrôle d'accès : un organisateur ne gère que ses propres événements

### 🎟️ Réservations
- Réservation d'une place pour un événement
- Consultation de ses propres réservations
- Annulation d'une réservation avec libération automatique de la place
- Consultation des réservations d'un événement par son organisateur
- Protection contre les doubles réservations concurrentes
- Annulation automatique des réservations actives lors de l'annulation d'un événement

---

## 🛠️ Technologies

| Couche | Technologie |
|--------|-------------|
| Backend | Java 21, Spring Boot 4.1.1 |
| Sécurité | Spring Security, OAuth2 Resource Server, Supabase Auth |
| Base de données | PostgreSQL 17, Spring Data JPA, Flyway |
| Tests | JUnit 5, Mockito, Testcontainers |
| Infra | Docker, Docker Compose, GitHub Actions |
| Documentation | SpringDoc OpenAPI, Swagger UI |

---

## 🏗️ Architecture

Le projet utilise une architecture backend en couches afin de séparer clairement les responsabilités.

```
Client
  │
  ├── Supabase Auth → JWT
  │
  ▼
Controller        ← validation HTTP, extraction JWT
  │
  ▼
Service           ← logique métier, règles, transactions
  │
  ├── Repository  ← accès base (Spring Data JPA)
  │       │
  │       ▼
  │    PostgreSQL
  │
  └── Mapper      ← conversion Entité ↔ DTO
```

| Couche | Responsabilité |
|--------|----------------|
| Controller | Expose les endpoints REST |
| Service | Contient la logique métier |
| Repository | Accès aux données via Spring Data JPA |
| Model | Entités JPA persistées |
| DTO | Données échangées avec l'API (les entités ne sortent jamais) |
| Mapper | Conversion Entité ↔ DTO |
| Exception | Gestion centralisée des erreurs HTTP |

---

## 📋 Prérequis

- Java 21
- Docker Desktop (requis pour `docker compose` et les tests Testcontainers)
- Git

Maven n'a pas besoin d'être installé — le projet utilise le Maven Wrapper (`mvnw`).

```bash
java -version
docker --version
docker compose version
git --version
```

---

## ⚙️ Configuration

Copier `.env.example` en `.env` et renseigner les valeurs :

```bash
# Linux / macOS
cp .env.example .env

# Windows PowerShell
Copy-Item .env.example .env
```

Contenu de `.env.example` :

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/reservation
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=votre_mot_de_passe
SPRING_PROFILES_ACTIVE=dev
```

> ⚠️ Le fichier `.env` est ignoré par Git. Ne jamais committer de secrets.

L'URL de l'issuer Supabase pour la validation des JWT est configurée dans `application.properties`.

---

## 🐳 Docker

```bash
# Lancer l'API + PostgreSQL
docker compose up --build

# En arrière-plan
docker compose up -d --build

# Arrêter
docker compose down
```

PostgreSQL utilise un **volume nommé** — les données sont conservées entre les redémarrages.
Un **healthcheck** garantit que PostgreSQL est prêt avant le démarrage de l'API.

---

## 💻 Développement local (sans Docker)

```bash
# Linux / macOS
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run

# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE="dev"
.\mvnw spring-boot:run
```

---

## 🧪 Tests

```bash
# Linux / macOS
./mvnw clean test

# Windows
.\mvnw clean test
```

**145 tests — 0 échec**

| Niveau | Nombre | Description |
|--------|--------|-------------|
| Validation DTOs | 37 | Bean Validation, sans contexte Spring |
| Services | 57 | Mockito, logique métier isolée |
| Controllers | 46 | WebMvcTest, couche HTTP et sécurité |
| Mappers | 2 | Conversion entité ↔ DTO |
| Intégration | 3 | Testcontainers, vrai PostgreSQL |

Le test de concurrence vérifie qu'en cas de deux réservations simultanées sur la même place, une seule est créée. La contrainte d'unicité partielle PostgreSQL est la protection finale :

```
Deux participants
      │
      ├──► même place, même événement, même instant
      │
      ▼
┌─────┴─────┐
▼           ▼
Réservée   Refusée
(1 seule)  (conflit PostgreSQL)
```

---

## 🔐 Authentification

L'authentification est assurée par **Supabase Auth**.

```
Inscription
    │
    ▼
Supabase crée l'utilisateur dans auth.users
    │
    ▼
Trigger PostgreSQL → INSERT dans user_profile (rôle : PARTICIPANT)
    │
    ▼
L'utilisateur obtient un JWT Supabase
    │
    ▼
Authorization: Bearer <JWT>
    │
    ▼
Spring Security valide le token (issuer-uri Supabase)
    │
    ▼
sub du JWT → findBySupabaseUserId() → profil applicatif
```

**Trois rôles applicatifs :**

| Rôle | Permissions |
|------|-------------|
| `ADMIN` | Gestion des salles et des utilisateurs |
| `ORGANISATEUR` | Création et gestion de ses propres événements |
| `PARTICIPANT` | Consultation et réservation |

Les vérifications de rôle sont appliquées dans la couche service.

---

## 📚 API

### Événements

| Méthode | Endpoint | Accès |
|---------|----------|-------|
| `GET` | `/api/events` | Public |
| `GET` | `/api/events/{id}` | Public |
| `POST` | `/api/events` | ORGANISATEUR |
| `PUT` | `/api/events/{id}` | ORGANISATEUR |
| `PATCH` | `/api/events/{id}/publish` | ORGANISATEUR |
| `PATCH` | `/api/events/{id}/cancel` | ORGANISATEUR |

### Salles

| Méthode | Endpoint | Accès |
|---------|----------|-------|
| `GET` | `/api/salles` | ADMIN / ORGANISATEUR |
| `POST` | `/api/salles` | ADMIN |
| `PUT` | `/api/salles/{id}` | ADMIN |
| `DELETE` | `/api/salles/{id}` | ADMIN |

### Places

| Méthode | Endpoint | Accès |
|---------|----------|-------|
| `GET` | `/api/events/{eventId}/seats` | Authentifié |

### Réservations

| Méthode | Endpoint | Accès |
|---------|----------|-------|
| `POST` | `/api/events/{eventId}/reservations` | PARTICIPANT |
| `GET` | `/api/reservations/me` | PARTICIPANT |
| `PATCH` | `/api/reservations/{id}/cancel` | PARTICIPANT |
| `GET` | `/api/events/{eventId}/reservations` | ORGANISATEUR |

> La documentation complète avec corps de requêtes et réponses est disponible via Swagger.

---

## 🗄️ Base de données

Les évolutions du schéma sont gérées avec Flyway :

```
src/main/resources/db/migration/
└── V1__creation_schema_initial.sql
```

Hibernate est configuré en mode `validate` — il vérifie que les entités correspondent au schéma sans jamais le modifier. Tout changement de schéma passe par un nouveau fichier de migration.

**Principales tables :**

```
user_profile
    │
    ├── event ──────────── salle
    │                        │
    │                      place
    └── reservation
          │
          ├── event
          └── place
```

---

## 🔄 Flux métier

### Création et publication d'un événement

```
ADMIN → crée une salle et ses places
              │
              ▼
    ORGANISATEUR → crée un événement → publie
                          │
                          ▼
              PARTICIPANT → consulte → réserve
```

### Annulation d'un événement

```
Événement PUBLIE
    │
    ▼
Événement ANNULE
    │
    ├── Réservation EN_ATTENTE  → ANNULEE
    └── Réservation CONFIRMEE   → ANNULEE
```

---

## 🔒 Règles métier

- Un utilisateur possède un seul rôle applicatif
- Un participant ne réserve qu'en son propre nom
- Seul un organisateur peut créer un événement
- Un organisateur ne gère que ses propres événements
- Seuls les événements publiés sont accessibles publiquement
- Une place possède un numéro unique dans sa salle
- Une place doit appartenir à la salle de l'événement pour être réservée
- Une place ne peut avoir qu'une réservation active par événement
- Une réservation annulée libère la place
- Un participant ne peut annuler que ses propres réservations
- L'annulation d'un événement annule automatiquement ses réservations actives

---

## 📁 Structure du projet

```
src/
├── main/
│   ├── java/com/seydi/plateformereservationevenements/
│   │   ├── config/          ← SecurityConfig
│   │   ├── controller/      ← EventController, SalleController...
│   │   ├── dto/
│   │   │   ├── request/     ← CreateEventRequest, CreateSalleRequest...
│   │   │   └── response/    ← EventResponse, ReservationResponse...
│   │   ├── exception/       ← GlobalExceptionHandler + exceptions métier
│   │   ├── mapper/          ← EventMapper, ReservationMapper...
│   │   ├── model/           ← Event, Salle, Place, Reservation, User
│   │   ├── repository/      ← EventRepository, ReservationRepository...
│   │   └── service/         ← EventService, ReservationService...
│   │
│   └── resources/
│       ├── db/migration/    ← V1__creation_schema_initial.sql
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-prod.properties
│
└── test/
    ├── java/                ← 145 tests
    └── resources/
```

---

## 📌 Roadmap

### MVP ✅

- [x] Architecture backend en couches
- [x] Gestion des salles, places, événements, réservations
- [x] Authentification Supabase Auth + trigger PostgreSQL
- [x] Protection contre le double booking
- [x] 163 tests (unitaires, controllers, intégration, concurrence)
- [x] Docker + Docker Compose
- [x] Flyway + profils dev/prod
- [x] GitHub Actions CI

### À venir

- [ ] Déploiement Railway
- [ ] Supabase Storage (affiches des événements)
- [ ] Réservation temporaire avec expiration automatique
- [ ] Génération de billet PDF + QR Code
- [ ] Notifications email

---

## 👨‍💻 Auteur

**Seydi Kamara** — Étudiant en Génie Logiciel (L2), ISI Keur Massar, Dakar

[![GitHub](https://img.shields.io/badge/GitHub-kamaraseydi-181717?logo=github)](https://github.com/kamaraseydi)

`Java` · `Spring Boot` · `PostgreSQL` · `JPA` · `Flyway` · `Spring Security` · `Supabase` · `Docker` · `Testcontainers`
