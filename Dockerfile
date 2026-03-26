# ---------- BUILD STAGE ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-Xms4m","-Xmx12m","-XX:MaxMetaspaceSize=24m","-XX:ReservedCodeCacheSize=4m","-XX:+UseSerialGC", "-XX:+UseContainerSupport","-jar","/app/app.jar"]