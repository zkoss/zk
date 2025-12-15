/* B103_ZK_5916VM.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 04 17:33:44 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

public class B103_ZK_5916VM {
    private ListModelList model;
    private String template;

    public ListModelList getModel() {
        return model;
    }

    public void setModel(ListModelList model) {
        this.model = model;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Init
    public void init() {
        model = new ListModelList(Arrays.asList("foo","bar","baz"));
        template = "tp1";
    }

    @Command
    @NotifyChange("template")
    public void switchTemplate() {
        template="tp2";
    }
}
