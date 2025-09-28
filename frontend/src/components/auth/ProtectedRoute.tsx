import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { Loader2 } from 'lucide-react';
import type { ProtectedRouteProps } from '../../types';

// Loading component
const LoadingSpinner: React.FC = () => (
  <div className="min-h-screen flex items-center justify-center bg-gray-50">
    <div className="text-center">
      <Loader2 className="h-8 w-8 animate-spin text-blue-600 mx-auto mb-4" />
      <p className="text-gray-600">Loading...</p>
    </div>
  </div>
);

// Unauthorized access component
const UnauthorizedAccess: React.FC<{ requiredRole?: string }> = ({ requiredRole }) => (
  <div className="min-h-screen flex items-center justify-center bg-gray-50">
    <div className="max-w-md w-full bg-white shadow-lg rounded-lg p-8 text-center">
      <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
        <svg className="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
        </svg>
      </div>
      <h2 className="text-xl font-semibold text-gray-900 mb-2">Access Denied</h2>
      <p className="text-gray-600 mb-4">
        {requiredRole 
          ? `You need ${requiredRole} privileges to access this page.`
          : 'You do not have permission to access this page.'
        }
      </p>
      <button
        onClick={() => window.history.back()}
        className="btn-primary"
      >
        Go Back
      </button>
    </div>
  </div>
);

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredRole,
  fallback
}) => {
  const { isAuthenticated, isLoading, hasRole } = useAuth();
  const location = useLocation();

  // Show loading spinner while checking authentication
  if (isLoading) {
    return <LoadingSpinner />;
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    // Save the attempted location so we can redirect back after login
    return (
      <Navigate 
        to="/login" 
        state={{ from: location.pathname }} 
        replace 
      />
    );
  }

  // Check role-based access if requiredRole is specified
  if (requiredRole && !hasRole(requiredRole)) {
    // Show custom fallback or default unauthorized component
    return fallback || <UnauthorizedAccess requiredRole={requiredRole} />;
  }

  // Render the protected content
  return <>{children}</>;
};

// Role-specific route components for convenience
export const CustomerRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <ProtectedRoute requiredRole="CUSTOMER">
    {children}
  </ProtectedRoute>
);

export const SellerRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <ProtectedRoute requiredRole="SELLER">
    {children}
  </ProtectedRoute>
);

export const AdminRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <ProtectedRoute requiredRole="ADMIN">
    {children}
  </ProtectedRoute>
);

export const SupportRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <ProtectedRoute requiredRole="SUPPORT">
    {children}
  </ProtectedRoute>
);

// Multi-role route component
interface MultiRoleRouteProps {
  children: React.ReactNode;
  allowedRoles: string[];
  fallback?: React.ReactNode;
}

export const MultiRoleRoute: React.FC<MultiRoleRouteProps> = ({
  children,
  allowedRoles,
  fallback
}) => {
  const { isAuthenticated, isLoading, hasAnyRole } = useAuth();
  const location = useLocation();

  // Show loading spinner while checking authentication
  if (isLoading) {
    return <LoadingSpinner />;
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    return (
      <Navigate 
        to="/login" 
        state={{ from: location.pathname }} 
        replace 
      />
    );
  }

  // Check if user has any of the allowed roles
  if (!hasAnyRole(allowedRoles)) {
    return fallback || <UnauthorizedAccess requiredRole={allowedRoles.join(' or ')} />;
  }

  return <>{children}</>;
};

// Staff route (Admin or Support)
export const StaffRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <MultiRoleRoute allowedRoles={['ADMIN', 'SUPPORT']}>
    {children}
  </MultiRoleRoute>
);

// Seller or Admin route
export const SellerOrAdminRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <MultiRoleRoute allowedRoles={['SELLER', 'ADMIN']}>
    {children}
  </MultiRoleRoute>
);

export default ProtectedRoute;