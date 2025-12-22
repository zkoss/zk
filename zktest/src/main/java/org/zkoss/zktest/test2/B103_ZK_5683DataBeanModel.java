/* B103_ZK_5683DataBeanModel.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 11 16:37:48 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Collection;

import org.zkoss.zul.ListModelList;

public class B103_ZK_5683DataBeanModel extends ListModelList<B103_ZK_5683DataBean> {
    private static final long serialVersionUID = 1L;
    final StringBuilder stack = new StringBuilder();
    int index = 0;

    public B103_ZK_5683DataBeanModel() {
        setMultiple(true);
    }

    public B103_ZK_5683DataBeanModel(final Collection<B103_ZK_5683DataBean> c) {
        super(c);
        setMultiple(true);
    }

    public String getStack() {
        return stack.toString();
    }

    @Override
    public void setSelection(final Collection<? extends B103_ZK_5683DataBean> selection) {
        if (selection.isEmpty()) {
            stack.append("empty\n");
        } else {
            selection.forEach(sel -> stack.append(sel + "\n"));
        }
        super.setSelection(selection);
    }

    @Override
    public void clearSelection() {
        stack.append("clear selection\n");
        super.clearSelection();
    }
}
