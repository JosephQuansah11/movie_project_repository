FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY gradlew gradle/ build.gradle settings.gradle ./
COPY src ./src
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --create-home appuser
COPY --from=build /workspace/build/libs/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]