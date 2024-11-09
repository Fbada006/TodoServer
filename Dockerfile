FROM openjdk:17-jdk-slim as build

# Copy the gradle files first to cache dependencies
WORKDIR /app
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies
RUN ./gradlew dependencies

# Copy the rest of the application
COPY . .

# Build the application
RUN ./gradlew buildFatJar --no-daemon

FROM openjdk:17-jdk-slim
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /app/build/libs/*.jar /app/ktor-todo-app.jar
ENTRYPOINT ["java","-jar","/app/ktor-todo-app.jar"]
