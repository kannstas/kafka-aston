
FROM eclipse-temurin:21.0.2_13-jdk-jammy as builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src

RUN ./mvnw clean install -Dmaven.test.skip=true
FROM eclipse-temurin:21.0.2_13-jre-jammy as final
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
ENTRYPOINT ["java", "-jar", "/opt/app/*.jar"]

