import { logoutAction } from "../actions/logout";

export default function LogoutButton() {
  return (
    <form action={logoutAction}>
      <button type="submit">ログアウト</button>
    </form>
  );
}
