/* F85_ZK_1148_IncludeVM.java

        Purpose:
                
        Description:
                
        History:
                Mon Jul 02 16:41:09 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.bind.annotation.Destroy;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

public class F85_ZK_1148_IncludeVM {
	private static final Logger log = LoggerFactory.getLogger(F85_ZK_1148_DestroyD.class);
	@Init
	public void init() {
		Clients.log("init includeVM");
	}

	@Destroy
	public void destroyInclude() {
		Clients.log("destroy includeVM");
		log.warn("IncludeVM is called!");
		F85_ZK_1148FileDealer.writeMsg("IncludeVM is called!");
	}
	public Date getNow(){
		return new Date();
	}
	
	public String getString() {return "test";}
}
