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

import org.zkoss.bind.annotation.Destroy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;

public class F85_ZK_1148_DestroyC extends F85_ZK_1148_DestroyB {
	private static final Logger log = LoggerFactory.getLogger(F85_ZK_1148_DestroyC.class);
	@Destroy(superclass = true)
	public void destroyC() {
		Component comp = null;
		for (Iterator itr = Executions.getCurrent().getDesktop().getComponents().iterator(); itr.hasNext();) {
			comp = (Component) itr.next();
			if (comp.getId().equals("resultLabel")) break;
		}
		Label l = (Label) comp;
		l.setValue(l.getValue() + "DestroyC ");
		Clients.log("DestroyC");
		log.warn("execute the DestroyC method");
		F85_ZK_1148FileDealer.writeMsg("DestroyC is called!");
	}
}
