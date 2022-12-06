package org.apache.pulsar.demo.website;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

@Component
public class BeerStockConsumer {
    
    BeerStockRepository beerStockRepository;

    ObjectMapper json;
    
    Logger log = LoggerFactory.getLogger(BeerStockConsumer.class);

    public BeerStockConsumer(BeerStockRepository beerStockRepository, ObjectMapper json) {
        this.beerStockRepository = beerStockRepository;
        this.json = json;
    }

    @PulsarListener(subscriptionName = "beer-stocks-sub", 
            topics = "beer-stocks-topic", 
            subscriptionType = SubscriptionType.Shared)
    void listen(String message) throws JsonProcessingException {
        log.info("**** Beer stock received **** " + message);
        BeerStock updatedBeerStock = json.readValue(message, BeerStock.class);
        beerStockRepository.save(updatedBeerStock);
    }
}
