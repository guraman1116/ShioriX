package net.guraman.shiorix;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class SettingsManager {
    private static final File FILE = new File("config.json");
    private static final ObjectMapper mapper = new ObjectMapper();

    private Settings settings;

    public SettingsManager() {
        load();
    }

    public void load() {
        if (FILE.exists()) {
            try {
                settings = mapper.readValue(FILE, Settings.class);
            } catch (IOException e) {
                e.printStackTrace();
                settings = new Settings(); // デフォルトに戻す
            }
        } else {
            settings = new Settings(); // 初回起動
        }
    }

    public void save() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE, settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Settings getSettings() {
        return settings;
    }
}
