瑞吉外卖 - プロジェクト概要
このプロジェクトは、黒馬プログラマーの「瑞吉外卖」プロジェクトに基づいて開発されたオンライン外食注文システムです。飲食店向けのバックエンド管理システムと、ユーザーが簡単に外食注文できるフロントエンド機能を提供します。
機能特徴
バックエンド管理システム

レストランのカテゴリ、料理、セットメニュー、注文、従業員の管理が可能です。
システム管理者と一般従業員に役割が分かれており、システム管理者は全ての管理権限を持ち、一般従業員は料理や注文の管理業務を行います。
フロントエンドユーザーシステム

ユーザーはメニューを閲覧し、料理をカートに追加して注文することができます。
レスポンシブデザインにより、モバイルデバイスでも利用可能です。
メール認証ログイン

本プロジェクトでは、従来の電話番号認証を メール認証 に変更しました。ユーザーは登録時に送信される認証コードをメールで受け取り、これによりログインが完了します。
この機能により、ユーザー体験が向上し、柔軟性が増しました。
技術スタック
バックエンド：Spring Boot、MyBatis Plus、Redis、MySQL
フロントエンド：Vue.js、ElementUI、Axios

###メール機能を使用するには、メールアカウントの認証を取得する必要があります。EmailSenderクラスで認証コードを送信するメールアドレスを設定してください。対応するデータは db_reggie.sql です。
