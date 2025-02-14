# Build stage
FROM gradle:jdk23-alpine AS builder

# Set working directory
WORKDIR /app

# Set environment variables for Gradle
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Copy Gradle files first
COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .

# Copy build configuration files (handles both .kts and regular Gradle files)
COPY settings.gradle* ./
COPY build.gradle* ./

# Grant execute permission for gradlew
RUN chmod +x gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon --stacktrace

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew build --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

# Copy the built artifact from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Configure JVM options
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Expose the port your app runs on
EXPOSE 8080

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
