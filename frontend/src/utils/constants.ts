export const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Authentication
export const AUTH_ENDPOINTS = {
  LOGIN: '/auth/signin',
  REGISTER: '/auth/signup',
  REFRESH: '/auth/refresh-token',
  LOGOUT: '/auth/signout'
} as const;

// API Endpoints
export const API_ENDPOINTS = {
  // User endpoints
  USERS: {
    PROFILE: '/users/profile',
    UPDATE_PROFILE: '/users/profile',
    CHANGE_PASSWORD: '/users/change-password',
    ALL: '/users/all',
    SEARCH: '/users/search',
    ACTIVATE: (userId: number) => `/users/${userId}/activate`,
    DEACTIVATE: (userId: number) => `/users/${userId}/deactivate`
  },
  
  // Product endpoints
  PRODUCTS: {
    PUBLIC: '/products/public',
    BY_ID: (id: number) => `/products/public/${id}`,
    SEARCH: '/products/public/search',
    FILTER: '/products/public/filter',
    CATEGORIES: '/products/public/categories',
    BRANDS: '/products/public/brands',
    MANAGE: '/products/manage',
    CREATE: '/products',
    UPDATE: (id: number) => `/products/${id}`,
    DELETE: (id: number) => `/products/${id}`,
    UPDATE_STOCK: (id: number) => `/products/${id}/stock`,
    ACTIVATE: (id: number) => `/products/${id}/activate`,
    DEACTIVATE: (id: number) => `/products/${id}/deactivate`
  },

  // Cart endpoints
  CART: {
    GET: '/cart',
    ADD: '/cart/add',
    UPDATE: (productId: number) => `/cart/update/${productId}`,
    REMOVE: (productId: number) => `/cart/remove/${productId}`,
    CLEAR: '/cart/clear',
    COUNT: '/cart/count',
    VALIDATE: '/cart/validate'
  },

  // Order endpoints
  ORDERS: {
    CREATE: '/orders',
    MY_ORDERS: '/orders/my-orders',
    BY_NUMBER: (orderNumber: string) => `/orders/${orderNumber}`,
    CANCEL: (orderId: number) => `/orders/${orderId}/cancel`,
    MANAGE: '/orders/manage',
    SEARCH: '/orders/search',
    UPDATE_STATUS: (orderId: number) => `/orders/${orderId}/status`,
    UPDATE_PAYMENT: (orderId: number) => `/orders/${orderId}/payment-status`,
    FILTER: '/orders/filter',
    STATS: '/orders/stats'
  },

  // Inventory endpoints
  INVENTORY: {
    GET: '/inventory',
    BY_PRODUCT: (productId: number) => `/inventory/product/${productId}`,
    LOW_STOCK: '/inventory/low-stock',
    OUT_OF_STOCK: '/inventory/out-of-stock',
    UPDATE_STOCK: (productId: number) => `/inventory/product/${productId}/stock`,
    ADD_STOCK: (productId: number) => `/inventory/product/${productId}/add-stock`,
    REORDER_LEVEL: (productId: number) => `/inventory/product/${productId}/reorder-level`,
    STATS: '/inventory/stats'
  },

  // Role-specific endpoints
  SELLER: {
    DASHBOARD: '/seller/dashboard',
    PRODUCTS: '/seller/products',
    CREATE_PRODUCT: '/seller/products',
    ANALYTICS: '/seller/analytics',
    ORDERS: '/seller/orders',
    INVENTORY: '/seller/inventory'
  },

  ADMIN: {
    DASHBOARD: '/admin/dashboard',
    ANALYTICS: {
      REVENUE: '/admin/analytics/revenue',
      TOP_PRODUCTS: '/admin/analytics/top-products',
      TOP_CUSTOMERS: '/admin/analytics/top-customers',
      ORDERS: '/admin/analytics/orders',
      USERS: '/admin/analytics/users',
      INVENTORY: '/admin/analytics/inventory'
    },
    NOTIFICATIONS: {
      TEST_EMAIL: '/admin/notifications/test-email',
      LOW_STOCK: '/admin/notifications/low-stock-alert'
    },
    SYSTEM: {
      HEALTH: '/admin/system/health',
      BACKUP: '/admin/system/backup',
      CLEANUP: '/admin/system/maintenance/cleanup'
    }
  },

  SUPPORT: {
    DASHBOARD: '/support/dashboard',
    CUSTOMERS: '/support/customers',
    CUSTOMER_DETAILS: (userId: number) => `/support/customers/${userId}`,
    CUSTOMER_ORDERS: (userId: number) => `/support/customers/${userId}/orders`,
    ORDERS: '/support/orders',
    PENDING_ORDERS: '/support/orders/pending',
    URGENT_ORDERS: '/support/orders/urgent'
  },

  // File upload
  FILES: {
    UPLOAD_PRODUCT_IMAGE: '/files/upload/product-image',
    UPLOAD_AVATAR: '/files/upload/avatar',
    DELETE: (publicId: string) => `/files/delete/${publicId}`,
    TRANSFORM: (publicId: string) => `/files/transform/${publicId}`
  },

  // Search
  SEARCH: {
    PRODUCTS: '/search/products',
    SUGGESTIONS: '/search/suggestions',
    FILTERS: '/search/filters',
    TRENDING: '/search/trending',
    POPULAR: '/search/popular'
  },

  // Health check
  HEALTH: '/health'
} as const;

// User Roles
export const USER_ROLES = {
  CUSTOMER: 'CUSTOMER',
  SELLER: 'SELLER',
  SUPPORT: 'SUPPORT', 
  ADMIN: 'ADMIN'
} as const;

export type UserRole = typeof USER_ROLES[keyof typeof USER_ROLES];

// Order Status
export const ORDER_STATUS = {
  PENDING: 'PENDING',
  CONFIRMED: 'CONFIRMED',
  PROCESSING: 'PROCESSING',
  SHIPPED: 'SHIPPED',
  DELIVERED: 'DELIVERED',
  CANCELLED: 'CANCELLED',
  REFUNDED: 'REFUNDED'
} as const;

export type OrderStatus = typeof ORDER_STATUS[keyof typeof ORDER_STATUS];

// Payment Status
export const PAYMENT_STATUS = {
  PENDING: 'PENDING',
  PAID: 'PAID',
  FAILED: 'FAILED',
  REFUNDED: 'REFUNDED',
  PARTIALLY_REFUNDED: 'PARTIALLY_REFUNDED'
} as const;

export type PaymentStatus = typeof PAYMENT_STATUS[keyof typeof PAYMENT_STATUS];

// Local Storage Keys
export const STORAGE_KEYS = {
  ACCESS_TOKEN: 'access_token',
  REFRESH_TOKEN: 'refresh_token',
  USER_DATA: 'user_data',
  CART_DATA: 'cart_data',
  THEME: 'theme',
  LANGUAGE: 'language'
} as const;

// Routes
export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  PRODUCTS: '/products',
  PRODUCT_DETAIL: (id: number) => `/products/${id}`,
  CART: '/cart',
  CHECKOUT: '/checkout',
  ORDERS: '/orders',
  ORDER_DETAIL: (orderNumber: string) => `/orders/${orderNumber}`,
  PROFILE: '/profile',
  
  // Customer routes
  CUSTOMER: {
    ORDERS: '/customer/orders',
    PROFILE: '/customer/profile'
  },

  // Seller routes  
  SELLER: {
    DASHBOARD: '/seller/dashboard',
    PRODUCTS: '/seller/products',
    ADD_PRODUCT: '/seller/products/add',
    EDIT_PRODUCT: (id: number) => `/seller/products/edit/${id}`,
    ORDERS: '/seller/orders',
    INVENTORY: '/seller/inventory',
    ANALYTICS: '/seller/analytics'
  },

  // Admin routes
  ADMIN: {
    DASHBOARD: '/admin/dashboard',
    USERS: '/admin/users',
    PRODUCTS: '/admin/products',
    ORDERS: '/admin/orders',
    ANALYTICS: '/admin/analytics',
    SETTINGS: '/admin/settings'
  },

  // Support routes
  SUPPORT: {
    DASHBOARD: '/support/dashboard',
    CUSTOMERS: '/support/customers',
    TICKETS: '/support/tickets',
    ORDERS: '/support/orders'
  },

  // Error routes
  NOT_FOUND: '/404',
  UNAUTHORIZED: '/unauthorized',
  SERVER_ERROR: '/500'
} as const;

// Pagination
export const PAGINATION = {
  DEFAULT_PAGE: 0,
  DEFAULT_SIZE: 12,
  DEFAULT_SIZE_ADMIN: 10,
  MAX_SIZE: 50
} as const;

// File Upload
export const FILE_UPLOAD = {
  MAX_SIZE: 10 * 1024 * 1024, // 10MB
  ALLOWED_TYPES: ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'],
  MAX_FILES: 10
} as const;

// Product Categories (you can expand this)
export const PRODUCT_CATEGORIES = [
  'Electronics',
  'Fashion',
  'Home & Kitchen',
  'Books',
  'Sports & Outdoors',
  'Health & Beauty',
  'Toys & Games',
  'Automotive',
  'Garden & Outdoors',
  'Office Products'
] as const;

// Price Ranges for filtering
export const PRICE_RANGES = [
  { label: 'Under $25', min: 0, max: 25 },
  { label: '$25 - $50', min: 25, max: 50 },
  { label: '$50 - $100', min: 50, max: 100 },
  { label: '$100 - $250', min: 100, max: 250 },
  { label: '$250 - $500', min: 250, max: 500 },
  { label: 'Over $500', min: 500, max: 999999 }
] as const;

// Sort Options
export const SORT_OPTIONS = [
  { label: 'Newest First', value: 'createdAt', direction: 'desc' },
  { label: 'Price: Low to High', value: 'price', direction: 'asc' },
  { label: 'Price: High to Low', value: 'price', direction: 'desc' },
  { label: 'Best Selling', value: 'popularity', direction: 'desc' },
  { label: 'Customer Rating', value: 'rating', direction: 'desc' }
] as const;

// Validation Rules
export const VALIDATION_RULES = {
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  PHONE: /^\+?[\d\s-()]+$/,
  PASSWORD_MIN_LENGTH: 6,
  NAME_MIN_LENGTH: 2,
  NAME_MAX_LENGTH: 100,
  SKU_PATTERN: /^[A-Z]{3,4}-[A-Z0-9]{3,10}-[0-9]{3}$/
} as const;

// Error Messages
export const ERROR_MESSAGES = {
  REQUIRED: 'This field is required',
  INVALID_EMAIL: 'Please enter a valid email address',
  INVALID_PHONE: 'Please enter a valid phone number',
  PASSWORD_TOO_SHORT: `Password must be at least ${VALIDATION_RULES.PASSWORD_MIN_LENGTH} characters`,
  NAME_TOO_SHORT: `Name must be at least ${VALIDATION_RULES.NAME_MIN_LENGTH} characters`,
  NAME_TOO_LONG: `Name must not exceed ${VALIDATION_RULES.NAME_MAX_LENGTH} characters`,
  NETWORK_ERROR: 'Network error. Please check your connection.',
  SERVER_ERROR: 'Server error. Please try again later.',
  UNAUTHORIZED: 'You are not authorized to perform this action.',
  FORBIDDEN: 'Access denied.',
  NOT_FOUND: 'The requested resource was not found.',
  VALIDATION_FAILED: 'Please check your input and try again.'
} as const;

// Success Messages
export const SUCCESS_MESSAGES = {
  LOGIN: 'Successfully logged in!',
  LOGOUT: 'Successfully logged out!',
  REGISTER: 'Account created successfully!',
  PROFILE_UPDATED: 'Profile updated successfully!',
  PASSWORD_CHANGED: 'Password changed successfully!',
  PRODUCT_ADDED: 'Product added successfully!',
  PRODUCT_UPDATED: 'Product updated successfully!',
  PRODUCT_DELETED: 'Product deleted successfully!',
  CART_UPDATED: 'Cart updated successfully!',
  ORDER_PLACED: 'Order placed successfully!',
  ORDER_CANCELLED: 'Order cancelled successfully!'
} as const;

// Theme
export const THEME = {
  LIGHT: 'light',
  DARK: 'dark'
} as const;

export type Theme = typeof THEME[keyof typeof THEME];