import Link from "next/link";

type ButtonProps = React.ButtonHTMLAttributes<HTMLButtonElement> & {
  variant: "button";
  href?: never;
  children: React.ReactNode;
  className?: string;
};

type LinkProps = React.AnchorHTMLAttributes<HTMLAnchorElement> & {
  variant: "link";
  href: string;
  children: React.ReactNode;
  className?: string;
};

type Props = ButtonProps | LinkProps;

const BASE_CLASS =
  "border-brown-dark bg-brown-light hover:bg-brown-dark flex h-10 w-fit cursor-pointer items-center justify-center gap-3 rounded-lg border px-7 text-sm font-medium text-white transition-all duration-300 max-md:gap-2 max-md:px-3 max-md:text-xs";

export const AccentButton = (props: Props) => {
  if (props.variant === "button") {
    const { children, className, ...buttonProps } = props;
    return (
      <button {...buttonProps} className={`${BASE_CLASS} ${className ?? ""}`}>
        {children}
      </button>
    );
  }

  const { children, className, href } = props;
  return (
    <Link href={href} className={`${BASE_CLASS} ${className ?? ""}`}>
      {children}
    </Link>
  );
};
