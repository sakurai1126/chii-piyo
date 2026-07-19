"use client";

import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useState } from "react";

export default function Providers({ children }: Readonly<{ children: React.ReactNode }>) {
  // QueryClientはクライアント側でのみ作成する必要があるため、useState内で初期化して永続化する
  const [queryClient] = useState(() => new QueryClient());
  return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
}
