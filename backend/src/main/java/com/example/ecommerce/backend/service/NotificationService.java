//package com.example.ecommerce.backend.service;
//
//import com.example.ecommerce.backend.model.Order;
//import com.example.ecommerce.backend.model.User;
//
//public interface NotificationService {
//
//    // Email notifications
//    void sendWelcomeEmail(User user);
//    void sendOrderConfirmationEmail(Order order);
//    void sendOrderStatusUpdateEmail(Order order);
//    void sendPaymentConfirmationEmail(Order order);
//    void sendPasswordResetEmail(User user, String resetToken);
//    void sendLowStockAlert(Long productId, Integer currentStock);
//
//    // SMS notifications (optional)
//    void sendOrderUpdateSMS(Order order, String message);
//
//    // Admin notifications
//    void notifyNewOrder(Order order);
//    void notifyLowStock();
//    void notifySystemError(String error, String details);
//}