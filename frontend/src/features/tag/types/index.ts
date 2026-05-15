import { type TagResponseDto } from "@/lib/api-client/gen";

export type UseTagsResult = {
  tags: TagResponseDto[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
};
