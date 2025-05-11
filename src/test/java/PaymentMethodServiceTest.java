import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.maropce.order.Order;
import pl.maropce.payment.PaymentMethod;
import pl.maropce.payment.PaymentMethodMapper;
import pl.maropce.payment.PaymentMethodService;
import pl.maropce.payment.strategy.PaymentStrategy;
import pl.maropce.wallet.ClientWallet;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentMethodServiceTest {


    private PaymentMethodService paymentMethodService;

    @Mock
    private Order mockOrder;

    @Mock
    private ClientWallet mockWallet;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        paymentMethodService = new PaymentMethodService(new PaymentMethodMapper());
    }

    @Test
    void testSortByNumberOfPromotionsDesc() {

        Order order1 = mock(Order.class);
        Order order2 = mock(Order.class);
        Order order3 = mock(Order.class);

        when(order1.getPromotions()).thenReturn(List.of("BosBankrut", "mZysk"));
        when(order2.getPromotions()).thenReturn(List.of());
        when(order3.getPromotions()).thenReturn(List.of("BosBankrut"));

        List<Order> mockOrders = List.of(
                order1,
                order2,
                order3
        );

        List<Order> orders = paymentMethodService.sortByNumberOfPromotionsDesc(mockOrders);

        assertEquals(List.of(order1, order3, order2), orders);
    }

    @Test
    void testFindBestStrategyForPayment() {

        Order order1 = new Order("ORDER1", 100, List.of("mZysk"));
        Order order2 = new Order("ORDER2", 200, List.of("BosBankrut"));
        Order order3 = new Order("ORDER3", 150, List.of("mZysk", "BosBankrut"));
        Order order4 = new Order("ORDER4", 50, List.of());

        List<Order> orders = List.of(order1, order2, order3, order4);

        PaymentMethod points = new PaymentMethod(PaymentMethod.POINTS_ID, 15, 100);
        PaymentMethod mZysk = new PaymentMethod("mZysk", 10, 180);
        PaymentMethod bosBankrut = new PaymentMethod("BosBankrut", 5, 200);

        List<PaymentMethod> paymentMethods = List.of(points, mZysk, bosBankrut);

        when(mockWallet.getPaymentMethods()).thenReturn(paymentMethods);
        when(mockWallet.getPointsMethod()).thenReturn(points);

        orders = paymentMethodService.sortByNumberOfPromotionsDesc(orders);
        for (Order order : orders) {
            PaymentStrategy bestStrategyForPayment = paymentMethodService.findBestStrategyForPayment(mockWallet, order);
            bestStrategyForPayment.pay(order);
        }

        StringBuilder result = new StringBuilder();
        for (PaymentMethod paymentMethod : paymentMethods) {
            result.append(paymentMethod.getId()).append(" ").append(paymentMethod.getUsedLimit()).append("\n");
        }

        String expectedResult = "PUNKTY 90.0\n" +
                "mZysk 175.0\n" +
                "BosBankrut 190.0\n";

        assertEquals(expectedResult, result.toString());
    }

}
