# Fase 1: Compilazione del progetto tramite Maven
FROM maven:3.9.6-eclipse-temurin-25 AS build
COPY . .
RUN mvn clean package -DskipTests

# Fase 2: Esecuzione dell'applicazione
FROM eclipse-temurin:25-jre-jammy
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]