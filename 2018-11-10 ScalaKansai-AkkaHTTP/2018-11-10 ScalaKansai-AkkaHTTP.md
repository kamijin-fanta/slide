#  Akka-HTTPで作るAPIサーバ
###### 2018-11-10 Scala Kansai Summit - kamijin-fanta

<!-- page_number: true -->
<!-- $size: 16:9 -->
<link rel="stylesheet" href="https://cdn.rawgit.com/kamijin-fanta/sakura-marp-theme/master/style.css">
<style>
#container .slide_wrapper[data-template="title"] .slide_inner {
  text-align: center;
  font-family: 'Yu Gothic Light';
  font-weight: 100;
  font-size: 45px;
  padding-top: 100px;
}
#container .slide_wrapper[data-template="title"] .slide {
  background: #62BA58;
  background-position: bottom center;
  background-repeat: no-repeat;
  background-size: 100%;
  color: white;
}
#container .slide_wrapper[data-template="title"] mark {
  background: white;
  color: #62BA58;
  border-radius: 10px;
}
#container .slide_wrapper[data-template="title"] .slide h1 {
  font-family: inherit;
  color: whilte;
  font-weight: 100;
  margin-bottom: 0;
  padding-bottom: 0;
}
#container .slide_wrapper[data-template="title"] .slide h6 {
  color: white;
  font-size: 0.7em;
  font-weight:100;
}
#container .slide_wrapper .slide h1 {
  color: #E76C00;
  font-family: 'Futura';
  font-weight: 500;
}
#container .slide_wrapper .slide h2 {
  color: #E76C00;
  font-family: 'Futura';
  font-weight: 300;
  font-style: italic;
  font-size: 1.7em;
}
#container .slide_wrapper .slide {
  background-image: url(footer.svg);
  background-position: bottom center;
  background-repeat: no-repeat;
  background-size: 100%;
  padding-bottom: 80px;
}
#container .slide_wrapper .slide .slide_page {
  bottom: 70px;
  background: none;
  text-align: right;
}
</style>


<!-- *template: title -->

<!--
## toc

- こんな人におすすめ
  - フレームワークが負担になってきた
    - アップグレード・独自の認証・スケーリング
  - シンプルな機能の組み合わせでHTTPサーバを作りたい
    - 単機能なディレクティブを組み合わせる
  - Scalaの言語をある程度覚え、何を作ろうか迷っている
    - ライブラリ固有の知識が少なくても扱えます
- 基本的なサーバ
  - 簡単なディレクティブの使い方
- モデル
  - パターンマッチング
- ディレクティブ
  - コンテキスト
  - 入れ子・結合
- テスト
- Marshall
  - JSON
  - HTML
- Rejection
- ディレクティブの自作
- 復習
- さいごに
    - 本の宣伝
-->

---

## 諸注意

- スライド公開します
- 撮影NG (イベントのレギュレーション)
- Twitter: #scala_ks #s1

---

## 自己紹介

上條 忠久
Scala関西Summit運営

- Work: さくらインターネット　IoT/レンタルサーバー
- Github: kamijin-fanta
- Twitter: kamijin_fanta

---

## Akka HTTP

- HTTP Server
  - HTMLテンプレート・フォーム等を使用したWebサイト
  - クライアント・バックエンドが分離したAPIサーバ
- HTTP Client
  - スクレイピング
  - マイクロサービスの他のエンドポイントの呼び出し
- HTTP Serverについて主に紹介

---

## Akka HTTP is...

- Webフレームワーク
  - 例
    - Play, Rails, Django, Laravel
  - 機能
    - ルーティング・テンプレート・データベース・セッション・認証・テスト

---

## Akka HTTP is HTTP Library

- Akka HTTPはWebフレームワークではない
  - 有: ルーティング・テスト
  - 無: テンプレート・データベース・セッション・認証

---

## Why Akka HTTP

- 何故機能の少ないAkka HTTPを選択するのか
- Webフレームワークが適切でないユースケースが存在
  - 大きなビジネスロジックが存在し、APIが付加的なものである
  - RDB(MySQL等)ではなくKVS(Dynamo等)を使用する
  - 認証に独自のSSOを使用する
  - 気に入ったライブラリ(twirl, circe等)を使用したい
  - フレームワークのアップグレードコストが無視できない
- プログラミングの考え方
  - 他のWebフレームワーク: HTTPのサービスを記述する
  - Akka HTTP: アプリケーションを記述し、HTTPと統合する

---

## Beginner friendly

- Akka HTTPは2つの側面が有る
  - 大規模サービス構築に向く
    - DB・セッション管理なども1から作れる
    - クリーンアーキテクチャ等の設計手法と親和性が高い
    - 単機能なので破壊的変更が少なく、アップグレードコストが低い
  - 単機能で覚える機能が少ない
    - Scalaの習得後のステップで扱うのに適している

---

## Recommended for...

- こんな人におすすめ
  - フレームワークが負担になってきた
    - アップグレード・独自の認証・スケーリング
  - シンプルな機能の組み合わせでHTTPサーバを作りたい
    - 単機能なディレクティブを組み合わせる
  - Scalaの言語をある程度覚え、何を作ろうか迷っている
    - ライブラリ特有の機能等の覚えるべきことが少ない

---

# Understand code

---

## Install

- Requirement
  - sbt
  - JDK

```scala
// build.sbt
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.5", 
  "com.typesafe.akka" %% "akka-stream" % "2.5.12",
)
```

---

## Basic Server

```scala
object HttpServerUseHttpApp {
  object WebServer extends HttpApp {
    override def routes: Route =
      path("hello") {
        get {
          val entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            "<h1>hello akka-http</h1>"
          )
          complete(entity)
        }
      }
  }
  
  def main(args: Array[String]): Unit =
    WebServer.startServer("localhost", 8080)
}
```

---

```bash
$ sbt
> run
```

```bash
$ curl localhost:8080/hello
<h1>hello akka-http</h1>
```

---

## Model, Types

- リクエスト・レスポンスに対応する型が定義されている
  - Request, Response, Uri, Header(Accept, Cookie) etc...
- Scalaのパターンマッチングを行うことができる
  - 例:Reqに含まれるAcceptヘッダからUEで許可されているMIMEタイプを抽出
- 多数の型が定義されているので、一部だけ紹介

---

### Methods

- `HttpMethods.CONNECT`
- `HttpMethods.DELETE`
- `HttpMethods.GET`
- `HttpMethods.HEAD`
- `HttpMethods.OPTIONS`
- `HttpMethods.PATCH`
- `HttpMethods.POST`
- `HttpMethods.PUT`
- `HttpMethods.TRACE`

---

### Uri / Query

```scala
assert(
  Uri("https://akka.io:443/try-akka/") ===
    Uri.from(scheme = "https", host = "akka.io", port = 443, path = "/try-akka/")
)

assert(Query("key1=value1&key2=Foo&key2=Bar").get("key1") === Some("value1"))

assert(Query("key=Foo&key=Bar").getAll("key") === List("Bar", "Foo"))
```

---

### Headers

```scala
RawHeader("x-custom-header", "value")
headers.Host("example.com")
headers.`Access-Control-Allow-Origin`(HttpOrigin("https://example.com"))
```

---

## Routing DSL

```scala
object RoutingBasic extends HttpApp {
  def main(args: Array[String]): Unit =
    startServer("localhost", 8080)
  
  override def routes: Route =
    pathPrefix("hello") {
      get {
        complete("get hello")
      } ~ post {
        complete("post hello")
      }
    } ~ ((get | post) & path("user" / Segment)) { userName =>
      complete(s"UserName: $userName")
    }
}
```

- Directive(pathPrefix, get, post, complete, path)を組み合わせてRouteを作る
- 入れ子にすることや、 `~` `&` `|` で連結することが出来る

---

## What is Directive?


```scala
// 1. 内部のルートに委譲または、拒否しフィルタリングを⾏う
filter(args...) {
  ???
}

// 2. 値を抽出し、内部のルートに渡す
extract(args...) { variable =>
  ???
}

// 3. コンテンツを返す
complate(???)
```

2つ以上の性質を組み合わせたディレクティブも存在

---

## Composing Route

```scala
  override def routes: Route =
    pathPrefix("hello") {
      get {
        complete("get hello")
      } ~ post {
        complete("post hello")
      }
    } ~ ((get | post) & path("user" / Segment)) { userName =>
      complete(s"UserName: $userName")
    }
```

- `~`: ルートで拒否されたときに、次のRouteへ処理を移す
- `&`: 両方のディレクティブの条件を満たす必要がある
- `|`: どちらかのディレクティブの条件を満たす必要がある

---

```scala
  val getOrPostUser = (get | post) & path("user" / Segment)

  override def routes: Route =
    pathPrefix("hello") {
      get {
        complete("get hello")
      } ~ post {
        complete("post hello")
      }
    } ~ getOrPostUser { userName =>
      complete(s"UserName: $userName")
    }
```

---

## Test Kit


```scala
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.3" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
```


```scala
class SimpleHttpServerSpec extends FunSpec with ScalatestRouteTest {
  it("basic test") {
    val route = path("hello") & get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
          "<h1>Say hello to akka-http</h1>"))
      }
    Get("/hello") ~> route ~> check {
      assert(status === StatusCodes.OK)
      assert(responseAs[String] === "<h1>Say hello to akka-http</h1>")
      assert(contentType === ContentTypes.`text/html(UTF-8)`)
    }
  }
}
```

---


- `Get("/hello")` はHttpRequestを作るためのショートハンド
  - `Get` `Post` `Put` `Patch` `Delete` `Options` `Head` もある


```scala
object HttpRequest {
  def apply(
    method:   HttpMethod                = HttpMethods.GET,
    uri:      Uri                       = Uri./,
    headers:  immutable.Seq[HttpHeader] = Nil,
    entity:   RequestEntity             = HttpEntity.Empty,
    protocol: HttpProtocol              = HttpProtocols.`HTTP/1.1`) = new HttpRequest(method, uri, headers, entity, protocol)
}
```

---

## Easy to test

- DirectiveはRequestから値の抽出・フィルタリングを行う純粋関数→テストしやすい
- 任意のHttpRequestをRouteに投げた結果を検証する仕組みがTestKitで用意されている
  - 簡易なテストを楽に書くためのショートハンドも存在

---

## Rejection

```scala
  override def routes: Route =
    path("hello") {
      get {
        complete("get hello")
      } ~ post {
        complete("post hello")
      }
    }
```

```bash
$ curl -X DELETE localhost:8080/hello

HTTP method not allowed, supported methods: GET, POST
```

- 適切なエラーをクライアントに返すための仕組み

---

## Provide Rejections

```scala
  override def routes: Route =
    path("foo") {
      reject
    } ~ path("bar") {
      reject(MissingQueryParamRejection("rejection reason"))
    }
```

```scala
final case class MissingQueryParamRejection(parameterName: String)
  extends jserver.MissingQueryParamRejection with Rejection

trait Rejection
```

```bash
$ curl localhost:8080/bar
Request is missing required query parameter 'rejection reason'
```

---

## Handle Rejection

- 標準ではデフォルトのRejectionHandlerが使用されている
- 上書きor拡張することで、カスタムのエラーメッセージを返すことが可能
  - `Route.seal`でラップし、implicit valを注入する

---

```scala
implicit def myRejectionHandler: RejectionHandler =  // point 1
  RejectionHandler.newBuilder()
    .handle {
      case reject: MissingQueryParamRejection =>
        complete(
          StatusCodes.BadRequest,
          s"required query parameter [${reject.parameterName}]")
    }.result()

override def routes: Route = Route.seal(internalRoutes)  // point 2

def internalRoutes: Route =
  path("bar") {
    reject(MissingQueryParamRejection("rejection reason"))
  }
```

```scala
final case class MissingQueryParamRejection(parameterName: String)
  extends jserver.MissingQueryParamRejection with Rejection
```

---

- エラーを型を使って表現することが出来る
- RejectionHandlerで、エラーメッセージを容易にカスタマイズ可能
  - JSON/HTML/XMLでのエラー対応等


---

## Marshall

- Marshal
  - オブジェクトをシリアライズする仕組み
  - 文字列・バイト列などに変換するMarshallerを定義する

---

```scala
import io.circe.generic.auto._
import io.circe.syntax._

case class User(name: String, age: Int)

implicit val userMarshaller: ToEntityMarshaller[User] = Marshaller.opaque { user =>
  HttpEntity(ContentTypes.`application/json`, user.asJson.noSpaces)
}

val route: Route = get {
  complete(User("mika", 20))
}

Get("/") ~> route ~> check {
  assert(contentType === ContentTypes.`application/json`)
  assert(responseAs[String] === """{"name":"mika","age":20}""")
}
```

---

## Examples

- https://github.com/kamijin-fanta/akka-http-2018/tree/master/src/main/scala/examples
- https://github.com/kamijin-fanta/akka-http-2018/tree/master/src/test/scala/examples

---

## Summary

- Route DSL
- Model
- Directive
- Rejection
- Marshall
- Test Kit

Akka HTTPはこれらのパーツを組み合わせ、
シンプルなHTTPサーバ構築をサポートするライブラリ

---


![85%](https://i.imgur.com/KSty2tL.png)
https://kinyoubenkyokai.github.io/book/techbook05/

---

ありがとうございました

#  Akka-HTTPで作るAPIサーバ
###### 2018-11-10 Scala Kansai Summit - kamijin-fanta
