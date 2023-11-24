package io.streamnative.pulsarbeerfactory.website;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
public class BeerStockController {
    BeerStockRepository beerStockRepository;
    BeerStockConsumer beerStockConsumer;
    private Logger log = LoggerFactory.getLogger(BeerStockController.class);

    public BeerStockController(BeerStockRepository beerStockRepository, BeerStockConsumer consumer) {
        this.beerStockRepository = beerStockRepository;
        this.beerStockConsumer = consumer;
    }

    @PostMapping("/beer_stocks")
    public void putBeerStock(@RequestBody BeerStock beerStock) {
        beerStockRepository.save(beerStock);
    }

    @GetMapping  ("/beer_stocks")
    public Iterable<BeerStock> getBeerStocks() throws Exception {
        pauseUntilMessageBurstConsumed();
        return beerStockRepository.findAll();
    }

    private void pauseUntilMessageBurstConsumed() throws InterruptedException {
        // Waits until 300ms after the last message of the messages burst was consumed.
        // The goal is to prevent invalid responses when the controller is called while 
        // messages are being read.
        // That assumes the messages burst takes less than 300ms to be consumed.
        // This is a hack to make the demo more stable without introducing too much code complexity.
        int MESSAGE_BURST_DURATION_IN_MS = 300;
        var now = Instant.now();
        var latest = beerStockConsumer.messageReceivedAt.orElse(Instant.MIN);
        var limit = latest.plusMillis(MESSAGE_BURST_DURATION_IN_MS);
        var pause = 0L;
        if(now.isBefore(limit)) {
            pause = Duration.between(now, limit).toMillis(); 
        }
        log.debug("Now: {}, Latest: {}, Limit: {}, Pause: {}", now, latest, limit, pause);
        Thread.sleep(pause);
    }
}
