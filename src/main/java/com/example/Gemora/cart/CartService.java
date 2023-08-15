package com.example.Gemora.cart;

import com.example.Gemora.user.User;
import com.example.Gemora.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private CartItemRepository cartItemRepository;
    private UserRepository userRepository;


    public CartService(CartItemRepository cartItemRepository, UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    public List<CartItem> getCartItemsForUser(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent()) {
            return cartItemRepository.findByUserId(user.get().getId());
        } else {
            return Collections.emptyList();
        }
    }

    public void addToCart(String userEmail, Integer productId, int quantity) {

        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isPresent()) {
            Integer userId = user.get().getId();

            List<CartItem> existingItems = cartItemRepository.findByUserIdAndProductId(userId, productId);

            if (!existingItems.isEmpty()) {

                CartItem existingItem = existingItems.get(0);
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                cartItemRepository.save(existingItem);
            } else {
                CartItem newItem = new CartItem();
                newItem.setUserId(userId);
                newItem.setProductId(productId);
                newItem.setQuantity(quantity);
                cartItemRepository.save(newItem);
            }
        } else {
            // TODO // Handle the case when the user does not exist
            // For example, you can throw an exception or take another action
        }
    }


}
