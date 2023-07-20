package com.example.Gemora.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany
    private List<Product> products;

    @ManyToOne
    private User user;

    @Column(name = "order_date_time")
    private LocalDateTime orderDateTime;

    @Column(name = "total_amount")
    private double totalAmount;
}
