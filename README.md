# Plateforme de Réservation d'Événements

API REST backend permettant de gérer des événements, des salles, des places et des réservations.

Le projet a été développé avec Spring Boot et met en œuvre une architecture en couches, une authentification JWT avec Supabase Auth, une persistance PostgreSQL, des migrations Flyway, une conteneurisation Docker ainsi que des tests unitaires et d'intégration avec Testcontainers.

---

## 🎯 Objectif

La plateforme permet à trois types d'utilisateurs d'interagir avec le système selon leurs responsabilités :

- **ADMIN** : gestion des salles et des places.
- **ORGANISATEUR** : création et gestion de ses événements.
- **PARTICIPANT** : consultation des événements publiés et réservation de places.

Le système prend également en compte la concurrence afin d'empêcher qu'une même place soit réservée plusieurs fois pour un même événement.

---

## ✨ Fonctionnalités

### 👤 Utilisateurs et authentification

- Authentification avec Supabase Auth.
- Authentification des requêtes API avec JWT.
- Gestion des rôles :
  - `ADMIN`
  - `ORGANISATEUR`
  - `PARTICIPANT`
- Synchronisation entre les utilisateurs Supabase Auth et la table `user_profile`.

### 🏢 Salles et places

- Création d'une salle.
- Modification d'une salle.
- Suppression d'une salle.
- Création de plusieurs places lors de la création d'une salle.
- Gestion des places associées à une salle.
- Unicité du numéro d'une place au sein d'une salle.

### 🎫 Événements

- Création d'un événement par un organisateur.
- Modification d'un événement.
- Publication d'un événement.
- Annulation d'un événement.
- Consultation publique des événements.
- Gestion de la salle associée à un événement.
- Gestion des droits sur les événements appartenant à leur organisateur.

### 🎟️ Réservations

- Réservation d'une place pour un événement.
- Consultation de ses propres réservations.
- Annulation d'une réservation.
- Consultation des réservations d'un événement par son organisateur.
- Vérification qu'une place appartient à la salle de l'événement.
- Protection contre les doubles réservations.
- Protection contre les doubles réservations concurrentes.
- Conservation de l'historique des réservations annulées.
- Annulation des réservations actives lorsqu'un événement est annulé.

---

## 🛠️ Technologies utilisées

### Backend

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- Bean Validation

### Base de données

- PostgreSQL 17
- Flyway

### Authentification

- Supabase Auth
- JWT
- Spring Security OAuth2 Resource Server

### Tests

- JUnit 5
- Mockito
- Spring Boot Test
- Testcontainers
- PostgreSQL avec Testcontainers

### Documentation API

- SpringDoc OpenAPI
- Swagger UI

### Conteneurisation

- Docker
- Docker Compose

### Gestion du projet

- Git
- GitHub
- Maven

---

## 🏗️ Architecture

Le projet utilise une architecture backend en couches afin de séparer les responsabilités.

```text
Client
  │
  ▼
Controller
  │
  ▼
Service
  │
  ├── Repository ──────► PostgreSQL
  │
  └── Mapper
        │
        ▼
       DTO
```

### Principales couches

| Couche | Responsabilité |
|---|---|
| Controller | Expose les endpoints REST et reçoit les requêtes HTTP |
| Service | Contient la logique métier |
| Repository | Assure l'accès aux données avec Spring Data JPA |
| Model | Contient les entités persistées |
| DTO | Définit les données échangées avec l'API |
| Mapper | Convertit les entités en DTO |
| Exception | Gère les erreurs métier et techniques |
| Security | Configure la sécurité et la validation des JWT |

Cette séparation facilite la maintenance, les tests et l'évolution du projet.

---

## 📋 Prérequis

Avant de lancer le projet, les éléments suivants doivent être installés :

- Java 21
- Docker
- Docker Compose
- Git

Maven n'a pas besoin d'être installé séparément puisque le projet utilise le Maven Wrapper (`mvnw`).

Docker doit être démarré pour :

- lancer PostgreSQL avec Docker Compose ;
- exécuter les tests d'intégration avec Testcontainers.

### Vérifier les installations

```bash
java -version
docker --version
docker compose version
git --version
```

---

## ⚙️ Configuration

Les informations sensibles ne sont pas stockées dans le dépôt Git.

La configuration utilise des variables d'environnement afin de séparer le code source des informations propres à chaque environnement.

### Variables principales

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
```

L'URL de l'issuer Supabase utilisée pour la validation des JWT est configurée dans la configuration Spring Security.

---

## 🔐 Fichier `.env`

Le fichier `.env` contient les valeurs propres à l'environnement local et ne doit pas être versionné.

Il est donc présent dans `.gitignore`.

Le projet fournit un fichier `.env.example` contenant les variables nécessaires à la configuration sans exposer les valeurs sensibles.

Après avoir cloné le projet, il est possible de créer son propre fichier `.env` à partir de cet exemple.

### Linux / macOS

```bash
cp .env.example .env
```

### Windows PowerShell

```powershell
Copy-Item .env.example .env
```

Les valeurs du fichier `.env` doivent ensuite être remplacées par celles de l'environnement utilisé.

> Ne jamais commiter le fichier `.env` ou des clés secrètes dans Git.

---

## 🚀 Installation

### 1. Cloner le projet

```bash
git clone https://github.com/kamaraseydi/plateforme-reservation-evenements.git
```

Puis :

```bash
cd plateforme-reservation-evenements
```

### 2. Configurer l'environnement

Créer le fichier `.env` à partir de `.env.example`.

Windows PowerShell :

```powershell
Copy-Item .env.example .env
```

Puis renseigner les valeurs nécessaires.

### 3. Lancer l'application

Avec Maven Wrapper :

#### Windows

```powershell
.\mvnw spring-boot:run
```

#### Linux / macOS

```bash
./mvnw spring-boot:run
```

L'application utilise par défaut le port :

```text
http://localhost:8080
```

---

## 🐳 Docker

Le projet fournit une configuration Docker permettant de lancer l'application avec PostgreSQL.

### Construire et démarrer les conteneurs

```bash
docker compose up --build
```

### Démarrer en arrière-plan

```bash
docker compose up -d --build
```

### Arrêter les conteneurs

```bash
docker compose down
```

### Afficher les conteneurs actifs

```bash
docker ps
```

Docker permet de reproduire l'environnement nécessaire au fonctionnement de l'application.

---

## 🧪 Tests

Le projet possède plusieurs niveaux de tests :

- Tests des controllers.
- Tests des services.
- Tests des DTO.
- Tests des mappers.
- Tests d'intégration.
- Tests avec PostgreSQL réel via Testcontainers.
- Tests de concurrence sur les réservations.

### Exécuter tous les tests

#### Windows

```powershell
.\mvnw test
```

#### Linux / macOS

```bash
./mvnw test
```

### Testcontainers

Les tests d'intégration utilisent Testcontainers afin de démarrer automatiquement un conteneur PostgreSQL isolé.

Cela permet de tester l'application avec une véritable instance PostgreSQL plutôt qu'une base simulée.

Le projet teste notamment le scénario suivant :

```text
Deux participants
       │
       ├──────────────► même événement
       │
       └──────────────► même place
                         │
                         ▼
                 Deux requêtes simultanées
                         │
                    ┌────┴────┐
                    ▼         ▼
              Réservation   Refus
                créée      (conflit)
```

Une seule réservation peut être créée pour une même place et un même événement.

La contrainte d'unicité PostgreSQL constitue la protection finale contre la double réservation concurrente.

---

## 🔐 Sécurité et authentification

L'authentification est assurée par Supabase Auth.

Après authentification, le client obtient un JWT qu'il transmet à l'API :

```http
Authorization: Bearer <JWT>
```

Spring Security valide le JWT grâce à OAuth2 Resource Server.

L'identifiant `sub` contenu dans le JWT correspond à l'identifiant de l'utilisateur Supabase et permet de retrouver son profil applicatif dans `user_profile`.

### Rôles

- `ADMIN`
- `ORGANISATEUR`
- `PARTICIPANT`

Les permissions sont appliquées dans la logique métier.

Exemples :

- `ADMIN` → gestion des salles.
- `ORGANISATEUR` → gestion de ses propres événements.
- `PARTICIPANT` → gestion de ses propres réservations.

---

## 📚 API

### Événements

| Méthode | Endpoint | Accès |
|---|---|---|
| GET | `/api/events` | Public |
| GET | `/api/events/{id}` | Public |
| POST | `/api/events` | ORGANISATEUR |
| PUT | `/api/events/{id}` | ORGANISATEUR |
| PATCH | `/api/events/{id}/publish` | ORGANISATEUR |
| PATCH | `/api/events/{id}/cancel` | ORGANISATEUR |

### Salles

| Méthode | Endpoint | Accès |
|---|---|---|
| GET | `/api/salles` | ADMIN / ORGANISATEUR |
| POST | `/api/salles` | ADMIN |
| PUT | `/api/salles/{id}` | ADMIN |
| DELETE | `/api/salles/{id}` | ADMIN |

### Places

| Méthode | Endpoint | Accès |
|---|---|---|
| GET | `/api/events/{eventId}/seats` | Authentifié |

### Réservations

| Méthode | Endpoint | Accès |
|---|---|---|
| POST | `/api/events/{eventId}/reservations` | PARTICIPANT |
| GET | `/api/reservations/me` | PARTICIPANT |
| PATCH | `/api/reservations/{id}/cancel` | PARTICIPANT |
| GET | `/api/events/{eventId}/reservations` | ORGANISATEUR |

---

## 📖 Documentation OpenAPI / Swagger

L'API est documentée avec SpringDoc OpenAPI et Swagger UI.

En environnement de développement, Swagger UI est accessible à :

```text
http://localhost:8080/swagger-ui/index.html
```

La documentation OpenAPI est accessible à :

```text
http://localhost:8080/v3/api-docs
```

La documentation contient notamment :

- les endpoints disponibles ;
- les méthodes HTTP ;
- les paramètres ;
- les schémas des requêtes ;
- les schémas des réponses ;
- les règles d'authentification Bearer JWT ;
- les codes de réponse HTTP.

Swagger UI et OpenAPI sont désactivés en production.

---

## 🗄️ Base de données

La base de données utilise PostgreSQL.

Les évolutions du schéma sont gérées avec Flyway.

### Principales tables

```text
user_profile
    │
    ├── event
    │     │
    │     └── salle
    │           │
    │           └── place
    │
    └── reservation
          │
          ├── event
          └── place
```

### Migrations Flyway

Les migrations sont situées dans :

```text
src/main/resources/db/migration/
```

Hibernate utilise :

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Hibernate vérifie donc que le modèle JPA correspond au schéma existant, tandis que Flyway est responsable de la création et de l'évolution du schéma.

---

## 🔄 Flux métier

### Création et publication d'un événement

```text
ADMIN
  │
  └── Crée une salle
        │
        └── Ajoute les places
              │
              ▼
        ORGANISATEUR
              │
              ├── Crée un événement
              ├── Modifie son événement
              └── Publie son événement
                    │
                    ▼
                PARTICIPANT
                    │
                    ├── Consulte les événements publiés
                    ├── Consulte les places
                    └── Réserve une place
```

### Réservation

Lorsqu'un participant réserve une place :

1. Le JWT est vérifié.
2. Le profil utilisateur est récupéré.
3. L'événement est vérifié.
4. La place est vérifiée.
5. La place doit appartenir à la salle de l'événement.
6. Le système vérifie qu'elle n'est pas déjà réservée.
7. La réservation est créée.
8. PostgreSQL garantit l'unicité en cas de concurrence.

### Annulation d'une réservation

```text
Réservation active
       │
       ▼
   ANNULEE
       │
       ▼
La place peut être
réservée à nouveau
```

L'historique de la réservation est conservé en base.

### Annulation d'un événement

```text
Événement PUBLIE
       │
       ▼
Événement ANNULE
       │
       ├── EN_ATTENTE  → ANNULEE
       │
       └── CONFIRMEE   → ANNULEE
```

---

## 🔒 Règles métier principales

Le système applique notamment les règles suivantes :

- Un utilisateur possède un seul rôle applicatif.
- Un participant ne peut réserver qu'en son propre nom.
- Seul un organisateur peut créer un événement.
- Un organisateur ne peut gérer que ses propres événements.
- Seuls les événements publiés sont accessibles publiquement.
- Une place appartient à une salle.
- Une place possède un numéro unique dans sa salle.
- Une place doit appartenir à la salle de l'événement pour pouvoir être réservée.
- Une place ne peut avoir qu'une réservation active pour un même événement.
- Une réservation annulée ne bloque plus la place.
- Un participant ne peut annuler que ses propres réservations.
- L'unicité des réservations actives est protégée au niveau PostgreSQL.
- L'annulation d'un événement entraîne l'annulation de ses réservations actives.

---

## 📁 Structure du projet

```text
plateforme-reservation-evenements/
│
├── .github/
│   └── workflows/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/seydi/plateformereservationevenements/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   └── response/
│   │   │       ├── exception/
│   │   │       ├── mapper/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       └── service/
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   │
│   └── test/
│       ├── java/
│       └── resources/
│
├── .env.example
├── .gitignore
├── Dockerfile
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

## 🔄 Gestion des environnements

Le projet distingue plusieurs configurations Spring :

```text
application.properties
application-dev.properties
application-prod.properties
```

Les fichiers de configuration sont versionnés car ils ne contiennent pas directement les secrets.

Les informations sensibles sont injectées à travers les variables d'environnement.

Le fichier :

```text
.env
```

est volontairement exclu du dépôt Git.

Le fichier :

```text
.env.example
```

est versionné afin d'indiquer les variables nécessaires à la configuration du projet.

---

## 🚀 Vérification de la configuration de production

Avant le déploiement, la configuration de production doit respecter les principes suivants :

- Les secrets ne doivent jamais être présents dans le code source.
- Les identifiants PostgreSQL doivent être fournis via des variables d'environnement.
- Le profil Spring `prod` doit être utilisé.
- Swagger UI doit être désactivé en production.
- OpenAPI `/v3/api-docs` doit être désactivé en production.
- PostgreSQL doit être utilisé comme base de données de production.
- Supabase PostgreSQL constitue la base de données distante utilisée en production.
- Flyway doit gérer les migrations de la base de données.
- Docker doit permettre de construire et d'exécuter l'application de manière reproductible.

---

## 🚢 Déploiement

L'objectif du déploiement est d'obtenir une architecture de production similaire à :

```text
GitHub
   │
   ▼
Build Maven
   │
   ▼
Docker
   │
   ▼
Application Spring Boot
   │
   ├──────────────► PostgreSQL Supabase
   │
   └──────────────► Supabase Auth
                         │
                         ▼
                  API accessible
                  sur Internet
```

Après le déploiement, l'application devra être vérifiée de bout en bout.

### Test fonctionnel de production

```text
Inscription
    │
    ▼
Supabase Auth
    │
    ▼
user_profile
    │
    ▼
ADMIN crée une salle
    │
    ▼
ORGANISATEUR crée et publie un événement
    │
    ▼
PARTICIPANT réserve une place
    │
    ▼
Annulation / concurrence
```

La vérification devra confirmer notamment :

- l'accès à l'API depuis Internet ;
- la connexion à PostgreSQL Supabase ;
- la validation des JWT Supabase ;
- la synchronisation avec `user_profile` ;
- le fonctionnement des rôles ;
- les migrations Flyway ;
- les réservations ;
- la protection contre les doubles réservations ;
- l'annulation des réservations ;
- l'annulation des événements ;
- l'absence d'exposition de Swagger/OpenAPI en production.

---

## 🔁 CI/CD

Après validation du premier déploiement, le projet pourra intégrer une pipeline CI/CD avec GitHub Actions.

Le workflow visé sera :

```text
Push GitHub
    │
    ▼
GitHub Actions
    │
    ├── Build Maven
    │
    ├── Tests
    │
    ├── Tests d'intégration
    │
    ├── Build Docker
    │
    └── Déploiement
             │
             ▼
       Application en production
```

L'objectif est de vérifier automatiquement le projet avant chaque déploiement.

---

## 📌 Roadmap

### MVP

- [x] Conception de l'application
- [x] Architecture backend
- [x] Gestion des utilisateurs et rôles
- [x] Gestion des salles et places
- [x] Gestion des événements
- [x] Gestion des réservations
- [x] Authentification Supabase
- [x] PostgreSQL
- [x] Flyway
- [x] Docker
- [x] Docker Compose
- [x] Tests unitaires
- [x] Tests d'intégration
- [x] Tests de concurrence
- [x] Testcontainers
- [x] Documentation README
- [x] Documentation OpenAPI / Swagger

### Prochaines étapes

- [ ] Vérification finale de la configuration de production
- [ ] Déploiement de l'application
- [ ] Vérification de l'application déployée
- [ ] Mise en place du CI/CD avec GitHub Actions
- [ ] Supabase Storage
- [ ] Fonctionnalités avancées

---

## 👨‍💻 Auteur

**Seydi Kamara**

Étudiant en Génie Logiciel.

### Technologies principales

Java · Spring Boot · PostgreSQL · JPA · Flyway · Spring Security · Supabase · Docker · Testcontainers

---

## 📄 Licence

Projet personnel à vocation pédagogique et professionnelle.