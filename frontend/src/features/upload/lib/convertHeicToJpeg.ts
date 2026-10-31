// iOSで撮影されたHEIC/HEIF画像をJPEGに変換する
// HEICのまま保存するとAndroidやWindowsのブラウザで表示できず、サーバー側のサムネイル生成も失敗するため保存形式をJPEGに統一する

// iOSではHEICのMIMEタイプが空になることがあるため、拡張子でも判定する
const HEIC_MIME_TYPES = new Set([
  "image/heic",
  "image/heif",
  "image/heic-sequence",
  "image/heif-sequence",
]);
const HEIC_EXTENSION = /\.(heic|heif)$/i;

// 元画像の劣化を避けるため最高品質で変換する
const JPEG_QUALITY = 1;

/**
 * ファイルがHEIC/HEIFかどうかを判定する
 */
export const isHeicFile = (file: File): boolean => {
  return HEIC_MIME_TYPES.has(file.type.toLowerCase()) || HEIC_EXTENSION.test(file.name);
};

/**
 * 画像として扱えるファイルかどうかを判定する
 * type が空のHEICを取りこぼさないよう、MIMEタイプに加えてHEICの判定も行う
 */
export const isImageFile = (file: File): boolean => {
  return file.type.startsWith("image/") || isHeicFile(file);
};

/**
 * HEIC/HEIFならJPEGに変換して返し、それ以外は元のファイルをそのまま返す
 *
 * @param file 変換対象のファイル
 * @returns JPEGに変換したFile、変換不要なら元のFile
 */
export const convertHeicToJpeg = async (file: File): Promise<File> => {
  if (!isHeicFile(file)) return file;

  // 変換ライブラリはWebAssemblyを含み重いため、HEICを扱うときだけ読み込む
  const { heicTo } = await import("heic-to/csp");

  // 拡張子とMIMEタイプをJPEGに揃えたファイルを作成して返す
  const jpeg = await heicTo({ blob: file, type: "image/jpeg", quality: JPEG_QUALITY });
  const name = file.name.replace(HEIC_EXTENSION, "") + ".jpg";
  return new File([jpeg], name, { type: "image/jpeg", lastModified: file.lastModified });
};
