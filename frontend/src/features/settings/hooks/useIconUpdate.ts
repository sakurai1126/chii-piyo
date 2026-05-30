"use client";
import imageCompression from "browser-image-compression";
import { useRef, useState } from "react";

import { toast } from "@/components/ui/Toast";
import { uploadToS3 } from "@/features/upload";
import { UserResponseDto } from "@/lib/api-client/gen";

import { generatePresignedIconUrlAction } from "../actions/generatePresignedIconUrlAction";
import { updateProfileAction } from "../actions/updateProfileAction";

export const useIconUpdate = (setUser: (user: UserResponseDto) => void) => {
  const inputRef = useRef<HTMLInputElement>(null);

  const [previewUrl, setPreviewUrl] = useState<string | undefined>();

  const handleChange = (file: File | undefined) => {
    if (!file) return;
    setPreviewUrl(URL.createObjectURL(file));
  };

  const cancelEdit = () => {
    if (previewUrl) URL.revokeObjectURL(previewUrl);
    setPreviewUrl(undefined);
    if (inputRef.current) inputRef.current.value = "";
  };

  const upload = async () => {
    try {
      // バリデーション
      if (!inputRef.current || !inputRef.current.files) return;
      const originalFile = inputRef.current.files[0];
      if (originalFile.type.split("/")[0] !== "image") {
        throw new Error("画像ファイルを選択してください");
      }

      // ライブラリを用いて画像をリサイズ
      const file = await imageCompression(originalFile, {
        maxSizeMB: 1, // 最大ファイルサイズ
        maxWidthOrHeight: 160, // 長辺を160pxにリサイズ
        useWebWorker: true, // メインスレッドをブロックしない
      });

      // キーデータの登録 + 署名付きURL取得
      const result = await generatePresignedIconUrlAction({
        filename: file.name,
        contentType: file.type,
      });

      if (!result.success) {
        toast.error("プロフィール画像の登録に失敗しました");
        return;
      }

      // S3に直接アップロード
      const { presignedUrl, s3key } = result;
      await uploadToS3({ presignedUrl, file });
      // メタデータの更新
      const updatedUser = await updateProfileAction({ s3key });
      if (updatedUser.success) {
        setUser(updatedUser.user);
        cancelEdit();
        toast.success("プロフィール画像のアップロードに成功しました");
      } else {
        toast.error("プロフィール画像のアップロードに失敗しました");
      }
    } catch (e) {
      console.error(e);
      toast.error("プロフィール画像のアップロードに失敗しました");
    }
  };

  return { inputRef, previewUrl, handleChange, cancelEdit, upload };
};
