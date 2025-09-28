import React from 'react';
import { Link, Navigate, useNavigate, useSearchParams } from 'react-router-dom';
import { ShoppingBag } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { RegisterForm } from '../../components/auth/RegisterForm';
import { USER_ROLES } from '../../utils/constants';

const Register: React.FC = () => {
    const { isAuthenticated, getDashboardRoute } = useAuth();
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();

    // Get default role from URL params (e.g., /register?role=seller)
    const roleParam = searchParams.get('role');
    const defaultRole = roleParam && Object.values(USER_ROLES).includes(roleParam as any)
        ? roleParam
        : USER_ROLES.CUSTOMER;

    // Redirect if already authenticated
    if (isAuthenticated) {
        return <Navigate to={getDashboardRoute()} replace />;
    }

    const handleRegisterSuccess = () => {
        // Redirect to dashboard after successful registration
        navigate(getDashboardRoute(), { replace: true });
    };

    const handleRegisterError = (error: string) => {
        console.error('Registration error:', error);
        // Error handling is already done in RegisterForm component
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                {/* Header */}
                <div className="text-center">
                    <div className="flex justify-center">
                        <div className="flex items-center space-x-2">
                            <ShoppingBag className="h-10 w-10 text-blue-600" />
                            <span className="text-2xl font-bold text-gray-900">E-Commerce</span>
                        </div>
                    </div>
                    <h2 className="mt-6 text-3xl font-extrabold text-gray-900">
                        Create your account
                    </h2>
                    <p className="mt-2 text-sm text-gray-600">
                        Already have an account?{' '}
                        <Link
                            to="/login"
                            className="font-medium text-blue-600 hover:text-blue-500"
                        >
                            Sign in here
                        </Link>
                    </p>
                </div>

                {/* Account Type Info */}
                {defaultRole === USER_ROLES.SELLER && (
                    <div className="bg-yellow-50 border border-yellow-200 rounded-md p-4">
                        <div className="text-sm text-yellow-800">
                            <strong>Creating a Seller Account</strong>
                            <p className="mt-1">
                                As a seller, you'll be able to list and manage your products,
                                track orders, and access seller analytics.
                            </p>
                        </div>
                    </div>
                )}

                {/* Register Form Card */}
                {/*TODO: Implement seller registration form to have additional fields*/}
                <div className="card">
                    <div className="p-8">
                        <RegisterForm
                            onSuccess={handleRegisterSuccess}
                            onError={handleRegisterError}
                            defaultRole={defaultRole}
                        />
                    </div>
                </div>

                {/* Account Type Selection Links */}
                <div className="text-center space-y-4">
                    <div className="text-sm text-gray-600">
                        Looking for a different account type?
                    </div>
                    <div className="flex justify-center space-x-4 text-sm">
                        <Link
                            to="/register?role=customer"
                            className={`px-3 py-2 rounded-md transition-colors ${
                                defaultRole === USER_ROLES.CUSTOMER
                                    ? 'bg-blue-100 text-blue-700'
                                    : 'text-gray-600 hover:text-blue-600'
                            }`}
                        >
                            Customer Account
                        </Link>
                        <Link
                            to="/register?role=seller"
                            className={`px-3 py-2 rounded-md transition-colors ${
                                defaultRole === USER_ROLES.SELLER
                                    ? 'bg-blue-100 text-blue-700'
                                    : 'text-gray-600 hover:text-blue-600'
                            }`}
                        >
                            Seller Account
                        </Link>
                    </div>
                </div>

                {/* Additional Info */}
                <div className="text-center space-y-4">
                    <div className="text-sm">
                        <Link
                            to="/"
                            className="text-gray-600 hover:text-gray-900"
                        >
                            ← Back to Home
                        </Link>
                    </div>

                    {/* Benefits Info */}
                    <div className="bg-gray-50 border border-gray-200 rounded-md p-4">
                        <h3 className="text-sm font-medium text-gray-800 mb-2">Why Join Us?</h3>
                        <div className="text-xs text-gray-600 space-y-1">
                            <div>✓ Secure and fast checkout process</div>
                            <div>✓ Track your orders in real-time</div>
                            <div>✓ Exclusive deals and discounts</div>
                            <div>✓ 24/7 customer support</div>
                        </div>
                    </div>
                </div>

                {/* Footer Links */}
                <div className="text-center">
                    <div className="flex justify-center space-x-4 text-sm text-gray-500">
                        <Link to="/about" className="hover:text-gray-700">About</Link>
                        <span>•</span>
                        <Link to="/contact" className="hover:text-gray-700">Contact</Link>
                        <span>•</span>
                        <Link to="/privacy" className="hover:text-gray-700">Privacy</Link>
                        <span>•</span>
                        <Link to="/terms" className="hover:text-gray-700">Terms</Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Register;