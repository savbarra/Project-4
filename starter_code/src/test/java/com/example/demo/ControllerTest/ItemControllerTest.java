package com.example.demo.ControllerTest;

import com.example.demo.Helper;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    @Autowired
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void init(){
        this.itemController = new ItemController();
        Helper.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void givenValidItemList_whenItemsExist_thenFindItemByName(){
        //Given
        Item item = Helper.getItem();
        List<Item> itemsFound = Arrays.asList(item);

        //When
        when( itemRepository.findByName(item.getName())).thenReturn(itemsFound);
        ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());
        List<Item> itemsFoundByName = response.getBody();

        //Then
        Assert.assertArrayEquals(itemsFound.toArray(), itemsFoundByName.toArray());
    }

    @Test
    public void givenValidItem_whenItemExist_thenFindItemById(){
        //Given
        Item item = Helper.getItem();
        Optional<Item> optionalItem = Optional.of(item);

        //When
        when(itemRepository.findById(item.getId())).thenReturn(optionalItem);
        ResponseEntity<Item> response = itemController.getItemById(item.getId());
        Item itemFoundById = response.getBody();

        //Then
        Assert.assertEquals(item.getName(), itemFoundById.getName());
        Assert.assertEquals(item.getDescription(), itemFoundById.getDescription());
        Assert.assertEquals(item.getPrice(), itemFoundById.getPrice());
    }

    @Test
    public void givenValidItem_whenItemExist_thenListAllItems(){
        //Given
        List<Item> itemList = Helper.getItems();

        //When
        when(itemRepository.findAll()).thenReturn(itemList);
        ResponseEntity<List<Item>> response = itemController.getItems();
        List<Item> itemsInList = response.getBody();

        //Then
        Assert.assertArrayEquals(itemList.toArray(), itemsInList.toArray());
    }
}
