import argparse
from setup import *
import json
from sseclient import SSEClient as EventSource
from kafka import KafkaProducer
from kafka.errors import NoBrokersAvailable
import traceback
from operator import itemgetter

# topics = dict([(name, cls) for name, cls in setup.__dict__.items() if isinstance(cls, type) and "Topic" in name])


def parse_command_line_arguments():
    parser = argparse.ArgumentParser(description='EventStreams Kafka producer')

    parser.add_argument('--bootstrap_server', default='localhost:9092', help='Kafka bootstrap broker(s) (host[:port])', type=str)
    parser.add_argument('--topic_name', default='wikipedia-events', help='Destination topic name', type=str)
    parser.add_argument('--events_to_produce', help='Kill producer after n events have been produced', type=int, default=1000)

    return parser.parse_args()


def create_kafka_producer(bootstrap_server):
    try:
        producer = KafkaProducer(bootstrap_servers=bootstrap_server,
                                 value_serializer=lambda o: json.dumps(o.__dict__, sort_keys=True, indent=4).encode('utf-8'),
                                 max_request_size=100971520, compression_type='gzip', buffer_memory=200971520)
    except NoBrokersAvailable:
        print('No broker found at {}'.format(bootstrap_server))
        raise

    if producer.bootstrap_connected():
        print('Kafka producer connected!')
        return producer
    else:
        print('Failed to establish connection!')
        exit(1)


def activate_producer():
    producer = create_kafka_producer(BOOTSTRAP_SERVER)
    for topic_url in TOPIC_URLs.values():
        messages_count = 0
        for event in EventSource(topic_url):
            if event.event == 'message' and len(event.data) > 1:
                try:
                    event_id = json.loads(event.id)
                    event_data = json.loads(event.data)
                    event_to_send = WikiEvent(event_id, event_data, topic_url)
                    # if len(event_to_send.toJSON()) > 180:
                    #     continue
                    print(event_to_send)
                    producer.send(TOPIC_NAME, value=event_to_send)
                    
                except (ValueError, KeyError):
                    traceback.print_exc()
                else:
                    messages_count += 1

            if messages_count >= EVENTS_TO_PRODUCE:
                print('Producer will stop fetching from {0} as {1} events were producted'.format(topic_url, EVENTS_TO_PRODUCE))
                break


if __name__ == "__main__":
    args = vars(parse_command_line_arguments())
    BOOTSTRAP_SERVER, TOPIC_NAME, EVENTS_TO_PRODUCE = \
        itemgetter('bootstrap_server', 'topic_name', 'events_to_produce')(args)
    activate_producer()
