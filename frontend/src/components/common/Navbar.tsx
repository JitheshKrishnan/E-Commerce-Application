import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
    Home,
    ShoppingBag,
    Package,
    Users,
    BarChart3,
    Settings,
    HeadphonesIcon,
    Menu,
    X,
    ChevronDown
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { PRODUCT_CATEGORIES } from '../../utils/constants';

interface NavbarProps {
    className?: string;
}

const Navbar: React.FC<NavbarProps> = ({ className = '' }) => {
    const { isAuthenticated, isCustomer, isSeller, isAdmin, isSupport } = useAuth();
    const location = useLocation();
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const [isCategoriesOpen, setIsCategoriesOpen] = useState(false);

    // Navigation items based on user role
    const getNavigationItems = () => {
        const items = [];

        // Public items
        items.push(
            { name: 'Home', href: '/', icon: <Home className="w-4 h-4" /> },
            { name: 'Products', href: '/products', icon: <ShoppingBag className="w-4 h-4" /> }
        );

        if (isAuthenticated) {
            if (isCustomer()) {
                items.push(
                    { name: 'My Orders', href: '/orders', icon: <Package className="w-4 h-4" /> },
                    { name: 'Dashboard', href: '/customer/dashboard', icon: <BarChart3 className="w-4 h-4" /> }
                );
            }

            if (isSeller()) {
                items.push(
                    { name: 'My Products', href: '/seller/products', icon: <Package className="w-4 h-4" /> },
                    { name: 'Orders', href: '/seller/orders', icon: <Package className="w-4 h-4" /> },
                    { name: 'Analytics', href: '/seller/analytics', icon: <BarChart3 className="w-4 h-4" /> },
                    { name: 'Dashboard', href: '/seller/dashboard', icon: <BarChart3 className="w-4 h-4" /> }
                );
            }

            if (isAdmin()) {
                items.push(
                    { name: 'Users', href: '/admin/users', icon: <Users className="w-4 h-4" /> },
                    { name: 'Products', href: '/admin/products', icon: <Package className="w-4 h-4" /> },
                    { name: 'Orders', href: '/admin/orders', icon: <Package className="w-4 h-4" /> },
                    { name: 'Analytics', href: '/admin/analytics', icon: <BarChart3 className="w-4 h-4" /> },
                    { name: 'Settings', href: '/admin/settings', icon: <Settings className="w-4 h-4" /> },
                    { name: 'Dashboard', href: '/admin/dashboard', icon: <BarChart3 className="w-4 h-4" /> }
                );
            }

            if (isSupport()) {
                items.push(
                    { name: 'Customers', href: '/support/customers', icon: <Users className="w-4 h-4" /> },
                    { name: 'Orders', href: '/support/orders', icon: <Package className="w-4 h-4" /> },
                    { name: 'Tickets', href: '/support/tickets', icon: <HeadphonesIcon className="w-4 h-4" /> },
                    { name: 'Dashboard', href: '/support/dashboard', icon: <BarChart3 className="w-4 h-4" /> }
                );
            }
        }

        return items;
    };

    const navigationItems = getNavigationItems();

    const isActiveLink = (href: string) => {
        if (href === '/') {
            return location.pathname === '/';
        }
        return location.pathname.startsWith(href);
    };

    return (
        <nav className={`bg-gray-50 border-b border-gray-200 ${className}`}>
            <div className="container">
                <div className="flex items-center justify-between h-12">
                    {/* Desktop Navigation */}
                    <div className="hidden md:flex items-center space-x-1">
                        {navigationItems.map((item) => (
                            <Link
                                key={item.href}
                                to={item.href}
                                className={`
                  flex items-center space-x-2 px-3 py-2 rounded-md text-sm font-medium transition-colors duration-200
                  ${isActiveLink(item.href)
                                    ? 'bg-blue-100 text-blue-700'
                                    : 'text-gray-600 hover:text-gray-900 hover:bg-gray-100'
                                }
                `}
                            >
                                {item.icon}
                                <span>{item.name}</span>
                            </Link>
                        ))}

                        {/* Categories Dropdown */}
                        <div className="relative">
                            <button
                                onClick={() => setIsCategoriesOpen(!isCategoriesOpen)}
                                className="flex items-center space-x-1 px-3 py-2 rounded-md text-sm font-medium text-gray-600 hover:text-gray-900 hover:bg-gray-100 transition-colors duration-200"
                            >
                                <span>Categories</span>
                                <ChevronDown className="w-4 h-4" />
                            </button>

                            {isCategoriesOpen && (
                                <div className="absolute top-full left-0 mt-1 w-48 bg-white rounded-md shadow-lg py-1 z-50 border border-gray-200">
                                    {PRODUCT_CATEGORIES.map((category) => (
                                        <Link
                                            key={category}
                                            to={`/products?category=${encodeURIComponent(category)}`}
                                            className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                            onClick={() => setIsCategoriesOpen(false)}
                                        >
                                            {category}
                                        </Link>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Mobile Menu Button */}
                    <div className="md:hidden">
                        <button
                            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                            className="inline-flex items-center justify-center p-2 rounded-md text-gray-600 hover:text-gray-900 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-blue-500"
                        >
                            {isMobileMenuOpen ? (
                                <X className="h-5 w-5" />
                            ) : (
                                <Menu className="h-5 w-5" />
                            )}
                        </button>
                    </div>

                    {/* Right side items (could be breadcrumbs or additional actions) */}
                    <div className="hidden md:flex items-center space-x-4">
                        {/* You can add additional navigation items here */}
                    </div>
                </div>

                {/* Mobile Navigation Menu */}
                {isMobileMenuOpen && (
                    <div className="md:hidden border-t border-gray-200">
                        <div className="px-2 pt-2 pb-3 space-y-1">
                            {navigationItems.map((item) => (
                                <Link
                                    key={item.href}
                                    to={item.href}
                                    className={`
                    flex items-center space-x-2 px-3 py-2 rounded-md text-base font-medium transition-colors duration-200
                    ${isActiveLink(item.href)
                                        ? 'bg-blue-100 text-blue-700'
                                        : 'text-gray-600 hover:text-gray-900 hover:bg-gray-100'
                                    }
                  `}
                                    onClick={() => setIsMobileMenuOpen(false)}
                                >
                                    {item.icon}
                                    <span>{item.name}</span>
                                </Link>
                            ))}

                            {/* Mobile Categories */}
                            <div className="pt-2 border-t border-gray-200">
                                <div className="px-3 py-2 text-sm font-medium text-gray-500">Categories</div>
                                {PRODUCT_CATEGORIES.slice(0, 5).map((category) => (
                                    <Link
                                        key={category}
                                        to={`/products?category=${encodeURIComponent(category)}`}
                                        className="block px-6 py-2 text-base text-gray-600 hover:text-gray-900 hover:bg-gray-100"
                                        onClick={() => setIsMobileMenuOpen(false)}
                                    >
                                        {category}
                                    </Link>
                                ))}
                                <Link
                                    to="/products"
                                    className="block px-6 py-2 text-base text-blue-600 hover:text-blue-700"
                                    onClick={() => setIsMobileMenuOpen(false)}
                                >
                                    View All Categories →
                                </Link>
                            </div>
                        </div>
                    </div>
                )}
            </div>

            {/* Click outside to close dropdowns */}
            {(isCategoriesOpen || isMobileMenuOpen) && (
                <div
                    className="fixed inset-0 z-40"
                    onClick={() => {
                        setIsCategoriesOpen(false);
                        setIsMobileMenuOpen(false);
                    }}
                />
            )}
        </nav>
    );
};

export default Navbar;