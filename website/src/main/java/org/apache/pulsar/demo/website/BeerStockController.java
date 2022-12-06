package org.apache.pulsar.demo.website;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BeerStockController {
    BeerStockRepository beerStockRepository;

    public BeerStockController(BeerStockRepository beerStockRepository) {
        this.beerStockRepository = beerStockRepository;
    }

    @PostMapping("/beer_stocks")
    public void putBeerStock(@RequestBody BeerStock beerStock) {
        beerStockRepository.save(beerStock);
    }

    @GetMapping("/beer_stocks/{id}")
    public ResponseEntity<BeerStock> getBeerStock(@PathVariable String id) {
        return ResponseEntity.of(beerStockRepository.findById(id));
    }
    
    @GetMapping  ("/beer_stocks")
    public Iterable<BeerStock> getBeerStocks() {
         return beerStockRepository.findAll();
    }
}
