type Props = React.HTMLAttributes<HTMLDivElement> & {
  isOpen: boolean;
  children: React.ReactNode;
};

export const AccordionContent = ({ isOpen, children, ...props }: Props) => {
  return (
    <div
      className={`grid transition-all duration-400 ${isOpen ? "grid-rows-[1fr]" : "grid-rows-[0fr]"}`}
      {...props}
    >
      <div className="overflow-hidden">{children}</div>
    </div>
  );
};
