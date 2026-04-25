# Amazon Corretto 25をベースイメージとして使用
# インフラをAWSに統一しているため親和性の高いAmazon Correttoを選択
FROM amazoncorretto:25-alpine


# コンテナ内の作業ディレクトリを設定
WORKDIR /app

# Mavenの依存関係キャッシュ用に pom.xml をコピーして依存関係を事前にダウンロードする
# pom.xmlに変更がない限りDockerのレイヤーキャッシュが再利用され、CI/CDでのイメージビルド時に依存関係のダウンロードをスキップできる
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline

# 初回のビルドやテスト用にソースコード全体をコピー
# 通常はcompose.ymlのvolumeマウントで上書きされるがCI/CDや初期構築時に必要
COPY src ./src

# 開発環境用起動コマンド
CMD ["./mvnw", "spring-boot:run"]