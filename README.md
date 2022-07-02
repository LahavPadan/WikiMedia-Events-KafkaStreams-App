# WikiMedia Events Statistics Application
Gathering statistics from [WikiMedia EventStreams][EventStreams] using **Kafka Streams**. 
Exposing the statistics via a REST API (using Kafka-Streams Interactive-Queries).

[EventStreams]: https://stream.wikimedia.org/v2/ui/#/


## Query the API

**_NOTE:_** In the url, month can be {hour, week, month, year}. Likewise, 'English', which can be any language.

### countPagesCreated
- curl localhost:7000/api.wikiStats/month/per-language/countPagesCreated
- curl localhost:7000/api.wikiStats/month/per-userType/countPagesCreated

### countPagesModified
- curl localhost:7000/api.wikiStats/month/per-language/countPagesModified
- curl localhost:7000/api.wikiStats/month/per-userType/countPagesModified

### MostActiveUsers
- curl localhost:7000/api.wikiStats/month/per-language/English/mostActiveUsers 
- curl localhost:7000/api.wikiStats/month/per-userType/English/mostActiveUsers

### MostActivePages
- curl localhost:7000/api.wikiStats/month/per-language/English/mostActivePages 
- curl localhost:7000/api.wikiStats/month/per-userType/English/mostActivePages 

---
# Kafka Pipeline FlowChart
![kafkaFlowChart](https://user-images.githubusercontent.com/64014604/176992674-0ed659f4-fd33-4177-b383-8f56070668e4.png)

# Running Locally
The only dependency for running this project is [Docker Compose][docker].

[docker]: https://docs.docker.com/compose/install/

Start the application components, by running the following commands:

## Kafka Cluster
**_NOTE:_**  Make sure docker daemon is running.
```sh
# start the local Kafka cluster (references 'scipts/' for topic creation)
$ docker-compose up

$ docker-compose exec kafka bash
$ kafka-configs --bootstrap-server localhost:9092 --alter --entity-type topics --entity-name WikiEvents --add-config max.message.bytes=100485880
```

## Kafka Producer (Python)
```sh
$ cd ./wikipedia-statistics/src/main/python
# activate virtual env
$ source ./venv/Scripts/activate
$ # (only do it once) pip install -r requirements.txt
$ export PYTHONPATH="$(pwd)/venv/Lib/site-packages"
# activate WikiMedia's EventStreams Kafka-producer
$ python3 ./wikiEventProducer.py --bootstrap_server localhost:29092 --topic_name WikiEvents --events_to_produce 10
```

## Kafka-Streams Client (Java)
**_NOTE:_**  **On Windows OS use gradlew.bat. On Linux OS use gradlew**
Now, to run the Kafka Streams application, simply run:

```sh
$ cd ./wikipedia-statistics

# build project
$ ./gradlew build

# run project
$ ./gradlew run --info

```
---
# Useful Resources
These have been a huge help for me:
- The book [Mastering Kafka Streams and ksqlDB][book] by Mitch Seymour.
- Introduction to Apache Kafka with Wikipedia’s EventStreams service article on [Medium][medium].

[book]: https://www.kafka-streams-book.com/
[medium]: https://towardsdatascience.com/introduction-to-apache-kafka-with-wikipedias-eventstreams-service-d06d4628e8d9
