package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class Helper {
    public static final String ENCODED_PASSWORD = "passwordEncoded";

    /** Gets a User with a Cart */
    public static User getUser() {
        User user = new User();
        user.setId(5l);
        user.setCart(new Cart());
        user.setUsername("NewUsername");
        user.setPassword("NewPassword");

        Cart cart = new Cart();
        cart.setId(5l);
        cart.setUser(user);
        cart.setTotal(BigDecimal.ZERO);

        user.setCart(cart);

        return user;
    }

    /** Gets Cart */
    public static Cart getCart() {
        User user = new User();
        user.setId(5l);
        user.setCart(new Cart());
        user.setUsername("NewUsername");
        user.setPassword("NewPassword");

        Cart cart = new Cart();
        cart.setId(5l);
        cart.setUser(user);
        cart.setTotal(BigDecimal.ZERO);

        user.setCart(cart);

        return cart;
    }

    /** Grabs a single Item */
    public static Item getItem() {
        Item item = new Item();
        item.setId(1l);
        item.setName("IPhone");
        item.setDescription("Apple Smartphone");
        item.setPrice(BigDecimal.valueOf(999.00));
        return item;
    }

    /** Grabs Multiple Items */
    public static List<Item> getItems() {
        Item item1 = new Item();
        item1.setId(1l);
        item1.setName("IPhone");
        item1.setDescription("Apple Smartphone");
        item1.setPrice(BigDecimal.valueOf(999.00));

        Item item2 = new Item();
        item2.setId(2l);
        item2.setName("Galaxy S20");
        item2.setDescription("Samsung Smartphone");
        item2.setPrice(BigDecimal.valueOf(1000.55));

        return Arrays.asList(item1, item2);
    }

    public static void injectObjects(Object target, String fieldName, Object toInject) {
        boolean wasPrivate = false;

        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            if (!f.isAccessible()) {
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);
            if (wasPrivate) {
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}