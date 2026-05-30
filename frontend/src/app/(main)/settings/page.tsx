import Container from "@/components/layout/Container";
import PageTitle from "@/components/ui/PageTitle";
import { getCurrentUser } from "@/features/auth";
import {
  Account,
  DisplayMode,
  Members,
  Profile,
  SharingGroups,
  Sidebar,
} from "@/features/settings";

export default async function SettingsPage() {
  const [currentUser] = await Promise.all([getCurrentUser()]);

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
          <Members />
          {/* 共有範囲 */}
          <SharingGroups />
          {/* 表示モード */}
          <DisplayMode />
          {/* アカウント */}
          <Account />
        </div>
      </div>
    </Container>
  );
}
