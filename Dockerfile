# =========================
# Étape 1 : build Maven
# =========================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copier le pom en premier pour profiter du cache Docker
COPY pom.xml .

RUN mvn -B dependency:go-offline

# Copier ensuite le code
COPY src ./src

# Compiler et générer le JAR
RUN mvn -B clean package -DskipTests


# =========================
# Étape 2 : image finale
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/plateforme-reservation-evenements-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]