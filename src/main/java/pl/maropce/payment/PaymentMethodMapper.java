package pl.maropce.payment;

import org.json.JSONObject;
import pl.maropce.order.Order;

import java.util.List;

public class PaymentMethodMapper {

    public PaymentMethod toPaymentMethod(JSONObject json) {
        PaymentMethod paymentMethod = new PaymentMethod();

        paymentMethod.setId(json.getString("id"));
        paymentMethod.setDiscount(json.getInt("discount"));
        paymentMethod.setLimit(json.getDouble("limit"));

        return paymentMethod;
    }
}
