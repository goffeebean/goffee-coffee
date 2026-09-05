# --- Stage 1: build the React frontend ---
FROM node:22-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# --- Stage 2: build the Spring Boot jar, embedding the frontend build as static resources ---
FROM eclipse-temurin:21-jdk AS backend-build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN ./mvnw clean package -DskipTests -B

# --- Stage 3: run just the jar on a JRE ---
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --create-home --shell /usr/sbin/nologin appuser
COPY --from=backend-build /app/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
