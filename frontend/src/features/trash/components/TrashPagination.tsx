import Link from "next/link";

type Props = {
  totalCount: number;
  currentPage: number;
  limit: number;
};

export const TrashPagination = ({ totalCount, currentPage, limit }: Props) => {
  const totalPages = Math.ceil(totalCount / limit);

  // 1ページしかない場合は表示しない
  if (totalPages <= 1) return null;

  return (
    <div className="mt-4 flex flex-wrap justify-end gap-x-3 gap-y-1">
      {/* 前へ */}
      {currentPage > 1 && <Link href={`?page=${currentPage - 1}`}>&lt;</Link>}

      {/* ページ番号リンク */}
      {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
        <Link key={page} href={`?page=${page}`} className={page === currentPage ? "underline" : ""}>
          {page}
        </Link>
      ))}

      {/* 次へ */}
      {currentPage < totalPages && <Link href={`?page=${currentPage + 1}`}>&gt;</Link>}
    </div>
  );
};
