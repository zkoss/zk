/* ClientCollector.java

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
public class ClientExecutionInfoCollector extends AbstractExecutionInfoCollector{

	@Override
	public void addExecutionInfo(JSONObject info) {
		String jsonstr = info.toJSONString();
		
		String jscript = 
				"var info = " + jsonstr+";" +
				"if(typeof zkBindInformer != 'undefined'){" +
				"zkBindInformer.addExecutionInfo(info);" +
				"}else if(console && typeof console.log == 'function'){" +
				"console.log('['+info.sid+']\t['+info.widget+']\t[$'+info.uuid+']\t['+info.id+']\t['+info.type+']\t'+info.fromExpr+' > '+info.toExpr+'\t= '+info.value);" +
				"}";
		Clients.evalJavaScript(jscript);
	}

}
