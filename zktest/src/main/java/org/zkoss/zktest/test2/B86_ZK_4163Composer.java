/* B86_ZK_4163Composer.java

        Purpose:
                
        Description:
                
        History:
                Wed Dec 26 14:21:47 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Fileupload;

public class B86_ZK_4163Composer extends SelectorComposer {

	@Listen("onClick = #button")
	public void upload() {
		Fileupload.get(event -> Clients.log(event.getMedia().getName()));
	}
}
