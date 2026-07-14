import Link from "next/link";

import { cn } from "@/utils/cn";

const variantStyles = {
  primary:
    "border-brown-dark bg-brown-light hover:bg-brown-dark flex h-10 w-fit cursor-pointer items-center justify-center gap-3 rounded-lg border px-7 text-sm font-medium text-white transition-all duration-300 @max-md:gap-2 @max-md:px-3 @max-md:text-xs",
  cancel:
    "border-line-gray bg-cancel-back text-black-text hover:bg-cancel-hover mx-auto mt-5 flex h-10 w-fit cursor-pointer items-center justify-center gap-3 rounded-lg border px-7 text-sm font-medium transition-all duration-300 @max-md:gap-2 @max-md:px-3 @max-md:text-xs",
};

type ButtonProps = React.ButtonHTMLAttributes<HTMLButtonElement> & {
  variant: "button";
  href?: never;
  children: React.ReactNode;
  styleVariant?: keyof typeof variantStyles;
  className?: string;
};

type LinkProps = React.AnchorHTMLAttributes<HTMLAnchorElement> & {
  variant: "link";
  styleVariant?: keyof typeof variantStyles;
  href: string;
  children: React.ReactNode;
  className?: string;
};

type aProps = React.AnchorHTMLAttributes<HTMLAnchorElement> & {
  variant: "a";
  styleVariant?: keyof typeof variantStyles;
  href: string;
  children: React.ReactNode;
  className?: string;
};

type Props = ButtonProps | LinkProps | aProps;

export const AccentButton = ({ styleVariant = "primary", ...props }: Props) => {
  if (props.variant === "button") {
    const { children, className, ...buttonProps } = props;
    return (
      <button {...buttonProps} className={cn(variantStyles[styleVariant], className)}>
        {children}
      </button>
    );
  }

  if (props.variant === "link") {
    const { children, className, href } = props;
    return (
      <Link href={href} className={cn(variantStyles[styleVariant], className)}>
        {children}
      </Link>
    );
  }

  if (props.variant === "a") {
    const { children, className, href } = props;
    return (
      <a href={href} className={cn(variantStyles[styleVariant], className)}>
        {children}
      </a>
    );
  }

  throw new Error("未対応のvariantです");
};
