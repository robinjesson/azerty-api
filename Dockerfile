FROM amazoncorretto:22
ADD target/azerty*.jar /azerty-api.jar
EXPOSE 8080
CMD java -jar /azerty-api.jar