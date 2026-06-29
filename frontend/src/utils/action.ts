/**
 * Server Actions の共通戻り値の型
 */
export type ActionResult<T = void> = T extends void
  ? { success: true } | { success: false; error: string }
  : { success: true; data: T } | { success: false; error: string };

/**
 * Server Actions における共通エラーハンドリング
 */
export const handleActionError = <T = void>(
  error: unknown,
  defaultMessage: string,
): ActionResult<T> => {
  console.error(defaultMessage, error);
  if (error instanceof Error && error.message === "UNAUTHORIZED") {
    return { success: false, error: "認証が必要です" } as ActionResult<T>;
  }
  return { success: false, error: defaultMessage } as ActionResult<T>;
};
