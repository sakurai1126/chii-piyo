// S3へのファイル直接アップロードを行うためのXHRラッパー
// fetch APIではアップロード進捗イベントが安定して取得できないため、XMLHttpRequestを使用する

type UploadOptions = {
  presignedUrl: string;
  file: File;
  onProgress?: (percent: number) => void;
  signal?: AbortSignal;
};

/**
 * 署名付きURLを使ってS3にファイルをアップロードする
 *
 * @param options アップロードオプション
 * - presignedUrl: S3の署名付きURL
 * - file: アップロードするファイル
 * - onProgress: 進捗イベントのコールバック (0-100の数値で進捗率を返す)
 * - signal: 中断シグナル
 *
 * @returns
 * 成功時: void
 * 失敗時: 例外
 */
export const uploadToS3 = ({
  presignedUrl,
  file,
  onProgress,
  signal,
}: UploadOptions): Promise<void> => {
  return new Promise((resolve, reject) => {
    // fetch APIではアップロード進捗イベントを取得できないため、XMLHttpRequestを使用してS3に直接アップロードする
    const xhr = new XMLHttpRequest();

    // PUTリクエストでS3に送信する
    xhr.open("PUT", presignedUrl);

    // Pre-signed URL生成時の署名に含めたcontentTypeと一致しているかS3が検証する
    xhr.setRequestHeader("Content-Type", file.type);

    // アップロード進捗イベント
    xhr.upload.addEventListener("progress", (event) => {
      // event.lengthComputable が false の場合は進捗計算不可
      if (!event.lengthComputable || !onProgress) return;
      const percent = Math.round((event.loaded / event.total) * 100);
      onProgress(percent);
    });

    // 完了時
    xhr.addEventListener("load", () => {
      // S3はアップロード成功時に200を返す
      if (xhr.status >= 200 && xhr.status < 300) {
        resolve();
      } else {
        reject(new Error(`S3アップロード失敗: HTTP ${xhr.status}`));
      }
    });

    // ネットワークエラー、中断時
    xhr.addEventListener("error", () => {
      reject(new Error("S3アップロード中にネットワークエラーが発生しました"));
    });

    xhr.addEventListener("abort", () => {
      reject(new Error("S3アップロードが中断されました"));
    });

    // 外部からの中断シグナル対応
    // signalがabortされたタイミングでXHRをabort()する
    if (signal) {
      // 既にabortされていれば即座に中断
      if (signal.aborted) {
        xhr.abort();
        return;
      }
      signal.addEventListener("abort", () => xhr.abort(), { once: true });
    }

    // 送信
    xhr.send(file);
  });
};
