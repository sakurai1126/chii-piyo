"use client";

import { useEffect } from "react";

type Props = {
  isDarkMode: boolean;
};

/**
 * ダークモード設定のCookieをセットする
 * 有効期限7日間でCookieにセット
 */
export const ThemeCookieSetter = ({ isDarkMode }: Props) => {
  useEffect(() => {
    document.cookie = `theme=${isDarkMode ? "dark" : "light"}; path=/; max-age=604800`;
  }, [isDarkMode]);

  return null;
};
