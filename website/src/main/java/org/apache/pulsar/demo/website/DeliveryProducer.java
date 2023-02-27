package org.apache.pulsar.demo.website;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DeliveryProducer {

    PulsarTemplate<String> producer;

    ObjectMapper objectMapper;
    
    AtomicInteger sequenceNumber = new AtomicInteger();
    
    Logger log = LoggerFactory.getLogger(DeliveryProducer.class);

    public DeliveryProducer(PulsarTemplate<String> producer, ObjectMapper objectMapper) {
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 1_000)
    public void sendDeliveryOrder() throws PulsarClientException, JsonProcessingException {
        String jsonPayment = generateDeliveryOrder();
        producer.send("delivery-orders-topic", jsonPayment);
//        log.info("Sent delivery order: {}", jsonPayment);
    }

    private String generateDeliveryOrder() throws JsonProcessingException {
        int seq = sequenceNumber.addAndGet(1);
        String now = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        int articlesNumber = new Random().nextInt(1, 4);
        String address = pickAddress();
        DeliveryOrder deliveryOrder = new DeliveryOrder(seq, now, articlesNumber, address);
        return objectMapper.writeValueAsString(deliveryOrder);
    }

    private String pickAddress() {
        int index = new Random().nextInt(0, ADDRESSES.length-1);
        return ADDRESSES[index];
    }

    private static final String[] ADDRESSES = new String[]{
            "2 RUE DE L ECOLE 56250 SAINT-NOLFF               ",
            "24 RUE DE QUIMPER 59450 SIN LE NOBLE             ",
            "2 ALLEE PIERRE SERVEL 56000 VANNES               ",
            "24 AVENUE PAUL CLAUDEL 59510 HEM                 ",
            "62 CHEMIN DE LA CROIX DE PIERRE 76640 HATTENVILLE",
            "41 RUE HENRI PLOYART 59260 HELLEMMES LILLE       ",
            "77 ALLEE DE LA CROIX CABOT 76160 PREAUX          ",
            "16 RUE EUGENE BOULY 59400 CAMBRAI                ",
            "68 RUE ALFRED DORVILLERS 59171 ERRE              ",
            "64 RUE LOUIS AUGUSTE BLANQUI SP 59140 DUNKERQUE  "
    };
}
