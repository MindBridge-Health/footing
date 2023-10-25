FROM openjdk:20-bullseye
RUN apt-get update && apt-get install -y imagemagick
WORKDIR /app
EXPOSE 8080
ADD ./build/libs/Footing.jar .
CMD ["java", "-jar", "Footing.jar"]