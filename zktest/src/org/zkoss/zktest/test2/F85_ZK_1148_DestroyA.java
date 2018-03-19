/* DestroyVM.java

        Purpose:
                
        Description:
                
        History:
                Fri Mar 09 2:45 PM:52 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;


import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Destroy;
import org.zkoss.bind.annotation.Init;

import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;

import java.util.Iterator;

public class F85_ZK_1148_DestroyA {

	private Boolean myValue;
	private Label resultLabelA = new Label();;
	
	@Command
	@NotifyChange("myValue")
	public void toggleMyValue(){
		myValue = !myValue;
	}
	
	public Boolean getMyValue(){
		return myValue;
	}
	
	@Destroy
	public void destroyA() {
		Component comp = null;
		for(Iterator itr = Executions.getCurrent().getDesktop().getComponents().iterator(); itr.hasNext();) {
			comp = (Component) itr.next();
			if (comp.getId().equals("resultLabel")) break;
		}
		Label l = (Label) comp;
		l.setValue(l.getValue() + "DestroyA ");
		Clients.log("DestroyA");
	}
	
	@Init
	public void Init(){
		myValue = true;
		Clients.log("DestroyP init 1");
		Clients.log("DestroyP init 2");
	}
}
