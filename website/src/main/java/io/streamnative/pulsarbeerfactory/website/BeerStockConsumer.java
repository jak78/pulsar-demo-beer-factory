package io.streamnative.pulsarbeerfactory.website;

import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

@Component
public class BeerStockConsumer {
    
    BeerStockRepository beerStockRepository;

    Logger log = LoggerFactory.getLogger(BeerStockConsumer.class);

    public BeerStockConsumer(BeerStockRepository beerStockRepository) {
        this.beerStockRepository = beerStockRepository;
    }

    @PulsarListener(subscriptionName = "beer-stocks-sub", 
            topics = "beer-stocks-topic", 
            subscriptionType = SubscriptionType.Failover,
            schemaType = SchemaType.AVRO)
    void listen(BeerStock beerStock) {
        log.info("**** Beer stock received **** {}", beerStock);
        beerStockRepository.save(beerStock);
    }
}
