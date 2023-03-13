package io.streamnative.pulsarbeerfactory.website;

import org.springframework.data.repository.CrudRepository;

public interface BeerStockRepository extends CrudRepository<BeerStock, String> {
}
