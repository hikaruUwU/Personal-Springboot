FROM eclipse-temurin:21-jre-alpine AS final

EXPOSE 8080

COPY target/base-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar"]

CMD ["/app.jar"]