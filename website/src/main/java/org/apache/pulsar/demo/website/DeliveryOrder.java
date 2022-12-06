package org.apache.pulsar.demo.website;

public class DeliveryOrder {
    int parcelNumber;
    String timestamp;
    int articlesNumber;
    String address;

    public DeliveryOrder(int parcelNumber, String timestamp, int articlesNumber, String address) {
        this.parcelNumber = parcelNumber;
        this.timestamp = timestamp;
        this.articlesNumber = articlesNumber;
        this.address = address;
    }

    public int getParcelNumber() {
        return parcelNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getArticlesNumber() {
        return articlesNumber;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeliveryOrder{");
        sb.append("parcelNumber=").append(parcelNumber);
        sb.append(", timestamp='").append(timestamp).append('\'');
        sb.append(", articlesNumber=").append(articlesNumber);
        sb.append(", address='").append(address).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
