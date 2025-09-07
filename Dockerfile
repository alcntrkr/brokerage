# Use Eclipse Temurin JDK 17
FROM eclipse-temurin:17-jdk-jammy as build
WORKDIR /workspace
COPY . /workspace
RUN ./mvnw -B -DskipTests package || mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/target/brokerage-final-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]