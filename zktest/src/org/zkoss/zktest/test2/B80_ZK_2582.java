/* B80_ZK_2582.java

	Purpose:
		
	Description:
		
	History:
		Wed May  4 16:56:33 CST 2016, Created by wenning

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;

/**
 * 
 * @author wenning
 */
public class B80_ZK_2582 {

    private String label = "111";

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
        Clients.log("0");
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireEventListeners(view, this);
    }

    @Listen("onChange(100)=#lb1")
    public void hello100(Event evt) {
        Clients.log("100");
    }

    @Listen("onChange(99)=#lb1")
    public void hello99(Event evt) {
        Clients.log("99");
    }

    @Listen("onChange(-100)=#lb1")
    public void hellom100(Event evt) {
        Clients.log("-100");
    }

}
