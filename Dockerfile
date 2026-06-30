# Fase 1: Build dell'applicazione Java
FROM eclipse-temurin:25-jdk AS build
RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Fase 2: Ambiente di esecuzione con Java 25 + Node.js + Puppeteer
FROM eclipse-temurin:25-jre

# Installiamo Node.js, NPM e le dipendenze necessarie per far girare Chrome/Puppeteer su Linux
RUN apt-get update && apt-get install -y \
    curl \
    gnupg \
    ca-certificates \
    libxss1 \
    libasound2 \
    libatk1.0-0 \
    libc6 \
    libcairo2 \
    libcups2 \
    libdbus-1-3 \
    libexpat1 \
    libfontconfig1 \
    libgbm1 \
    libgcc1 \
    libgconf-2-4 \
    libgdk-pixbuf2.0-0 \
    libglib2.0-0 \
    libgtk-3-0 \
    libnspr4 \
    libpango-1.0-0 \
    libpangocairo-1.0-0 \
    libstdc++6 \
    libx11-6 \
    libx11-xcb1 \
    libxcb1 \
    libxcomposite1 \
    libxcursor1 \
    libxdamage1 \
    libext6 \
    libxfixes3 \
    libxi6 \
    libxrandr2 \
    librender1 \
    libxtst6 \
    xdg-utils \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiamo il file JAR compilato
COPY --from=build /app/target/*.jar app.jar

# IMPORTANTISSIMO: Copiamo l'intero progetto sorgente (comprese le cartelle src/main/resources/...) 
# così Node troverà il file pdf.js al percorso "./src/main/resources/static/js/pdf.js"
COPY --from=build /app/src /app/src
COPY --from=build /app/package.json /app/package.json

# Installiamo le dipendenze di Node (Puppeteer) dentro l'ambiente di esecuzione
RUN npm install

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]