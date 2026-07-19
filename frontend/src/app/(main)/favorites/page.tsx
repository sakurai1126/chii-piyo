import { Container } from "@/components/layout/Container";
import { PageTitle } from "@/components/ui/PageTitle";
import { isEasyMode, getUsers } from "@/features/auth/server";
import { FavoriteMedia } from "@/features/media";
import { getMediaList } from "@/features/media/server";

export default async function FavoritesPage() {
  const [isEasy, initialData, users] = await Promise.all([
    isEasyMode(),
    getMediaList({ offset: 0, limit: 12, isFavorite: true }),
    getUsers(),
  ]);

  return (
    <Container className="mt-20 @max-md:mt-5">
      <PageTitle isEasy={isEasy} text="お気に入り" />

      {/* お気に入りメディアグリッド */}
      <FavoriteMedia isEasy={isEasy} initialData={initialData} users={users} />
    </Container>
  );
}
