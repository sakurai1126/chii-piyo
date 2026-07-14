import Image from "next/image";

import Container from "@/components/layout/Container";
import { AccentButton } from "@/components/ui/AccentButton";
import { isAdminUser, isEasyMode } from "@/features/auth";
import { getUsers } from "@/features/auth/actions/getUsers";
import { MediaFilter, MediaListSection, MediaTitle } from "@/features/media";
import { getMediaList } from "@/features/media/server";
import { getSharingGroups } from "@/features/sharing/server";
import { getTags } from "@/features/tag/server";
import { cn } from "@/utils/cn";

export default async function MediaPage() {
  const [isAdmin, isEasy, initialData, users, tags, sharingGroups] = await Promise.all([
    isAdminUser(),
    isEasyMode(),
    getMediaList({ offset: 0, limit: 12 }),
    getUsers(),
    getTags(),
    getSharingGroups(),
  ]);

  return (
    <Container className="mt-20 @max-md:mt-5">
      <MediaTitle isEasy={isEasy} />

      {/* 絞り込みUI */}
      <MediaFilter isEasy={isEasy} tags={tags} sharingGroups={sharingGroups} />

      {/* 遷移ボタン */}
      <AccentButton
        href="/upload"
        className={cn(
          "mt-10 ml-auto",
          isEasy ? "mx-auto @max-md:px-6 @max-md:text-[15px]" : "@max-md:mt-4",
        )}
        variant="link"
      >
        <p>新規アップロード</p>
        <Image
          src="/images/upload.svg"
          alt=""
          width={22}
          height={22}
          className="max-md:w-4 @max-md:h-4"
        />
      </AccentButton>

      {/* 一括編集UI+メディアグリッド */}
      <MediaListSection
        isAdmin={isAdmin}
        isEasy={isEasy}
        initialData={initialData}
        users={users}
        tags={tags}
        sharingGroups={sharingGroups}
      />
    </Container>
  );
}
