package pl.maropce.payment.strategy;

import pl.maropce.order.Order;
import pl.maropce.payment.PaymentMethod;

public class NoPromotionPayment implements PaymentStrategy {

    private final PaymentMethod noPromotionMethod;

    public NoPromotionPayment(PaymentMethod noPromotionMethod) {
        this.noPromotionMethod = noPromotionMethod;
    }

    @Override
    public void pay(Order order) {
        if (noPromotionMethod.canPayFor(order.getValue())) {
            noPromotionMethod.pay(order.getValue());
        }
    }
}
