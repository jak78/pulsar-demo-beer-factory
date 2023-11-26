package io.streamnative.pulsarbeerfactory.website;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BeerStockController {
    BeerStockRepository beerStockRepository;

    public BeerStockController(BeerStockRepository beerStockRepository) {
        this.beerStockRepository = beerStockRepository;
    }

    @GetMapping  ("/beer_stocks")
    public Iterable<BeerStock> getBeerStocks() {
         return beerStockRepository.findAll();
    }
}
