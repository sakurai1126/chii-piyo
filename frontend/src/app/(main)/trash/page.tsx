import Container from "@/components/layout/Container";
import PageTitle from "@/components/ui/PageTitle";
import { TrashAllDelete, TrashContent, TrashInfo, TrashPagination } from "@/features/trash";
import { getTrashItems } from "@/features/trash/server";

type Props = {
  searchParams: Promise<{ [key: string]: string | string[] | undefined }>;
};

export default async function TrashPage({ searchParams }: Readonly<Props>) {
  // パラメータからページ数を取得
  const params = await searchParams;
  // ページ数を指定（不正な値は1に）
  const page = Math.max(1, Number(params.page) || 1);
  // 1ページに表示する件数
  const limit = 50;
  // 開始位置を計算
  const offset = (page - 1) * limit;
  const trashItems = await getTrashItems({ offset, limit });

  return (
    <Container className="mt-20 max-md:mt-5">
      <PageTitle text="ゴミ箱" />
      <TrashInfo trashItems={trashItems} />
      {trashItems.totalCount > 0 && (
        <>
          <TrashContent key={page} trashItems={trashItems} />
          <TrashPagination totalCount={trashItems.totalCount} currentPage={page} limit={limit} />
          <TrashAllDelete />
        </>
      )}
    </Container>
  );
}
