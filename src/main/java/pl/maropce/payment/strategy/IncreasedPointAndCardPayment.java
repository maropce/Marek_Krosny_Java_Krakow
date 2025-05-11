package pl.maropce.payment.strategy;

import pl.maropce.order.Order;
import pl.maropce.payment.PaymentMethod;

public class IncreasedPointAndCardPayment implements PaymentStrategy {

    private final PaymentMethod pointsPaymentMethod;
    private final PaymentMethod cardPaymentMethod;

    private final double pointsAmount;

    public IncreasedPointAndCardPayment(PaymentMethod pointsPaymentMethod, PaymentMethod cardPaymentMethod, double pointsAmount) {
        this.pointsPaymentMethod = pointsPaymentMethod;
        this.cardPaymentMethod = cardPaymentMethod;
        this.pointsAmount = pointsAmount;
    }

    @Override
    public void pay(Order order) {

        double cardAmount = order.getValue() * 0.9 - pointsAmount;
        if (pointsPaymentMethod.canPayFor(pointsAmount) && cardPaymentMethod.canPayFor(cardAmount)) {
            pointsPaymentMethod.pay(pointsAmount);
            cardPaymentMethod.pay(cardAmount);
        }
    }
}
