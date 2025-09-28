// User-related types
export interface User {
  id: number;
  name: string;
  email: string;
  address?: string;
  phoneNumber?: string;
  role: string;
  isActive: boolean;
  createdAt: string;
  updatedAt?: string;
}

export interface AuthUser {
  id: number;
  name: string;
  email: string;
  role: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  phoneNumber?: string;
  address?: string;
}

export interface JwtResponse {
  token: string;
  type: string; 
  refreshToken: string;
  id: number;
  name: string;
  email: string;
  roles: string[];
}

export interface TokenRefreshResponse{
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}

// Product-related types
export interface Product {
  id: number;
  title: string;
  description?: string;
  price: number;
  qtyAvailable: number;
  category?: string;
  brand?: string;
  imageUrl?: string;
  sku?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt?: string;
}

export interface ProductCreateRequest {
  title: string;
  description?: string;
  price: number;
  qtyAvailable: number;
  category?: string;
  brand?: string;
  sku?: string;
}

export interface ProductUpdateRequest {
  title?: string;
  description?: string;
  price?: number;
  qtyAvailable?: number;
  category?: string;
  brand?: string;
  sku?: string;
}

// Cart-related types
export interface CartItem {
  id: number;
  productId: number;
  product: Product;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  createdAt: string;
}

export interface Cart {
  id: number;
  userId: number;
  cartItems: CartItem[];
  totalPrice: number;
  totalItems: number;
  createdAt: string;
  updatedAt: string;
}

export interface AddToCartRequest {
  productId: number;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}

// Order-related types
export interface OrderItem {
  id: number;
  productId: number;
  product: Product;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface Order {
  id: number;
  orderNumber: string;
  user: User;
  orderItems: OrderItem[];
  totalPrice: number;
  orderStatus: string;
  paymentStatus: string;
  shippingAddress?: string;
  notes?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface CreateOrderRequest {
  shippingAddress?: string;
  notes?: string;
}

export interface UpdateOrderStatusRequest {
  orderStatus: string;
}

export interface UpdatePaymentStatusRequest {
  paymentStatus: string;
}

// Inventory-related types
export interface InventoryItem {
  id: number;
  productId: number;
  product: Product;
  quantityAvailable: number;
  quantitySold: number;
  reorderLevel: number;
  lastRestocked: string;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateStockRequest {
  quantity: number;
}

export interface AddStockRequest {
  quantity: number;
}

export interface UpdateReorderLevelRequest {
  reorderLevel: number;
}

// Analytics-related types
export interface DashboardStats {
  totalRevenue: number;
  totalOrders: number;
  totalUsers: number;
  totalProducts: number;
}

export interface RevenueData {
  date: string;
  revenue: number;
}

export interface TopProduct {
  product: Product;
  totalSold: number;
  revenue: number;
}

export interface TopCustomer {
  user: User;
  totalOrders: number;
  totalSpent: number;
}

export interface SellerDashboardStats {
  totalRevenue: number;
  totalOrders: number;
  totalProducts: number;
  lowStockProducts: number;
}

export interface SupportDashboardStats {
  pendingOrders: number;
  urgentOrders: number;
  totalCustomers: number;
  activeTickets: number;
}

// API response types
export interface ApiResponse<T> {
  message: string;
  data: T;
  success: boolean;
  timestamp: number;
}

export interface ApiError {
  message: string;
  errors?: Record<string, string[]>;
  status: number;
  timestamp: number;
}

export interface PaginatedResponse<T> {
  message: string;
  data: {
    content: T[];
    pageable: {
      pageNumber: number;
      pageSize: number;
      sort: {
        sorted: boolean;
        unsorted: boolean;
      };
    };
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
    numberOfElements: number;
  };
  success: boolean;
  timestamp: number;
}

// Search and filter types
export interface ProductFilter {
  category?: string;
  brand?: string;
  minPrice?: number;
  maxPrice?: number;
  inStock?: boolean;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

export interface SearchRequest {
  query: string;
  page?: number;
  size?: number;
  filters?: ProductFilter;
}

// File upload types
export interface FileUploadResponse {
  fileName: string;
  fileUrl: string;
  publicId: string;
  format: string;
  size: number;
}

// Form types
export interface LoginFormData {
  email: string;
  password: string;
}

export interface RegisterFormData {
  name: string;
  email: string;
  password: string;
  confirmPassword: string;
  phoneNumber?: string;
  address?: string;
  role?: string;
}

export interface ProfileUpdateFormData {
  name: string;
  email: string;
  phoneNumber?: string;
  address?: string;
}

export interface ChangePasswordFormData {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ProductFormData {
  title: string;
  description?: string;
  price: number;
  qtyAvailable: number;
  category?: string;
  brand?: string;
  sku?: string;
  imageFiles?: FileList;
}

// Component prop types
export interface ButtonProps {
  children: React.ReactNode;
  type?: 'button' | 'submit' | 'reset';
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  disabled?: boolean;
  loading?: boolean;
  onClick?: () => void;
  className?: string;
}

export interface InputProps {
  label?: string;
  type?: string;
  placeholder?: string;
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onBlur?: (e: React.FocusEvent<HTMLInputElement>) => void;
  error?: string;
  disabled?: boolean;
  required?: boolean;
  className?: string;
}

export interface SelectProps {
  label?: string;
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLSelectElement>) => void;
  options: Array<{ value: string; label: string }>;
  placeholder?: string;
  error?: string;
  disabled?: boolean;
  required?: boolean;
  className?: string;
}

export interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  children: React.ReactNode;
  size?: 'sm' | 'md' | 'lg' | 'xl';
}

export interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  showFirstLast?: boolean;
  showPrevNext?: boolean;
  className?: string;
}

// Context types
export interface AuthContextType {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (userData: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  updateUser: (userData: Partial<AuthUser>) => void;
  error: string | null;
  clearError: () => void;
  hasRole: (role: string) => boolean;
  hasAnyRole: (roles: string[]) => boolean;
  refreshAuth: () => Promise<void>;
}

export interface CartContextType {
  cart: Cart | null;
  isLoading: boolean;
  addToCart: (productId: number, quantity: number) => Promise<void>;
  updateCartItem: (productId: number, quantity: number) => Promise<void>;
  removeFromCart: (productId: number) => Promise<void>;
  clearCart: () => Promise<void>;
  refreshCart: () => Promise<void>;
}

export interface ThemeContextType {
  theme: 'light' | 'dark';
  toggleTheme: () => void;
}

export interface NotificationContextType {
  notifications: Notification[];
  addNotification: (notification: Omit<Notification, 'id' | 'timestamp'>) => void;
  removeNotification: (id: string) => void;
  clearNotifications: () => void;
}

export interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message: string;
  timestamp: number;
  duration?: number;
}

// Route types
export interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: string;
  fallback?: React.ReactNode;
}

// Hook types
export interface UseApiResult<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
}

export interface UsePaginationResult {
  currentPage: number;
  totalPages: number;
  goToPage: (page: number) => void;
  goToNext: () => void;
  goToPrevious: () => void;
  canGoNext: boolean;
  canGoPrevious: boolean;
}

// Utility types
export type UserRole = 'CUSTOMER' | 'SELLER' | 'SUPPORT' | 'ADMIN';
export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'REFUNDED';
export type PaymentStatus = 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED' | 'PARTIALLY_REFUNDED';
export type Theme = 'light' | 'dark';
export type SortDirection = 'asc' | 'desc';

// Generic utility types
export type Optional<T, K extends keyof T> = Omit<T, K> & Partial<Pick<T, K>>;
export type RequiredFields<T, K extends keyof T> = T & Required<Pick<T, K>>;
export type Nullable<T> = T | null;
export type AsyncFunction<T = void> = () => Promise<T>;
export type EventHandler<T> = (event: T) => void;