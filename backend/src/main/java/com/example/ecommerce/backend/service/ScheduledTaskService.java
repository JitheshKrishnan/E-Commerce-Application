package com.example.ecommerce.backend.service;

public interface ScheduledTaskService {

    // Cleanup tasks
    void cleanupExpiredCartItems();
    void cleanupOldSessions();

    // Inventory tasks
    void checkLowStockItems();
    void syncInventoryWithProducts();

    // Analytics tasks
    void generateDailyReport();
    void updateSearchIndexes();

    // Maintenance tasks
    void performDatabaseMaintenance();
    void archiveOldData();
}