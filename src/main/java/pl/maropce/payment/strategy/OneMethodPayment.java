package pl.maropce.payment.strategy;

import pl.maropce.order.Order;
import pl.maropce.payment.PaymentMethod;

public class OneMethodPayment implements PaymentStrategy {

    private final PaymentMethod paymentMethod;

    public OneMethodPayment(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public void pay(Order order) {

        double value = order.calculateValueAfterDiscount(paymentMethod.getDiscount());
        if (paymentMethod.canPayFor(value)) {
            paymentMethod.pay(value);
        }
    }
}
