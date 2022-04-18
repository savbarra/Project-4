package com.example.demo.ControllerTest;

import com.example.demo.Helper;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    @Autowired
    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void init(){
        this.cartController = new CartController();
        Helper.injectObjects(cartController, "userRepository", userRepository);
        Helper.injectObjects(cartController, "cartRepository", cartRepository);
        Helper.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void givenValidUser_whenItemsValid_thenAddToCart(){
        //Given
        Item itemToAdd = Helper.getItem();
        User user = Helper.getUser();
        ModifyCartRequest request = new ModifyCartRequest();

        request.setUsername(user.getUsername());
        request.setItemId(itemToAdd.getId());
        request.setQuantity(5);

        //When
        Optional<Item> optionalItem = Optional.of(itemToAdd);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(user);
        when(itemRepository.findById(request.getItemId())).thenReturn(optionalItem);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        Cart cart = response.getBody();

        //Then
        Assert.assertEquals(5, cart.getItems().size());
        Assert.assertEquals(itemToAdd.getName(), cart.getItems().get(0).getName());
    }

    @Test
    public void givenValidUser_whenCartValid_thenRemoveFromCart(){
        //Given
        List<Item> itemsOnCart = Helper.getItems();
        User user = Helper.getUser();
        Cart userCart = user.getCart();
        for(Item item: itemsOnCart){
            userCart.addItem( item );
            userCart.getTotal().add( item.getPrice() );
        }

        Item itemToRemove = itemsOnCart.get(0);
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername(user.getUsername());
        request.setItemId(itemToRemove.getId());
        request.setQuantity(1);

        Cart cartItemRemoved = new Cart();
        cartItemRemoved.setId(userCart.getId());
        cartItemRemoved.setTotal(userCart.getTotal());
        cartItemRemoved.setUser(user);
        cartItemRemoved.setItems(userCart.getItems());
        cartItemRemoved.setTotal(userCart.getTotal());

        cartItemRemoved.removeItem(itemToRemove);

        //When
        Optional<Item> optionalItem = Optional.of(itemToRemove);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(user);
        when(itemRepository.findById(request.getItemId())).thenReturn(optionalItem);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        Cart cart = response.getBody();

        //Then
        Assert.assertEquals(cartItemRemoved.getItems().size(), cart.getItems().size());
        Assert.assertArrayEquals(cartItemRemoved.getItems().toArray(), cart.getItems().toArray());
        Assert.assertEquals(cartItemRemoved.getTotal(), cart.getTotal());
    }

    @Test
    public void givenValidUser_whenItemsNotPresent_thenReturnNOTFOUND(){
        //Given
        User user = Helper.getUser();
        ModifyCartRequest request = new ModifyCartRequest();

        //When
        when(userRepository.findByUsername(request.getUsername())).thenReturn(user);

        //Then
        ResponseEntity<Cart> response = cartController.addTocart(request);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void givenValidUser_whenItemsaValid_thenAddToCart(){
        //Given
        Item itemToAdd = Helper.getItem();
        Cart cart = Helper.getCart();
        ModifyCartRequest request = new ModifyCartRequest();

        request.setItemId(itemToAdd.getId());
        request.setQuantity(5);

        //When
        Optional<Item> optionalItem = Optional.of(itemToAdd);
        when(itemRepository.findById(request.getItemId())).thenReturn(optionalItem);

        //Then
        ResponseEntity<Cart> response = cartController.addTocart(request);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}