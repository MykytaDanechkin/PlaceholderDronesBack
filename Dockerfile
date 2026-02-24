FROM gradle:8.5-jdk21 AS build
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN ./gradlew dependencies --no-daemon || true

COPY . .

RUN ./gradlew build --no-daemon

FROM ghcr.io/graalvm/graalvm-community:23.0.2
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]