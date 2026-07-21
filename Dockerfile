# Fase 1: Compilazione del codice Java
FROM eclipse-temurin:17-jdk AS build
RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Fase 2: Esecuzione - Usiamo l'immagine ufficiale di Puppeteer (ha già Node + Chrome pronti e funzionanti)
FROM ghcr.io/puppeteer/puppeteer:22.6.0

# Diventiamo root temporaneamente per installare Java senza rompere i permessi
USER root

# Installiamo Java 17 (OpenJDK) dentro l'ambiente Puppeteer
RUN rm -f /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends openjdk-17-jre-headless \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Configura la cartella di lavoro
WORKDIR /app

# Copiamo il file JAR compilato dalla Fase 1
COPY --from=build /app/target/*.jar app.jar

# Copiamo i sorgenti e il package.json per far trovare pdf.js a Node
COPY --from=build /app/src /app/src
COPY --from=build /app/package.json /app/package.json

# Installiamo i moduli di Node (Puppeteer userà il Chrome preinstallato nell'immagine)
RUN npm install --omit=dev

EXPOSE 8080

# Torniamo all'utente sicuro di Puppeteer per far girare l'app
USER pptruser

ENTRYPOINT ["java", "-jar", "app.jar"]