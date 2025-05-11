package pl.maropce.order;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final OrderMapper orderMapper;

    public OrderService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public List<Order> importOrders(String path) throws IOException {
        JSONArray json = new JSONArray(Files.readString(Path.of(path)));

        List<Order> orderList = new ArrayList<>();

        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            orderList.add(orderMapper.toOrder(obj));
        }

        return orderList;
    }
}
