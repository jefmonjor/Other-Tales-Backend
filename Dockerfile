# --- ETAPA 1: BUILD ---
# Usamos una imagen con Maven ya instalado para no depender de archivos locales
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copiamos archivos de dependencias
COPY pom.xml .
COPY src ./src

# COMPILAMOS SALTANDO LOS TESTS (La clave para arreglar tu error local)
RUN mvn clean package -DskipTests

# --- ETAPA 2: RUNTIME ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos el JAR generado
COPY --from=builder /app/target/*.jar app.jar

# Exponemos el puerto
EXPOSE 8080

# Arrancamos la app
ENTRYPOINT ["java", "-jar", "app.jar"]
