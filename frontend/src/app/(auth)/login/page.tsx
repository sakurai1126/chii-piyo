import Image from "next/image";

import { LoginForm } from "@/features/auth";

type SearchParams = Promise<{ logout?: string }>;

export default async function Login({ searchParams }: Readonly<{ searchParams: SearchParams }>) {
  // ログアウト後のリダイレクトで?logout=successが付与される
  const { logout } = await searchParams;
  const logoutMessage = logout === "success";

  return (
    <div className="grid h-screen place-content-center px-5">
      <Image
        src="/images/logo.png"
        alt="Chii-Piyo"
        width={300}
        height={100}
        className="mx-auto max-md:w-50"
      />
      <LoginForm logoutMessage={logoutMessage} />
    </div>
  );
}
