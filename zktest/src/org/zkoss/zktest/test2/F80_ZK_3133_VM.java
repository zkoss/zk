/* F80_ZK_3133_VM.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 18 16:20:12 CST 2016, Created by wenninghsu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.MatchMedia;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.ClientInfoEvent;

/**
 * 
 * @author wenninghsu
 */
public class F80_ZK_3133_VM {

	private String windowWidth = "500px";
	private String templateName = "a";

	public String getWindowWidth() {
		return windowWidth;
	}

	public String getTemplateName() {
		return templateName;
	}

	@MatchMedia("screen and (max-width: 500px)")
	@NotifyChange("*")
	public void max500(@ContextParam(ContextType.TRIGGER_EVENT) ClientInfoEvent evt) {
		System.out.println("max500");
		windowWidth = "350px";
		templateName = "b";
	}

	@MatchMedia("all and (min-width: 501px)")
	@NotifyChange("*")
	public void min501(@ContextParam(ContextType.TRIGGER_EVENT) ClientInfoEvent evt) {
		System.out.println("min501");
		windowWidth = "500px";
		templateName = "a";
	}
}
