import { Link, Navigate, useNavigate, useLocation } from 'react-router-dom';
import { ShoppingBag } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { LoginForm } from '../../components/auth/LoginForm';

const Login: React.FC = () => {
    const { isAuthenticated, getDashboardRoute } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    // Get the redirect path from location state or default to dashboard
    const from = (location.state as any)?.from || getDashboardRoute();

    // Redirect if already authenticated
    if (isAuthenticated) {
        return <Navigate to={from} replace />;
    }

    const handleLoginSuccess = () => {
        // Redirect to the intended page or dashboard
        navigate(from, { replace: true });
    };

    const handleLoginError = (error: string) => {
        console.error('Login error:', error);
        // Error handling is already done in LoginForm component
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
                        Sign in to your account
                    </h2>
                    <p className="mt-2 text-sm text-gray-600">
                        Or{' '}
                        <Link
                            to="/register"
                            className="font-medium text-blue-600 hover:text-blue-500"
                        >
                            create a new account
                        </Link>
                    </p>
                </div>

                {/* Login Form Card */}
                <div className="card">
                    <div className="p-8">
                        <LoginForm
                            onSuccess={handleLoginSuccess}
                            onError={handleLoginError}
                        />
                    </div>
                </div>

                {/* Additional Links */}
                <div className="text-center space-y-4">
                    <div className="text-sm">
                        <Link
                            to="/"
                            className="text-gray-600 hover:text-gray-900"
                        >
                            ← Back to Home
                        </Link>
                    </div>

                    {/* Demo Accounts Info */}
                    <div className="bg-blue-50 border border-blue-200 rounded-md p-4">
                        <h3 className="text-sm font-medium text-blue-800 mb-2">Demo Accounts</h3>
                        <div className="text-xs text-blue-700 space-y-1">
                            <div><strong>Customer:</strong> customer@demo.com / password123</div>
                            <div><strong>Seller:</strong> seller@demo.com / password123</div>
                            <div><strong>Admin:</strong> admin@demo.com / password123</div>
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

export default Login;