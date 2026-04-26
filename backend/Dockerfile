# Amazon Corretto 25をベースイメージとして使用
# インフラをAWSに統一しているため親和性の高いAmazon Correttoを選択
FROM amazoncorretto:25-alpine

# amazoncorrettoはデフォルトでrootユーザーを使用するため非rootユーザーを作成
RUN addgroup -S spring && adduser -S spring -G spring

# コンテナ内の作業ディレクトリを設定
WORKDIR /app

# 依存関係の準備のために必要なファイルをコピー
# root所有でコピーし 非rootユーザーは読み取りのみ可能で書き換え不可とする
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
# 初回のビルドやテスト用にソースコード全体をコピー
# 通常はcompose.ymlのvolumeマウントで上書きされるがCI/CDや初期構築時に必要
COPY src ./src

# 依存関係を事前ダウンロード
# mvnwに実行権限を付与
# キャッシュ先 .m2 は先に用意して spring ユーザーが書き込めるように権限を設定
# ビルド成果物の出力先 target も同様に作成して書き込み権限を設定
RUN chmod +x mvnw \
  && mkdir -p /home/spring/.m2 \
  && chown -R spring:spring /home/spring/.m2 \
  && mkdir -p /app/target \
  && chown spring:spring /app/target

# ここから非rootに切替
USER spring

# 依存関係を事前にダウンロードすることで、pom.xmlに変更がない限りDockerのレイヤーキャッシュが再利用され、CI/CDでのイメージビルド時に依存関係のダウンロードをスキップできる
RUN ./mvnw dependency:go-offline

# 開発環境用起動コマンド
CMD ["./mvnw", "spring-boot:run"]