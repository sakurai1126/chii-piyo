import { NextRequest, NextResponse } from "next/server";

import { getMediaList } from "@/features/media/server";
import { GetMediaListMediaKindEnum } from "@/lib/api-client/gen";
import { handleApiError } from "@/utils/api";

export const GET = async (request: NextRequest) => {
  const params = request.nextUrl.searchParams;
  const offset = params.get("offset");
  const limit = params.get("limit");
  const mediaKind = params.get("mediaKind");
  const albumId = params.get("albumId");
  const excludeAlbumId = params.get("excludeAlbumId");
  const tagIds = params.getAll("tagId");
  const sharingGroupId = params.get("sharingGroupId");
  const startDate = params.get("startDate");
  const endDate = params.get("endDate");
  const isFavorite = params.get("isFavorite");

  try {
    const data = await getMediaList({
      offset: offset ? Number(offset) : undefined,
      limit: limit ? Number(limit) : undefined,
      mediaKind: (mediaKind as GetMediaListMediaKindEnum) ?? undefined,
      albumId: albumId ? Number(albumId) : undefined,
      excludeAlbumId: excludeAlbumId ? Number(excludeAlbumId) : undefined,
      tagId: tagIds.length > 0 ? tagIds.map(Number) : undefined,
      sharingGroupId: sharingGroupId ? Number(sharingGroupId) : undefined,
      startDate: startDate ? new Date(startDate) : undefined,
      endDate: endDate ? new Date(endDate) : undefined,
      isFavorite: isFavorite === "true" ? true : undefined,
    });
    return NextResponse.json(data);
  } catch (error) {
    return handleApiError(error, "メディアの取得に失敗しました");
  }
};
