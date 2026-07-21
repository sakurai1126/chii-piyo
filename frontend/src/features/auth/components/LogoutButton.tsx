"use client";

import { useFormStatus } from "react-dom";

import { logoutAction } from "../actions/logoutAction";

const SubmitButton = () => {
  // useFormStatus は「親（祖先）の <form>」の送信状態を取得するフック
  const { pending } = useFormStatus();
  return (
    <button type="submit" disabled={pending}>
      ログアウト
    </button>
  );
};

export const LogoutButton = () => {
  return (
    <form action={logoutAction}>
      <SubmitButton />
    </form>
  );
};
