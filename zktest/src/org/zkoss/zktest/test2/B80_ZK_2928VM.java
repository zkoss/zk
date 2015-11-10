/* B80_ZK_2928VM.java

	Purpose:
		
	Description:
		
	History:
		10:00 AM 10/28/15, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Locale;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

/**
 * @author jameschu
 */
public class B80_ZK_2928VM {
    private String ourProp;
    private ListModel model = new ListModelList(Locale.getAvailableLocales());

    public String getOurProp() {
        return ourProp;
    }

    public void setOurProp(String ourProp) {
        this.ourProp = ourProp;
        Clients.log(">>" + ourProp);
    }

    @Command
    public void showSelectedItem(){
        Clients.log("showSelectedItem:" + ourProp);
    }

    public ListModel getModel() {
        return model;
    }

    public void setModel(ListModel model) {
        this.model = model;
    }
}