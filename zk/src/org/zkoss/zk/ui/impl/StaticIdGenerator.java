/* StaticIdGenerator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 5 10:04:53 AM 2013, Created by Vincent
}}IS_NOTE

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zk.ui.impl;

import java.io.Serializable;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.IdGenerator;

/**
 * Use this generator will generate fixed uuid for each component. 
 * It is recommended if you want to performance stress test such as JMeter.
 * <p>To use this Id Generator, add system-config in zk.xml.
 * <pre>
 * &lt;system-config&gt;
 *     &lt;id-generator-class&gt;org.zkoss.zk.ui.impl.StaticIdGenerator&lt;/id-generator-class&gt;
 * &lt;/system-config&gt;
 * </pre>
 * 
 * @since 7.0.0
 * @author Vincent
 */
public class StaticIdGenerator implements IdGenerator, Serializable {
	private static final long serialVersionUID = 20130905100453L;

	private static final String ID_PREFIX = "z_";
	private static final String ID_NUM = "num";

	public String nextComponentUuid(Desktop desktop, Component comp,
			ComponentInfo compInfo) {
		String number;
		if ((number = (String) desktop.getAttribute(ID_NUM)) == null) {
			number = "0";
			desktop.setAttribute(ID_NUM, number);
		}
		int i = Integer.parseInt(number);
		i++;
		desktop.setAttribute(ID_NUM, String.valueOf(i));
		return ComponentsCtrl.toAutoId(ID_PREFIX, i);
	}

	public String nextPageUuid(Page page) {
		final Desktop desktop = page.getDesktop();
		String number;
		if ((number = (String) desktop.getAttribute(ID_NUM)) == null) {
			number = "0";
			desktop.setAttribute(ID_NUM, number);
		}
		int i = Integer.parseInt(number);
		i++;
		desktop.setAttribute(ID_NUM, String.valueOf(i));
		return ComponentsCtrl.toAutoId(ID_PREFIX, i);
	}

	public String nextDesktopId(Desktop desktop) {
		return null;
	}

}
