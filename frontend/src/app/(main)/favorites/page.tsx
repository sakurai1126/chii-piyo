import Container from "@/components/layout/Container";
import PageTitle from "@/components/ui/PageTitle";
import { getUsers } from "@/features/auth/actions/getUsers";
import { FavoriteMedia } from "@/features/media";
import { getMediaList } from "@/features/media/server";

export default async function FavoritesPage() {
  const [initialData, users] = await Promise.all([
    getMediaList({ offset: 0, limit: 12, isFavorite: true }),
    getUsers(),
  ]);

  return (
    <Container className="mt-20 max-md:mt-5">
      <PageTitle text="お気に入り" />

      {/* お気に入りメディアグリッド */}
      <FavoriteMedia initialData={initialData} users={users} />
    </Container>
  );
}
