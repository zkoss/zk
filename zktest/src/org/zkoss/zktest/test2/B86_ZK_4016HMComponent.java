/* B86_ZK_4016Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Aug 07 15:07:38 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;

public class B86_ZK_4016HMComponent extends HtmlMacroComponent {
	@Listen ("onCreate = #box")
	public void create(Event e){ 
		Clients.log(this.getClass() + e.getName());
	}
}
