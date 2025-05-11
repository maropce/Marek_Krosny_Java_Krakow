package pl.maropce.payment.strategy;

import pl.maropce.order.Order;

public interface PaymentStrategy {

    void pay(Order order);
}
