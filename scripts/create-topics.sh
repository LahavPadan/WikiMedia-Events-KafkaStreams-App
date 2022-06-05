echo "Waiting for Kafka to come online..."

cub kafka-ready -b kafka:9092 1 20

# create the game events topic
kafka-topics \
  --bootstrap-server kafka:9092 \
  --topic WikiEvents \
  --replication-factor 1 \
  --partitions 2 \
  --create

sleep infinity