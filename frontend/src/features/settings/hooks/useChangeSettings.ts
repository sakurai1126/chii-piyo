"use client";

import imageCompression from "browser-image-compression";
import { useRef, useState } from "react";

import { toast } from "@/components/ui/Toast";
import { uploadToS3 } from "@/features/upload";
import { UserResponseDto } from "@/lib/api-client/gen";

import { generatePresignedIconUrlAction } from "../actions/generatePresignedIconUrlAction";
import { updateProfileAction } from "../actions/updateProfileAction";

type Props = {
  currentUser: UserResponseDto;
};

export const useChangeSettings = ({ currentUser }: Props) => {
  // 現在ログイン中のユーザー
  const [user, setUser] = useState<UserResponseDto>(currentUser);
  // アイコン画像変更用のinput
  const iconInputRef = useRef<HTMLInputElement>(null);
  // 新しいアイコン画像のプレビューURL
  const [previewUrl, setPreviewUrl] = useState<string | undefined>();
  // 表示名変更表示のフラグ
  const [isNameChangeMode, setIsNameChangeMode] = useState<boolean>(false);
  // 新しい表示名
  const [newName, setNewName] = useState<string>("");

  // ダークモードの状態管理
  const [isDarkMode, setIsDarkMode] = useState<boolean>(currentUser.isDarkMode);

  // かんたんモードの状態管理
  const [isEasyMode, setIsEasyMode] = useState<boolean>(currentUser.isEasyMode);

  /**
   * アイコン画像変更
   * inputが変更された際にプレビューURLの状態を更新する
   *
   * @param file 入力するファイル
   */
  const iconEdit = (file: File | undefined) => {
    if (!file) return;
    setPreviewUrl(URL.createObjectURL(file));
  };

  /**
   * アイコン画像変更中止処理
   * キャンセルボタンを押した際に実行される
   * inputの値とpreviewUrlをクリア
   */
  const cancelIconEdit = () => {
    if (previewUrl) URL.revokeObjectURL(previewUrl);
    setPreviewUrl(undefined);
    if (iconInputRef.current) iconInputRef.current.value = "";
  };

  /**
   * アイコン画像アップロード処理
   * 1. アイコンをリサイズ
   * 2. 署名付きアップロード用URLを取得
   * 3. S3に直接アップロード
   * 4. メタデータの更新
   *
   * 成功/失敗はトーストで通知
   */
  const iconUpload = async () => {
    try {
      // バリデーション
      if (!iconInputRef.current?.files) return;
      const originalFile = iconInputRef.current.files[0];
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
      const { presignedUrl, s3key } = result.data;
      await uploadToS3({ presignedUrl, file });

      // メタデータの更新
      const updatedUser = await updateProfileAction({ s3key });
      if (updatedUser.success) {
        setUser({
          ...user,
          presignedIconUrl: URL.createObjectURL(file),
        });
        cancelIconEdit();
        toast.success("プロフィール画像のアップロードに成功しました");
      } else {
        toast.error("プロフィール画像のアップロードに失敗しました");
      }
    } catch (e) {
      console.error(e);
      toast.error("プロフィール画像のアップロードに失敗しました");
    }
  };

  /**
   * 表示名の変更処理
   *
   * 成功/失敗はトーストで通知
   */
  const nameChange = async () => {
    if (!newName) {
      toast.error("新しい表示名を入力してください");
      return;
    }

    try {
      const updatedUser = await updateProfileAction({ displayName: newName });
      if (updatedUser.success) {
        setUser({ ...user, displayName: newName });
        setNewName("");
        setIsNameChangeMode(false);
        toast.success("表示名を変更しました");
      } else {
        toast.error("表示名の変更に失敗しました");
      }
    } catch (e) {
      console.error(e);
      toast.error("表示名の変更に失敗しました");
    }
  };

  /**
   * ダークモードの変更処理
   * 読み込み時にブラウザ側で即時判定ができるようCookieに状態を保存
   *
   * 成功/失敗はトーストで通知
   */
  const darkModeChange = async () => {
    try {
      const updatedUser = await updateProfileAction({ isDarkMode: !isDarkMode });
      if (updatedUser.success) {
        // Cookieに保存(有効期限:7日間)
        document.cookie = `theme=${user.isDarkMode ? "light" : "dark"}; path=/; max-age=604800; SameSite=Lax; Secure`;

        // htmlタグのクラスを操作してクライアント側に即時反映
        if (user.isDarkMode) {
          document.documentElement.classList.remove("dark");
        } else {
          document.documentElement.classList.add("dark");
        }

        setUser({
          ...user,
          isDarkMode: !user.isDarkMode,
        });

        setIsDarkMode(!user.isDarkMode);

        toast.success(`ダークモード表示を${user.isDarkMode ? "OFF" : "ON"}にしました`);
      } else {
        toast.error("ダークモード表示の変更に失敗しました");
      }
    } catch (e) {
      console.error(e);
      toast.error("ダークモード表示の変更に失敗しました");
    }
  };

  /**
   * かんたんモードの変更処理
   *
   * 成功/失敗はトーストで通知
   */
  const easyModeChange = async () => {
    try {
      const updatedUser = await updateProfileAction({ isEasyMode: !isEasyMode });
      if (updatedUser.success) {
        toast.success(`かんたんモードを${isEasyMode ? "OFF" : "ON"}にしました`);
        setIsEasyMode(!user.isEasyMode);
        setUser({
          ...user,
          isEasyMode: !user.isEasyMode,
        });
      } else {
        toast.error("かんたんモードの変更に失敗しました");
      }
    } catch (e) {
      console.error(e);
      toast.error("かんたんモードの変更に失敗しました");
    }
  };

  return {
    user,
    setUser,
    isNameChangeMode,
    setIsNameChangeMode,
    newName,
    setNewName,
    nameChange,
    iconInputRef,
    previewUrl,
    iconEdit,
    cancelIconEdit,
    iconUpload,
    isDarkMode,
    darkModeChange,
    isEasyMode,
    easyModeChange,
  };
};
