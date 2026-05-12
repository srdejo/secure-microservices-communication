FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /home/app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build --no-daemon -x test

FROM eclipse-temurin:25-jre-alpine
EXPOSE 8081
COPY --from=build /home/app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
