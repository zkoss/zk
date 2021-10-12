/* DefaultScriptErrorListener.java
	Purpose:

	Description:

	History:
		Tue June 29 18:09:07 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ScriptErrorEvent;

/**
 * A Default Event Listener for handling client javascript error
 * @author jameschu
 */
public class DefaultScriptErrorListener implements EventListener<ScriptErrorEvent>, java.io.Serializable {
	private static final long serialVersionUID = 20210629180907L;
	private static final Logger log = LoggerFactory.getLogger(DefaultScriptErrorListener.class);

	@Override
	public void onEvent(ScriptErrorEvent event) throws Exception {
		log.error("Clients.evalJavascript error! message: " + event.getMessage() + ", stack: " + event.getStack());
	}
}
