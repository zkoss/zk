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
	public void addInfo(JSONObject info) {
		Integer stack = (Integer)info.get("stack");
		StringBuilder sb = new StringBuilder();
		if(stack!=null){
			for(int i=stack;i>0;i--){
				sb.append("  ");
			}
		}
		sb.append(info.toJSONString());
		out(sb.toString());
	}

	private void out(String string) {
		System.out.println(string);
	}

}
