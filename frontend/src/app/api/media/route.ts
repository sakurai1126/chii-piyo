import { NextRequest, NextResponse } from "next/server";

import { getMediaList } from "@/features/media/api/getMediaList";
import { GetMediaListMediaKindEnum } from "@/lib/api-client/gen";

export const GET = async (request: NextRequest) => {
  const params = request.nextUrl.searchParams;
  const offset = params.get("offset");
  const limit = params.get("limit");
  const mediaKind = params.get("mediaKind");
  const albumId = params.get("albumId");
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
      tagId: tagIds.length > 0 ? tagIds.map(Number) : undefined,
      sharingGroupId: sharingGroupId ? Number(sharingGroupId) : undefined,
      startDate: startDate ? new Date(startDate) : undefined,
      endDate: endDate ? new Date(endDate) : undefined,
      isFavorite: isFavorite === "true" ? true : undefined,
    });
    return NextResponse.json(data);
  } catch (error) {
    if (error instanceof Error && error.message === "UNAUTHORIZED") {
      return NextResponse.json({ error: "認証が必要です" }, { status: 401 });
    }
    console.error("GET /api/media失敗", error);
    return NextResponse.json({ error: "メディアの取得に失敗しました" }, { status: 500 });
  }
};
