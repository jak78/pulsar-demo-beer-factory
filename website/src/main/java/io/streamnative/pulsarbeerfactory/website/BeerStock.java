package io.streamnative.pulsarbeerfactory.website;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("BeerStock")
public class BeerStock {
    @Id
    String beerName;
    int stockLevel;

    public BeerStock(String beerName, int stockLevel) {
        this.beerName = beerName;
        this.stockLevel = stockLevel;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BeerStock{");
        sb.append("beerName='").append(beerName).append('\'');
        sb.append(", stockLevel=").append(stockLevel);
        sb.append('}');
        return sb.toString();
    }

    public String getBeerName() {
        return beerName;
    }

    public int getStockLevel() {
        return stockLevel;
    }
}
