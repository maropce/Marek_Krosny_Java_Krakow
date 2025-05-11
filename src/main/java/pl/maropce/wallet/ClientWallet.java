package pl.maropce.wallet;

import pl.maropce.payment.PaymentMethod;

import java.util.List;

public class ClientWallet {
    private String id;
    private List<PaymentMethod> paymentMethods;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public PaymentMethod getPointsMethod() {
        return paymentMethods.stream()
                .filter(method -> method.getId().equals(PaymentMethod.POINTS_ID))
                .findFirst().orElse(new PaymentMethod(PaymentMethod.POINTS_ID, 0, 0));
    }
}
