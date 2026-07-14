import { isEasyMode } from "@/features/auth";
import { cn } from "@/utils/cn";

type Props = {
  children: React.ReactNode;
  className?: string;
};

export default async function Container({ children, className }: Readonly<Props>) {
  const isEasy = await isEasyMode();
  return <div className={cn("mx-auto max-w-250", !isEasy && "px-5", className)}>{children}</div>;
}
