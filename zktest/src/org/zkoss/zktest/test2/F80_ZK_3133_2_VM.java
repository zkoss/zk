/* F80_ZK_3133_2_VM.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 24 11:43:15 CST 2016, Created by wenninghsu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.MatchMedia;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.util.Clients;

/**
 * 
 * @author wenninghsu
 */
public class F80_ZK_3133_2_VM {

	private String windowWidth = "500px";
	private String label1 = "browser width > 500px";
	private static boolean b;

	public String getWindowWidth() {
		return windowWidth;
	}

	public String getLabel1() {
		return label1;
	}

	@MatchMedia("all and (min-width: 501px)")
	@NotifyChange("*")
	public void min501() {
		windowWidth = "500px";
		label1 = "browser width > 500px";
	}

	@MatchMedia("screen and (max-width: 500px)")
	@NotifyChange("*")
	public void max500(@ContextParam(ContextType.TRIGGER_EVENT) ClientInfoEvent evt) {
		windowWidth = "350px";
		label1 = "browser width = " + evt.getDesktopWidth() + "px";
	}

	@Command("all and (min-width: 501px)")
	public void create() {
		Clients.log("onCreate");
	}

}
