package pl.maropce.wallet;

import pl.maropce.order.Order;
import pl.maropce.payment.PaymentMethodService;
import pl.maropce.payment.strategy.PaymentStrategy;

import java.util.List;

public class WalletService {

    private final PaymentMethodService paymentService;

    public WalletService(PaymentMethodService paymentService) {
        this.paymentService = paymentService;
    }

    public void payForOrders(ClientWallet wallet, List<Order> orders) {

        orders = paymentService.sortByNumberOfPromotionsDesc(orders);

        for (Order order : orders) {
            PaymentStrategy bestStrategyForPayment = paymentService.findBestStrategyForPayment(wallet, order);

            if (bestStrategyForPayment != null) {
                bestStrategyForPayment.pay(order);
            }
        }
    }
}
