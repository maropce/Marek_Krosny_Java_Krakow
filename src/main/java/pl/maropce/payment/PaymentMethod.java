package pl.maropce.payment;

public class PaymentMethod  {

    public static final String POINTS_ID = "PUNKTY";

    private String id;
    private int discount;
    private double limit;
    private double usedLimit = 0.0;

    public PaymentMethod(String id, int discount, double limit) {
        this.id = id;
        this.discount = discount;
        this.limit = limit;
    }

    public PaymentMethod() {

    }

    public void pay(double value) {
        if (usedLimit + value > limit) {
            System.err.println("Limit exceeded");
            return;
        }
        usedLimit += value;
    }

    public boolean canPayFor(double value) {
        return usedLimit + value <= limit;
    }

    @Override
    public String toString() {
        return "PaymentMethod{" +
                "id='" + id + '\'' +
                ", discount=" + discount +
                ", limit=" + limit +
                ", usedLimit=" + usedLimit +
                '}';
    }

    public double getRemainingLimit() {
        return limit - usedLimit;
    }

    public boolean isPointsMethod() {
        return id.equals(POINTS_ID);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public double getUsedLimit() {
        return usedLimit;
    }

}
