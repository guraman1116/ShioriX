package net.guraman.shiorix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ShioriXApplication extends Application {
    private static Stage primaryStage;
    public static String VERSION = "0.1.0";
    private PersistentCookieManager persistentCookieManager;

    @Override
    public void init() throws Exception {
        super.init();
        persistentCookieManager = new PersistentCookieManager();
        persistentCookieManager.load();
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(ShioriXApplication.class.getResource("BrowserView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 810);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("ShioriX");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        persistentCookieManager.save();
        super.stop();
    }

    public static Stage getPrimaryStage() { return primaryStage; }
}
