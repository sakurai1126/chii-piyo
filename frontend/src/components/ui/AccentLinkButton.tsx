import Link from "next/link";

type Props = React.AnchorHTMLAttributes<HTMLAnchorElement> & {
  href: string;
  children: React.ReactNode;
  className?: string;
};

export const AccentLinkButton = ({ href, children, ...props }: Props) => {
  return (
    <Link
      href={href}
      {...props}
      className={`border-brown-dark bg-brown-light hover:bg-borwn-dark hover:bg-brown-dark flex h-10 w-fit cursor-pointer items-center justify-center gap-3 rounded-lg border px-7 text-sm font-medium text-white transition-all duration-300 max-md:gap-2 max-md:px-3 max-md:text-xs ${props.className || ""}`}
    >
      {children}
    </Link>
  );
};
