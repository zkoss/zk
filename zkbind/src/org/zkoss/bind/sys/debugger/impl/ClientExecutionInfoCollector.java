/* ClientCollector.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.json.JSONObject;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
/**
 * 
 * @author dennis
 * @since 6.5.2
 */
public class ClientExecutionInfoCollector extends AbstractExecutionInfoCollector{

	private boolean _start = false;
	@Override
	public void addExecutionInfo(JSONObject info) {
		
		String jsonstr = info.toJSONString();
		StringBuilder sb = new StringBuilder();
		Object type = info.get("type");
		sb.append("var info = ").append(jsonstr).append(";");
		sb.append("if(typeof zkBindInformer != 'undefined'){");
		sb.append("zkBindInformer.addExecutionInfo(info);");
		sb.append("}else if(console && typeof console.log == 'function'){");
		if(!_start){
			sb.append("console.log('=======================================');");
		}
		sb.append("console.log('['+info.sid+']");
		int stack = (Integer)info.get("stack");
		for (int i = 0; i < stack; i++) {
			sb.append(" ");
			if(i==stack-1){
				sb.append("+");
			}else{
				sb.append(" ");
			}
		}
		sb.append("['+info.subject+']\t'+");
		if("enter-info".equals(type)){
			sb.append("'['+info.entry +']'");
		}else if("command-info".equals(type)){
			sb.append("'['+info.event +']\t'+ info.commandExpr +'\t= '+info.value");
		}else if("validation-info".equals(type)){
			sb.append("info.validatorExpr +'\t= '+info.validator +'\t result = '+info.result");
		}else if("save-info".equals(type)){
			sb.append("'['+info.condition+']\t'+ info.fromExpr+' > '+info.toExpr+'\t= '+info.value");
		}else if("notify-info".equals(type)){
			sb.append("'['+info.base +']['+ info.prop+']'");
		}else{
			sb.append("'['+info.condition+']\t'+info.fromExpr+' > '+info.toExpr+'\t= '+info.value");
		}
		
		sb.append("+' / on component ['+info.widget+','+info.uuid+','+info.id+']'");
		if(!Strings.isEmpty((String)info.get("note"))){
			sb.append("+'\t*'+info.note");
		}
		sb.append(");}");
		Clients.evalJavaScript(sb.toString());
		if(!_start){
			_start = true;
		}
	}



}
