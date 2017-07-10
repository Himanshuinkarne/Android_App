package com.svc.sml.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class HairContent implements Serializable {

    /**
     * An array of sample (dummy) items.
     */
    public BaseAccessoryItem hairItem1;
    public BaseAccessoryItem hairItem2;
    public static final List<BaseAccessoryItem> ITEMS = new ArrayList<BaseAccessoryItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, BaseAccessoryItem> ITEM_MAP = new HashMap<String, BaseAccessoryItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    public HairContent(BaseAccessoryItem hairItem1,BaseAccessoryItem hairItem2) {
        this.hairItem1 = hairItem1;
        this.hairItem2 = hairItem2;

    }

    public HairContent() {

    }

    private static void addItem(BaseAccessoryItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.objId, item);
    }

    private static HairItem createDummyItem(int position) {
        return new HairItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
}
