package io.streamnative.pulsarbeerfactory.warehouse;

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

    PulsarTemplate<BeerStock> pulsarTemplate;

    Logger log = LoggerFactory.getLogger(BeerProducer.class);

    public BeerProducer(PulsarTemplate<BeerStock> pulsarTemplate) {
        this.pulsarTemplate = pulsarTemplate;
    }

    @Scheduled(fixedRate = 1_000)
    public void produce() throws PulsarClientException {
        for (BeerStock beerStock : beerStocks()) {
            pulsarTemplate.newMessage(beerStock)
                    .withMessageCustomizer(c -> c.key(beerStock.getBeerName()))
                    .withTopic("beer-stocks-topic")
                    .send();
            log.info("Sent {}", beerStock);
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
