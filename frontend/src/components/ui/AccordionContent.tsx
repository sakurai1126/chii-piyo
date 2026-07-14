import { cn } from "@/utils/cn";

type Props = React.HTMLAttributes<HTMLDivElement> & {
  isOpen: boolean;
  children: React.ReactNode;
};

export const AccordionContent = ({ isOpen, children, ...props }: Props) => {
  return (
    <div
      className={cn(
        "grid grid-rows-[0fr] transition-all duration-400",
        isOpen && "grid-rows-[1fr]",
      )}
      {...props}
    >
      <div className="overflow-hidden">{children}</div>
    </div>
  );
};
