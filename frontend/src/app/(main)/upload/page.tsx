"use client";

import Container from "@/components/layout/Container";
import PageTitle from "@/components/ui/PageTitle";
import { useAlbums } from "@/features/album/hooks/useAlbums";
import { useSharingGroups } from "@/features/sharing/hooks/useSharingGroups";
import { useTags } from "@/features/tag";
import {
  ImageUploader,
  VideoUploader,
  MultipleSettings,
  UpdateFileList,
  useUploadPage,
} from "@/features/upload";

export default function UploadPage() {
  const {
    items,
    setImageAndUrl,
    setVideoAndUrl,
    removeFile,
    removeAllFiles,
    handleUpload,
    isUploading,
    updateItemMetadata,
    updateAllMetadata,
    limits,
  } = useUploadPage();

  // 各種既存メタデータはページで一度だけ取得し、配下のセレクターに配布する
  const tagsState = useTags();
  const albumsState = useAlbums();
  const sharingGroupsState = useSharingGroups();

  return (
    <Container className="mt-20 max-md:mt-5">
      <PageTitle text="アップロード" />
      <div className="mt-15 grid grid-cols-2 items-start gap-10 max-lg:gap-3 max-md:mt-6">
        <ImageUploader
          onFilesAdd={setImageAndUrl}
          maxFiles={limits.MAX_UPLOAD_IMAGE_LIMIT}
          maxSize={limits.MAX_IMAGE_SIZE_MB}
        />
        <VideoUploader
          onFilesAdd={setVideoAndUrl}
          maxFiles={limits.MAX_UPLOAD_VIDEO_LIMIT}
          maxSize={limits.MAX_VIDEO_SIZE_MB}
        />
      </div>

      {/* 条件一括設定 */}
      {items.length > 1 && (
        <MultipleSettings
          tagsState={tagsState}
          albumsState={albumsState}
          sharingGroupsState={sharingGroupsState}
          updateAllMetadata={updateAllMetadata}
        />
      )}

      {/* アップロードするファイルの一覧 */}
      {items.length > 0 && (
        <UpdateFileList
          items={items}
          onRemove={removeFile}
          onRemoveAll={removeAllFiles}
          onUpload={handleUpload}
          isUploading={isUploading}
          tagsState={tagsState}
          albumsState={albumsState}
          sharingGroupsState={sharingGroupsState}
          updateItemMetadata={updateItemMetadata}
        />
      )}
    </Container>
  );
}
