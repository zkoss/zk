/* ClientInformerExecutionInfoCollector.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.util.Clients;

/**
 * 
 * @author dennis
 * @since 6.5.2
 */
public class ClientInformerExecutionInfoCollector extends AbstractExecutionInfoCollector {

	@Override
	public void addInfo(JSONObject info) {

		String jsonstr = info.toJSONString();
		StringBuilder sb = new StringBuilder();
		sb.append("var info = ").append(jsonstr).append(";");
		sb.append("if(typeof zkBindInformer != 'undefined'){");
		sb.append("zkBindInformer.addExecutionInfo(info);");
		sb.append("}");
		Clients.evalJavaScript(sb.toString());
	}

}
