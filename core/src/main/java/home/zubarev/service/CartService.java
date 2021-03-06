package home.zubarev.service;

import home.zubarev.dao.PhoneDao;
import home.zubarev.model.Cart;
import home.zubarev.model.CartItem;
import home.zubarev.model.DeliveryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartService {
    @Autowired
    private PhoneDao phoneDao;
    @Autowired
    private Cart cart;

    public PhoneDao getPhoneDao() {
        return phoneDao;
    }
    public void setPhoneDao(PhoneDao phoneDao) {
        this.phoneDao = phoneDao;
    }
    public Cart getCart() {
        return cart;
    }
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Long getTotalQuantity(){
        return cart.getTotalQuantity();
    }

    public BigDecimal getCartPrice(){
        return cart.getTotalPrice();
    }

    public void calculateTotalQuantity(){
        Long quantity = 0L;
        List<CartItem> cartItems = cart.getCartItems();
        for (CartItem cartItem : cartItems) {
            quantity += cartItem.getQuantity();
        }
        cart.setTotalQuantity(quantity);
    }

    public void calculateCartPrice(){
        BigDecimal cartPrice = BigDecimal.ZERO;
        List<CartItem> cartItems = cart.getCartItems();
        for (CartItem cartItem : cartItems) {
            Long quantity = cartItem.getQuantity();
            BigDecimal price = cartItem.getPhone().getPrice();
            cartPrice = cartPrice.add(price.multiply(BigDecimal.valueOf(quantity)));
        }
        cart.setTotalPrice(cartPrice);
    }

    public BigDecimal getTotalPrice(){
        return cart.getTotalPrice().add(cart.getDeliveryInfo().getDeliveryPrice());
    }

    public boolean deleteCartItem(Long id){
        List<CartItem> cartItems = cart.getCartItems();
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getId().equals(id)){
                cartItems.remove(i);
                calculateCartPrice();
                calculateTotalQuantity();
                return true;
            }
        }
        return false;
    }

    public void addItemToCart(Long id, Long quantity) {
        List<CartItem> cartItems = cart.getCartItems();
        boolean isExist = false;
        int index = 0;
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getId().equals(id)){
                isExist = true;
                index = i;
            }
        }
        CartItem cartItem = null;
        if (isExist){
            cartItem = cartItems.get(index);
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }else {
            cartItem = createCartItem(id, quantity);
            cartItems.add(cartItem);
        }
        calculateCartPrice();
        calculateTotalQuantity();
    }

    // Map<cart item id, current quantity>
    public void updateCart(Map<Long, Long> cartItemsModifier){
        List<CartItem> cartItems = cart.getCartItems();
        for (Map.Entry<Long, Long> entry : cartItemsModifier.entrySet()) {
            for (int i = 0; i < cartItems.size(); i++) {
                if (cartItems.get(i).getId().equals(entry.getKey())){
                    if (entry.getValue().equals(0)){
                        cartItems.remove(i);
                        break;
                    }else {
                        cartItems.get(i).setQuantity(entry.getValue());
                    }
                }
            }
        }
        calculateCartPrice();
        calculateTotalQuantity();
    }

    public CartItem createCartItem(Long id, Long quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setPhone(phoneDao.getPhone(id));
        cartItem.setQuantity(quantity);
        return cartItem;
    }

    public void setDeliveryInfo(String firstName, String lastName, String address, String phone, String comment) {
        DeliveryInfo deliveryInfo = cart.getDeliveryInfo();
        deliveryInfo.setFirstName(firstName);
        deliveryInfo.setLastName(lastName);
        deliveryInfo.setDeliveryAddress(address);
        deliveryInfo.setContactPhone(phone);
        deliveryInfo.setComment(comment);
    }

    public void clearCart() {
        cart.getCartItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setTotalQuantity(0L);
    }
}
