import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
    ShoppingBag,
    Search,
    ShoppingCart,
    User,
    Menu,
    X,
    LogOut,
    Settings,
    Package,
    BarChart3
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import Button from '../ui/Button';

interface HeaderProps {
    className?: string;
}

const Header: React.FC<HeaderProps> = ({ className = '' }) => {
    const {
        isAuthenticated,
        user,
        logout,
        getDisplayName,
        getUserInitials,
        getDashboardRoute,
        isCustomer,
        isSeller,
        isAdmin
    } = useAuth();

    const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const navigate = useNavigate();

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
        if (searchQuery.trim()) {
            navigate(`/products?search=${encodeURIComponent(searchQuery.trim())}`);
        }
    };

    const handleLogout = async () => {
        await logout();
        setIsProfileMenuOpen(false);
    };

    const profileMenuItems = [
        {
            label: 'Dashboard',
            href: getDashboardRoute(),
            icon: <BarChart3 className="w-4 h-4" />,
            show: true
        },
        {
            label: 'My Profile',
            href: '/profile',
            icon: <User className="w-4 h-4" />,
            show: true
        },
        {
            label: 'My Orders',
            href: '/orders',
            icon: <Package className="w-4 h-4" />,
            show: isCustomer()
        },
        {
            label: 'My Products',
            href: '/seller/products',
            icon: <Package className="w-4 h-4" />,
            show: isSeller()
        },
        {
            label: 'Settings',
            href: '/settings',
            icon: <Settings className="w-4 h-4" />,
            show: isAdmin()
        },
    ];

    return (
        <header className={`bg-white shadow-sm border-b border-gray-200 ${className}`}>
            <div className="container">
                <div className="flex items-center justify-between h-16">
                    {/* Logo */}
                    <div className="flex items-center">
                        <Link
                            to="/"
                            className="flex items-center space-x-2 text-xl font-bold text-gray-900"
                        >
                            <ShoppingBag className="h-8 w-8 text-blue-600" />
                            <span className="hidden sm:block">E-Commerce</span>
                        </Link>
                    </div>

                    {/* Search Bar */}
                    <div className="flex-1 max-w-lg mx-4 hidden md:block">
                        <form onSubmit={handleSearch} className="relative">
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Search className="h-5 w-5 text-gray-400" />
                                </div>
                                <input
                                    type="text"
                                    value={searchQuery}
                                    onChange={(e) => setSearchQuery(e.target.value)}
                                    className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500"
                                    placeholder="Search products..."
                                />
                            </div>
                        </form>
                    </div>

                    {/* Right Side Actions */}
                    <div className="flex items-center space-x-4">
                        {/* Mobile Search Button */}
                        <button className="md:hidden p-2 text-gray-600 hover:text-gray-900">
                            <Search className="h-5 w-5" />
                        </button>

                        {/* Cart Button */}
                        {isAuthenticated && isCustomer() && (
                            <Link
                                to="/cart"
                                className="relative p-2 text-gray-600 hover:text-gray-900"
                            >
                                <ShoppingCart className="h-6 w-6" />
                                {/* Cart Badge - You can connect this to cart context */}
                                <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                  0
                </span>
                            </Link>
                        )}

                        {/* User Menu */}
                        {isAuthenticated ? (
                            <div className="relative">
                                <button
                                    onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)}
                                    className="flex items-center space-x-2 p-1 rounded-full text-gray-600 hover:text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                >
                                    <div className="w-8 h-8 bg-blue-600 text-white rounded-full flex items-center justify-center text-sm font-medium">
                                        {getUserInitials()}
                                    </div>
                                    <span className="hidden md:block text-sm font-medium">
                    {getDisplayName()}
                  </span>
                                </button>

                                {/* Profile Dropdown */}
                                {isProfileMenuOpen && (
                                    <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50 border border-gray-200">
                                        {/* User Info */}
                                        <div className="px-4 py-2 border-b border-gray-200">
                                            <p className="text-sm font-medium text-gray-900">
                                                {getDisplayName()}
                                            </p>
                                            <p className="text-sm text-gray-500">{user?.email}</p>
                                        </div>

                                        {/* Menu Items */}
                                        {profileMenuItems
                                            .filter(item => item.show)
                                            .map((item) => (
                                                <Link
                                                    key={item.href}
                                                    to={item.href}
                                                    className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                                    onClick={() => setIsProfileMenuOpen(false)}
                                                >
                                                    {item.icon}
                                                    <span className="ml-2">{item.label}</span>
                                                </Link>
                                            ))}

                                        {/* Logout */}
                                        <button
                                            onClick={handleLogout}
                                            className="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                        >
                                            <LogOut className="w-4 h-4" />
                                            <span className="ml-2">Sign Out</span>
                                        </button>
                                    </div>
                                )}
                            </div>
                        ) : (
                            /* Login/Register Buttons */
                            <div className="flex items-center space-x-2">
                                <Button
                                    variant="ghost"
                                    onClick={() => navigate('/login')}
                                    size="sm"
                                >
                                    Sign In
                                </Button>
                                <Button
                                    variant="primary"
                                    onClick={() => navigate('/register')}
                                    size="sm"
                                >
                                    Sign Up
                                </Button>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Mobile Search Bar */}
            <div className="md:hidden border-t border-gray-200 p-4">
                <form onSubmit={handleSearch} className="relative">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <Search className="h-5 w-5 text-gray-400" />
                    </div>
                    <input
                        type="text"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="Search products..."
                    />
                </form>
            </div>
        </header>
    );
};

export default Header;