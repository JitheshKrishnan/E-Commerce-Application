import React, { createContext, useContext, useReducer, useEffect } from 'react';
import { authService } from '../services/authService';
import { SUCCESS_MESSAGES } from '../utils/constants';
import type { 
  AuthUser, 
  LoginRequest, 
  RegisterRequest, 
  AuthContextType 
} from '../types';

// Auth state interface
interface AuthState {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

// Auth actions
type AuthAction =
  | { type: 'AUTH_START' }
  | { type: 'AUTH_SUCCESS'; payload: AuthUser }
  | { type: 'AUTH_ERROR'; payload: string }
  | { type: 'AUTH_LOGOUT' }
  | { type: 'UPDATE_USER'; payload: Partial<AuthUser> }
  | { type: 'CLEAR_ERROR' };

// Initial state
const initialState: AuthState = {
  user: null,
  isAuthenticated: false,
  isLoading: true,
  error: null,
};

// Auth reducer
const authReducer = (state: AuthState, action: AuthAction): AuthState => {
  switch (action.type) {
    case 'AUTH_START':
      return {
        ...state,
        isLoading: true,
        error: null,
      };

    case 'AUTH_SUCCESS':
      return {
        ...state,
        user: action.payload,
        isAuthenticated: true,
        isLoading: false,
        error: null,
      };

    case 'AUTH_ERROR':
      return {
        ...state,
        user: null,
        isAuthenticated: false,
        isLoading: false,
        error: action.payload,
      };

    case 'AUTH_LOGOUT':
      return {
        ...state,
        user: null,
        isAuthenticated: false,
        isLoading: false,
        error: null,
      };

    case 'UPDATE_USER':
      return {
        ...state,
        user: state.user ? { ...state.user, ...action.payload } : null,
      };

    case 'CLEAR_ERROR':
      return {
        ...state,
        error: null,
      };

    default:
      return state;
  }
};

// Create Auth Context
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Auth Provider Props
interface AuthProviderProps {
  children: React.ReactNode;
}

// Auth Provider Component
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  // Initialize auth state on mount
  useEffect(() => {
    initializeAuth();
  }, []);

  // Initialize authentication
  const initializeAuth = async () => {
    try {
      dispatch({ type: 'AUTH_START' });
      
      const user = await authService.initializeAuth();
      
      if (user) {
        dispatch({ type: 'AUTH_SUCCESS', payload: user });
      } else {
        dispatch({ type: 'AUTH_ERROR', payload: 'No valid session found' });
      }
    } catch (error) {
      dispatch({ type: 'AUTH_ERROR', payload: 'Failed to initialize authentication' });
    }
  };

  // Login function
  const login = async (credentials: LoginRequest): Promise<void> => {
    try {
      dispatch({ type: 'AUTH_START' });
      
      const user = await authService.login(credentials);
      dispatch({ type: 'AUTH_SUCCESS', payload: user });

      // Show success message (you can use toast here)
      console.log(SUCCESS_MESSAGES.LOGIN);
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Login failed';
      dispatch({ type: 'AUTH_ERROR', payload: errorMessage });
      throw error;
    }
  };

  // Register function
  const register = async (userData: RegisterRequest): Promise<void> => {
    try {
      dispatch({ type: 'AUTH_START' });
      
      const user = await authService.register(userData);
      dispatch({ type: 'AUTH_SUCCESS', payload: user });

      // Show success message
      console.log(SUCCESS_MESSAGES.REGISTER);
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Registration failed';
      dispatch({ type: 'AUTH_ERROR', payload: errorMessage });
      throw error;
    }
  };

  // Logout function
  const logout = async (): Promise<void> => {
    try {
      await authService.logout();
      dispatch({ type: 'AUTH_LOGOUT' });

      // Show success message
      console.log(SUCCESS_MESSAGES.LOGOUT);
      
      // Redirect to home page
      window.location.href = '/';
    } catch (error) {
      // Even if logout fails on server, clear local state
      dispatch({ type: 'AUTH_LOGOUT' });
      window.location.href = '/';
    }
  };

  // Update user function
  const updateUser = (userData: Partial<AuthUser>): void => {
    authService.updateUserData(userData);
    dispatch({ type: 'UPDATE_USER', payload: userData });
  };

  // Clear error function
  const clearError = (): void => {
    dispatch({ type: 'CLEAR_ERROR' });
  };

  // Check if user has specific role
  const hasRole = (role: string): boolean => {
    return authService.hasRole(role);
  };

  // Check if user has any of the specified roles
  const hasAnyRole = (roles: string[]): boolean => {
    return authService.hasAnyRole(roles);
  };

  // Context value
  const contextValue: AuthContextType = {
    user: state.user,
    isAuthenticated: state.isAuthenticated,
    isLoading: state.isLoading,
    login,
    register,
    logout,
    updateUser,
  };

  // Additional context value with extra utilities
  const extendedContextValue = {
    ...contextValue,
    error: state.error,
    clearError,
    hasRole,
    hasAnyRole,
    refreshAuth: initializeAuth,
  };

  return (
    <AuthContext.Provider value={extendedContextValue as any}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook to use Auth Context
export const useAuthContext = () => {
  const context = useContext(AuthContext);
  
  if (context === undefined) {
    throw new Error('useAuthContext must be used within an AuthProvider');
  }
  
  return context;
};

// Export the context for direct access if needed
export default AuthContext;