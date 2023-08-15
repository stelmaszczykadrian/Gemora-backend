package com.example.Gemora.product;

import com.example.Gemora.cart.CartItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @ManyToOne
    private CartItem cart;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private double price;
    private String manufacturer;
    private String description;
    private String category;
    @Lob
    private byte[] image;
}
