# Fase 1: build del JAR Spring Boot
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests && \
	JAR_FILE=$(find target -maxdepth 1 -type f -name "*.jar" ! -name "*.jar.original" | head -n 1) && \
	test -n "$JAR_FILE" && \
	cp "$JAR_FILE" /app/app.jar

# Fase 2: runtime Java puro (niente Node/Chrome)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/app.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]