package com.brokage.domain.entity;

import com.brokage.domain.valueobject.AssetSymbol;
import com.brokage.domain.valueobject.Quantity;
import com.brokage.domain.valueobject.UsableSize;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"customer_id", "asset_name"})
})
@Data
@EntityListeners(AuditingEntityListener.class)
public class Asset {

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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "size", precision = 19, scale = 2, nullable = false))
    })
    private Quantity size;

    @Embedded
    private UsableSize usableSize;

    public void reserveAmount(BigDecimal amount) {
        this.usableSize = this.usableSize.reserve(amount);
    }

    public void releaseAmount(BigDecimal amount) {
        this.usableSize = this.usableSize.release(amount);
    }

    public void addSize(Quantity amount) {
        this.size = this.size.add(amount);
        this.usableSize = this.usableSize.add(amount.getValue());
    }

    public void subtractSize(Quantity amount) {
        if (!this.size.isSufficient(amount)) {
            throw new IllegalStateException("Insufficient total size");
        }
        this.size = this.size.subtract(amount);
    }
}
