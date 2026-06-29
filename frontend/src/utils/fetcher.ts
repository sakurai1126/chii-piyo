/**
 * クライアントからのAPI呼び出しとエラーハンドリングを共通化したフェッチャー
 */
export const fetchApi = async <T>(url: string, defaultErrorMessage: string): Promise<T> => {
  const res = await fetch(url);
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.error ?? defaultErrorMessage);
  }
  return res.json() as Promise<T>;
};
