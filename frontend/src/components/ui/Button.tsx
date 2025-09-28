import React from 'react';
import { Loader2 } from 'lucide-react';
import { clsx } from 'clsx';
import type { ButtonProps } from '../../types';

const Button: React.FC<ButtonProps> = ({
   children,
   type = 'button',
   variant = 'primary',
   size = 'md',
   disabled = false,
   loading = false,
   onClick,
   className = '',
   ...props
}) => {
    // Base button styles
    const baseStyles = `
    inline-flex items-center justify-center font-medium rounded-md
    transition-colors duration-200 focus:outline-none focus:ring-2 
    focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed
  `;

    // Variant styles
    const variantStyles = {
        primary: `
      bg-blue-600 hover:bg-blue-700 text-white 
      focus:ring-blue-500 border border-transparent
    `,
        secondary: `
      bg-gray-200 hover:bg-gray-300 text-gray-800 
      focus:ring-gray-500 border border-gray-300
    `,
        danger: `
      bg-red-600 hover:bg-red-700 text-white 
      focus:ring-red-500 border border-transparent
    `,
        ghost: `
      bg-transparent hover:bg-gray-100 text-gray-700 
      focus:ring-gray-500 border border-transparent
    `,
        outline: `
      bg-transparent hover:bg-blue-50 text-blue-600 
      focus:ring-blue-500 border border-blue-300
    `,
        success: `
      bg-green-600 hover:bg-green-700 text-white 
      focus:ring-green-500 border border-transparent
    `,
        warning: `
      bg-yellow-600 hover:bg-yellow-700 text-white 
      focus:ring-yellow-500 border border-transparent
    `,
    };

    // Size styles
    const sizeStyles = {
        xs: 'px-2 py-1 text-xs',
        sm: 'px-3 py-1.5 text-sm',
        md: 'px-4 py-2 text-sm',
        lg: 'px-6 py-3 text-base',
        xl: 'px-8 py-4 text-lg',
    };

    // Combine all styles
    const buttonClasses = clsx(
        baseStyles,
        variantStyles[variant],
        sizeStyles[size],
        className
    );

    return (
        <button
            type={type}
            className={buttonClasses}
            disabled={disabled || loading}
            onClick={onClick}
            {...props}
        >
            {loading && (
                <Loader2 className="animate-spin -ml-1 mr-2 h-4 w-4" />
            )}
            {children}
        </button>
    );
};

// Icon Button variant
export const IconButton: React.FC<ButtonProps & { icon: React.ReactNode }> = ({
  icon,
  children,
  size = 'md',
  ...props
}) => {
    const iconSizes = {
        xs: 'w-4 h-4',
        sm: 'w-4 h-4',
        md: 'w-5 h-5',
        lg: 'w-6 h-6',
        xl: 'w-6 h-6',
    };

    return (
        <Button size={size} {...props}>
            <span className={iconSizes[size]}>{icon}</span>
            {children && <span className="ml-2">{children}</span>}
        </Button>
    );
};

// Button Group component
interface ButtonGroupProps {
    children: React.ReactNode;
    className?: string;
}

export const ButtonGroup: React.FC<ButtonGroupProps> = ({
                                                            children,
                                                            className = ''
                                                        }) => {
    return (
        <div className={clsx('inline-flex rounded-md shadow-sm', className)}>
            {React.Children.map(children, (child, index) => {
                if (!React.isValidElement(child)) return child;

                const isFirst = index === 0;
                const isLast = index === React.Children.count(children) - 1;

                return React.cloneElement(child as React.ReactElement<{ className?: string }>, {
                    className: clsx(
                        (child as React.ReactElement<{ className?: string }>).props.className,
                        !isFirst && '-ml-px',
                        isFirst && 'rounded-r-none',
                        isLast && 'rounded-l-none',
                        !isFirst && !isLast && 'rounded-none'
                    ),
                });
            })}
        </div>
    );
};

export default Button;