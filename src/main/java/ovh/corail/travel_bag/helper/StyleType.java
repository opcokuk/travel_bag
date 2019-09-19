package ovh.corail.travel_bag.helper;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public enum StyleType {
    TOOLTIP_DESC(TextFormatting.GRAY, true, false),
    TOOLTIP_USE(TextFormatting.DARK_PURPLE, true, false);

    private final Style style;
    private final String styleString;

    StyleType(TextFormatting color, boolean italic, boolean bold) {
        this.style = new Style().setColor(color).setItalic(italic).setBold(bold);
        this.styleString = color.toString() + (italic ? TextFormatting.ITALIC : "") + (bold ? TextFormatting.BOLD : "");
    }

    public Style getStyle() {
        return this.style;
    }

    @Override
    public String toString() {
        return styleString;
    }
}
