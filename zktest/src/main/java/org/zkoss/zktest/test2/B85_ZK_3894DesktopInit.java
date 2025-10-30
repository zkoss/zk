/* B85_ZK_3894Init.java

        Purpose:
                
        Description:
                
        History:
                Mon Mar 19 11:57:43 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopInit;

public class B85_ZK_3894DesktopInit implements DesktopInit {
	@Override
	public void init(Desktop desktop, Object request) throws Exception {
		desktop.getExecution().sendRedirect("/test");
	}
}