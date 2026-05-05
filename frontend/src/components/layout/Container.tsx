type Props = {
  children: React.ReactNode;
  className?: string;
};

export default function Container({ children, className }: Readonly<Props>) {
  return <div className={`mx-auto max-w-250 px-5 ${className ?? ""}`}>{children}</div>;
}
