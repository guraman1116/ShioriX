package net.guraman.shiorix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import org.cef.CefApp;
import org.cef.CefClient;

import java.io.File;
import java.io.IOException;

public class ShioriXApplication extends Application {
    private static Stage primaryStage;
    private static CefClient cefClient;
    @Override
    public void start(Stage stage) throws IOException {
        CefAppBuilder builder = new CefAppBuilder();
        builder.setInstallDir(new File("jcef-bundle"));
        builder.addJcefArgs("--disable-gpu");
        builder.getCefSettings().windowless_rendering_enabled = true;

        try  {
            CefApp app = builder.build();
            cefClient = app.createClient();
        }catch (Exception _){
            System.err.println("Failed to initialize CEF.");
        }
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
