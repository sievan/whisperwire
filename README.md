# WhisperWire
This repository contains the WhisperWire chat application, consisting of a web frontend, backend API, and supporting infrastructure.
## Project Structure
```
/
├── ww-web/         # Frontend application
├── ww-api/         # Backend API service
└── infrastructure/ # Infrastructure configuration
```
## Development Setup
### Prerequisites

- Node.js (v23 or higher)
- Java 23
- Docker and Docker Compose (eg. through Docker Desktop)
- IntelliJ IDEA (recommended for backend development)
- Minikube (for local Kubernetes development)

### Frontend (ww-web)
Navigate to the frontend directory:
```bash
cd ww-web
```
Install dependencies:
```bash
npm install
```
Start the development server:
```bash
npm run dev
```
The application will be available at http://localhost:5173/conversations/1.

You can specify the user name by changing the query parameter `userName`, eg. go to http://localhost:5173/conversations/1?userName=Frodo
### Backend (ww-api)

1. Open the ww-api directory in IntelliJ IDEA
2. Let the IDE sync the Gradle dependencies
3. Add a run/debug configuration (if it doesn't pick it up automatically from `.run/Start dev server.run.xml`) with:

```bash
./gradlew bootRun SPRING_PROFILES_ACTIVE=local
```
The API will be available at http://localhost:8080.

### Infrastructure

The application requires Zookeeper, Kafka, and PostgreSQL. Start these services using Docker Compose:
```bash
cd infrastructure
docker compose up
```

#### Kafka Topic Setup
After starting with docker compose, create the required Kafka topic:
```bash
docker exec -it kafka kafka-topics \
  --create \
  --topic test-topic \
  --partitions 1 \
  --replication-factor 1 \
  --bootstrap-server localhost:9092
```
## Production-like Environment

### Using Docker Compose
Run all services in a production-like configuration:
```bash
docker compose -f docker-compose-prod.yml up
```

### Using Kubernetes (Minikube)
Start Minikube:
```bash
minikube start
```
Load the images into minikube (given that you've built them through docker compose above, if not, do that first through `docker compose -f docker-compose-prod.yml build`):
```bash
minikube image load wwapi:latest
minikube image load wwweb:latest
```
Apply infrastructure configurations:
```bash
kubectl apply -f persistent-volumes.yml
kubectl apply -f persistent-volume-claims.yml
kubectl apply -f zookeeper.yml
kubectl apply -f kafka.yml
kubectl apply -f postgres.yml
kubectl apply -f jobs/kafka-topic-init.yml
kubectl apply -f api.yml
kubectl apply -f www.yml
```
Use port forwarding so that both www and the api can be reached inside the cluster:
```bash
kubectl port-forward service/api 8080:8080
```
Open www:
```bash
minikube service www
```
