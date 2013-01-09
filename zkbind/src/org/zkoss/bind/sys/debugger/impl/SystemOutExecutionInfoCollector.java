/* SystemOutExecutionInfoCollector.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.json.JSONObject;

/**
 * 
 * @author dennis
 * @since 6.5.2
 */
public class SystemOutExecutionInfoCollector extends AbstractExecutionInfoCollector {

	@Override
	public void addExecutionInfo(JSONObject info) {
		System.out.println("[" + info.get("sid") + "]\t[" + info.get("widget") + "]\t[$" + info.get("uuid") + "]\t["
				+ info.get("id") + "]\t["
				+ info.get("type") + "]\t[" 
				+ info.get("fromExpr") + " > " 
				+ info.get("toExpr") + "\t= "
				+ info.get("value"));
	}

}
