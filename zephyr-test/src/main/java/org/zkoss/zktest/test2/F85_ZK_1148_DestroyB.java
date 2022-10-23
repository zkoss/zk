/* DestroyedVM.java

        Purpose:
                
        Description:
                
        History:
                Fri Mar 09 5:25 PM:54 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;

public class F85_ZK_1148_DestroyB extends F85_ZK_1148_DestroyA {
	private static final Logger log = LoggerFactory.getLogger(F85_ZK_1148_DestroyA.class);

	
	public void destroyB() {
		Component comp = null;
		for (Iterator itr = Executions.getCurrent().getDesktop().getComponents().iterator(); itr.hasNext();) {
			comp = (Component) itr.next();
			if (comp.getId().equals("resultLabel")) break;
		}
		Label l = (Label) comp;
		l.setValue(l.getValue() + "DestroyB ");
		Clients.log("DestroyB");
		log.warn("execute the DestroyB method");
		F85_ZK_1148FileDealer.writeMsg("DestroyB is called!");
	}
}
