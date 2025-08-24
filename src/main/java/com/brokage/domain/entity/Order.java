package com.brokage.domain.entity;

import com.brokage.domain.enums.OrderSide;
import com.brokage.domain.enums.OrderStatus;
import com.brokage.domain.valueobject.AssetSymbol;
import com.brokage.domain.valueobject.Money;
import com.brokage.domain.valueobject.Quantity;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Table(name = "orders")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Embedded
    private AssetSymbol assetSymbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_side", nullable = false)
    private OrderSide orderSide;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "size", precision = 19, scale = 2, nullable = false))
    })
    private Quantity size;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price", precision = 19, scale = 2, nullable = false))
    })
    private Money price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    public Money getTotalAmount() {
        return price.multiply(size);
    }

    public boolean canBeCanceled() {
        return status == OrderStatus.PENDING;
    }

    public void cancel() {
        if (!canBeCanceled()) {
            throw new IllegalStateException("Order cannot be canceled");
        }
        status = OrderStatus.CANCELED;
    }

    public void match() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be matched");
        }
        status = OrderStatus.MATCHED;
    }
}
