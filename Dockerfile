# ---------- BUILD ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -q -e -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package


# ---------- RUNTIME ----------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

#ENV JAVA_TOOL_OPTIONS="-Xms16m -Xmx32m -XX:MaxMetaspaceSize=32m -XX:+UseSerialGC"

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]