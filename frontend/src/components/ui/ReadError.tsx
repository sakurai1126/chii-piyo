type Props = {
  // 取得失敗時のエラーメッセージ
  error?: string | null;
  // 取得失敗時の再試行処理
  onRetry?: () => void;
};

export default function ReadError({ error, onRetry }: Readonly<Props>) {
  return (
    <div className="mt-3 flex items-center gap-3">
      <p className="text-warning text-xs">{error}</p>
      {onRetry && (
        <button type="button" onClick={onRetry} className="text-brown-middle text-xs underline">
          再試行
        </button>
      )}
    </div>
  );
}
