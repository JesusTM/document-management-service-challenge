# Stage 1: Build
FROM maven:3.8.5-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# Stage 2: Runtime (Tag corregido: bellsoft/liberica-openjre-alpine-musl:17)
FROM bellsoft/liberica-openjre-alpine-musl:17

# Mantenemos las banderas de "Supervivencia Extrema"
ENV JAVA_OPTS="-Xmx10m -Xms10m -XX:MaxMetaspaceSize=36m -XX:TieredStopAtLevel=1 -XX:ReservedCodeCacheSize=4m -Xss256k -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError"

WORKDIR /app
COPY --from=build /home/app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]