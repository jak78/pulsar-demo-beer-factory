package org.apache.pulsar.demo.warehouse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class BeerProducer {

    PulsarTemplate<String> pulsarTemplate;

    ObjectMapper json;
    
    Logger log = LoggerFactory.getLogger(BeerProducer.class);

    public BeerProducer(PulsarTemplate<String> pulsarTemplate, ObjectMapper json) {
        this.pulsarTemplate = pulsarTemplate;
        this.json = json;
    }

    @Scheduled(fixedRate = 5_000)
    public void produce() throws JsonProcessingException, PulsarClientException {
        for (BeerStock b : beerStocks()) {
            String message = json.writeValueAsString(b);
            pulsarTemplate.newMessage(message)
                    .withMessageCustomizer(c -> c.key(b.getBeerName()))
                    .withTopic("beer-stocks-topic")
                    .send();
            log.info("Sent {}", message);
        }
        log.info("--------------------------");
    }

    private Collection<BeerStock> beerStocks() {
        return List.of(
                new BeerStock("Chimay",      6),
                new BeerStock("Chouffe",     2),
                new BeerStock("Chimay",      4),
                new BeerStock("Moinette",   43),
                new BeerStock("Karmeliet", 140),
                new BeerStock("Moinette",   41),
                new BeerStock("Moinette",   39),
                new BeerStock("Moinette",   42),
                new BeerStock("Karmeliet", 141),
                new BeerStock("Karmeliet", 142),
                new BeerStock("Chimay",      1),
                new BeerStock("Chimay",      0),
                new BeerStock("Chouffe",     0)
        );
    }
}
