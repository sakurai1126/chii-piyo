const cacheName = "chii-piyo-cache-v1";

// インストール時にオフライン用画面をキャッシュ
self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(cacheName).then((cache) => {
      return cache.addAll(["/offline.html"]);
    }),
  );
  // 新しいService Workerを即座にアクティブ化
  self.skipWaiting();
});

// アクティベーション時に古いキャッシュを整理
self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys().then((cacheNames) =>
      Promise.all(
        cacheNames
          .filter((name) => {
            return name !== cacheName;
          })
          .map((name) => {
            return caches.delete(name);
          }),
      ),
    ),
  );
  // ページのリロードを待たずに現在開いているページを即座にService Workerの制御下に置く
  self.clients.claim();
});

// ネットワークリクエストのハンドリング
self.addEventListener("fetch", (event) => {
  // ページ遷移の場合
  if (event.request.mode === "navigate") {
    event.respondWith(
      // ネットワークリクエストを実行
      fetch(event.request)
        // オフラインなど失敗した場合、オフライン用画面を表示する
        .catch(async () => {
          const cache = await caches.open(cacheName);
          const cachedResponse = await cache.match("/offline.html");
          return cachedResponse;
        }),
    );
  }
});
