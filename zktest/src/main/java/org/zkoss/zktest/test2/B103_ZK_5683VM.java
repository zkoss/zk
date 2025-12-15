/* B103_ZK_5683VM.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 11 16:36:18 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.event.SelectEvent;

public class B103_ZK_5683VM {
    final B103_ZK_5683DataBeanModel model = new B103_ZK_5683DataBeanModel(Arrays.asList(
            new B103_ZK_5683DataBean(1, "Auto"),
            new B103_ZK_5683DataBean(2, "Telephone"),
            new B103_ZK_5683DataBean(3, "Zeppelin")
    ));
    Set<B103_ZK_5683DataBean> selection = new HashSet();
    int index = 0;

    @Init
    public void init() {
    }

    public B103_ZK_5683DataBeanModel getModel() {
        return model;
    }

    public Set<B103_ZK_5683DataBean> getSelection() {
        return selection;
    }

    public void setSelection(Set<B103_ZK_5683DataBean> selection) {
        this.selection = selection;
    }

    @DependsOn("selection")
    public String getSelectionLabel() {
        return Arrays.toString(selection.toArray());
    }

    @DependsOn("selection")
    public String getStack() {
        return model.getStack();
    }

    @Command
    public void onSelect(
            @ContextParam(ContextType.TRIGGER_EVENT) final SelectEvent event) {
        selection.clear();
        selection.addAll(event.getPreviousSelectedObjects());
        selection.addAll(event.getSelectedObjects());
        selection.removeAll(event.getUnselectedObjects());
        setSelection(selection);
        BindUtils.postNotifyChange(null, null, this, "selection");
    }
}
