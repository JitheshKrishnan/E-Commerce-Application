import React, { forwardRef } from 'react';
import { clsx } from 'clsx';
import type { InputProps } from '../../types';

const Input = forwardRef<HTMLInputElement, InputProps>(({
                                                            label,
                                                            type = 'text',
                                                            placeholder,
                                                            value,
                                                            onChange,
                                                            onBlur,
                                                            error,
                                                            disabled = false,
                                                            required = false,
                                                            className = '',
                                                            ...props
                                                        }, ref) => {
    const inputId = React.useId();

    const inputClasses = clsx(
        'block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400',
        'focus:outline-none focus:ring-2 focus:ring-offset-0 transition-colors duration-200',
        'disabled:bg-gray-50 disabled:text-gray-500 disabled:cursor-not-allowed',
        {
            'border-gray-300 focus:ring-blue-500 focus:border-blue-500': !error,
            'border-red-300 focus:ring-red-500 focus:border-red-500 bg-red-50': error,
        },
        className
    );

    return (
        <div className="w-full">
            {label && (
                <label
                    htmlFor={inputId}
                    className="block text-sm font-medium text-gray-700 mb-1"
                >
                    {label}
                    {required && <span className="text-red-500 ml-1">*</span>}
                </label>
            )}

            <input
                ref={ref}
                id={inputId}
                type={type}
                className={inputClasses}
                placeholder={placeholder}
                value={value}
                onChange={onChange}
                onBlur={onBlur}
                disabled={disabled}
                required={required}
                aria-invalid={error ? 'true' : 'false'}
                aria-describedby={error ? `${inputId}-error` : undefined}
                {...props}
            />

            {error && (
                <p
                    id={`${inputId}-error`}
                    className="mt-1 text-sm text-red-600"
                >
                    {error}
                </p>
            )}
        </div>
    );
});

Input.displayName = 'Input';

// Textarea component
interface TextareaProps {
    label?: string;
    placeholder?: string;
    value?: string;
    onChange?: (e: React.ChangeEvent<HTMLTextAreaElement>) => void;
    onBlur?: (e: React.FocusEvent<HTMLTextAreaElement>) => void;
    error?: string;
    disabled?: boolean;
    required?: boolean;
    rows?: number;
    className?: string;
}

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(({
                                                                            label,
                                                                            placeholder,
                                                                            value,
                                                                            onChange,
                                                                            onBlur,
                                                                            error,
                                                                            disabled = false,
                                                                            required = false,
                                                                            rows = 4,
                                                                            className = '',
                                                                            ...props
                                                                        }, ref) => {
    const textareaId = React.useId();

    const textareaClasses = clsx(
        'block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400',
        'focus:outline-none focus:ring-2 focus:ring-offset-0 transition-colors duration-200',
        'disabled:bg-gray-50 disabled:text-gray-500 disabled:cursor-not-allowed',
        'resize-vertical',
        {
            'border-gray-300 focus:ring-blue-500 focus:border-blue-500': !error,
            'border-red-300 focus:ring-red-500 focus:border-red-500 bg-red-50': error,
        },
        className
    );

    return (
        <div className="w-full">
            {label && (
                <label
                    htmlFor={textareaId}
                    className="block text-sm font-medium text-gray-700 mb-1"
                >
                    {label}
                    {required && <span className="text-red-500 ml-1">*</span>}
                </label>
            )}

            <textarea
                ref={ref}
                id={textareaId}
                className={textareaClasses}
                placeholder={placeholder}
                value={value}
                onChange={onChange}
                onBlur={onBlur}
                disabled={disabled}
                required={required}
                rows={rows}
                aria-invalid={error ? 'true' : 'false'}
                aria-describedby={error ? `${textareaId}-error` : undefined}
                {...props}
            />

            {error && (
                <p
                    id={`${textareaId}-error`}
                    className="mt-1 text-sm text-red-600"
                >
                    {error}
                </p>
            )}
        </div>
    );
});

Textarea.displayName = 'Textarea';

// Select component
interface SelectOption {
    value: string;
    label: string;
}

interface SelectProps {
    label?: string;
    value?: string;
    onChange?: (e: React.ChangeEvent<HTMLSelectElement>) => void;
    options: SelectOption[];
    placeholder?: string;
    error?: string;
    disabled?: boolean;
    required?: boolean;
    className?: string;
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(({
                                                                      label,
                                                                      value,
                                                                      onChange,
                                                                      options,
                                                                      placeholder,
                                                                      error,
                                                                      disabled = false,
                                                                      required = false,
                                                                      className = '',
                                                                      ...props
                                                                  }, ref) => {
    const selectId = React.useId();

    const selectClasses = clsx(
        'block w-full px-3 py-2 border rounded-md shadow-sm',
        'focus:outline-none focus:ring-2 focus:ring-offset-0 transition-colors duration-200',
        'disabled:bg-gray-50 disabled:text-gray-500 disabled:cursor-not-allowed',
        'appearance-none bg-white',
        {
            'border-gray-300 focus:ring-blue-500 focus:border-blue-500': !error,
            'border-red-300 focus:ring-red-500 focus:border-red-500 bg-red-50': error,
        },
        className
    );

    return (
        <div className="w-full">
            {label && (
                <label
                    htmlFor={selectId}
                    className="block text-sm font-medium text-gray-700 mb-1"
                >
                    {label}
                    {required && <span className="text-red-500 ml-1">*</span>}
                </label>
            )}

            <div className="relative">
                <select
                    ref={ref}
                    id={selectId}
                    className={selectClasses}
                    value={value}
                    onChange={onChange}
                    disabled={disabled}
                    required={required}
                    aria-invalid={error ? 'true' : 'false'}
                    aria-describedby={error ? `${selectId}-error` : undefined}
                    {...props}
                >
                    {placeholder && (
                        <option value="" disabled>
                            {placeholder}
                        </option>
                    )}
                    {options.map((option) => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>

                {/* Custom dropdown arrow */}
                <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                    <svg className="fill-current h-4 w-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20">
                        <path d="M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z"/>
                    </svg>
                </div>
            </div>

            {error && (
                <p
                    id={`${selectId}-error`}
                    className="mt-1 text-sm text-red-600"
                >
                    {error}
                </p>
            )}
        </div>
    );
});

Select.displayName = 'Select';

// Input with icon
interface InputWithIconProps extends InputProps {
    icon: React.ReactNode;
    iconPosition?: 'left' | 'right';
}

export const InputWithIcon: React.FC<InputWithIconProps> = ({
                                                                icon,
                                                                iconPosition = 'left',
                                                                className = '',
                                                                ...props
                                                            }) => {
    const iconClasses = clsx(
        'absolute inset-y-0 flex items-center pointer-events-none',
        {
            'left-0 pl-3': iconPosition === 'left',
            'right-0 pr-3': iconPosition === 'right',
        }
    );

    const inputClasses = clsx(
        {
            'pl-10': iconPosition === 'left',
            'pr-10': iconPosition === 'right',
        },
        className
    );

    return (
        <div className="w-full">
            {props.label && (
                <label className="block text-sm font-medium text-gray-700 mb-1">
                    {props.label}
                    {props.required && <span className="text-red-500 ml-1">*</span>}
                </label>
            )}

            <div className="relative">
                <div className={iconClasses}>
                    <span className="text-gray-400 h-5 w-5">{icon}</span>
                </div>
                <Input {...props} className={inputClasses} label={undefined} />
            </div>
        </div>
    );
};

export default Input;