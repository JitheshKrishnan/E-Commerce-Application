package com.example.ecommerce.backend.repository;

import com.example.ecommerce.backend.model.Inventory;
import com.example.ecommerce.backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Basic queries
    Optional<Inventory> findByProduct(Product product);

    Optional<Inventory> findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    // Stock level queries
    @Query("SELECT i FROM Inventory i WHERE i.qtyAvailable <= i.reorderLevel")
    List<Inventory> findLowStockItems();

    @Query("SELECT i FROM Inventory i WHERE i.qtyAvailable = 0")
    List<Inventory> findOutOfStockItems();

    @Query("SELECT i FROM Inventory i WHERE i.qtyAvailable > 0")
    List<Inventory> findInStockItems();

    @Query("SELECT i FROM Inventory i WHERE i.qtyAvailable <= :threshold")
    List<Inventory> findItemsWithStockBelow(@Param("threshold") Integer threshold);

    // Pagination queries
    Page<Inventory> findByQtyAvailableLessThanEqual(Integer threshold, Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE i.qtyAvailable <= i.reorderLevel")
    Page<Inventory> findLowStockItems(Pageable pageable);

    // Reserved stock queries
    @Query("SELECT i FROM Inventory i WHERE i.qtyReserved > 0")
    List<Inventory> findItemsWithReservedStock();

    @Query("SELECT SUM(i.qtyReserved) FROM Inventory i WHERE i.product.id = :productId")
    Integer getTotalReservedByProductId(@Param("productId") Long productId);

    // Available stock calculations
    @Query("SELECT i FROM Inventory i WHERE (i.qtyAvailable - i.qtyReserved) >= :requiredQuantity")
    List<Inventory> findItemsWithAvailableQuantity(@Param("requiredQuantity") Integer requiredQuantity);

    @Query("SELECT (i.qtyAvailable - i.qtyReserved) FROM Inventory i WHERE i.product.id = :productId")
    Integer getAvailableQuantityByProductId(@Param("productId") Long productId);

    // Warehouse location queries
    List<Inventory> findByWarehouseLocation(String warehouseLocation);

    @Query("SELECT DISTINCT i.warehouseLocation FROM Inventory i WHERE i.warehouseLocation IS NOT NULL")
    List<String> findAllWarehouseLocations();

    // Stock update operations
    @Modifying
    @Query("UPDATE Inventory i SET i.qtyAvailable = :quantity, i.updatedAt = CURRENT_TIMESTAMP WHERE i.product.id = :productId")
    int updateStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Inventory i SET i.qtyAvailable = i.qtyAvailable + :quantity, i.updatedAt = CURRENT_TIMESTAMP WHERE i.product.id = :productId")
    int addStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Inventory i SET i.qtyAvailable = i.qtyAvailable - :quantity, i.updatedAt = CURRENT_TIMESTAMP WHERE i.product.id = :productId AND i.qtyAvailable >= :quantity")
    int reduceStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Inventory i SET i.qtyReserved = i.qtyReserved + :quantity, i.updatedAt = CURRENT_TIMESTAMP WHERE i.product.id = :productId")
    int reserveStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Inventory i SET i.qtyReserved = i.qtyReserved - :quantity, i.updatedAt = CURRENT_TIMESTAMP WHERE i.product.id = :productId AND i.qtyReserved >= :quantity")
    int releaseReservedStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying
    @Query("UPDATE Inventory i SET i.reorderLevel = :reorderLevel WHERE i.product.id = :productId")
    int updateReorderLevel(@Param("productId") Long productId, @Param("reorderLevel") Integer reorderLevel);

    // Analytics and reporting
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.qtyAvailable <= i.reorderLevel")
    Long countLowStockItems();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.qtyAvailable = 0")
    Long countOutOfStockItems();

    @Query("SELECT SUM(i.qtyAvailable) FROM Inventory i")
    Long getTotalInventoryQuantity();

    @Query("SELECT SUM(i.qtyReserved) FROM Inventory i")
    Long getTotalReservedQuantity();

    // Recent updates
    @Query("SELECT i FROM Inventory i WHERE i.updatedAt >= :since ORDER BY i.updatedAt DESC")
    List<Inventory> findRecentlyUpdatedInventory(@Param("since") LocalDateTime since);

    @Query("SELECT i FROM Inventory i ORDER BY i.updatedAt DESC")
    Page<Inventory> findRecentlyUpdatedInventory(Pageable pageable);

    // Stock movement tracking (useful for audit)
    @Query("SELECT i.product.id, i.product.title, i.qtyAvailable, i.qtyReserved, i.updatedAt " +
            "FROM Inventory i " +
            "ORDER BY i.updatedAt DESC")
    Page<Object[]> getInventorySnapshot(Pageable pageable);
}