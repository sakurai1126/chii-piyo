import Image from "next/image";

import Container from "@/components/layout/Container";
import { AccentLinkButton } from "@/components/ui/AccentLinkButton";
import PageTitle from "@/components/ui/PageTitle";
import { MultiEdit, MediaFilter, MediaList, getMediaListAction } from "@/features/media/";

export default async function MediaPage() {
  const result = await getMediaListAction();

  return result.success ? (
    <Container className="mt-20 max-md:mt-5">
      <PageTitle text="写真・動画一覧" />

      {/* 絞り込みUI */}
      <MediaFilter />
      {/* ボタン */}

      <AccentLinkButton href="/media/upload" className="mt-10 ml-auto max-md:mt-4">
        <p>新規アップロード</p>
        <Image
          src="/images/upload.svg"
          alt=""
          width={22}
          height={22}
          className="max-md:h-4 max-md:w-4"
        />
      </AccentLinkButton>

      {/* 一括編集UI */}
      <MultiEdit />

      {/* メディア一覧 */}
      <MediaList data={result.data} />
    </Container>
  ) : (
    <Container className="mt-20 max-md:mt-5">
      <PageTitle text="写真・動画一覧" />
      <Image
        src="/images/error.png"
        alt=""
        width={230}
        height={146}
        className="mx-auto mt-10 max-md:w-38"
      />
      <p className="mt-10 text-center font-medium">{result.error}...</p>
      <a
        href="/media"
        className="bg-brown-light border-brown-middle hover:text-brown-dark mx-auto mt-8 grid h-12 w-60 place-content-center rounded-lg border text-white transition-all hover:bg-white max-md:mt-5 max-md:h-9 max-md:w-45 max-md:text-sm"
      >
        再読み込みする
      </a>
    </Container>
  );
}
