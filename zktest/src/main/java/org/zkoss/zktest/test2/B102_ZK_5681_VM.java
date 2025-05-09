/* B102_ZK_5681_VM.java

        Purpose:
                
        Description:
                
        History:
                Mon May 05 15:38:55 CST 2025, Created by cherrylee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;

public class B102_ZK_5681_VM {
    private final DefaultTreeModel treeModel;
    private String template;
    private boolean open = false;

    public B102_ZK_5681_VM () {
        treeModel = new DefaultTreeModel(new DefaultTreeNode("ROOT",
                Arrays.asList(new DefaultTreeNode[]{
                        new DefaultTreeNode("David"),
                        new DefaultTreeNode("Thomas"),
                        new DefaultTreeNode("Steven"),
                        new DefaultTreeNode("Steven"),
                        new DefaultTreeNode("Steven"),
                        new DefaultTreeNode("Steven"),
                })));
    }

    @Command @NotifyChange({"template", "open"})
    public void create(){
        template = "groupbox";
    }

    @Command @NotifyChange("open")
    public void open(){
        open = true;
    }

    public String getTemplate() {
        return template;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

}


