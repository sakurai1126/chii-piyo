import { logoutAction } from "../actions/logoutAction";

export const LogoutButton = () => {
  return (
    <form action={logoutAction}>
      <button type="submit">ログアウト</button>
    </form>
  );
};
