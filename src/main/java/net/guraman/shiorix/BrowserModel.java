package net.guraman.shiorix;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class BrowserModel {

    // アドレスバーから入力された、遷移リクエスト先のURL
    private final StringProperty requestedUrl = new SimpleStringProperty();

    // Settingsをプロパティとして保持
    private final ObjectProperty<Settings> settingsProperty = new SimpleObjectProperty<>();

    public StringProperty requestedUrlProperty() {
        return requestedUrl;
    }

    public void requestNavigation(String url) {
        this.requestedUrl.set(null); // 一旦クリアして変更を確実に検知させる
        this.requestedUrl.set(url);
    }

    // Settingsのgetter/setter
    public ObjectProperty<Settings> settingsProperty() {
        return settingsProperty;
    }
    public Settings getSettings() {
        return settingsProperty.get();
    }
    public void setSettings(Settings settings) {
        settingsProperty.set(settings);
    }
}
