# Goals

This demo aims to illustrate the Pulsar subscription modes through a code walkthrough and a demo with [Spring Boot Pulsar](https://docs.spring.io/spring-pulsar/docs/current-SNAPSHOT/reference/html/).

# Prerequisites

- Maven 3.8.7 or higher
- JDK 17 or higher
- Docker

# Background

You run a Belgian beer reseller company. You sell beers online and deliver them to your customers. As your online store is very successful, you need scalability. You also want to use one single platform to handle both messaging & streaming use cases. This is why you have chosen [Pulsar](https://pulsar.apache.org/). 

# Use-case #1: messaging

<img src="messaging.png" width="300px">

When a beer order is placed, the Website sends it to the Delivery service. 

This service is responsible for delivering the order to the customer’s home. 

Delivery service is not designed to handle a website's performance and availability requirements. We need a message queue to decouple Delivery from the Website.

Website publishes delivery orders to a `delivery-orders-topic` topic. Here is what a delivery order looks like:

```json
{
  "parcelNumber": 41,
  "timestamp": "2023-03-10T10:54:07.574714Z",
  "articlesNumber": 2,
  "address": "16 RUE EUGENE BOULY 59400 CAMBRAI"
}
```

Website & Delivery are Spring Boot microservices.

In this demo, the Website service sends a random delivery order every 200ms to the topic. 

The addresses list is a random sample of the [French national addresses open database](https://adresse.data.gouv.fr/).

## One instance

### Step 1: build & launch the Delivery service

In a terminal, run the following commands:

```bash
cd delivery
mvn package
java -jar target/delivery*jar
```

### Step 2: build & launch the Website service

In another terminal, run the following commands:

```bash
cd website
docker compose up -d # this will launch a standalone Pulsar and a Redis database
mvn package
java -jar target/website*jar
```

### Step 3: messages consumption

Go back to the Delivery service terminal.

You should now see 5 messages consumed per second:

```
2023-03-10T15:46:38.745+01:00  INFO 54419 --- [ntainer#0-0-C-1] o.a.p.demo.delivery.DeliveryConsumer     : **** Delivery order received **** {"parcelNumber":26,"timestamp":"2023-03-10T14:46:38.647528Z","articlesNumber":1,"address":"2 ALLEE PIERRE SERVEL 56000 VANNES               "}
2023-03-10T15:46:39.759+01:00  INFO 54419 --- [ntainer#0-0-C-1] o.a.p.demo.delivery.DeliveryConsumer     : **** Delivery order received **** {"parcelNumber":27,"timestamp":"2023-03-10T14:46:39.652370Z","articlesNumber":2,"address":"41 RUE HENRI PLOYART 59260 HELLEMMES LILLE       "}
```

## Scaling the consumption

Now, the Delivery service has to process a larger number of orders. So you need to be ready to scale out by running several instances of the Delivery service. 

Open two additional terminal windows and launch an additional instance of the Delivery service:

```bash
java -jar target/delivery*jar
```

You now have 3 Delivery service instances.

You should see the messages being delivered to all the Delivery services instances in a round-robin fashion. Pulsar balances the load among the consumers.

The **Shared** subscription enables this behavior.

## Walking through the code

### Consumer

You can the `DeliveryConsumer`class from the `delivery` module to see how the Delivery service consumes the messages. 

The `PulsarListener` annotation has specified a `Shared`subscription type.

For more information on how this annotation works, refer to the [Spring for Apache Pulsar](https://docs.spring.io/spring-pulsar/docs/current-SNAPSHOT/reference/html/) documentation. 

### Producer

You can open the `DeliveryProducer` class from the website module to see how the website service produces the messages. It leverages the `PulsarTemplate`API.

For more information, refer to the [Spring for Apache Pulsar](https://docs.spring.io/spring-pulsar/docs/current-SNAPSHOT/reference/html/) documentation.

# Use-case #2: streaming

![streaming](streaming.png)

The Website service exposes the current stock level of every Belgian beer through a `/beer_stock` REST endpoint.

The current stock level is stored in a Redis database.

When the stock level of a beer changes in the warehouse, the Warehouse service produces a message to a topic. This message contains the new stock level of the beer. For example, when the warehouse has been replenished with Moinette beer, then the stock level of the Moinette changes, and the Warehouse service produces a message.

The Website service reads these events to update the current stock level on Redis.

The Website has to be scalable, so you’ll run several instances of it in parallel.

<aside>
⚠️ Please note that this architecture is not realistic. But it is simple for the sake of the demo.

</aside>

Here is what the `BeerStock`event looks like:

```json
{"beerName":"Moinette","stockLevel":42}
```

## Getting the current stock level

### Step 1: launch the Redis database & Pulsar

```bash
cd website
docker compose up -d
```

### Step 2: build & launch the Warehouse service

In a terminal, run the following commands:

```bash
cd warehouse
mvn package
java -jar target/warehouse*jar
```

You now see in the terminal that the Warehouse service send a list of events to a topic every second:

```
Sent {"beerName":"Chimay","stockLevel":6}
Sent {"beerName":"Chouffe","stockLevel":2}
Sent {"beerName":"Chimay","stockLevel":4}
Sent {"beerName":"Moinette","stockLevel":43}
Sent {"beerName":"Karmeliet","stockLevel":140}
Sent {"beerName":"Moinette","stockLevel":41}
Sent {"beerName":"Moinette","stockLevel":39}
Sent {"beerName":"Moinette","stockLevel":42}
Sent {"beerName":"Karmeliet","stockLevel":141}
Sent {"beerName":"Karmeliet","stockLevel":142}
Sent {"beerName":"Chimay","stockLevel":1}
Sent {"beerName":"Chimay","stockLevel":0}
Sent {"beerName":"Chouffe","stockLevel":0}
```

### Step 3: build & launch the Website service

Kill any running Website service instance.

In another terminal, run the following commands:

```bash
cd website
mvn package
java -jar target/website*jar
```

In a third terminal, run a second instance of the Website service:

```bash
java -jar target/website*jar --server.port=9091
```

You will observe one consumer instance consuming the beer stock messages while the other consumes nothing.

This is because the subscription type here is **Failover**. Pulsar delivers the messages to only one consumer of the same subscription.

Try killing the first instance. Then Pulsar will deliver the messages to the other instance.

### Step 4: get the current stock level

Call the API REST endpoint. The URL is: `http://localhost:9090/beer_stocks`

You will get the following payload:

```json
[
  {
    "beerName": "Moinette",
    "stockLevel": 42
  },
  {
    "beerName": "Chouffe",
    "stockLevel": 0
  },
  {
    "beerName": "Chimay",
    "stockLevel": 0
  },
  {
    "beerName": "Karmeliet",
    "stockLevel": 142
  }
]
```

Remember the events sequence:

```
Sent {"beerName":"Chimay","stockLevel":6}
Sent {"beerName":"Chouffe","stockLevel":2}
Sent {"beerName":"Chimay","stockLevel":4}
Sent {"beerName":"Moinette","stockLevel":43}
Sent {"beerName":"Karmeliet","stockLevel":140}
Sent {"beerName":"Moinette","stockLevel":41}
Sent {"beerName":"Moinette","stockLevel":39}
Sent {"beerName":"Moinette","stockLevel":42}
Sent {"beerName":"Karmeliet","stockLevel":141}
Sent {"beerName":"Karmeliet","stockLevel":142}
Sent {"beerName":"Chimay","stockLevel":1}
Sent {"beerName":"Chimay","stockLevel":0}
Sent {"beerName":"Chouffe","stockLevel":0}
```

The last known stock level for Moinette is 42, 142 for Karmeliet & 0 for the others.

So the payload is right. Everything happened as expected.

## Scaling - attempt #1 - Shared subscription mode

Let’s say we need to handle many more events, and a single consumer instance cannot cope with more events. Then we’ll need to add more consumer instances. However, because of the failover mode, only one instance can consume the events within the same subscription.

We’ve seen in the previous section (Messaging) that the Shared subscription mode enables consumers to scale out. The load is balanced among several consumer instances.

Let’s try switching to a Shared subscription and see what will happen.

Open the `BeerStockConsumer` class in the `website` module.

Locate the subscription type:

```java
@PulsarListener(subscriptionName = "beer-stocks-sub", 
            topics = "beer-stocks-topic", 
            subscriptionType = SubscriptionType.Failover)
```

Replace `Failover` with `Shared`

Kill any running instance of the Website service.

Rebuild & launch the Website service:

```bash
cd website
mvn package
java -jar target/website*jar
```

In another terminal, launch a second instance of the Website service:

```bash
java -jar target/website*jar --server.port=9091
```

Wait a few seconds, then get the current stock level at  `http://localhost:9090/beer_stocks`

Run this several times.

You will get different payloads. Some of them will have **wrong** stock levels:

```json
[
  {
    "beerName": "Moinette",
    "stockLevel": 39 // Should be 42
  },
  {
    "beerName": "Chouffe",
    "stockLevel": 0
  },
  {
    "beerName": "Chimay",
    "stockLevel": 1 // Should be 0
  },
  {
    "beerName": "Karmeliet",
    "stockLevel": 142
  }
]
```

What happens?

If you carefully look at the logs, you may see that the messages are not consumed in the right order. So a consumer may save a stale stock level to the Redis database. 

This explains why sometimes the stock value is wrong.

## Scaling - attempt #2 - Key_Shared subscription mode

Open the `BeerStockConsumer` class in the `website` module.

Locate the subscription type:

```java
@PulsarListener(subscriptionName = "beer-stocks-sub", 
            topics = "beer-stocks-topic", 
            subscriptionType = SubscriptionType.Shared)
```

Replace `Shared` with `Key_Shared`

Kill any running instance of the Website service.

Rebuild & launch the Website service:

```bash
cd website
mvn package
java -jar target/website*jar
```

In another terminal, launch a second instance of the Website service:

```bash
java -jar target/website*jar --server.port=9091
```

Wait a few seconds, then get the current stock level at  `http://localhost:9090/beer_stocks`

Run this several times.

This time you’ll get the right stock levels every time:

```json
[
  {
    "beerName": "Moinette",
    "stockLevel": 42
  },
  {
    "beerName": "Chouffe",
    "stockLevel": 0
  },
  {
    "beerName": "Chimay",
    "stockLevel": 0
  },
  {
    "beerName": "Karmeliet",
    "stockLevel": 142
  }
]
```

What happens?

If you carefully look at the logs, you will see that Pulsar delivers the stock levels of the same beer to the same customer. So the messages are consumed in the right order for the same beer. Now, consumers always save the latest stock level to the Redis database. 

This explains why the stock value is right this time.

This works because the messages are produced using the `beerName` as the **key**.

## Producing messages with a key

Open the `BeerProducer` class from the `warehouse` module.

Locate the `produce` method.

We’re using the fluent API to customize the message production. Here we use the `beerName` as the message key.

```java
pulsarTemplate.newMessage(message)
                    .withMessageCustomizer(c -> c.key(b.getBeerName()))
                    .withTopic("beer-stocks-topic")
                    .send();
```

# What to do next

- [Reach out to me](https://streamnative.io/people/julien) to get more demos and move through your journey to Pulsar.
- Join the Pulsar Slack channel to reach out to the community.
- Subscribe to the [StreamNative Youtube channel](https://www.youtube.com/channel/UCywxUI5HlIyc0VEKYR4X9Pg) and [read our blog](https://streamnative.io/blog) to learn more about Pulsar.
- Try out [StreamNative’s managed Pulsar](https://streamnative.io/deployment/hosted).

