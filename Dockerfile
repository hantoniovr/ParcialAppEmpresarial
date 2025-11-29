FROM eclipse-temurin:21-jdk-alpine

COPY target/corte3-1.jar /api-v1.jar

ENTRYPOINT [ "java", "-jar", "api-v1.jar" ]S