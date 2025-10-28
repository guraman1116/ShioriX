package net.guraman.shiorix;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class AddressBarController {

    @FXML
    private TextField urlTextField;
    @FXML
    private Button backButton;
    @FXML
    private Button forwardButton;

    private BrowserModel browserModel;
    private SettingsManager settingsManager;
    private BrowserController browserController;

    public void setBrowserModel(BrowserModel model) {
        this.browserModel = model;

        backButton.disableProperty().unbind();
        forwardButton.disableProperty().unbind();
    }

    public void setBackButtonDisable(boolean disable) {
        backButton.setDisable(disable);
    }

    public void setForwardButtonDisable(boolean disable) {
        forwardButton.setDisable(disable);
    }

    public void setBrowserController(BrowserController controller) {
        this.browserController = controller;
    }

    public void setLocation(String url) {
        urlTextField.setText(url);
    }

    @FXML
    private void handleGoAction(ActionEvent event) {
        String url = urlTextField.getText().trim();
        if (browserModel != null && !url.isEmpty()) {
            if (!url.matches("^https?://.*")) {
                url = "https://" + url;
            }
            browserModel.requestNavigation(url);
        }
    }

    @FXML
    private void handleSettingsAction(ActionEvent event) {
        new SettingsWindow(settingsManager.getSettings(), () -> {
            settingsManager.save();
            // UI全体の更新などもここで実行可能
        }).show();
    }

    @FXML
    private void handleBackAction(ActionEvent event) {
        if (browserController != null) {
            browserController.goBackCurrentTab();
        }
    }

    @FXML
    private void handleForwardAction(ActionEvent event) {
        if (browserController != null) {
            browserController.goForwardCurrentTab();
        }
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void applySettings(Settings settings) {
        if (settings != null) {
            urlTextField.setPromptText(settings.getSearchEngine());
        }
    }
}