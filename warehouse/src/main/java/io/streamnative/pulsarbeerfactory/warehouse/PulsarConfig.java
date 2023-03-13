package io.streamnative.pulsarbeerfactory.warehouse;

import jakarta.annotation.PostConstruct;
import org.apache.pulsar.client.api.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.core.PulsarTemplate;

@Configuration
public class PulsarConfig {

    @Autowired
    private PulsarTemplate<BeerStock> pulsarTemplate;

    @PostConstruct
    void setupTopicSchema() {
        pulsarTemplate.setSchema(Schema.JSON(BeerStock.class));
    }
}
