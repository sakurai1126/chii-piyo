"use client";

import { useActionState } from "react";

import { loginAction, type LoginState } from "../actions/login";

const initialState: LoginState = {};

type Props = {
  logoutMessage?: boolean;
};

export const LoginForm = ({ logoutMessage }: Readonly<Props>) => {
  const [state, formAction, isPending] = useActionState(loginAction, initialState);

  return (
    <form action={formAction} className="mx-auto max-w-135">
      <label htmlFor="email" className="text-sm max-md:text-xs">
        メールアドレス
      </label>
      <input
        type="email"
        id="email"
        name="email"
        required
        autoComplete="email"
        className="focus:outline-brown-light bg-light-dark mt-1 mb-5 block h-12 w-full rounded-sm border border-[#ccc] px-3 autofill:shadow-[inset_0_0_0_30px_white] max-md:mb-4 max-md:h-10"
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
        className="focus:outline-brown-light bg-light-dark mt-1 block h-12 w-full rounded-sm border border-[#ccc] px-3 autofill:shadow-[inset_0_0_0_30px_white] max-md:h-10"
      />
      <button
        type="submit"
        disabled={isPending}
        className="bg-brown-light border-brown-middle hover:text-brown-middle hover:bg-light-dark mx-auto mt-10 block h-12 w-60 cursor-pointer rounded-lg border text-white transition max-md:mt-8 max-md:h-9 max-md:w-45 max-md:text-sm"
      >
        {isPending ? "ログイン中..." : "ログイン"}
      </button>

      {logoutMessage && <p className="text-success mt-4 text-center text-sm">ログアウトしました</p>}

      {state.error && <p className="text-warning mt-4 text-center text-sm">{state.error}</p>}
    </form>
  );
};
