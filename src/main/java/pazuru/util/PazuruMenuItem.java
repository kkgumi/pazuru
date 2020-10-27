package pazuru.util;

public class PazuruMenuItem {
    private final String group;
    private final String url;

    public PazuruMenuItem(String group, String url) {
        this.group = group;
        this.url = url;
    }

    public String getGroup() { return this.group; }

    public String getUrl() { return this.url; }
}
