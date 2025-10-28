package net.guraman.shiorix;

public class Settings {
    private String defaultFont = "Meiryo UI";
    private int fontSize = 14;
    private String searchEngine = "https://www.google.com/search?q=";
    private boolean darkMode = false;

    // --- Getter/Setter ---
    public String getDefaultFont() { return defaultFont; }
    public void setDefaultFont(String defaultFont) { this.defaultFont = defaultFont; }

    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }

    public String getSearchEngine() { return searchEngine; }
    public void setSearchEngine(String searchEngine) { this.searchEngine = searchEngine; }

    public boolean isDarkMode() { return darkMode; }
    public void setDarkMode(boolean darkMode) { this.darkMode = darkMode; }
}