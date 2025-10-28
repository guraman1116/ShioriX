package net.guraman.shiorix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;

import java.io.IOException;

public class ShioriXApplication extends Application {
    private static Stage primaryStage;
    private static CefClient cefClient;
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

    public static Stage getPrimaryStage() { return primaryStage; }
    public static CefClient getCefClient() { return cefClient; }
}
