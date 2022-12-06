package org.apache.pulsar.demo.website;

import org.springframework.data.repository.CrudRepository;

public interface BeerStockRepository extends CrudRepository<BeerStock, String> {
}
