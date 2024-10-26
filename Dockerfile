FROM eclipse-temurin:21-alpine

WORKDIR /app

COPY . .

RUN ./mvnw clean package

ENTRYPOINT ["java", "-jar", "target/ecommerce-crud-0.0.1-SNAPSHOT.jar"]


