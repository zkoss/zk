/* B86_ZK_4016Composer.java

        Purpose:
                
        Description:
                
        History:
                Tue Aug 07 15:10:05 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;

public class B86_ZK_4016Composer extends SelectorComposer {
	@Listen ("onClick = listitem")
	public void click(){
		Clients.log("click");
	}
}
