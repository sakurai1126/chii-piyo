import Container from "@/components/layout/Container";
import { PageTitle } from "@/components/ui/PageTitle";
import { getAlbums } from "@/features/album/server";
import { getCurrentUser, isAdminUser, isEasyMode } from "@/features/auth";
import { getUsers } from "@/features/auth/actions/getUsers";
import {
  Account,
  Albums,
  DisplayMode,
  Members,
  Profile,
  SharingGroups,
  Sidebar,
  Tags,
} from "@/features/settings";
import { getAllSharingGroups } from "@/features/sharing/server";
import { getTags } from "@/features/tag/server";

export default async function SettingsPage() {
  const [isAdmin, isEasy, currentUser, users, tags, albums] = await Promise.all([
    isAdminUser(),
    isEasyMode(),
    getCurrentUser(),
    getUsers(),
    getTags(),
    getAlbums(),
  ]);

  const sharingGroups = isAdmin ? await getAllSharingGroups() : undefined;

  return (
    <Container className={`mt-20 @max-md:mt-5 ${isEasy ? "px-5" : ""}`}>
      <div className="relative flex items-start gap-10 @max-lg:gap-5 @max-md:flex-col">
        {/* サイドバー */}
        {!isEasy && <Sidebar isAdmin={isAdmin} />}

        <div className="w-full">
          {/* タイトル */}
          <PageTitle isEasy={isEasy} text="設定" />
          {/* プロフィール */}
          <Profile isEasy={isEasy} currentUser={currentUser} />
          {/* メンバー一覧 */}
          {!isEasy && (
            <Members
              isAdmin={isAdmin}
              currentUser={currentUser}
              users={users}
              sharingGroups={sharingGroups}
            />
          )}

          {isAdmin && !isEasy && (
            <>
              {/* タグ */}
              <Tags tags={tags} />
              {/* 共有範囲 */}
              {sharingGroups && <SharingGroups users={users} sharingGroups={sharingGroups} />}
              {/* アルバム */}
              <Albums albums={albums} />
            </>
          )}

          {/* 表示モード */}
          <DisplayMode isEasy={isEasy} currentUser={currentUser} />
          {/* アカウント */}
          <Account isEasy={isEasy} />
        </div>
      </div>
    </Container>
  );
}
