package net.guraman.shiorix;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrowserController {

    @FXML
    private BorderPane rootPane;
    @FXML
    private HBox titleBarBox;
    @FXML
    private VBox sideBarBox;
    @FXML
    private VBox tabContainer; // Vertical tab bar
    @FXML
    private StackPane tabBox; // Content area

    private BrowserModel browserModel;
    private SettingsManager settingsManager;
    private AddressBarController addressBarController;

    private final List<Parent> tabContents = new ArrayList<>();
    private final List<TabController> tabControllers = new ArrayList<>();
    private final List<TabItemController> tabItemControllers = new ArrayList<>();
    private int activeTabIndex = -1;
    private double xOffset = 0;
    private double yOffset = 0;


    @FXML
    public void initialize() {
        browserModel = new BrowserModel();
        settingsManager = new SettingsManager();
        browserModel.setSettings(settingsManager.getSettings());

        try {
            FXMLLoader addressBarLoader = new FXMLLoader(getClass().getResource("AddressBar.fxml"));
            Parent addressBarNode = addressBarLoader.load();
            addressBarController = addressBarLoader.getController();
            addressBarController.setBrowserModel(browserModel);
            addressBarController.setSettingsManager(settingsManager);
            addressBarController.setBrowserController(this);
            sideBarBox.getChildren().addFirst(addressBarNode); // Add to the top

        } catch (IOException e) {
            e.printStackTrace();
        }
        //window drag move
        titleBarBox.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        titleBarBox.setOnMouseDragged(event -> {
            ShioriXApplication.getPrimaryStage().setX(event.getScreenX() - xOffset);
            ShioriXApplication.getPrimaryStage().setY(event.getScreenY() - yOffset);
        });


        browserModel.settingsProperty().addListener((obs, oldSettings, newSettings) -> updateUIWithSettings(newSettings));
        updateUIWithSettings(settingsManager.getSettings());

        // "+" button for new tabs
        Button newTabButton = new Button("+  New Tab");
        newTabButton.setMaxWidth(Double.MAX_VALUE);
        newTabButton.getStyleClass().add("tab-item");
        newTabButton.setOnAction(event -> createNewTab("https://www.google.com"));
        tabContainer.getChildren().addFirst(newTabButton);

        // Create the first tab
        createNewTab("https://www.google.com");

        // Initialize window controls animation
        titleBarAnimation = new Timeline();
        setupWindowControlsAnimation();
        TBVisible = false;
        toggleWindowControlsAnimation();
    }

    @FXML
    public void onCloseButtonClicked() {
        ShioriXApplication.getPrimaryStage().close();
    }
    private boolean TBVisible = false; // Window Controls visibility flag
    private Timeline titleBarAnimation;

    private void setupWindowControlsAnimation() {
        rootPane.setOnMouseMoved(event -> {
            boolean newVisibility = event.getY() < 30;
            if (newVisibility != TBVisible) {
                TBVisible = newVisibility;
                titleBarAnimation.stop(); // Stop the current animation
                toggleWindowControlsAnimation(); // Start the new one
            }
        });
    }

    @FXML
    public void onMaximizeButtonClicked() {
        Stage stage = ShioriXApplication.getPrimaryStage();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    public void onMinimizeButtonClicked() {
        Stage stage = ShioriXApplication.getPrimaryStage();
        stage.setIconified(true);
    }

    private void toggleWindowControlsAnimation() {
        final double targetHeight = TBVisible ? 35.0 : 0.0;

        // Clear old frames and handlers from the single timeline instance
        titleBarAnimation.getKeyFrames().clear();
        titleBarAnimation.setOnFinished(null);

        // KeyValue automatically starts from the current property value, making it reversible.
        KeyValue kvPref = new KeyValue(titleBarBox.prefHeightProperty(), targetHeight);
        KeyValue kvMin = new KeyValue(titleBarBox.minHeightProperty(), targetHeight);
        KeyFrame kf = new KeyFrame(Duration.millis(150), kvPref, kvMin);
        titleBarAnimation.getKeyFrames().add(kf);

        if (TBVisible) {
            // SHOWING
            titleBarBox.setManaged(true);
            titleBarBox.setVisible(true);
            titleBarAnimation.setOnFinished(event -> {
                //for (Node node : titleBarBox.getChildren()) {
                //    node.setVisible(true);
                //}
            });
        } else {
            // HIDING
            //for (Node node : titleBarBox.getChildren()) {
            //    node.setVisible(false);
            //}
            titleBarAnimation.setOnFinished(event -> {
                titleBarBox.setManaged(false);
                titleBarBox.setVisible(false);
            });
        }

        titleBarAnimation.play();
    }

    private void createNewTab(String initialUrl) {
        try {
            // 1. Load tab content (WebView)
            FXMLLoader tabContentLoader = new FXMLLoader(getClass().getResource("TabView.fxml"));
            Parent tabContent = tabContentLoader.load();
            TabController tabController = tabContentLoader.getController();

            // 2. Load tab item (UI in the sidebar)
            FXMLLoader tabItemLoader = new FXMLLoader(getClass().getResource("TabItem.fxml"));
            Node tabItem = tabItemLoader.load();
            TabItemController tabItemController = tabItemLoader.getController();

            // 3. Add to lists and scene graph
            int newIndex = tabControllers.size();
            tabContents.add(tabContent);
            tabControllers.add(tabController);
            tabItemControllers.add(tabItemController);
            tabBox.getChildren().add(tabContent);
            tabContainer.getChildren().addFirst(tabItem); // Add before the "+" button

            // 4. Setup controllers
            tabController.setup(this, browserModel);
            tabController.applySettings(browserModel.getSettings());
            tabItemController.setup(this, newIndex);

            // 5. Load URL and switch to the new tab
            tabController.loadUrl(initialUrl);
            switchToTab(newIndex);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void createJcefTab(String url) {
        //CefBrowser browser = Main.cefClient.createBrowser(url, false, false);
//
//
        //JComponent component = (JComponent) browser.getUIComponent();
//
        //SwingNode swingNode = new SwingNode();
        //swingNode.setContent(component);
        //tabBox.getChildren().add(swingNode);
    }


    public void switchToTab(int index) {
        if (index < 0 || index >= tabControllers.size() || index == activeTabIndex) {
            return;
        }

        // Deactivate previous tab
        if (activeTabIndex != -1) {
            tabControllers.get(activeTabIndex).setActive(false);
            tabItemControllers.get(activeTabIndex).setActive(false);
            tabContents.get(activeTabIndex).setVisible(false);
        }

        // Activate new tab
        activeTabIndex = index;
        tabControllers.get(activeTabIndex).setActive(true);
        tabItemControllers.get(activeTabIndex).setActive(true);
        tabContents.get(activeTabIndex).setVisible(true);
        tabContents.get(activeTabIndex).toFront();
    }

    public void closeTab(int index) {
        if (index < 0 || index >= tabControllers.size()) {
            return;
        }

        int oldActiveIndex = activeTabIndex;

        // Remove from scene and lists
        tabBox.getChildren().remove(tabContents.get(index));
        // The tab item UI is at the same index as the tab in the lists, before the '+' button.
        tabContainer.getChildren().remove(index);
        tabContents.remove(index);
        tabControllers.remove(index);
        tabItemControllers.remove(index);

        // Update indices of remaining tabs
        for (int i = index; i < tabItemControllers.size(); i++) {
            tabItemControllers.get(i).setTabIndex(i);
        }

        // If no tabs are left, create a new one and stop.
        if (tabControllers.isEmpty()) {
            activeTabIndex = -1; // Reset index
            createNewTab("https://www.google.com");
            return;
        }

        // Determine the next tab to activate
        int newActiveIndex = -1;
        if (index == oldActiveIndex) {
            // If the active tab was closed, select the one before it, or the new first tab.
            newActiveIndex = Math.max(0, index - 1);
        } else if (index < oldActiveIndex) {
            // If a tab before the active one was closed, the active index shifts down.
            newActiveIndex = oldActiveIndex - 1;
        } else { // index > oldActiveIndex
            // If a tab after the active one was closed, the active index doesn't change.
            newActiveIndex = oldActiveIndex;
        }

        // Force the switch to the new active tab.
        activeTabIndex = -1;
        switchToTab(newActiveIndex);
    }

    public void reorderTabs(int draggedIndex, int targetIndex) {
        if (draggedIndex == targetIndex) {
            return;
        }

        // Assuming direct mapping where child index = model index
        // Reorder UI first
        Node draggedNode = tabContainer.getChildren().remove(draggedIndex);
        tabContainer.getChildren().add(targetIndex, draggedNode);

        // Reorder models
        Parent content = tabContents.remove(draggedIndex);
        tabContents.add(targetIndex, content);

        TabController controller = tabControllers.remove(draggedIndex);
        tabControllers.add(targetIndex, controller);

        TabItemController itemController = tabItemControllers.remove(draggedIndex);
        tabItemControllers.add(targetIndex, itemController);

        // Update active tab index
        if (activeTabIndex == draggedIndex) {
            activeTabIndex = targetIndex;
        } else if (activeTabIndex > draggedIndex && activeTabIndex <= targetIndex) {
            activeTabIndex--;
        } else if (activeTabIndex < draggedIndex && activeTabIndex >= targetIndex) {
            activeTabIndex++;
        }

        // Update indices in all tab item controllers
        for (int i = 0; i < tabItemControllers.size(); i++) {
            tabItemControllers.get(i).setTabIndex(i);
        }
    }

    public void updateTabTitle(TabController controller, String title) {
        int index = tabControllers.indexOf(controller);
        if (index != -1) {
            tabItemControllers.get(index).setTitle(title);
        }
    }

    public void updateTabFavicon(TabController controller, javafx.scene.image.Image favicon) {
        int index = tabControllers.indexOf(controller);
        if (index != -1) {
            tabItemControllers.get(index).setFavicon(favicon);
        }
    }

    private void updateUIWithSettings(Settings settings) {
        if (addressBarController != null) {
            addressBarController.applySettings(settings);
        }
        for (TabController tc : tabControllers) {
            tc.applySettings(settings);
        }
    }

    public void updateAddressBarLocation(String url) {
        if (addressBarController != null) {
            addressBarController.setLocation(url);
        }
    }

    private TabController getCurrentTabController() {
        if (activeTabIndex != -1 && activeTabIndex < tabControllers.size()) {
            return tabControllers.get(activeTabIndex);
        }
        return null;
    }

    public void goBackCurrentTab() {
        TabController tc = getCurrentTabController();
        if (tc != null) {
            int i = tc.goBack();
            if (i > 0) {
                addressBarController.setBackButtonDisable(false);
            }
        }
        addressBarController.setForwardButtonDisable(false);

    }

    public void goForwardCurrentTab() {
        TabController tc = getCurrentTabController();
        if (tc != null) {
            int i = tc.goForward();
            if ( i > 0 ) {
                addressBarController.setForwardButtonDisable(false);
            }
        }
        addressBarController.setBackButtonDisable(false);
    }

    public void setBackForwardButtonDisable() {
        addressBarController.setBackButtonDisable(false);
        addressBarController.setForwardButtonDisable(true);
    }

}