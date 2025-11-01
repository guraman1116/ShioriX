package net.guraman.shiorix;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.web.WebHistory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;

public class TabController {

    @FXML
    private WebView webView;
    private WebEngine webEngine;
    private BrowserController browserController; // 親コントローラへの参照
    private boolean isActive = false;

    @FXML
    public void initialize() {
        webEngine = webView.getEngine();
    }

    // BrowserControllerから呼び出される初期設定メソッド (Tabへの依存を削除)
    public void setup(BrowserController browserController, BrowserModel browserModel) {
        this.browserController = browserController;

        // 1. アドレスバーからのナビゲーションリクエストを監視
        browserModel.requestedUrlProperty().addListener((obs, oldVal, newVal) -> {
            if (isActive && newVal != null) {
                loadUrl(newVal);
            }
        });

        // 2. ページ内のリンククリックなどでURLが変わった場合、アドレスバーに反映
        webEngine.locationProperty().addListener((obs, oldLocation, newLocation) -> {
            if (isActive) {
                browserController.updateAddressBarLocation(newLocation);
                browserController.updateBackForwardButtons(webEngine.getHistory());
            }
        });

        // 3. ページのタイトルが変わったら、BrowserController経由でタブのテキストに反映
        webEngine.titleProperty().addListener((obs, oldTitle, newTitle) -> {
            String title = (newTitle != null && !newTitle.isEmpty()) ? newTitle : "読み込み中...";
            browserController.updateTabTitle(this, title);
        });

        // 4. ページの読み込みが完了したらFaviconを取得
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    Document doc = webEngine.getDocument();
                    if (doc == null) return;

                    NodeList linkTags = doc.getElementsByTagName("link");
                    String faviconUrl = null;
                    for (int i = 0; i < linkTags.getLength(); i++) {
                        Element link = (Element) linkTags.item(i);
                        String rel = link.getAttribute("rel");
                        if (rel != null && (rel.equals("icon") || rel.equals("shortcut icon"))) {
                            faviconUrl = link.getAttribute("href");
                            break;
                        }
                    }

                    if (faviconUrl != null) {
                        // 相対URLを絶対URLに変換
                        URL pageUrl = new URL(webEngine.getLocation());
                        URL absoluteFaviconUrl = new URL(pageUrl, faviconUrl);
                        Image favicon = new Image(absoluteFaviconUrl.toExternalForm());
                        browserController.updateTabFavicon(this, favicon);
                    } else {
                        // デフォルトのFaviconを試す
                        URL pageUrl = new URL(webEngine.getLocation());
                        URL defaultFaviconUrl = new URL(pageUrl.getProtocol() + "://" + pageUrl.getHost() + "/favicon.ico");
                        Image favicon = new Image(defaultFaviconUrl.toExternalForm(), true); // trueでバックグラウンド読み込み
                        favicon.errorProperty().addListener((errObs, hadError, isError) -> {
                            if (!isError) {
                                browserController.updateTabFavicon(this, favicon);
                            }
                        });

                    }
                } catch (Exception e) {
                    // エラー処理
                    e.printStackTrace();
                }
            }
        });
    }

    // BrowserControllerからタブのアクティブ状態を制御
    public void setActive(boolean active) {
        this.isActive = active;
        // アクティブになったら、現在表示中のURLをアドレスバーに反映させる
        if (active) {
            browserController.updateAddressBarLocation(webEngine.getLocation());
        }
    }

    public void loadUrl(String url) {
        webEngine.load(url);
    }

    public void applySettings(Settings settings) {
        // 設定に基づいてWebViewのスタイルや動作を変更
        webView.setFontScale((double) settings.getFontSize() / 10);
        // 他の設定もここで適用可能
    }

        // 追加: 戻る

        public void goBack() {

            WebHistory history = webEngine.getHistory();

            if (history.getCurrentIndex() > 0) {

                history.go(-1);

            }

        }

    

        // 追加: 進む

        public void goForward() {

            WebHistory history = webEngine.getHistory();

            if (history.getCurrentIndex() < history.getEntries().size() - 1) {

                history.go(1);

            }

        }

    

        public WebEngine getWebEngine() {

            return webEngine;

        }

    }

    