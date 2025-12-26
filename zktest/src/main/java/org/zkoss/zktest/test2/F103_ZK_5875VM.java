/* F103_ZK_5875VM.java

        Purpose:
                
        Description:
                
        History:
                Mon Dec 15 17:03:34 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

public class F103_ZK_5875VM {
    private ListModelList<String> items;
    private ListModelList<String> selectedItems;

    @Init
    public void init() {
        items = new ListModelList<>();
        selectedItems = new ListModelList<>();

        items.add("ZK");
        items.add("ZKtest");
        items.add("ZKoss");
        items.add("ZK Framework");
        items.add("ZK Developer");
        items.add("ZK Community");
        items.add("ZK User");
        items.add("ZK Test Case");
        items.add("ZK Contributor");
        items.add("ZKworld");
    }

    public ListModelList<String> getItems() {
        return items;
    }

    public void setItems(ListModelList<String> items) {
        this.items = items;
    }

    public ListModelList<String> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(ListModelList<String> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
