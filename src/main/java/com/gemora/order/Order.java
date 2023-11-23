package com.gemora.order;

import com.gemora.user.User;
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

    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderSimplifiedProduct> products;

    @ManyToOne
    private User user;

    @Column(name = "order_date_time")
    private LocalDateTime orderDateTime = LocalDateTime.now();

    @Column(name = "total_amount")
    private double totalAmount;

    @Embedded
    private ShippingDetails shippingDetails;
}
