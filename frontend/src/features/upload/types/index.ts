// アップロードのライフサイクル状態
// idle: ファイル選択直後 / creating: メタデータ登録中 / uploading: S3送信中
// completing: ステータス更新中 / completed: 完了 / failed: 失敗
export type UploadStatus =
  | "idle"
  | "creating"
  | "uploading"
  | "completing"
  | "completed"
  | "failed";

export type UploadImage = {
  /**
   * クライアント側で生成する一意ID
   * サーバー側のmediaIdが採番される前からReactのkeyや状態キーとして使うため、別軸で必要
   */
  id: string;
  /** アップロード対象のファイルオブジェクト */
  file: File;
  /** URL.createObjectURLで生成したプレビュー用のURL */
  previewUrl: string;
  /** 画像の横サイズ */
  width?: number;
  /** 画像の縦サイズ */
  height?: number;
  /** アップロードのライフサイクル状態 */
  status: UploadStatus;
  /** 0-100 の進捗率 */
  progress: number;
  /** POST /media で採番されたサーバー側ID */
  mediaId?: number;
  /** failed時のエラーメッセージ */
  errorMessage?: string;
  /** フォームで入力されたメタデータ*/
  metadata: UploadMetadata;
};

// アップロード時のメタデータ
export type UploadMetadata = {
  /** コメント */
  comment?: string;
  /** アルバムID */
  albumId?: number;
  /** 撮影日時 */
  takenAt?: string;
  /** タグIDの配列 */
  tagIds?: number[];
  /** 共有グループID */
  sharingGroupId?: number;
};

// 更新される状態の型
export type ItemState = {
  /** アップロードのライフサイクル状態 */
  status: UploadStatus;
  /** 0-100 の進捗率 */
  progress: number;
  /** POST /media で採番されたサーバー側ID */
  mediaId?: number;
  /** エラーメッセージ */
  errorMessage?: string;
};
