import Link from "next/link";

import { isAdminUser, isEasyMode } from "@/features/auth";
import { cn } from "@/utils/cn";

type Props = {
  currentPage: "care" | "graph" | "first" | "word";
};

const variantStyles = {
  current: "bg-accent-pink text-white",
  other: "hover:bg-accent-pink/80 hover:text-white transition-all text-brown-dark cursor-pointer",
};

export const ChildCareNavigation = async ({ currentPage }: Props) => {
  const [isAdmin, isEasy] = await Promise.all([isAdminUser(), isEasyMode()]);

  return (
    <div className="bg-background-accent dark:border-brown-dark mx-auto w-fit rounded-lg p-2 @max-md:py-1 dark:border">
      <div
        className={cn(
          "flex gap-2 @max-md:grid @max-md:grid-cols-3",
          isAdmin && !isEasy && "@max-md:grid-cols-4",
        )}
      >
        {isAdmin && !isEasy && (
          <Link
            href="/care"
            className={cn(
              "grid h-12 w-40 place-content-center rounded-lg font-medium @max-md:h-10 @max-md:w-auto @max-md:px-2 @max-md:text-xs dark:text-white",
              currentPage === "care" ? variantStyles["current"] : variantStyles["other"],
            )}
          >
            記録
          </Link>
        )}

        <Link
          href="/analysis"
          className={cn(
            "grid h-12 w-40 place-content-center rounded-lg font-medium @max-md:h-10 @max-md:w-auto @max-md:px-2 @max-md:text-xs dark:text-white",
            currentPage === "graph" ? variantStyles["current"] : variantStyles["other"],
            isEasy && "@max-md:text-sm",
          )}
        >
          グラフ
        </Link>
        <Link
          href="/first-records"
          className={cn(
            "grid h-12 w-40 place-content-center rounded-lg font-medium @max-md:h-10 @max-md:w-auto @max-md:px-2 @max-md:text-xs dark:text-white",
            currentPage === "first" ? variantStyles["current"] : variantStyles["other"],
            isEasy && "@max-md:text-sm",
          )}
        >
          はじめて
        </Link>
        <Link
          href="/word-records"
          className={cn(
            "grid h-12 w-40 place-content-center rounded-lg font-medium @max-md:h-10 @max-md:w-auto @max-md:px-2 @max-md:text-xs dark:text-white",
            currentPage === "word" ? variantStyles["current"] : variantStyles["other"],
            isEasy && "@max-md:text-sm",
          )}
        >
          ことば
        </Link>
      </div>
    </div>
  );
};
