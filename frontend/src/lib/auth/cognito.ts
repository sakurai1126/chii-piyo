import {
  CognitoIdentityProviderClient,
  InitiateAuthCommand,
  AuthFlowType,
  InitiateAuthCommandOutput,
} from "@aws-sdk/client-cognito-identity-provider";

// 環境変数からCognitoの設定を取得
const region = process.env.COGNITO_REGION!;
const clientId = process.env.COGNITO_CLIENT_ID!;
const clientSecret = process.env.COGNITO_CLIENT_SECRET!;

// Cognito へのリクエストを送信するクライアントを生成
const cognitoClient = new CognitoIdentityProviderClient({ region });

/**
 * CognitoアプリクライアントのSecretを使ったハッシュ値を生成する
 * AWS Cognitoで指定のHMAC-SHA256でBase64出力に対応するための関数
 * @param value ユーザーネームやユーザーIDなど、ハッシュ化の元となる値
 * @returns valueとクライアントIDを元にハッシュ化された値
 */
export const calculateSecretHash = async (value: string): Promise<string> => {
  const encoder = new TextEncoder();
  const keyData = encoder.encode(clientSecret);
  const messageData = encoder.encode(value + clientId);
  const cryptoKey = await crypto.subtle.importKey(
    "raw",
    keyData,
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"],
  );
  const signature = await crypto.subtle.sign("HMAC", cryptoKey, messageData);
  return btoa(String.fromCharCode(...new Uint8Array(signature)));
};

export const signIn = async (
  username: string,
  password: string,
): Promise<InitiateAuthCommandOutput> => {
  try {
    const secretHash = await calculateSecretHash(username);

    // Cognitoに送信するコマンドを作成
    const command = new InitiateAuthCommand({
      // メール+パスワードでの認証方式を設定
      AuthFlow: AuthFlowType.USER_PASSWORD_AUTH,
      ClientId: clientId,
      AuthParameters: {
        USERNAME: username,
        PASSWORD: password,
        SECRET_HASH: secretHash,
      },
    });

    // Cognitoに認証リクエストを送信してトークンを取得
    return await cognitoClient.send(command);
  } catch (error: unknown) {
    return handleCognitoError(error);
  }
};

/**
 * リフレッシュトークンを使ったトークン再発行
 * REFRESH_TOKEN_AUTHではUSERNAMEではなくsubに対するSECRET_HASHが必要
 * @param refreshTokenValue リフレッシュトークン
 * @param sub CognitoユーザーID
 * @returns 再発行したトークン情報
 */
export const refreshToken = async (
  refreshTokenValue: string,
  sub: string,
): Promise<InitiateAuthCommandOutput> => {
  try {
    const secretHash = await calculateSecretHash(sub);
    const command = new InitiateAuthCommand({
      // リフレッシュトークンを使った認証方式を設定
      AuthFlow: AuthFlowType.REFRESH_TOKEN_AUTH,
      ClientId: clientId,
      AuthParameters: {
        REFRESH_TOKEN: refreshTokenValue,
        SECRET_HASH: secretHash,
      },

      // Next.jsのfetchキャッシュバグを回避するためのキャッシュバストパラメータ
      ClientMetadata: {
        timestamp: Date.now().toString(),
      },
    });

    // Cognitoに認証リクエストを送信してトークンを取得
    return await cognitoClient.send(command);
  } catch (error: unknown) {
    return handleCognitoError(error);
  }
};

/**
 * Cognitoが返すエラー名で認証失敗の原因を判別してメッセージを出し分ける
 * @param error エラー情報（型不明のためunknownで受け取る）
 * @throws 認証失敗の原因に応じたエラーメッセージを持つErrorオブジェクト
 */
const handleCognitoError = (error: unknown): never => {
  if (error instanceof Error) {
    switch (error.name) {
      case "NotAuthorizedException":
        throw new Error("メールアドレスまたはパスワードが正しくありません");
      case "UserNotFoundException":
        throw new Error("ユーザーが見つかりません");
      case "UserNotConfirmedException":
        throw new Error("メールアドレスの確認が完了していません");
    }
  }
  throw new Error("認証中にエラーが発生しました");
};
