# Fase 1: Build usando l'immagine ufficiale di Java 25
FROM eclipse-temurin:25-jdk AS build

# Installiamo Maven manualmente dentro l'immagine Java 25
RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Fase 2: Esecuzione dell'applicazione
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]