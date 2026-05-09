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
  FileList,
  useUploadPage,
} from "@/features/upload";

export default function UploadPage() {
  const {
    items,
    setFileAndUrl,
    removeFile,
    removeAllFiles,
    handleUpload,
    isUploading,
    resultMessage,
  } = useUploadPage();

  // 各種既存メタデータはページで一度だけ取得し、配下のセレクターに配布する
  const tagsState = useTags();
  const albumsState = useAlbums();
  const sharingGroupsState = useSharingGroups();

  return (
    <Container className="mt-20 max-md:mt-5">
      <PageTitle text="アップロード" />
      <div className="mt-15 grid grid-cols-2 gap-10 max-lg:gap-3 max-md:mt-6">
        <ImageUploader onFilesAdd={setFileAndUrl} />
        <VideoUploader />
      </div>

      {/* 条件一括設定 */}
      {items.length > 1 && (
        <MultipleSettings
          tagsState={tagsState}
          albumsState={albumsState}
          sharingGroupsState={sharingGroupsState}
        />
      )}

      {/* アップロードするファイルの一覧 */}
      {items.length > 0 && (
        <FileList
          items={items}
          onRemove={removeFile}
          onRemoveAll={removeAllFiles}
          onUpload={handleUpload}
          isUploading={isUploading}
          tagsState={tagsState}
          albumsState={albumsState}
          sharingGroupsState={sharingGroupsState}
        />
      )}

      {/* 結果メッセージ */}
      {resultMessage && (
        <output className="bg-white-back border-brown-dark mt-10 block rounded-xl border px-5 py-4 text-sm">
          {resultMessage}
        </output>
      )}
    </Container>
  );
}
