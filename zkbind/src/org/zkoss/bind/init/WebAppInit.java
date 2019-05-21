/* WebAppInit.java

	Purpose:
		
	Description:
		
	History:
		Fri May 17 09:53:37 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.init;

import org.zkoss.bind.callback.DestroyCallback;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.Configuration;

/**
 * The initialization of zkbind.
 *
 * @author rudyhuang
 * @since 8.6.2
 */
public class WebAppInit implements org.zkoss.zk.ui.util.WebAppInit {
	public void init(WebApp wapp) throws Exception {
		// ZK-1148: @Destroy support
		Configuration conf = wapp.getConfiguration();
		if (!conf.hasCallBack("destroy")) {
			conf.registerCallBack("destroy", new DestroyCallback());
		}
	}
}
