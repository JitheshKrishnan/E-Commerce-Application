import React from 'react';
import { clsx } from 'clsx';

interface CardProps {
    children: React.ReactNode;
    className?: string;
    hover?: boolean;
    padding?: 'none' | 'sm' | 'md' | 'lg';
    shadow?: 'none' | 'sm' | 'md' | 'lg';
    border?: boolean;
    rounded?: 'none' | 'sm' | 'md' | 'lg' | 'xl';
}

const Card: React.FC<CardProps> = ({
                                       children,
                                       className = '',
                                       hover = false,
                                       padding = 'md',
                                       shadow = 'md',
                                       border = true,
                                       rounded = 'lg',
                                   }) => {
    const cardClasses = clsx(
        'bg-white overflow-hidden',
        // Padding variants
        {
            'p-0': padding === 'none',
            'p-3': padding === 'sm',
            'p-4': padding === 'md',
            'p-6': padding === 'lg',
        },
        // Shadow variants
        {
            'shadow-none': shadow === 'none',
            'shadow-sm': shadow === 'sm',
            'shadow-md': shadow === 'md',
            'shadow-lg': shadow === 'lg',
        },
        // Border
        {
            'border border-gray-200': border,
        },
        // Rounded corners
        {
            'rounded-none': rounded === 'none',
            'rounded-sm': rounded === 'sm',
            'rounded-md': rounded === 'md',
            'rounded-lg': rounded === 'lg',
            'rounded-xl': rounded === 'xl',
        },
        // Hover effect
        {
            'hover:shadow-lg transition-shadow duration-200': hover,
        },
        className
    );

    return (
        <div className={cardClasses}>
            {children}
        </div>
    );
};

// Card Header component
interface CardHeaderProps {
    children: React.ReactNode;
    className?: string;
    border?: boolean;
}

export const CardHeader: React.FC<CardHeaderProps> = ({
                                                          children,
                                                          className = '',
                                                          border = true,
                                                      }) => {
    const headerClasses = clsx(
        'px-4 py-3',
        {
            'border-b border-gray-200': border,
        },
        className
    );

    return (
        <div className={headerClasses}>
            {children}
        </div>
    );
};

// Card Body component
interface CardBodyProps {
    children: React.ReactNode;
    className?: string;
    padding?: 'none' | 'sm' | 'md' | 'lg';
}

export const CardBody: React.FC<CardBodyProps> = ({
                                                      children,
                                                      className = '',
                                                      padding = 'md',
                                                  }) => {
    const bodyClasses = clsx(
        {
            'p-0': padding === 'none',
            'p-3': padding === 'sm',
            'p-4': padding === 'md',
            'p-6': padding === 'lg',
        },
        className
    );

    return (
        <div className={bodyClasses}>
            {children}
        </div>
    );
};

// Card Footer component
interface CardFooterProps {
    children: React.ReactNode;
    className?: string;
    border?: boolean;
}

export const CardFooter: React.FC<CardFooterProps> = ({
                                                          children,
                                                          className = '',
                                                          border = true,
                                                      }) => {
    const footerClasses = clsx(
        'px-4 py-3',
        {
            'border-t border-gray-200': border,
        },
        className
    );

    return (
        <div className={footerClasses}>
            {children}
        </div>
    );
};

// Card Title component
interface CardTitleProps {
    children: React.ReactNode;
    className?: string;
    size?: 'sm' | 'md' | 'lg';
}

export const CardTitle: React.FC<CardTitleProps> = ({
                                                        children,
                                                        className = '',
                                                        size = 'md',
                                                    }) => {
    const titleClasses = clsx(
        'font-semibold text-gray-900',
        {
            'text-sm': size === 'sm',
            'text-lg': size === 'md',
            'text-xl': size === 'lg',
        },
        className
    );

    return (
        <h3 className={titleClasses}>
            {children}
        </h3>
    );
};

// Card Description component
interface CardDescriptionProps {
    children: React.ReactNode;
    className?: string;
}

export const CardDescription: React.FC<CardDescriptionProps> = ({
                                                                    children,
                                                                    className = '',
                                                                }) => {
    const descClasses = clsx(
        'text-sm text-gray-600',
        className
    );

    return (
        <p className={descClasses}>
            {children}
        </p>
    );
};

// Stats Card component
interface StatsCardProps {
    title: string;
    value: string | number;
    change?: string;
    changeType?: 'increase' | 'decrease' | 'neutral';
    icon?: React.ReactNode;
    className?: string;
}

export const StatsCard: React.FC<StatsCardProps> = ({
                                                        title,
                                                        value,
                                                        change,
                                                        changeType = 'neutral',
                                                        icon,
                                                        className = '',
                                                    }) => {
    const changeClasses = clsx(
        'text-sm font-medium',
        {
            'text-green-600': changeType === 'increase',
            'text-red-600': changeType === 'decrease',
            'text-gray-600': changeType === 'neutral',
        }
    );

    return (
        <Card className={className} hover>
            <CardBody>
                <div className="flex items-center justify-between">
                    <div className="flex-1">
                        <p className="text-sm font-medium text-gray-600 truncate">
                            {title}
                        </p>
                        <p className="text-2xl font-semibold text-gray-900">
                            {value}
                        </p>
                        {change && (
                            <p className={changeClasses}>
                                {change}
                            </p>
                        )}
                    </div>
                    {icon && (
                        <div className="flex-shrink-0">
                            <div className="w-8 h-8 text-gray-400">
                                {icon}
                            </div>
                        </div>
                    )}
                </div>
            </CardBody>
        </Card>
    );
};

// Product Card component
interface ProductCardProps {
    title: string;
    price: number;
    image?: string;
    description?: string;
    badge?: string;
    onCardClick?: () => void;
    onAddToCart?: () => void;
    className?: string;
}

export const ProductCard: React.FC<ProductCardProps> = ({
                                                            title,
                                                            price,
                                                            image,
                                                            description,
                                                            badge,
                                                            onCardClick,
                                                            onAddToCart,
                                                            className = '',
                                                        }) => {
    return (
        <Card
            className={clsx('cursor-pointer', className)}
            hover
            padding="none"
            onClick={onCardClick}
        >
            {/* Product Image */}
            <div className="aspect-w-1 aspect-h-1 w-full overflow-hidden bg-gray-200">
                {image ? (
                    <img
                        src={image}
                        alt={title}
                        className="h-48 w-full object-cover object-center group-hover:opacity-75"
                    />
                ) : (
                    <div className="h-48 w-full bg-gray-200 flex items-center justify-center">
                        <span className="text-gray-400">No Image</span>
                    </div>
                )}

                {badge && (
                    <div className="absolute top-2 left-2">
            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
              {badge}
            </span>
                    </div>
                )}
            </div>

            {/* Product Info */}
            <CardBody>
                <h3 className="text-sm font-medium text-gray-900 truncate">
                    {title}
                </h3>

                {description && (
                    <p className="text-sm text-gray-600 mt-1 line-clamp-2">
                        {description}
                    </p>
                )}

                <div className="mt-3 flex items-center justify-between">
                    <p className="text-lg font-semibold text-gray-900">
                        ${price.toFixed(2)}
                    </p>

                    {onAddToCart && (
                        <button
                            onClick={(e) => {
                                e.stopPropagation();
                                onAddToCart();
                            }}
                            className="btn-primary text-xs px-3 py-1"
                        >
                            Add to Cart
                        </button>
                    )}
                </div>
            </CardBody>
        </Card>
    );
};

export default Card;