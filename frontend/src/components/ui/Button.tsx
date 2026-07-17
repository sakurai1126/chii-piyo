import { cn } from "@/utils/cn";

const variantStyles = {
  primary:
    "border-brown-middle text-brown-middle bg-brown-back hover:bg-brown-light h-10 w-35 cursor-pointer rounded-lg border text-sm font-medium transition-all duration-300 hover:text-white @max-md:text-xs",
  cancel:
    "border-line-gray bg-cancel-back text-black-text hover:bg-cancel-hover h-10 w-35 cursor-pointer rounded-lg border text-sm font-medium transition-all duration-300 dark:hover:text-white @max-md:text-xs",
  remove:
    "border-remove h-10 w-35 cursor-pointer rounded-lg border bg-remove-back text-remove text-sm font-medium transition-all duration-300 hover:bg-remove @max-md:text-xs hover:text-white",
};

type ButtonProps = React.ButtonHTMLAttributes<HTMLButtonElement> & {
  isEasy?: boolean;
  variant?: keyof typeof variantStyles;
  className?: string;
  disabledStyle?: boolean;
};

export const Button = ({
  isEasy = false,
  variant = "primary",
  disabledStyle = false,
  children,
  ...props
}: ButtonProps) => {
  return (
    <button
      {...props}
      className={cn(
        variantStyles[variant],
        disabledStyle &&
          "border-disabled-text bg-disabled-back text-disabled-text pointer-events-none cursor-not-allowed",
        isEasy && variant === "primary" && "bg-brown-light text-white",
        props.className,
      )}
    >
      {children}
    </button>
  );
};
