import React from 'react';
import { Loader2 } from 'lucide-react';
import { clsx } from 'clsx';

interface LoadingSpinnerProps {
    size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
    color?: 'primary' | 'secondary' | 'white' | 'gray';
    className?: string;
    text?: string;
    fullScreen?: boolean;
}

const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
                                                           size = 'md',
                                                           color = 'primary',
                                                           className = '',
                                                           text,
                                                           fullScreen = false,
                                                       }) => {
    // Size classes
    const sizeClasses = {
        xs: 'w-3 h-3',
        sm: 'w-4 h-4',
        md: 'w-6 h-6',
        lg: 'w-8 h-8',
        xl: 'w-12 h-12',
    };

    // Color classes
    const colorClasses = {
        primary: 'text-blue-600',
        secondary: 'text-gray-600',
        white: 'text-white',
        gray: 'text-gray-400',
    };

    const spinnerClasses = clsx(
        'animate-spin',
        sizeClasses[size],
        colorClasses[color],
        className
    );

    const textSizeClasses = {
        xs: 'text-xs',
        sm: 'text-sm',
        md: 'text-base',
        lg: 'text-lg',
        xl: 'text-xl',
    };

    if (fullScreen) {
        return (
            <div className="fixed inset-0 bg-white bg-opacity-75 flex items-center justify-center z-50">
                <div className="text-center">
                    <Loader2 className={clsx(spinnerClasses, 'mx-auto mb-4')} />
                    {text && (
                        <p className={clsx('text-gray-600', textSizeClasses[size])}>
                            {text}
                        </p>
                    )}
                </div>
            </div>
        );
    }

    return (
        <div className="flex items-center justify-center space-x-2">
            <Loader2 className={spinnerClasses} />
            {text && (
                <span className={clsx('text-gray-600', textSizeClasses[size])}>
          {text}
        </span>
            )}
        </div>
    );
};

// Inline spinner for buttons
export const ButtonSpinner: React.FC<{ size?: 'xs' | 'sm' | 'md' }> = ({
                                                                           size = 'sm'
                                                                       }) => {
    const sizeClasses = {
        xs: 'w-3 h-3',
        sm: 'w-4 h-4',
        md: 'w-5 h-5',
    };

    return (
        <Loader2 className={clsx('animate-spin', sizeClasses[size])} />
    );
};

// Page loading spinner
export const PageSpinner: React.FC<{ text?: string }> = ({
                                                             text = 'Loading...'
                                                         }) => {
    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="text-center">
                <div className="inline-flex items-center px-4 py-2 font-semibold leading-6 text-gray-700">
                    <Loader2 className="animate-spin -ml-1 mr-3 h-8 w-8 text-blue-600" />
                    {text}
                </div>
            </div>
        </div>
    );
};

// Card loading skeleton
export const CardSkeleton: React.FC<{ className?: string }> = ({
                                                                   className = ''
                                                               }) => {
    return (
        <div className={clsx('animate-pulse bg-white rounded-lg shadow-md p-6', className)}>
            <div className="h-4 bg-gray-200 rounded w-3/4 mb-4"></div>
            <div className="space-y-3">
                <div className="h-3 bg-gray-200 rounded"></div>
                <div className="h-3 bg-gray-200 rounded w-5/6"></div>
                <div className="h-3 bg-gray-200 rounded w-4/6"></div>
            </div>
        </div>
    );
};

// Table loading skeleton
export const TableSkeleton: React.FC<{
    rows?: number;
    columns?: number;
    className?: string;
}> = ({
          rows = 5,
          columns = 4,
          className = ''
      }) => {
    return (
        <div className={clsx('animate-pulse', className)}>
            {/* Table header */}
            <div className="bg-gray-50 px-6 py-3 border-b border-gray-200">
                <div className="grid gap-4" style={{ gridTemplateColumns: `repeat(${columns}, 1fr)` }}>
                    {Array.from({ length: columns }).map((_, i) => (
                        <div key={i} className="h-4 bg-gray-200 rounded"></div>
                    ))}
                </div>
            </div>

            {/* Table rows */}
            {Array.from({ length: rows }).map((_, rowIndex) => (
                <div key={rowIndex} className="px-6 py-4 border-b border-gray-200">
                    <div className="grid gap-4" style={{ gridTemplateColumns: `repeat(${columns}, 1fr)` }}>
                        {Array.from({ length: columns }).map((_, colIndex) => (
                            <div key={colIndex} className="h-4 bg-gray-200 rounded"></div>
                        ))}
                    </div>
                </div>
            ))}
        </div>
    );
};

// Product card skeleton
export const ProductCardSkeleton: React.FC<{ className?: string }> = ({
                                                                          className = ''
                                                                      }) => {
    return (
        <div className={clsx('animate-pulse bg-white rounded-lg shadow-md overflow-hidden', className)}>
            {/* Image skeleton */}
            <div className="h-48 bg-gray-200"></div>

            {/* Content skeleton */}
            <div className="p-4">
                <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
                <div className="h-3 bg-gray-200 rounded w-1/2 mb-4"></div>
                <div className="flex justify-between items-center">
                    <div className="h-5 bg-gray-200 rounded w-16"></div>
                    <div className="h-8 bg-gray-200 rounded w-20"></div>
                </div>
            </div>
        </div>
    );
};

// List item skeleton
export const ListItemSkeleton: React.FC<{ className?: string }> = ({
                                                                       className = ''
                                                                   }) => {
    return (
        <div className={clsx('animate-pulse flex items-center space-x-4 p-4', className)}>
            <div className="rounded-full bg-gray-200 h-10 w-10"></div>
            <div className="flex-1 space-y-2">
                <div className="h-4 bg-gray-200 rounded w-3/4"></div>
                <div className="h-3 bg-gray-200 rounded w-1/2"></div>
            </div>
        </div>
    );
};

// Text skeleton
export const TextSkeleton: React.FC<{
    lines?: number;
    className?: string;
}> = ({
          lines = 3,
          className = ''
      }) => {
    return (
        <div className={clsx('animate-pulse space-y-2', className)}>
            {Array.from({ length: lines }).map((_, i) => (
                <div
                    key={i}
                    className={clsx(
                        'h-4 bg-gray-200 rounded',
                        i === lines - 1 ? 'w-3/4' : 'w-full'
                    )}
                ></div>
            ))}
        </div>
    );
};

// Dashboard stats skeleton
export const StatsSkeleton: React.FC<{ count?: number }> = ({ count = 4 }) => {
    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {Array.from({ length: count }).map((_, i) => (
                <div key={i} className="animate-pulse bg-white rounded-lg shadow-md p-6">
                    <div className="flex items-center justify-between">
                        <div className="flex-1">
                            <div className="h-4 bg-gray-200 rounded w-20 mb-2"></div>
                            <div className="h-8 bg-gray-200 rounded w-16 mb-2"></div>
                            <div className="h-3 bg-gray-200 rounded w-12"></div>
                        </div>
                        <div className="w-8 h-8 bg-gray-200 rounded"></div>
                    </div>
                </div>
            ))}
        </div>
    );
};

// Loading overlay for existing content
export const LoadingOverlay: React.FC<{
    isLoading: boolean;
    children: React.ReactNode;
    text?: string;
}> = ({
          isLoading,
          children,
          text = 'Loading...'
      }) => {
    return (
        <div className="relative">
            {children}
            {isLoading && (
                <div className="absolute inset-0 bg-white bg-opacity-75 flex items-center justify-center z-10">
                    <div className="text-center">
                        <Loader2 className="animate-spin h-8 w-8 text-blue-600 mx-auto mb-2" />
                        <p className="text-gray-600 text-sm">{text}</p>
                    </div>
                </div>
            )}
        </div>
    );
};

// Progress spinner with percentage
export const ProgressSpinner: React.FC<{
    progress: number;
    size?: 'sm' | 'md' | 'lg';
    className?: string;
}> = ({
          progress,
          size = 'md',
          className = ''
      }) => {
    const sizeClasses = {
        sm: 'w-8 h-8',
        md: 'w-12 h-12',
        lg: 'w-16 h-16',
    };

    const textSizeClasses = {
        sm: 'text-xs',
        md: 'text-sm',
        lg: 'text-base',
    };

    const radius = size === 'sm' ? 14 : size === 'md' ? 20 : 28;
    const circumference = 2 * Math.PI * radius;
    const strokeDashoffset = circumference - (progress / 100) * circumference;

    return (
        <div className={clsx('relative', sizeClasses[size], className)}>
            <svg className="transform -rotate-90 w-full h-full">
                <circle
                    cx="50%"
                    cy="50%"
                    r={radius}
                    stroke="currentColor"
                    strokeWidth="2"
                    fill="transparent"
                    className="text-gray-200"
                />
                <circle
                    cx="50%"
                    cy="50%"
                    r={radius}
                    stroke="currentColor"
                    strokeWidth="2"
                    fill="transparent"
                    strokeDasharray={circumference}
                    strokeDashoffset={strokeDashoffset}
                    strokeLinecap="round"
                    className="text-blue-600 transition-all duration-300"
                />
            </svg>
            <div className={clsx(
                'absolute inset-0 flex items-center justify-center font-semibold text-gray-700',
                textSizeClasses[size]
            )}>
                {Math.round(progress)}%
            </div>
        </div>
    );
};

// Dots loading animation
export const DotsSpinner: React.FC<{
    size?: 'sm' | 'md' | 'lg';
    color?: 'primary' | 'secondary' | 'white';
    className?: string;
}> = ({
          size = 'md',
          color = 'primary',
          className = ''
      }) => {
    const sizeClasses = {
        sm: 'w-1 h-1',
        md: 'w-2 h-2',
        lg: 'w-3 h-3',
    };

    const colorClasses = {
        primary: 'bg-blue-600',
        secondary: 'bg-gray-600',
        white: 'bg-white',
    };

    const dotClass = clsx(
        'rounded-full animate-pulse',
        sizeClasses[size],
        colorClasses[color]
    );

    return (
        <div className={clsx('flex space-x-1', className)}>
            <div className={clsx(dotClass, 'animation-delay-0')}></div>
            <div className={clsx(dotClass, 'animation-delay-200')}></div>
            <div className={clsx(dotClass, 'animation-delay-400')}></div>
        </div>
    );
};

export default LoadingSpinner;