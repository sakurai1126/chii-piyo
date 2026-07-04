import Link from "next/link";

type Props = {
  currentPage: "care" | "graph" | "first" | "word";
};

const variantStyles = {
  current: "bg-accent-pink text-white",
  other: "hover:bg-accent-pink/80 hover:text-white transition-all text-brown-dark cursor-pointer",
};

export const ChildCareNavigation = ({ currentPage }: Props) => {
  return (
    <div className="bg-brown-back mx-auto w-fit rounded-lg p-2 max-md:py-1">
      <div className="flex gap-2 max-md:grid max-md:grid-cols-4">
        <Link
          href="/care"
          className={`grid h-12 w-40 place-content-center rounded-lg font-medium max-md:h-10 max-md:px-2 ${currentPage === "care" ? variantStyles["current"] : variantStyles["other"]} max-md:w-auto max-md:text-xs`}
        >
          記録
        </Link>
        <button
          className={`grid h-12 w-40 place-content-center rounded-lg font-medium max-md:h-10 max-md:px-2 ${currentPage === "graph" ? variantStyles["current"] : variantStyles["other"]} max-md:w-auto max-md:text-xs`}
        >
          グラフ
        </button>
        <Link
          href="/first-records"
          className={`grid h-12 w-40 place-content-center rounded-lg font-medium max-md:h-10 max-md:px-2 ${currentPage === "first" ? variantStyles["current"] : variantStyles["other"]} max-md:w-auto max-md:text-xs`}
        >
          はじめて
        </Link>
        <Link
          href="/word-records"
          className={`grid h-12 w-40 place-content-center rounded-lg font-medium max-md:h-10 max-md:px-2 ${currentPage === "word" ? variantStyles["current"] : variantStyles["other"]} max-md:w-auto max-md:text-xs`}
        >
          ことば
        </Link>
      </div>
    </div>
  );
};
