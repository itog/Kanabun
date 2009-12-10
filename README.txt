----------------------------------------
  かなぶん for Android

  @author itog
----------------------------------------

■かなぶん for Android とは

ひらがなゲーム「かなぶん」のAndroid版です。

かなぶんについては以下を参照して下さい。
http://kanabun.champierre.com/

・辞書について
「かなぶん」で使っている辞書についてはこちらをご参照下さい。
http://kanabun.champierre.com/dictionary



■制限
データベースファイルは含まれていません。
下記手順により、Kanabunから辞書を参照できるようにして下さい。

・データベースの作成
下記のスキーマ、文字コードはUTF-8でテーブルを
作成します。

CREATE TABLE "dictionary" (
  "id" INTEGER PRIMARY KEY  NOT NULL ,
  "reading" TEXT NOT NULL ,
  "similarSoundingWords" TEXT NOT NULL );


・インストール
アプリをインストール後、

/data/dat/info.itog_lab.kanabun/databases
以下に、
kanabun.sqlite
というファイル名で置いて下さい。


----------------------------------------

