"use client";
import {
  ImageUploader,
  VideoUploader,
  MultipleSettings,
  UpdateFileList,
  useUploadPage,
} from "@/features/upload";
import { AlbumResponseDto, SharingGroupResponseDto, TagResponseDto } from "@/lib/api-client/gen";

type Props = {
  isAdmin: boolean;
  albums: AlbumResponseDto[];
  sharingGroups: SharingGroupResponseDto[];
  tags: TagResponseDto[];
};

export const UploadPageContents = ({ isAdmin, albums, sharingGroups, tags }: Readonly<Props>) => {
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

  return (
    <>
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
          isAdmin={isAdmin}
          tags={tags}
          albums={albums}
          sharingGroups={sharingGroups}
          updateAllMetadata={updateAllMetadata}
        />
      )}

      {/* アップロードするファイルの一覧 */}
      {items.length > 0 && (
        <UpdateFileList
          isAdmin={isAdmin}
          items={items}
          onRemove={removeFile}
          onRemoveAll={removeAllFiles}
          onUpload={handleUpload}
          isUploading={isUploading}
          tags={tags}
          albums={albums}
          sharingGroups={sharingGroups}
          updateItemMetadata={updateItemMetadata}
        />
      )}
    </>
  );
};
