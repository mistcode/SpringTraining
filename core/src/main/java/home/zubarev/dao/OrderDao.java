package home.zubarev.dao;

import home.zubarev.model.Order;

import java.util.List;

public interface OrderDao {
    Long save (Order order);

    List<Order> getOrders();

    void updateOrder(Long orderId, String status);
}
