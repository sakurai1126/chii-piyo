import { type AlbumResponseDto } from "@/lib/api-client/gen";

export type UseAlbumsResult = {
  albums: AlbumResponseDto[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
};
