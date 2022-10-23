/* DestroyVM.java

        Purpose:
                
        Description:
                
        History:
                Fri Mar 09 2:45 PM:52 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Destroy;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;

public class F85_ZK_1148_DestroyA {
	private static final Logger log = LoggerFactory.getLogger(F85_ZK_1148_DestroyA.class);
	private Boolean myValue = true;
	private Label resultLabelA = new Label();

	@Command
	@NotifyChange ("myValue")
	public void toggleMyValue() {
		myValue = !myValue;
	}
	
	public Boolean getMyValue() {
		return myValue; 
	}

	@Destroy
	public void destroyA() {
		Component comp = null;
		for (Iterator itr = Executions.getCurrent().getDesktop().getComponents().iterator(); itr.hasNext();) {
			comp = (Component) itr.next();
			if (comp.getId().equals("resultLabel")) break;
		}
		Label l = (Label) comp;
		l.setValue(l.getValue() + "DestroyA ");
		Clients.log("DestroyA");
		log.warn("execute the DestroyA method");
		F85_ZK_1148FileDealer.writeMsg("DestroyA is called!");
	}

	@Init
	public void init() {
		myValue = true;
		Clients.log("DestroyP init 1");
		Clients.log("DestroyP init 2");
	}
}
