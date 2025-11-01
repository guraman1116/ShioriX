package net.guraman.shiorix.API;

import net.guraman.shiorix.BrowserController;

public interface ShioriXAPI {
    static ShioriXAPI getAPI() {
        return BrowserController.getAPI();
    }

    String getVersion();
    void openUrlInNewTab(String url);
    void openUrlInCurrentTab(String url);
    void closeCurrentTab();
    void reloadCurrentTab();
    void goBackCurrentTab();
    void goForwardCurrentTab();
    String getCurrentUrl();
    String getPageTitle();
}
