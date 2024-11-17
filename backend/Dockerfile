FROM openjdk:17
EXPOSE 8080
COPY ./build/libs/backend-0.0.1-SNAPSHOT.jar /app.jar
CMD ["java", "-jar", "/app.jar", "--spring.servlet.multipart.max-file-size=30MB", "--spring.servlet.multipart.max-request-size=30MB"]