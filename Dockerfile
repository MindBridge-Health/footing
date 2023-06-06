FROM openjdk:20-bullseye
WORKDIR /app
EXPOSE 8080
ADD ./build/libs/Footing-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "Footing-0.0.1-SNAPSHOT.jar"]