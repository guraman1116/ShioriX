package net.guraman.shiorix;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;

public class TabItemController {

    @FXML
    private HBox root;
    @FXML
    private ImageView faviconView;
    @FXML
    private Label titleLabel;
    @FXML
    private Button closeButton;

    private BrowserController browserController;
    private int tabIndex;

    @FXML
    public void initialize() {
        // タブがクリックされたときの処理
        root.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                browserController.switchToTab(tabIndex);
            }
        });
        root.hoverProperty().addListener((obs, oldHover, newHover) -> {
            closeButton.setVisible(newHover);
        });

        // Drag and Drop handlers
        root.setOnDragDetected(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Dragboard db = root.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                // Store the index of the tab being dragged
                content.putString(String.valueOf(tabIndex));
                db.setContent(content);

                // Create a snapshot for the drag view
                WritableImage snapshot = root.snapshot(new SnapshotParameters(), null);
                db.setDragView(snapshot);

                event.consume();
            }
        });

        root.setOnDragOver(event -> {
            if (event.getGestureSource() != root && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        root.setOnDragEntered(event -> {
            if (event.getGestureSource() != root && event.getDragboard().hasString()) {
                root.getStyleClass().add("drag-over");
            }
        });

        root.setOnDragExited(event -> {
            root.getStyleClass().remove("drag-over");
        });

        root.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                int draggedIndex = Integer.parseInt(db.getString());
                int targetIndex = this.tabIndex;

                browserController.reorderTabs(draggedIndex, targetIndex);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    // BrowserControllerから各種インスタンスとインデックスを受け取る
    public void setup(BrowserController browserController, int tabIndex) {
        this.browserController = browserController;
        this.tabIndex = tabIndex;
    }

    // タブのタイトルを設定
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setFavicon(Image image) {
        faviconView.setImage(image);
    }

    // 閉じるボタンが押されたときの処理
    @FXML
    private void handleClose() {
        browserController.closeTab(tabIndex);
    }

    // このタブがアクティブかどうかでスタイルを変更
    public void setActive(boolean active) {
        if (active) {
            root.getStyleClass().add("active-tab");
        } else {
            root.getStyleClass().remove("active-tab");
        }
    }

    // インデックスを更新する（タブが削除されたときに必要）
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }
    
    public Node getRootNode() {
        return root;
    }
}
