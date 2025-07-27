package net.desolatesky.menu.pattern;

import net.desolatesky.menu.Menu;
import net.desolatesky.menu.item.MenuButton;
import net.desolatesky.menu.item.SimpleMenuButton;

import java.util.List;

public interface Pattern {

    void apply(Menu menu);

    static Pattern border(List<MenuButton> items) {
        return new BorderPattern(items);
    }

    static Pattern border(MenuButton item) {
        return new BorderPattern(item);
    }

}
