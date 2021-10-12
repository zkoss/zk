/* F96_ZK_4914_ScriptErrorListener.java
	Purpose:

	Description:

	History:
		Tue June 29 18:09:07 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ScriptErrorEvent;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class F96_ZK_4914_ScriptErrorListener implements EventListener<ScriptErrorEvent> {
	private int logCnt = 0;

	@Override
	public void onEvent(ScriptErrorEvent event) throws Exception {
		Clients.log(++logCnt + "-Clients.evalJavaScript Error!" + "message: " + event.getMessage() + ", stack: " +event.getStack());
	}
}
