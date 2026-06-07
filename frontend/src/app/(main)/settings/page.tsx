import Container from "@/components/layout/Container";
import PageTitle from "@/components/ui/PageTitle";
import { getCurrentUser } from "@/features/auth";
import { getUsers } from "@/features/auth/actions/getUsers";
import {
  Account,
  DisplayMode,
  Members,
  Profile,
  SharingGroups,
  Sidebar,
} from "@/features/settings";
import { getSharingGroups } from "@/features/sharing/api/getSharingGroups";

export default async function SettingsPage() {
  const [currentUser, users, sharingGroups] = await Promise.all([
    getCurrentUser(),
    getUsers(),
    getSharingGroups(),
  ]);

  return (
    <Container className="mt-20 max-md:mt-5">
      <div className="relative flex items-start gap-10 max-lg:gap-5 max-md:flex-col">
        {/* サイドバー */}
        <Sidebar />
        <div className="w-full">
          {/* タイトル */}
          <PageTitle text="設定" />
          {/* プロフィール */}
          <Profile currentUser={currentUser} />
          {/* メンバー一覧 */}
          <Members users={users} sharingGroups={sharingGroups} />
          {/* 共有範囲 */}
          <SharingGroups users={users} sharingGroups={sharingGroups} />
          {/* 表示モード */}
          <DisplayMode currentUser={currentUser} />
          {/* アカウント */}
          <Account />
        </div>
      </div>
    </Container>
  );
}
