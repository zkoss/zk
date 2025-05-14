/* B102_ZK_5887_VM.java

        Purpose:
                
        Description:
                
        History:
                Wed May 14 10:16:04 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.concurrent.atomic.AtomicInteger;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

public class B102_ZK_5887_VM {
    public static final String SOME_ARBITRARY_GLOBAL_COMMAND = "someGlobalCommand";
    private ItemListVM itemListVM;

    @Init
    public void init() {
        itemListVM = new ItemListVM();
    }

    @Command("refresh")
    public void refresh() {
        itemListVM = new ItemListVM();
        BindUtils.postNotifyChange(this, "itemListVM");
        BindUtils.postGlobalCommand(null, null, SOME_ARBITRARY_GLOBAL_COMMAND, null);
    }

    public ItemListVM getItemListVM() {
        return itemListVM;
    }

    @GlobalCommand(SOME_ARBITRARY_GLOBAL_COMMAND)
    public void someGlobalCommand() {
        System.out.println(SOME_ARBITRARY_GLOBAL_COMMAND);
    }

    public static class ItemListVM {
        private AtomicInteger idCount = new AtomicInteger(0);
        ListModelList<NestedItem> nestedItems;

        public ItemListVM() {
            this.nestedItems = new ListModelList<>();
            nestedItems.add(new NestedItem("complex", idCount.incrementAndGet()));
            nestedItems.add(new NestedItem("simple", idCount.incrementAndGet()));
        }

        @Command("addNew")
        public void addNew(@BindingParam("target") NestedItem target) {
            nestedItems.add(nestedItems.indexOf(target) + 1, new NestedItem("complex", idCount.incrementAndGet()));
        }

        public ListModelList<NestedItem> getNestedItems() {
            return nestedItems;
        }

    }

    public static class NestedItem {
        private String type;
        private int id;

        public NestedItem(String type, int id) {
            this.type = type;
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public int getId() {
            return id;
        }
    }
}
