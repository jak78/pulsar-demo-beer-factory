package io.streamnative.pulsarbeerfactory.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

import static org.apache.pulsar.client.api.SubscriptionType.Shared;
import static org.apache.pulsar.common.schema.SchemaType.JSON;

@Component
public class DeliveryConsumer {
    
    Logger log = LoggerFactory.getLogger(DeliveryConsumer.class);
    
    @PulsarListener(topics = "delivery-orders-topic",
            subscriptionName = "delivery-subscription",
            subscriptionType = Shared,
            schemaType = JSON)
    void listen(DeliveryOrder message) {
        log.info("**** Delivery order received **** {}", message);
    }
}
