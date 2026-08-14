import { test } from "@playwright/test";

import { createCarePage } from "./pages/care.page";

test.describe("育児記録シナリオ", () => {
  test("E2E-08: 育児記録がカレンダーに反映されること", async ({ page }) => {
    // ページ操作関数の初期化
    const carePage = createCarePage({ page });

    // 育児記録ページへ移動
    await carePage.goto();

    // 食事記録を登録
    await carePage.recordMeal({ note: "ごはん" });

    // カレンダー上に記録が反映されていることを検証
    await carePage.expectMealRecordInCalendar({ note: "ごはん" });
  });
});
