package pl.maropce.order;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public Order toOrder(JSONObject json) {
        Order order = new Order();

        order.setId(json.getString("id"));
        order.setValue(json.getDouble("value"));

        if (order.getValue() < 0) {
            throw new RuntimeException("Order value should be greater than zero!");
        }

        List<String> promotions;
        if (json.has("promotions")) {
             promotions = json.getJSONArray("promotions").toList()
                     .stream()
                     .map(Object::toString)
                     .toList();

        } else {
            promotions = new ArrayList<>();
        }

        order.setPromotions(promotions);
        return order;
    }

}
