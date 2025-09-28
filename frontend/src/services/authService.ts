import { apiUtils } from './apiClient';
import { 
  API_ENDPOINTS,
  AUTH_ENDPOINTS, 
  STORAGE_KEYS 
} from '../utils/constants';
import type { 
  LoginRequest, 
  RegisterRequest, 
  JwtResponse, 
  AuthUser,
  TokenRefreshResponse
} from '../types';

class AuthService {
  private static instance: AuthService;

  static getInstance(): AuthService {
    if (!AuthService.instance) {
      AuthService.instance = new AuthService();
    }
    return AuthService.instance;
  }

  // Login user
  async login(credentials: LoginRequest): Promise<AuthUser> {
    try {
      const response: JwtResponse = await apiUtils.post(
        AUTH_ENDPOINTS.LOGIN, 
        credentials
      );

      const { token, refreshToken, id, name, email, roles } = response;

      const user: AuthUser = {
        id, name, email, role: roles[0]
      }

      // Store tokens and user data
      this.setAuthData(token, refreshToken, user);

      return user;
    } catch (error) {
      throw this.handleAuthError(error);
    }
  }

  // Register new user
  async register(userData: RegisterRequest): Promise<AuthUser> {
    try {
      const response: JwtResponse = await apiUtils.post(
        AUTH_ENDPOINTS.REGISTER, 
        userData
      );

      const { token, refreshToken, id, name, email, roles } = response;

      const user: AuthUser = {
        id, name, email, role: roles[0]
      }

      // Store tokens and user data
      this.setAuthData(token, refreshToken, user);

      return user;
    } catch (error) {
      throw this.handleAuthError(error);
    }
  }

  // Logout user
  async logout(): Promise<void> {
    try {
      // Call logout endpoint to invalidate tokens on server
      await apiUtils.post(AUTH_ENDPOINTS.LOGOUT);
    } catch (error) {
      // Even if server call fails, we should clear local data
      console.error('Logout error:', error);
    } finally {
      this.clearAuthData();
    }
  }

  // Refresh access token
  async refreshToken(): Promise<string> {
    try {
      const refreshToken = this.getRefreshToken();
      if (!refreshToken) {
        throw new Error('No refresh token available');
      }

      const response: TokenRefreshResponse = await apiUtils.post(
        AUTH_ENDPOINTS.REFRESH,
        { refreshToken }
      );

      const { accessToken, refreshToken: newRefreshToken } = response;

      // Update stored tokens
      this.setAuthData(accessToken, newRefreshToken);

      return accessToken;
    } catch (error) {
      this.clearAuthData();
      throw this.handleAuthError(error);
    }
  }

  // Get current user from localStorage
  getCurrentUser(): AuthUser | null {
    try {
      const userData = localStorage.getItem(STORAGE_KEYS.USER_DATA);
      return userData ? JSON.parse(userData) : null;
    } catch (error) {
      console.error('Error parsing user data:', error);
      return null;
    }
  }

  // Check if user is authenticated
  isAuthenticated(): boolean {
    const accessToken = this.getAccessToken();
    const user = this.getCurrentUser();
    
    if (!accessToken || !user) {
      return false;
    }

    // Check if token is expired
    return !this.isTokenExpired(accessToken);
  }

  // Get access token
  getAccessToken(): string | null {
    return localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
  }

  // Get refresh token
  getRefreshToken(): string | null {
    return localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN);
  }

  // Check if token is expired
  isTokenExpired(token: string): boolean {
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      
      // Add 5 minute buffer before expiry
      return payload.exp < (currentTime + 300);
    } catch (error) {
      console.error('Error checking token expiry:', error);
      return true;
    }
  }

  // Check if user has specific role
  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user?.role === role;
  }

  // Check if user has any of the specified roles
  hasAnyRole(roles: string[]): boolean {
    const user = this.getCurrentUser();
    return user ? roles.includes(user.role) : false;
  }

  // Update user data in localStorage
  updateUserData(userData: Partial<AuthUser>): void {
    const currentUser = this.getCurrentUser();
    if (currentUser) {
      const updatedUser = { ...currentUser, ...userData };
      localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(updatedUser));
    }
  }

  // Private helper methods
  private setAuthData(accessToken: string, refreshToken: string, user?: AuthUser): void {
    localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken);
    localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
    if(user){
      localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(user));
    }
  }

  private clearAuthData(): void {
    localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.REFRESH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER_DATA);
    localStorage.removeItem(STORAGE_KEYS.CART_DATA);
  }

  private handleAuthError(error: any): Error {
    if (error.response?.data?.message) {
      return new Error(error.response.data.message);
    }
    
    if (error.message) {
      return new Error(error.message);
    }

    return new Error('Authentication failed. Please try again.');
  }

  // Token validation utilities
  async validateToken(): Promise<boolean> {
    try {
      const token = this.getAccessToken();
      if (!token || this.isTokenExpired(token)) {
        // Try to refresh token
        await this.refreshToken();
        return true;
      }
      return true;
    } catch (error) {
      console.error('Token validation failed:', error);
      return false;
    }
  }

  // Initialize auth state on app start
  async initializeAuth(): Promise<AuthUser | null> {
    try {
      const user = this.getCurrentUser();
      const token = this.getAccessToken();

      if (!user || !token) {
        return null;
      }

      // Validate token
      const isValid = await this.validateToken();
      if (!isValid) {
        this.clearAuthData();
        return null;
      }

      return user;
    } catch (error) {
      console.error('Auth initialization failed:', error);
      this.clearAuthData();
      return null;
    }
  }

  // Password utilities (if needed for change password feature)
  async changePassword(oldPassword: string, newPassword: string): Promise<void> {
    try {
      await apiUtils.post(API_ENDPOINTS.USERS.CHANGE_PASSWORD, {
        oldPassword,
        newPassword
      });
    } catch (error) {
      throw this.handleAuthError(error);
    }
  }

  // TODO: Forgot password
  // async forgotPassword(email: string): Promise<void> {
  //   try {
  //     await apiUtils.post('/auth/forgot-password', { email });
  //   } catch (error) {
  //     throw this.handleAuthError(error);
  //   }
  // }

  // TODO: Reset password
  // async resetPassword(token: string, newPassword: string): Promise<void> {
  //   try {
  //     await apiUtils.post('/auth/reset-password', { token, newPassword });
  //   } catch (error) {
  //     throw this.handleAuthError(error);
  //   }
  // }
}

// Export singleton instance
export const authService = AuthService.getInstance();
export default authService;