package pl.maropce.order;

import java.util.List;

public class Order {
    private String id;
    private double value;
    private List<String> promotions;

    public Order(String id, double value, List<String> promotions) {
        this.id = id;
        this.value = value;
        this.promotions = promotions;
    }

    public Order() {

    }

    public String getId() {
        return id;
    }

    public double calculateValueAfterDiscount(int discount) {
        return value - (value / 100) * discount;
    }


    public void setId(String id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<String> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<String> promotions) {
        this.promotions = promotions;
    }
}
