FROM openjdk:20-bullseye
WORKDIR /app
EXPOSE 8080
ADD ./build/libs/Footing.jar .
CMD ["java", "-jar", "Footing.jar"]