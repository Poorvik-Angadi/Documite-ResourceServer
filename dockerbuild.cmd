mvnw package && java -jar target/spring-resource-server-0.0.1-SNAPSHOT.jar;
docker build -f Dockerfile.dev -t documite-resource-server:1.0 .;

