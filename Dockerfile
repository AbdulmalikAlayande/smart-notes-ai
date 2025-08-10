# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/target/smartnotesai-*.jar app.jar
EXPOSE ${PORT:-8080}
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]