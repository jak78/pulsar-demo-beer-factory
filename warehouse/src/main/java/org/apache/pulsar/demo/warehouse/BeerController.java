package org.apache.pulsar.demo.warehouse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BeerController {
    
    PulsarTemplate<String> pulsarTemplate;
    
    ObjectMapper json;

    public BeerController(PulsarTemplate<String> pulsarTemplate, ObjectMapper json) {
        this.pulsarTemplate = pulsarTemplate;
        this.json = json;
    }

    @PutMapping("/beer_stocks")
    public void putBeerStock(@RequestBody List<BeerStock> beerStocks) throws JsonProcessingException, PulsarClientException {
        for (BeerStock b : beerStocks) {
            String message = json.writeValueAsString(b);
            pulsarTemplate.newMessage(message)
                    .withMessageCustomizer(c -> c.key(b.getBeerName()))
                    .withTopic("beer-stocks-topic")
                    .send();
        }
    }
}
