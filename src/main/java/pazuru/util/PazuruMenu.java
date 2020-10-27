package pazuru.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PazuruMenu implements Iterable<PazuruMenuItem> {
    private final String title;
    private final List<PazuruMenuItem> menu;

    public PazuruMenu(String title) {
        this.title = title;
        this.menu = new ArrayList<>();
    }

    public int size() {
        return this.menu.size();
    }

    public void add(String group, String url) {
        this.menu.add(new PazuruMenuItem(group, url));
    }

    public void add(int index, String group, String url) {
        this.menu.add(index, new PazuruMenuItem(group, url));
    }

    public String getTitle() {
        return this.title;
    }

    public PazuruMenuItem get(int index) {
        return this.menu.get(index);
    }

    public String getUrl(int index) {
        return this.menu.get(index).getUrl();
    }

    public String getGroup(int index) {
        return this.menu.get(index).getGroup();
    }

    @NotNull
    @Override
    public Iterator<PazuruMenuItem> iterator() {
        return this.menu.iterator();
    }
}
