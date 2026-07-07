const variantStyles = {
  primary:
    "border-brown-middle text-brown-middle bg-brown-back hover:bg-brown-light h-10 w-35 cursor-pointer rounded-lg border text-sm font-medium transition-all duration-300 hover:text-white max-md:text-xs",
  cancel:
    "border-line-gray h-10 w-35 cursor-pointer rounded-lg border bg-cancel-back text-black-text text-sm font-medium transition-all duration-300 hover:bg-cancel-hover max-md:text-xs",
  remove:
    "border-remove h-10 w-35 cursor-pointer rounded-lg border bg-remove-back text-remove text-sm font-medium transition-all duration-300 hover:bg-remove max-md:text-xs hover:text-white",
};

type ButtonProps = React.ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: keyof typeof variantStyles;
  className?: string;
  disabledStyle?: boolean;
};

export const Button = ({
  variant = "primary",
  disabledStyle = false,
  children,
  ...props
}: ButtonProps) => {
  return (
    <button
      {...props}
      className={`${variantStyles[variant]} ${disabledStyle ? "border-disabled-text bg-disabled-back text-disabled-text pointer-events-none cursor-not-allowed" : ""} ${props.className || ""}`}
    >
      {children}
    </button>
  );
};
