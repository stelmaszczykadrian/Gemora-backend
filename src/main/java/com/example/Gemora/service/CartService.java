package com.example.Gemora.service;

import com.example.Gemora.model.Cart;
import com.example.Gemora.model.Product;
import com.example.Gemora.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public void addToCart(Cart cart, Product product) {
        cart.addProduct(product);
        cartRepository.save(cart);
    }

    public void removeFromCart(Cart cart, Product product) {
        cart.removeProduct(product);
        cartRepository.save(cart);
    }

    public void clearCart(Cart cart) {
        cart.clearCart();
        cartRepository.save(cart);
    }

    public List<Product> getCartProducts(Cart cart) {
        return cart.getProducts();
    }

    public double getCartTotalPrice(Cart cart) {
        return cart.getTotalPrice();
    }
}
