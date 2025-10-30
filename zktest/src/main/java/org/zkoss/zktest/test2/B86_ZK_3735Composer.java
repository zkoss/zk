/* B86_ZK_3735Composer.java

        Purpose:
                
        Description:
                
        History:
                Wed Aug 22 10:46:02 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

/**
 * @author LeonLee
 */
public class B86_ZK_3735Composer extends SelectorComposer<Window> {
    @Wire
    Listbox listbox;

    @Listen("onClick=#set1")
    public void setM1() {
        ArrayList<String> list1 = new ArrayList<String>();
        list1.add("item1");
        ListModelList<String> listmodel1 = new ListModelList<String>(list1);
        listbox.setModel(listmodel1);
        print();
    }

    @Listen("onClick=#set2")
    public void setM2() {
        ArrayList<String> list2 = new ArrayList<String>();
        list2.add("item1");
        ListModelList<String> listmodel2 = new ListModelList<String>(list2);
        listbox.setModel(listmodel2);
        print();
    }

    @Listen("onClick=#set3")
    public void setM3() {
        ArrayList<String> list3 = new ArrayList<String>();
        for (int i = 1; i <= 8; i++) {
            list3.add("item" + i);
        }
        ListModelList<String> listmodel3 = new ListModelList<String>(list3);
        listbox.setModel(listmodel3);
        print();
    }

    public void print() {
        Clients.log("listboxSize : " + listbox.getItemCount());
        Clients.log("visibleItemCount : " + listbox.getVisibleItemCount());
//        for (Listitem item : listbox.getItems()) {
//            Clients.log(item + " - " + item.isVisible());
//        }
    }
}
