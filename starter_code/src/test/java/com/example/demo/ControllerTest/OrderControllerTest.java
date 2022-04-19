package com.example.demo.ControllerTest;

import com.example.demo.Helper;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    @Autowired
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void init(){
        this.orderController = new OrderController();
        Helper.injectObjects(orderController, "userRepository", userRepository);
        Helper.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void givenOrder_whenUserInfoValid_thenSubmitOrder(){
        //Given
        User user = Helper.getUser();
        List<Item> itemsOnCart = Helper.getItems();
        Cart userCart = user.getCart();

        for(Item item : itemsOnCart){
            userCart.addItem(item);
            userCart.getTotal().add(item.getPrice());
        }

        user.setCart(userCart);
        userCart.setUser(user);

        //When
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        UserOrder userOrder = response.getBody();

        //Then
        Assert.assertArrayEquals(userCart.getItems().toArray(), userOrder.getItems().toArray());
        Assert.assertEquals(userCart.getTotal(), userOrder.getTotal());
    }

    @Test
    public void givenOrder_whenUserInfoValid_thenRetrieveOrderHistory(){
        //Given
        User user = Helper.getUser();
        List<Item> items = Helper.getItems();

        UserOrder orderToRetrieve = new UserOrder();

        orderToRetrieve.setItems(items);
        orderToRetrieve.setTotal(BigDecimal.valueOf(999.00));
        orderToRetrieve.setUser(user);
        orderToRetrieve.setId(2l);

        //When
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList( orderToRetrieve));
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        List<UserOrder> userOrders = response.getBody();

        //Then
        Assert.assertArrayEquals(orderToRetrieve.getItems().toArray(), userOrders.get(0).getItems().toArray());
        Assert.assertEquals(orderToRetrieve.getTotal(), userOrders.get(0).getTotal());
        Assert.assertEquals(orderToRetrieve.getUser().getUsername(), userOrders.get(0).getUser().getUsername());
    }

    @Test
    public void givenOrder_whenUserInfoInvalid_thenThrowNotFound(){
        //Given
        User user = Helper.getUser();
        List<Item> itemsOnCart = Helper.getItems();
        Cart userCart = user.getCart();

        for(Item item : itemsOnCart){
            userCart.addItem(item);
            userCart.getTotal().add(item.getPrice());
        }

        user.setCart(userCart);
        userCart.setUser(user);

        //When
        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        UserOrder userOrder = response.getBody();

        //Then
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void givenGetOrder_whenUserInfoInvalid_thenThrowNotFound(){
        //Given
        User user = Helper.getUser();
        List<Item> items = Helper.getItems();

        UserOrder orderToRetrieve = new UserOrder();

        orderToRetrieve.setItems(items);
        orderToRetrieve.setTotal(BigDecimal.valueOf(999.00));
        orderToRetrieve.setUser(user);
        orderToRetrieve.setId(2l);

        //When
        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList( orderToRetrieve));
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());

        //Then
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
