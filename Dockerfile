# ---------- Build stage ----------
FROM gradle:8.7-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon -x test

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
# dependencies----------------
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        z3 libz3-4                     \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*.jar app.jar
ENV PORT=8080
EXPOSE 8080
CMD sh -c "java -jar /app/app.jar --server.port=$PORT"
