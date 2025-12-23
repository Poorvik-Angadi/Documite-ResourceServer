$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/mydatabase";
$env:SPRING_DATASOURCE_USERNAME="postgres";
$env:SPRING_DATASOURCE_PASSWORD="1080@Post";
mvn clean package;
docker build -f C:\Users\poorv\IdeaProjects\spring-resource-server\Dockerfile.dev -t documite-resource-server:1.0 .;

