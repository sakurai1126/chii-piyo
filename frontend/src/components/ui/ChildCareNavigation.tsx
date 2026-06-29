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
        <button
          className={`grid h-12 w-40 place-content-center rounded-lg font-medium max-md:h-10 ${currentPage === "care" ? variantStyles["current"] : variantStyles["other"]} max-md:w-auto max-md:text-xs`}
        >
          記録
        </button>
        <button
          className={`grid h-12 w-40 place-content-center rounded-lg font-medium max-md:h-10 ${currentPage === "graph" ? variantStyles["current"] : variantStyles["other"]} max-md:w-auto max-md:text-xs`}
        >
          グラフ
        </button>
        <button
          className={`grid h-12 w-45 place-content-center rounded-lg font-medium max-md:h-10 ${currentPage === "first" ? variantStyles["current"] : variantStyles["other"]} max-md:w-auto max-md:text-xs`}
        >
          はじめて一覧
        </button>
        <button
          className={`grid h-12 w-40 place-content-center rounded-lg font-medium max-md:h-10 ${currentPage === "word" ? variantStyles["current"] : variantStyles["other"]} max-md:w-auto max-md:text-xs`}
        >
          ことば
        </button>
      </div>
    </div>
  );
};
