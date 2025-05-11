package pl.maropce;

import pl.maropce.order.Order;
import pl.maropce.order.OrderMapper;
import pl.maropce.order.OrderService;
import pl.maropce.payment.PaymentMethod;
import pl.maropce.payment.PaymentMethodMapper;
import pl.maropce.payment.PaymentMethodService;
import pl.maropce.wallet.ClientWallet;
import pl.maropce.wallet.WalletService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        if(args.length < 1) {
            System.out.println("You need to pass 2 paths to jsons files as arguments: [pathToOrders] [pathToPaymentMethods]");
            return;
        }

        String pathToOrders = args[0];
        String pathToPayments = args[1];

        if (!Files.exists(Path.of(pathToOrders))) {
            System.out.println("Cannot find file: " + pathToOrders);
            return;
        }
        if (!Files.exists(Path.of(pathToPayments))) {
            System.out.println("Cannot find file: " + pathToPayments);
            return;
        }

        ClientWallet clientWallet = new ClientWallet();

        PaymentMethodService paymentMethodService = new PaymentMethodService(new PaymentMethodMapper());
        List<PaymentMethod> paymentMethods = paymentMethodService.importPaymentMethods(pathToPayments);

        clientWallet.setPaymentMethods(paymentMethods);

        OrderService orderService = new OrderService(new OrderMapper());
        List<Order> orders = orderService.importOrders(pathToOrders);

        WalletService walletService = new WalletService(paymentMethodService);
        walletService.payForOrders(clientWallet, orders);

        clientWallet.getPaymentMethods().forEach(paymentMethod -> {
            System.out.println(paymentMethod.getId() + " " + paymentMethod.getUsedLimit());
        });

    }
}