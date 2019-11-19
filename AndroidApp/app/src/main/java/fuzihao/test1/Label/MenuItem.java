package fuzihao.test1.Label;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class MenuItem {
    public Drawable iconResId;
    public int itemId;
    public String itemTitle;

    public MenuItem(int itemId, String itemTitle) {
        this.itemId = itemId;
        this.itemTitle = itemTitle;
    }

    public MenuItem(Drawable iconResId, int itemId, String itemTitle) {
        this.iconResId = iconResId;
        this.itemId = itemId;
        this.itemTitle = itemTitle;
    }

    public Drawable getIconResId() {
        return iconResId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemTitle() {
        return itemTitle;
    }
}
