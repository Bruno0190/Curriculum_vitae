# Fase 1: Compilazione del codice Java
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests && \
	JAR_FILE=$(find target -maxdepth 1 -type f -name "*.jar" ! -name "*.jar.original" | head -n 1) && \
	test -n "$JAR_FILE" && \
	cp "$JAR_FILE" /app/app.jar && \
	cp -a "$JAVA_HOME" /app/java-runtime

# Fase 2: Esecuzione - Usiamo l'immagine ufficiale di Puppeteer (ha già Node + Chrome pronti e funzionanti)
FROM ghcr.io/puppeteer/puppeteer:22.6.0

# Diventiamo root temporaneamente per copiare Java runtime
USER root

# Evitiamo apt nel runtime (meno fragile su Render): portiamo Java dallo stage build.
COPY --from=build /app/java-runtime /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Configura la cartella di lavoro
WORKDIR /app

# Copiamo il file JAR compilato dalla Fase 1
COPY --from=build /app/app.jar /app/app.jar

# Copiamo i sorgenti e il package.json per far trovare pdf.js a Node
COPY --from=build /app/src /app/src
COPY --from=build /app/package.json /app/package.json

# Installiamo i moduli di Node (Puppeteer userà il Chrome preinstallato nell'immagine)
RUN npm install --omit=dev

EXPOSE 8080

# Torniamo all'utente sicuro di Puppeteer per far girare l'app
USER pptruser

ENTRYPOINT ["java", "-jar", "app.jar"]