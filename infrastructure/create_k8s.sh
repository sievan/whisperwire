kubectl apply -f k8s/persistent-volumes.yml
kubectl apply -f k8s/persistent-volume-claims.yml
kubectl apply -f k8s/zookeeper.yml
kubectl apply -f k8s/kafka.yml
kubectl apply -f k8s/jobs/kafka-topic-init.yml
kubectl apply -f k8s/postgres.yml
kubectl apply -f k8s/api.yml
kubectl apply -f k8s/www.yml
