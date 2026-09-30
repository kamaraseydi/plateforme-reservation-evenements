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
       
       
Principales couches
Couche	Responsabilité

Controller	Expose les endpoints REST et reçoit les requêtes HTTP
Service	Contient la logique métier
Repository	Assure l'accès aux données avec Spring Data JPA
Model	Contient les entités persistées
DTO	Définit les données échangées avec l'API
Mapper	Convertit les entités en DTO
Exception	Gère les erreurs métier et techniques
Security	Configure la sécurité et la validation des JWT

Cette séparation facilite la maintenance, les tests et l'évolution du projet.

📋 Prérequis

Avant de lancer le projet, les éléments suivants doivent être installés :

Java 21
Docker
Docker Compose
Git

Maven n'a pas besoin d'être installé séparément puisque le projet utilise le Maven Wrapper (mvnw).

Docker doit être démarré pour :

lancer PostgreSQL avec Docker Compose ;
exécuter les tests d'intégration avec Testcontainers.
Vérifier les installations
java -version
docker --version
docker compose version
git --version

⚙️ Configuration

Les informations sensibles ne sont pas stockées dans le dépôt Git.

La configuration utilise des variables d'environnement afin de séparer le code source des informations propres à chaque environnement.

Variables principales
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD

L'URL de l'issuer Supabase utilisée pour la validation des JWT est configurée dans la configuration Spring Security.

🔐 Fichier .env

Le fichier .env contient les valeurs propres à l'environnement local et ne doit pas être versionné.

Il est donc présent dans .gitignore.

Exemple :

.env
.env.example

Le projet fournit un fichier .env.example contenant les variables nécessaires à la configuration sans exposer les valeurs sensibles.

Après avoir cloné le projet, il est possible de créer son propre fichier .env à partir de cet exemple :

cp .env.example .env

Sous Windows PowerShell :

Copy-Item .env.example .env

Les valeurs du fichier .env doivent ensuite être remplacées par celles de l'environnement utilisé.

Ne jamais commiter le fichier .env ou des clés secrètes dans Git.

🚀 Installation

1. Cloner le projet
git clone https://github.com/kamaraseydi/plateforme-reservation-evenements.git

Puis :

cd plateforme-reservation-evenements

2. Configurer l'environnement

Créer le fichier .env à partir de .env.example :

Copy-Item .env.example .env

Puis renseigner les valeurs nécessaires.

3. Lancer l'application

Avec Maven Wrapper :

Windows
.\mvnw spring-boot:run

Linux / macOS
./mvnw spring-boot:run

L'application utilise par défaut le port :

http://localhost:8080

🐳 Docker

Le projet fournit une configuration Docker permettant de lancer l'application avec PostgreSQL.

Construire et démarrer les conteneurs
docker compose up --build
Démarrer en arrière-plan
docker compose up -d --build
Arrêter les conteneurs
docker compose down
Afficher les conteneurs actifs
docker ps

Docker permet de reproduire l'environnement nécessaire au fonctionnement de l'application.

🧪 Tests

Le projet possède plusieurs niveaux de tests :

Tests des controllers.
Tests des services.
Tests des DTO.
Tests des mappers.
Tests d'intégration.
Tests avec PostgreSQL réel via Testcontainers.
Tests de concurrence sur les réservations.
Exécuter tous les tests

Windows
.\mvnw test

Linux / macOS
./mvnw test

Testcontainers

Les tests d'intégration utilisent Testcontainers afin de démarrer automatiquement un conteneur PostgreSQL isolé.

Cela permet de tester l'application avec une véritable instance PostgreSQL plutôt qu'une base simulée.

Le projet teste notamment le scénario suivant :

Deux participants
       │
       ├──────────────► même événement
       │
       └──────────────► même place
                         │
                         ▼
                Deux requêtes simultanées
                         │
                  ┌──────┴──────┐
                  ▼             ▼
              Réservation    Refus
                créée       (conflit)

Une seule réservation peut être créée pour une même place et un même événement.

La contrainte d'unicité PostgreSQL constitue la protection finale contre la double réservation concurrente.

🔐 Sécurité et authentification

L'authentification est assurée par Supabase Auth.

Après authentification, le client obtient un JWT qu'il transmet à l'API :

Authorization: Bearer <JWT>

Spring Security valide le JWT grâce à OAuth2 Resource Server.

L'identifiant sub contenu dans le JWT correspond à l'identifiant de l'utilisateur Supabase et permet de retrouver son profil applicatif dans user_profile.

Rôles
ADMIN
ORGANISATEUR
PARTICIPANT

Les permissions sont appliquées dans la logique métier.

Exemples :

ADMIN → gestion des salles.
ORGANISATEUR → gestion de ses propres événements.
PARTICIPANT → gestion de ses propres réservations.

📚 API

Événements

Méthode	Endpoint	Accès

GET	/api/events	Public
GET	/api/events/{id}	Public
POST	/api/events	ORGANISATEUR
PUT	/api/events/{id}	ORGANISATEUR
PATCH	/api/events/{id}/publish	ORGANISATEUR
PATCH	/api/events/{id}/cancel	ORGANISATEUR

Salles

Méthode	Endpoint	Accès

GET	/api/salles	ADMIN / ORGANISATEUR
POST	/api/salles	ADMIN
PUT	/api/salles/{id}	ADMIN
DELETE	/api/salles/{id}	ADMIN

Places

Méthode	Endpoint	Accès

GET	/api/events/{eventId}/seats	Authentifié
Réservations
Méthode	Endpoint	Accès
POST	/api/events/{eventId}/reservations	PARTICIPANT
GET	/api/reservations/me	PARTICIPANT
PATCH	/api/reservations/{id}/cancel	PARTICIPANT
GET	/api/events/{eventId}/reservations	ORGANISATEUR

La documentation détaillée des endpoints est également disponible via Swagger UI lorsque celui-ci est activé.

🗄️ Base de données

La base de données utilise PostgreSQL.

Les évolutions du schéma sont gérées avec Flyway.

Principales tables

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
           
Migrations Flyway

Les migrations sont situées dans :

src/main/resources/db/migration/

Hibernate utilise :

spring.jpa.hibernate.ddl-auto=validate

Hibernate vérifie donc que le modèle JPA correspond au schéma existant, tandis que Flyway est responsable de la création et de l'évolution du schéma.

🔄 Flux métier

Création et publication d'un événement

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
Réservation

Lorsqu'un participant réserve une place :

Le JWT est vérifié.
Le profil utilisateur est récupéré.
L'événement est vérifié.
La place est vérifiée.
La place doit appartenir à la salle de l'événement.
Le système vérifie qu'elle n'est pas déjà réservée.
La réservation est créée.
PostgreSQL garantit l'unicité en cas de concurrence.
Annulation d'une réservation
Réservation active
       │
       ▼
   ANNULEE
       │
       ▼
La place peut être

réservée à nouveau

L'historique de la réservation est conservé en base.

Annulation d'un événement

Événement PUBLIE
       │
       ▼
Événement ANNULE
       │
       ├── EN_ATTENTE  → ANNULEE
       │
       └── CONFIRMEE   → ANNULEE
       
🔒 Règles métier principales

Le système applique notamment les règles suivantes :

Un utilisateur possède un seul rôle applicatif.
Un participant ne peut réserver qu'en son propre nom.
Seul un organisateur peut créer un événement.
Un organisateur ne peut gérer que ses propres événements.
Seuls les événements publiés sont accessibles publiquement.
Une place appartient à une salle.
Une place possède un numéro unique dans sa salle.
Une place doit appartenir à la salle de l'événement pour pouvoir être réservée.
Une place ne peut avoir qu'une réservation active pour un même événement.
Une réservation annulée ne bloque plus la place.
Un participant ne peut annuler que ses propres réservations.
L'unicité des réservations actives est protégée au niveau PostgreSQL.
L'annulation d'un événement entraîne l'annulation de ses réservations actives.

📁 Structure du projet

plateforme-reservation-evenements/
│
├── .github/
│   └── workflows/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/seydi/plateformereservationevenements/
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


🔄 Gestion des environnements

Le projet distingue plusieurs configurations Spring :

application.properties
application-dev.properties
application-prod.properties

Les fichiers de configuration sont versionnés car ils ne contiennent pas directement les secrets.

Les informations sensibles sont injectées à travers les variables d'environnement.

Le fichier :

.env

est volontairement exclu du dépôt Git.

Le fichier :

.env.example

est versionné afin d'indiquer les variables nécessaires à la configuration du projet.

📌 Roadmap

MVP

 Conception de l'application
 Architecture backend
 Gestion des utilisateurs et rôles
 Gestion des salles et places
 Gestion des événements
 Gestion des réservations
 Authentification Supabase
 PostgreSQL
 Flyway
 Docker
 Docker Compose
 Tests unitaires
 Tests d'intégration
 Tests de concurrence
 Testcontainers
 Documentation README
 
Prochaines étapes

 Documentation OpenAPI complète
 Vérification de la configuration de production
 Déploiement de l'application
 Vérification de l'application déployée
 CI/CD
 Supabase Storage
 Fonctionnalités avancées
 
 
👨‍💻 Auteur

Seydi Kamara

Étudiant en Génie Logiciel.

Technologies principales

Java · Spring Boot · PostgreSQL · JPA · Flyway · Spring Security · Supabase · Docker · Testcontainers

📄 Licence

Projet personnel à vocation pédagogique et professionnelle.