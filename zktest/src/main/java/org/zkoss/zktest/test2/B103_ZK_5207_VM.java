/* B103_ZK_5207_VM.java

        Purpose:

        Description:

        History:
                Tue Nov 04 17:49:48 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B103_ZK_5207_VM {
    private List<String> tempList1 = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5"));
    private String selectedItem = getTempList1().get(0);

    @Command
    @NotifyChange({ "tempList1", "selectedItem" })
//    @NotifyChange({"selectedItem", "tempList1"}) // workaround before fix: just clear first, then set model
    public void clear() {
        tempList1 = null;
        selectedItem = null;
    }

    public void setSelectedItem(String selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public List<String> getTempList1() {
        return tempList1;
    }

    public void setTempList1(List<String> tempList1) {
        this.tempList1 = tempList1;
    }
}
