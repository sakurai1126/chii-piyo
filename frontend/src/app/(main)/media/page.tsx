import Image from "next/image";

import Container from "@/components/layout/Container";
import { AccentLinkButton } from "@/components/ui/AccentLinkButton";
import PageTitle from "@/components/ui/PageTitle";
import { getUsers } from "@/features/auth/actions/getUsers";
import { MediaFilter, getMediaList, MediaListSection } from "@/features/media/";

export default async function MediaPage() {
  const [initialData, users] = await Promise.all([
    getMediaList({ offset: 0, limit: 12 }),
    getUsers(),
  ]);

  return (
    <Container className="mt-20 max-md:mt-5">
      <PageTitle text="写真・動画一覧" />

      {/* 絞り込みUI */}
      <MediaFilter />

      {/* 遷移ボタン */}
      <AccentLinkButton href="/upload" className="mt-10 ml-auto max-md:mt-4">
        <p>新規アップロード</p>
        <Image
          src="/images/upload.svg"
          alt=""
          width={22}
          height={22}
          className="max-md:h-4 max-md:w-4"
        />
      </AccentLinkButton>

      {/* 一括編集UI+メディアグリッド */}
      <MediaListSection initialData={initialData} users={users} />
    </Container>
  );
}
