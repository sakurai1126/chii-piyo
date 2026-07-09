import Link from "next/link";

import { isAdminUser } from "@/features/auth";

type Props = {
  currentPage: "care" | "graph" | "first" | "word";
};

const variantStyles = {
  current: "bg-accent-pink text-white",
  other: "hover:bg-accent-pink/80 hover:text-white transition-all text-brown-dark cursor-pointer",
};

export const ChildCareNavigation = async ({ currentPage }: Props) => {
  const isAdmin = await isAdminUser();

  return (
    <div className="bg-background-accent dark:border-brown-dark mx-auto w-fit rounded-lg p-2 max-md:py-1 dark:border">
      <div className="flex gap-2 max-md:grid max-md:grid-cols-4">
        {isAdmin && (
          <Link
            href="/care"
            className={`grid h-12 w-40 place-content-center rounded-lg font-medium max-md:h-10 max-md:px-2 dark:text-white ${currentPage === "care" ? variantStyles["current"] : variantStyles["other"]} max-md:w-auto max-md:text-xs`}
          >
            記録
          </Link>
        )}

        <Link
          href="/analysis"
          className={`grid h-12 w-40 place-content-center rounded-lg font-medium max-md:h-10 max-md:px-2 dark:text-white ${currentPage === "graph" ? variantStyles["current"] : variantStyles["other"]} max-md:w-auto max-md:text-xs`}
        >
          グラフ
        </Link>
        <Link
          href="/first-records"
          className={`grid h-12 w-40 place-content-center rounded-lg font-medium max-md:h-10 max-md:px-2 dark:text-white ${currentPage === "first" ? variantStyles["current"] : variantStyles["other"]} max-md:w-auto max-md:text-xs`}
        >
          はじめて
        </Link>
        <Link
          href="/word-records"
          className={`grid h-12 w-40 place-content-center rounded-lg font-medium max-md:h-10 max-md:px-2 dark:text-white ${currentPage === "word" ? variantStyles["current"] : variantStyles["other"]} max-md:w-auto max-md:text-xs`}
        >
          ことば
        </Link>
      </div>
    </div>
  );
};
