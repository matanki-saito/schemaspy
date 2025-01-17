# Schemaspy on webserver
## 概要
CIなどで並列に走らせてるSchemaspyのdockerコンテナに対して、別Dockerコンテナからドキュメント生成を指示できるように、プログラムを拡張してwebserver機能を載せました。起動するには引数に```-servermode```をつける必要があります。

## API
### [GET] /run
対象に対してscheme解析を指示し、その結果を指定ディレクトリに保存します。下記のパラメータをクエリパラメータとして渡せます。非同期処理です。詳細はControllerを確認してください。

- type
- host
- database
- user
- pass
- timezone
- out

### [GET] /runnable
実行可能であればtrueが解析が実行中であればfalseが返却されます。

### [GET] /download
対象に対してscheme解析を指示し、その結果をzipとしてダウンロードします。下記のパラメータをクエリパラメータとして渡せます。同期処理です。詳細はControllerを確認してください。

- type
- host
- database
- user
- pass
- timezone

## その他の変更点
 - openjdkのイメージからAdoptOpenJDKのイメージに変更しています。
 - java8をjava11に変更しています
 - mysqlは8が基準になっています。
 - 一部の外部ライブラリをアップデート
 - lombokの追加
 - version採番のためのライブラリを変更

## サンプル
windows for dockerにおけるimage作成の例
```
docker build -t gnagaoka/schemaspy:snapshot .
```
上記を起動する例
```
docker run -v "C:\repo\schemaspy\output:/output" --name hoge -p 8080:8080 -it gnagaoka/schemaspy:snapshot -servermode
```
解析を実行する例
```
http://localhost:8080/run?host=host.docker.internal:3306&database=mydb&user=homu&pass=geso
```

## dockerhub
下記に生成済みのdcoker imageがあります。
https://cloud.docker.com/u/gnagaoka/repository/docker/gnagaoka/schemaspyweb


