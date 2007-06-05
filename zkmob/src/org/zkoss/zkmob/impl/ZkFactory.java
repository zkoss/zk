/* ZkFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 16, 2007 5:44:29 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.impl;

import javax.microedition.lcdui.Command;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.AbstractUiFactory;
import org.zkoss.zkmob.ZkComponent;

/**
 * Empty implementation; the root tag for MIL file.
 * @author henrichen
 *
 */
public class ZkFactory extends AbstractUiFactory {
	public ZkFactory(String name) {
		super(name);
	}

	public ZkComponent create(ZkComponent parent, String tag, Attributes attrs, String hostURL) {
		final String dtid = attrs.getValue("di"); //desktop id
		final String action = attrs.getValue("za"); //zk_action
		final String proctoStr = attrs.getValue("zp"); //zk_procto
		final int procto = proctoStr != null ? Integer.parseInt(proctoStr) : 900; 
		final String tiptoStr = attrs.getValue("zt"); //zk_tipto
		final int tipto = tiptoStr != null ? Integer.parseInt(tiptoStr) : 800;
		final String ver = attrs.getValue("zv"); //zk_ver
		
		Zk zk = new Zk(dtid, action, procto, tipto, ver, hostURL);
		
		return zk;
	}
}
