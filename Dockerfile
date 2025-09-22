# ===== Stage 1: Build =====
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests package

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre
ENV TZ=UTC \
    JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker} \
    SERVER_PORT=${SERVER_PORT:-8080}

# Non-root user
RUN useradd -ms /bin/bash appuser
USER appuser

WORKDIR /opt/app
COPY --from=build /app/target/*.jar /opt/app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /opt/app/app.jar"]
