"use client";

import { useRouter } from "next/navigation";
import { useActionState, useEffect } from "react";

import { toast } from "@/components/ui/Toast";

import { loginAction, type LoginState } from "../actions/loginAction";

const initialState: LoginState = {};

type Props = {
  logoutMessage?: boolean;
};

export const LoginForm = ({ logoutMessage }: Readonly<Props>) => {
  const [state, formAction, isPending] = useActionState(loginAction, initialState);
  const router = useRouter();

  // ログアウト時トーストを表示
  // リロード時に表示が繰り返されないようにURLから?logout=successを削除
  useEffect(() => {
    if (logoutMessage) {
      toast.success("ログアウトしました");
      router.replace("/login", { scroll: false });
    }
  }, [logoutMessage, router]);

  useEffect(() => {
    if (state.error) {
      toast.error(state.error);
    }
  }, [state.error]);

  return (
    <form action={formAction} className="mx-auto w-screen max-w-135 max-md:max-w-[calc(100%-40px)]">
      <label htmlFor="email" className="text-sm max-md:text-xs">
        メールアドレス
      </label>
      <input
        type="email"
        id="email"
        name="email"
        required
        autoComplete="email"
        className="focus:outline-brown-light bg-login-form border-line-gray mt-1 mb-5 block h-12 w-full rounded-sm border px-3 autofill:shadow-[inset_0_0_0_30px_var(--color-login-form)] max-md:mb-4 max-md:h-10 dark:outline-none"
        disabled={isPending}
      />
      <label htmlFor="password" className="text-sm max-md:text-xs">
        パスワード
      </label>
      <input
        type="password"
        id="password"
        name="password"
        required
        autoComplete="current-password"
        className="focus:outline-brown-light bg-login-form border-line-gray mt-1 block h-12 w-full rounded-sm border px-3 autofill:shadow-[inset_0_0_0_30px_var(--color-login-form)] max-md:h-10 dark:outline-none"
        disabled={isPending}
      />
      <button
        type="submit"
        disabled={isPending}
        className="bg-brown-light border-brown-middle hover:text-brown-middle mx-auto mt-10 block h-12 w-60 cursor-pointer rounded-lg border font-medium text-white transition hover:bg-white max-md:mt-8 max-md:h-9 max-md:w-45 max-md:text-sm"
      >
        {isPending ? "ログイン中..." : "ログイン"}
      </button>
    </form>
  );
};
