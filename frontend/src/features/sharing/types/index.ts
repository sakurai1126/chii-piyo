import { type SharingGroupResponseDto } from "@/lib/api-client/gen";

export type UseSharingGroupsResult = {
  sharingGroups: SharingGroupResponseDto[];
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
};
