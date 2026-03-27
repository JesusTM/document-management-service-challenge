FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY --from=build /workspace/app/target/*.jar app.jar

#ENV JAVA_OPTS="-Xmx12m -Xms12m -XX:MaxMetaspaceSize=28m -Xss256k -XX:TieredStopAtLevel=1 -XX:+UseSerialGC -XX:ReservedCodeCacheSize=4m"
#ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
ENTRYPOINT ["sh", "-c", "java -jar /app.jar"]