package pl.maropce.payment.strategy;

import pl.maropce.order.Order;
import pl.maropce.payment.PaymentMethod;

public class PointAndCardPayment implements PaymentStrategy {

    private final PaymentMethod pointsPaymentMethod;
    private final PaymentMethod cardPaymentMethod;

    public PointAndCardPayment(PaymentMethod pointsPaymentMethod, PaymentMethod cardPaymentMethod) {
        this.pointsPaymentMethod = pointsPaymentMethod;
        this.cardPaymentMethod = cardPaymentMethod;
    }

    @Override
    public void pay(Order order) {
        if (!pointsPaymentMethod.isPointsMethod()) {
            System.err.println("First method must be PointsMethod!" + pointsPaymentMethod);
        }

        double fullValue = order.getValue();
        double payByPoints = fullValue * 0.1;
        double payByCard = fullValue * 0.9 - payByPoints;

        if (pointsPaymentMethod.canPayFor(payByPoints) && cardPaymentMethod.canPayFor(payByCard)) {
            pointsPaymentMethod.pay(payByPoints);
            cardPaymentMethod.pay(payByCard);
        }

    }
}
