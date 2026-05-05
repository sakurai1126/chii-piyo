import { logoutAction } from "../actions/logout";

export const LogoutButton = () => {
  return (
    <form action={logoutAction}>
      <button type="submit">ログアウト</button>
    </form>
  );
};
