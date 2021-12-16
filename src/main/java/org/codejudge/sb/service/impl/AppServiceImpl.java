package org.codejudge.sb.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.codejudge.sb.model.Cart;
import org.codejudge.sb.model.Command;
import org.codejudge.sb.model.Item;
import org.codejudge.sb.model.User;
import org.codejudge.sb.service.api.AppService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppServiceImpl implements AppService {

    private Set<Item> items;
    private Map<Item, Double> itemToPriceMap;
    private Map<Item, Integer> itemToQuantityMap;
    private Map<String, User> nameToUserMap;
    private Map<String, Cart> nameToCartMap;

    @Override
    public void initialize(String filename) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        String line = bufferedReader.readLine();
        while (line != null) {
            log.info("--------------------------------");
            String[] inputSplit = line.split(" ");
            Command command = Command.valueOf(inputSplit[0]);
            switch (command) {
                case AddItem:
                case UpdateItem:
                    addItem(inputSplit); break;
                case AddInventory: addInventory(inputSplit); break;
                case AddUser: addUser(inputSplit); break;
                case AddToCart: addToCart(inputSplit); break;
                case GetCart: getCartDetail(inputSplit); break;
                case SearchItem: searchItems(inputSplit); break;
                case Checkout: checkout(inputSplit); break;
                case exit: return;
            }
            line = bufferedReader.readLine();
        }
        
    }

    private void checkout(String[] inputSplit) {
        String name = inputSplit[1];
        Double price = getCartPrice(name);

        if (nameToUserMap.get(name).getWalletAmount() < price) {
            log.info("Cannot proceed, prices have been updated! Wallet amount is {}, price is {}", nameToUserMap.get(name).getWalletAmount(), price);
        }
        else {
            deductAmountFromCart(nameToUserMap.get(name), price);
            log.info("Amount deducted! Wallet amount is {}", nameToUserMap.get(name).getWalletAmount());
            nameToCartMap.put(name, new Cart(nameToUserMap.get(name)));
        }
    }

    private void searchItems(String[] inputSplit) {

        List<String> brands = Arrays.stream(inputSplit[1].split(",")).collect(Collectors.toList());
        List<String> categories = inputSplit[2].equals("") ? Lists.newArrayList() : Arrays.stream(inputSplit[2].split(",")).collect(Collectors.toList());

        for (String brand : brands) {
            for (String category : categories) {
                Item item = new Item(brand, category);
                log.info("{} -> {} -> {} -> {}", brand, category, itemToPriceMap.get(item), itemToQuantityMap.get(item));
            }
        }
    }

    private void getCartDetail(String[] inputSplit) {
        String user = inputSplit[1];
        Cart userCart = nameToCartMap.get(user);
        if (null == userCart) {
            log.info("Empty cart!");
        }
        else {
            for(Map.Entry<Item, Integer> entry : userCart.getItemToQuantityMap().entrySet()) {
                Item item = new Item(entry.getKey().getBrand(), entry.getKey().getCategory());
                log.info("{} -> {} -> {} -> {}", entry.getKey().getBrand(), entry.getKey().getCategory(), itemToPriceMap.get(item) * entry.getValue(), entry.getValue());
            }
        }
    }

    public void addItem(String[] commands) {

        String brand = commands[1];
        String category = commands[2];
        Double price = Double.parseDouble(commands[3]);

        Item newItem = new Item(brand, category, price);

        if(CollectionUtils.isEmpty(itemToPriceMap)) {
            itemToPriceMap = Maps.newHashMap();
        }
        itemToPriceMap.put(newItem, price);

    }

    public void addInventory(String[] commands) {

        String brand = commands[1];
        String category = commands[2];
        Integer quantity = Integer.parseInt(commands[3]);

        Item newItem = new Item(brand, category, quantity);

        if(CollectionUtils.isEmpty(itemToQuantityMap)) {
            itemToQuantityMap = Maps.newHashMap();
        }
        if (!itemToQuantityMap.containsKey(newItem)) {
            itemToQuantityMap.put(newItem, quantity);
        }
        else {
            Integer existingQuantity = itemToQuantityMap.get(newItem);
            itemToQuantityMap.put(newItem, quantity + existingQuantity);
        }
    }

    public void addUser(String[] commands) {

        String name = commands[1];
        String address = commands[2];
        Double walletAmt = Double.parseDouble(commands[3]);
        User user = new User(name, address, walletAmt);
        if (CollectionUtils.isEmpty(nameToUserMap)) {
            nameToUserMap = Maps.newHashMap();
        }
        nameToUserMap.put(user.getName(), user);
    }

    public void addToCart(String[] commands) {

        String name = commands[1];
        String category = commands[2];
        String brand = commands[3];
        Integer units = Integer.parseInt(commands[4]);

        if (CollectionUtils.isEmpty(nameToCartMap)) {
            nameToCartMap = Maps.newHashMap();
        }
        User user = nameToUserMap.get(name);
        if (!nameToCartMap.containsKey(user.getName())) {
            nameToCartMap.put(user.getName(), new Cart(user));
        }
        Cart userCart = nameToCartMap.get(user.getName());
        Item itemToAdd = new Item(brand, category);
        if (units > itemToQuantityMap.get(itemToAdd)) {
            log.info("ADDTOCART: Units more than inventory!!");
            return;
        }
//        Double totalPrice = units * itemToPriceMap.get(itemToAdd);

//        if(user.getWalletAmount() < totalPrice) {
//            log.info("Wallet amount less than total price!!");
//            return;
//        }
        Map<Item, Integer> userItemToQuantityMap = userCart.getItemToQuantityMap();
        if (!userItemToQuantityMap.containsKey(itemToAdd)) {
            userItemToQuantityMap.put(itemToAdd, units);
        }
        else {
            userItemToQuantityMap.put(itemToAdd, userItemToQuantityMap.get(itemToAdd) + units);
        }

//        deductAmountFromCart(user, totalPrice);
        itemToQuantityMap.put(itemToAdd, itemToQuantityMap.get(itemToAdd) - units);
    }

    public void deductAmountFromCart(User user, Double price) {
        Double remainingWalletAmount = user.getWalletAmount() - price;
        user.setWalletAmount(remainingWalletAmount);
    }

    public Double getCartPrice(String name) {
        Cart userCart = nameToCartMap.get(name);
        Double price = 0.0;
        if (null == userCart)
            return price;
        for(Map.Entry<Item, Integer> entry : userCart.getItemToQuantityMap().entrySet()) {
            Item item = new Item(entry.getKey().getBrand(), entry.getKey().getCategory());
            price += itemToPriceMap.get(item) * entry.getValue();
        }
        return price;

    }


}
