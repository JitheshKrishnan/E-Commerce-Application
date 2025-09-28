import { useAuthContext } from '../contexts/AuthContext';
import {API_ENDPOINTS, USER_ROLES} from '../utils/constants';
import type { LoginRequest, RegisterRequest } from '../types';

// Custom hook that provides auth functionality
export const useAuth = () => {
  const context = useAuthContext();

  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }

  const {
    user,
    isAuthenticated,
    isLoading,
    login,
    register,
    logout,
    updateUser,
    error,
    clearError,
    hasRole,
    hasAnyRole,
    refreshAuth,
  } = context;

  // Role checking utilities
  const isCustomer = () => hasRole(USER_ROLES.CUSTOMER);
  const isSeller = () => hasRole(USER_ROLES.SELLER);
  const isAdmin = () => hasRole(USER_ROLES.ADMIN);
  const isSupport = () => hasRole(USER_ROLES.SUPPORT);
  
  // Check if user is staff (admin or support)
  const isStaff = () => hasAnyRole([USER_ROLES.ADMIN, USER_ROLES.SUPPORT]);
  
  // Check if user can manage products (seller or admin)
  const canManageProducts = () => hasAnyRole([USER_ROLES.SELLER, USER_ROLES.ADMIN]);
  
  // Check if user can view analytics (seller, support, admin)
  const canViewAnalytics = () => hasAnyRole([
    USER_ROLES.SELLER, 
    USER_ROLES.SUPPORT, 
    USER_ROLES.ADMIN
  ]);

  // Safe login wrapper
  const handleLogin = async (credentials: LoginRequest): Promise<boolean> => {
    try {
      await login(credentials);
      return true;
    } catch (error) {
      console.error('Login failed:', error);
      return false;
    }
  };

  // Safe register wrapper
  const handleRegister = async (userData: RegisterRequest): Promise<boolean> => {
    try {
      await register(userData);
      return true;
    } catch (error) {
      console.error('Registration failed:', error);
      return false;
    }
  };

  // Safe logout wrapper
  const handleLogout = async (): Promise<void> => {
    try {
      await logout();
    } catch (error) {
      console.error('Logout error:', error);
      // Force logout even if server call fails
      window.location.href = '/';
    }
  };

  // Get user display name
  const getDisplayName = (): string => {
    if (!user) return 'Guest';
    return user.name || user.email || 'User';
  };

  // Get user initials for avatar
  const getUserInitials = (): string => {
    if (!user?.name) return 'U';
    return user.name
      .split(' ')
      .map(name => name.charAt(0))
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  // Get user role display name
  const getRoleDisplayName = (): string => {
    if (!user?.role) return '';
    
    switch (user.role) {
      case USER_ROLES.CUSTOMER:
        return 'Customer';
      case USER_ROLES.SELLER:
        return 'Seller';
      case USER_ROLES.ADMIN:
        return 'Administrator';
      case USER_ROLES.SUPPORT:
        return 'Support Agent';
      default:
        return user.role;
    }
  };

  // Get dashboard route based on user role
  const getDashboardRoute = (): string => {
    if (!user?.role) return '/';
    
    switch (user.role) {
      case USER_ROLES.CUSTOMER:
        return API_ENDPOINTS.USERS.PROFILE;
      case USER_ROLES.SELLER:
        return API_ENDPOINTS.SELLER.DASHBOARD;
      case USER_ROLES.ADMIN:
        return API_ENDPOINTS.ADMIN.DASHBOARD;
      case USER_ROLES.SUPPORT:
        return API_ENDPOINTS.SUPPORT.DASHBOARD;
      default:
        return '/';
    }
  };

  // Check if user profile is complete
  const isProfileComplete = (): boolean => {
    if (!user) return false;
    
    return !!(
      user.name &&
      user.email
    );
  };

  // Get required permissions for different actions
  // TODO: Verify routes with backend
  const canAccessRoute = (route: string): boolean => {
    if (!isAuthenticated) return false;

    // Public routes that require authentication
    if (route.startsWith('/profile') || route.startsWith('/orders')) {
      return true;
    }

    // Customer routes
    if (route.startsWith('/customer')) {
      return isCustomer();
    }

    // Seller routes
    if (route.startsWith('/seller')) {
      return isSeller();
    }

    // Admin routes
    if (route.startsWith('/admin')) {
      return isAdmin();
    }

    // Support routes
    if (route.startsWith('/support')) {
      return isSupport();
    }

    return true;
  };

  // Refresh user data
  const refreshUserData = async (): Promise<void> => {
    try {
      await refreshAuth();
    } catch (error) {
      console.error('Failed to refresh user data:', error);
    }
  };

  return {
    // User state
    user,
    isAuthenticated,
    isLoading,
    error,

    // Auth actions
    login: handleLogin,
    register: handleRegister,
    logout: handleLogout,
    updateUser,
    clearError,
    refreshUserData,

    // Role checking
    isCustomer,
    isSeller,
    isAdmin,
    isSupport,
    isStaff,
    canManageProducts,
    canViewAnalytics,
    hasRole,
    hasAnyRole,
    canAccessRoute,

    // User utilities
    getDisplayName,
    getUserInitials,
    getRoleDisplayName,
    getDashboardRoute,
    isProfileComplete,
  };
};

export default useAuth;