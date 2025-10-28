package net.guraman.shiorix;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsWindow extends Stage {
    public SettingsWindow(Settings settings, Runnable onSaveCallback) {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        TextField fontField = new TextField(settings.getDefaultFont());
        Spinner<Integer> fontSizeSpinner = new Spinner<>(8, 72, settings.getFontSize());
        TextField searchEngineField = new TextField(settings.getSearchEngine());
        CheckBox darkModeCheck = new CheckBox("ダークモード");
        darkModeCheck.setSelected(settings.isDarkMode());

        Button saveButton = new Button("保存");
        saveButton.setOnAction(e -> {
            settings.setDefaultFont(fontField.getText());
            settings.setFontSize(fontSizeSpinner.getValue());
            settings.setSearchEngine(searchEngineField.getText());
            settings.setDarkMode(darkModeCheck.isSelected());
            onSaveCallback.run(); // 設定保存処理などを呼ぶ
            this.close();
        });

        root.getChildren().addAll(
                new Label("デフォルトフォント"), fontField,
                new Label("フォントサイズ"), fontSizeSpinner,
                new Label("検索エンジンURL"), searchEngineField,
                darkModeCheck,
                saveButton
        );

        Scene scene = new Scene(root, 300, 300);
        this.setScene(scene);
        this.setTitle("設定");
    }
}
