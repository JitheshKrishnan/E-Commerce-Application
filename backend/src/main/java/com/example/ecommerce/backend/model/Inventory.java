package com.example.ecommerce.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventory",
        indexes = {
                @Index(name = "idx_inventory_product", columnList = "product_id", unique = true),
                @Index(name = "idx_inventory_updated", columnList = "updated_at")
        }
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"product"})
public class Inventory {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_inventory_product"))
    private Product product;

    @Min(value = 0, message = "Available quantity cannot be negative")
    @Column(name = "qty_available", nullable = false)
    private Integer qtyAvailable = 0;

    @Min(value = 0, message = "Reserved quantity cannot be negative")
    @Column(name = "qty_reserved", nullable = false)
    private Integer qtyReserved = 0;

    @Min(value = 0, message = "Reorder level cannot be negative")
    @Column(name = "reorder_level")
    private Integer reorderLevel = 10;

    @Column(name = "warehouse_location", length = 100)
    private String warehouseLocation;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public Integer getAvailableForSale() {
        return qtyAvailable - qtyReserved;
    }

    public boolean isLowStock() {
        return qtyAvailable <= reorderLevel;
    }

    public boolean canFulfillOrder(Integer requestedQuantity) {
        return getAvailableForSale() >= requestedQuantity;
    }
}