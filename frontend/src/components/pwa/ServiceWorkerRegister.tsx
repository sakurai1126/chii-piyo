"use client";

import { useEffect } from "react";

export const ServiceWorkerRegister = () => {
  useEffect(() => {
    // Service Worker対応ブラウザかつ本番環境の場合に登録処理
    if ("serviceWorker" in navigator && process.env.NODE_ENV === "production") {
      window.addEventListener("load", () => {
        navigator.serviceWorker.register("/service-worker.js").catch((error) => {
          console.error("Service Workerの登録に失敗しました:", error);
        });
      });
    }
  }, []);

  return null;
};
