package com.example.Gemora.controller;

import com.example.Gemora.model.Cart;
import com.example.Gemora.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(@RequestBody Cart cart, @RequestBody Product product) {
        // TODO: Implement logic to add product to the cart
        return null;
    }

    @PostMapping("/remove")
    public ResponseEntity<Void> removeFromCart(@RequestBody Cart cart, @RequestBody Product product) {
        // TODO: Implement logic to remove product from the cart
        return null;
    }

    @PostMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestBody Cart cart) {
        // TODO: Implement logic to clear the cart
        return null;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getCartProducts(@RequestBody Cart cart) {
        // TODO: Implement logic to get products from the cart
        return null;
    }

    @GetMapping("/total-price")
    public ResponseEntity<Double> getCartTotalPrice(@RequestBody Cart cart) {
        // TODO: Implement logic to calculate the total price of the cart
        return null;
    }
}
