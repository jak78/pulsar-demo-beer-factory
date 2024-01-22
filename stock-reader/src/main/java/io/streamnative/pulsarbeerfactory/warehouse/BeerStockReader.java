package io.streamnative.pulsarbeerfactory.warehouse;

import org.apache.pulsar.client.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.pulsar.annotation.PulsarReader;
import org.springframework.stereotype.Service;

@Service
public class BeerStockReader {
    
    Logger log = LoggerFactory.getLogger(BeerStockReader.class);
    
    @PulsarReader(topics = "beer-stocks-topic", startMessageId = "earliest")
    public void read(Message<BeerStock> beerStock) {
        BeerStock value = beerStock.getValue();
        log.info("beer stock for {}: {}", value.beerName, value.stockLevel);
    }
    
}
