package org.apache.pulsar.demo.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

import static org.apache.pulsar.client.api.SubscriptionType.Shared;

@Component
public class DeliveryConsumer {
    
    Logger log = LoggerFactory.getLogger(DeliveryConsumer.class);
    
    @PulsarListener(subscriptionName = "delivery-subscription", topics = "delivery-orders-topic", subscriptionType = Shared)
    void listen(String message) {
        log.info("**** Delivery order received **** " + message);
    }
}
