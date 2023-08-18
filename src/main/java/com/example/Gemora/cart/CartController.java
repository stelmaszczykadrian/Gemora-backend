package com.example.Gemora.cart;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userEmail}")
    public List<CartItem> getCartItems(@PathVariable String userEmail) {
        return cartService.getCartItemsForUser(userEmail);
    }

    @PostMapping("/{userEmail}/{productId}/{quantity}")
    public void addToCart(@PathVariable String userEmail, @PathVariable Integer productId, @PathVariable int quantity) {
        cartService.addToCart(userEmail, productId, quantity);
    }


    @PostMapping("/updateQuantity")
    public void updateProductQuantity(@RequestBody UpdateQuantityRequest request) {
        cartService.updateQuantity(request.getProductId(), request.getNewQuantity());
    }

//    @PostMapping("/remove")
//    public ResponseEntity<Void> removeFromCart(@RequestBody CartItem cart, @RequestBody Product product) {
//        // TODO: Implement logic to remove product from the cart
//        return null;
//    }
//
//    @PostMapping("/clear")
//    public ResponseEntity<Void> clearCart(@RequestBody CartItem cart) {
//        // TODO: Implement logic to clear the cart
//        return null;
//    }


//    @GetMapping("/total-price")
//    public ResponseEntity<Double> getCartTotalPrice(@RequestBody CartItem cart) {
//        // TODO: Implement logic to calculate the total price of the cart
//        return null;
//    }
}
