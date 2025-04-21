FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY gradle gradle
COPY gradlew .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# Use a simpler approach that doesn't rely on specific JAR structure
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]

# Non-sensitive environment variables
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/groundplay
ENV SPRING_DATASOURCE_USERNAME=postgres

# Note: Sensitive environment variables should be passed at runtime
# through docker-compose environment section or docker run --env flags
# rather than being baked into the image
