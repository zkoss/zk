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

import org.zkoss.bind.annotation.Destroy;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;

public class F85_ZK_1148_DestroyD extends F85_ZK_1148_DestroyC {
	private static final Logger log = LoggerFactory.getLogger(F85_ZK_1148_DestroyD.class);
	
	@Init
	public void init() {
		Clients.log("init DestroyD");
	}
	
	@Destroy(superclass = true)
	public void destroyD() {
		Component comp = null;
		for (Iterator itr = Executions.getCurrent().getDesktop().getComponents().iterator(); itr.hasNext();) {
			comp = (Component) itr.next();
			if (comp.getId().equals("resultLabel")) break;
		}
		Label l = (Label) comp;
		l.setValue(l.getValue() + "DestroyD ");
		Clients.log("DestroyD");
		log.warn("execute the DestroyD method");
		F85_ZK_1148FileDealer.writeMsg("DestroyD is called!");
	}
}
