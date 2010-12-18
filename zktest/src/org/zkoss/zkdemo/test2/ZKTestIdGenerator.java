/* ZKTestIdGenerator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 16, 2009 12:25:28 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.IdGenerator;

/**
 * An ID generator for ZK Test platform.
 * @author jumperchen
 * @since 5.0.0
 */
public class ZKTestIdGenerator implements IdGenerator {
	private final static String PREFIX = "zk_comp_";
	private final static String ID_NUMBER = "zk_id_num";
	public String nextComponentUuid(Desktop desktop, Component comp) {
		int i = Integer.parseInt(desktop.getAttribute(ID_NUMBER).toString());
		i++;// Start from 1
		desktop.setAttribute(ID_NUMBER, String.valueOf(i));
		return PREFIX + i;
	}

	public String nextDesktopId(Desktop desktop) {
		if (desktop.getAttribute(ID_NUMBER) == null) {
			desktop.setAttribute(ID_NUMBER, "0");
		}
		return null;
	}

	public String nextPageUuid(Page page) {
		return null;
	}
}
