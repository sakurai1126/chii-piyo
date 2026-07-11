import Container from "@/components/layout/Container";
import PageTitle from "@/components/ui/PageTitle";
import { getAlbums } from "@/features/album/server";
import { getCurrentUser, isAdminUser } from "@/features/auth";
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
  const [isAdmin, currentUser, users, tags, albums] = await Promise.all([
    isAdminUser(),
    getCurrentUser(),
    getUsers(),
    getTags(),
    getAlbums(),
  ]);

  const sharingGroups = isAdmin ? await getAllSharingGroups() : undefined;

  return (
    <Container className="mt-20 max-md:mt-5">
      <div className="relative flex items-start gap-10 max-lg:gap-5 max-md:flex-col">
        {/* サイドバー */}
        <Sidebar isAdmin={isAdmin} />
        <div className="w-full">
          {/* タイトル */}
          <PageTitle text="設定" />
          {/* プロフィール */}
          <Profile currentUser={currentUser} />
          {/* メンバー一覧 */}
          <Members
            isAdmin={isAdmin}
            currentUser={currentUser}
            users={users}
            sharingGroups={sharingGroups}
          />
          {isAdmin && (
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
          <DisplayMode currentUser={currentUser} />
          {/* アカウント */}
          <Account />
        </div>
      </div>
    </Container>
  );
}
