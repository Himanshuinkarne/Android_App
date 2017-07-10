package com.svc.sml.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by himanshu on 1/26/16.
 */
public class SpecsContent implements Serializable {


    /**
     * An array of sample (dummy) items.
     */
    public BaseAccessoryItem item1;
    public BaseAccessoryItem item2;
    public BaseAccessoryItem item3;
    public static final List<BaseAccessoryItem> ITEMS = new ArrayList<BaseAccessoryItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, BaseAccessoryItem> ITEM_MAP = new HashMap<String, BaseAccessoryItem>();

    private static final int COUNT = 25;


    public SpecsContent(BaseAccessoryItem item1,BaseAccessoryItem item2, BaseAccessoryItem item3) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
    }

    public SpecsContent() {

    }

    private static void addItem(BaseAccessoryItem item) {
        ITEMS.add(item);
        //ITEM_MAP.put(item.objId, item);
    }
}
