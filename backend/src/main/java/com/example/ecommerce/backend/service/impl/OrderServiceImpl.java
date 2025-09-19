package com.example.ecommerce.backend.service.impl;

import com.example.ecommerce.backend.model.*;
import com.example.ecommerce.backend.repository.OrderRepository;
import com.example.ecommerce.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderItemService orderItemService;

    private static final AtomicLong orderCounter = new AtomicLong();

    @Override
    public Order createOrderFromCart(Long userId, String shippingAddress, String paymentMethod) {
        // Validate user
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Get cart items
        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Validate cart
        if (!cartService.validateCartForCheckout(userId)) {
            throw new RuntimeException("Cart validation failed. Some items may be out of stock.");
        }

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(generateOrderNumber());
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            BigDecimal itemTotal = cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }

        BigDecimal totalWeight = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            BigDecimal itemWeight = cartItem.getProduct().getWeight().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalWeight = totalWeight.add(itemWeight);
        }

        BigDecimal taxAmount = calculateTax(subtotal);
        BigDecimal shippingCost = calculateShipping(totalWeight); // Default weight
        BigDecimal totalPrice = subtotal.add(taxAmount).add(shippingCost);

        order.setTaxAmount(taxAmount);
        order.setShippingCost(shippingCost);
        order.setTotalPrice(totalPrice);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Create order items from cart items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getProduct().getPrice());
            orderItem.setProductTitle(cartItem.getProduct().getTitle());
            orderItem.setProductSku(cartItem.getProduct().getSku());

            orderItemService.createOrderItem(orderItem);

            // Reserve inventory
            inventoryService.reserveStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        }

        // Clear cart
        cartService.clearCart(userId);

        return savedOrder;
    }

    @Override
    public Order createOrder(Order order) {
        if (order.getOrderNumber() == null) {
            order.setOrderNumber(generateOrderNumber());
        }
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public Order updateOrder(Order order) {
        if (!orderRepository.existsById(order.getId())) {
            throw new RuntimeException("Order not found with id: " + order.getId());
        }
        return orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!canCancelOrder(orderId)) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        // Release reserved inventory
        for (OrderItem orderItem : order.getOrderItems()) {
            inventoryService.releaseReservedStock(orderItem.getProduct().getId(), orderItem.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Validate transition
        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Invalid transition from " + order.getStatus() + " to " + newStatus
            );
        }

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED) {
            for (OrderItem orderItem : order.getOrderItems()) {
                inventoryService.reduceStock(orderItem.getProduct().getId(), orderItem.getQuantity());
                inventoryService.releaseReservedStock(orderItem.getProduct().getId(), orderItem.getQuantity());
            }
        } else if (newStatus == OrderStatus.CANCELLED) {
            for (OrderItem orderItem : order.getOrderItems()) {
                inventoryService.releaseReservedStock(orderItem.getProduct().getId(), orderItem.getQuantity());
            }
        } else if (newStatus == OrderStatus.REFUNDED) {
            // Optional: only restock if item is returned
            // inventoryService.addStock(orderItem.getProduct().getId(), orderItem.getQuantity());
            //TODO: Implement When Upgrading Your Application
        }

        return orderRepository.save(order);
    }


    @Override
    public Order updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        Order order = getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setPaymentStatus(paymentStatus);

        // If payment is successful, confirm the order
        if (paymentStatus == PaymentStatus.PAID && order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CONFIRMED);
        }

        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> searchOrders(String searchTerm, Pageable pageable) {
        return orderRepository.searchOrders(searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrdersWithFilters(Long userId, OrderStatus status, PaymentStatus paymentStatus,
                                            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderRepository.findOrdersWithFilters(userId, status, paymentStatus, startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrdersBetweenDates(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenueBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = orderRepository.getTotalRevenueBetweenDates(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countOrdersByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageOrderValue() {
        BigDecimal average = orderRepository.getAverageOrderValue();
        return average != null ? average : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalSpentByUser(Long userId) {
        BigDecimal total = orderRepository.getTotalSpentByUser(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Object[]> getTopCustomersByRevenue(Pageable pageable) {
        return orderRepository.findTopCustomersByRevenue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateOrderTotal(Long orderId) {
        Order order = getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return order.calculateSubTotal().add(order.getTaxAmount()).add(order.getShippingCost());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTax(BigDecimal subtotal) {
        // Simple tax calculation - 8% tax rate
        BigDecimal taxRate = new BigDecimal("0.08");
        return subtotal.multiply(taxRate);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateShipping(BigDecimal weight) {
        // Simple shipping calculation based on weight
        BigDecimal baseShipping = new BigDecimal("5.00");
        BigDecimal perKgRate = new BigDecimal("2.00");
        return baseShipping.add(weight.multiply(perKgRate));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCancelOrder(Long orderId) {
        Order order = getOrderById(orderId).orElse(null);
        if (order == null) {
            return false;
        }

        // Can cancel if order is PENDING or CONFIRMED
        return order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidOrderForPayment(Long orderId) {
        Order order = getOrderById(orderId).orElse(null);
        return order != null && order.getStatus() == OrderStatus.PENDING &&
                order.getPaymentStatus() == PaymentStatus.PENDING;
    }

    @Override
    public String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long sequence = orderCounter.incrementAndGet();
        return "ORD-" + timestamp + "-" + String.format("%06d", sequence);
    }

    @Override
    public Long countOrdersByUserId(Long userId) {
        return orderRepository.countOrdersByUserId(userId);
    }
}