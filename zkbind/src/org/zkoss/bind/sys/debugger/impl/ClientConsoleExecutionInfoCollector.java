/* ClientExecutionInfoCollector.java

	Purpose:
		
	Description:
		
	History:
		2013/1/7 Created by Dennis Chen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.bind.sys.debugger.impl.info.AddBindingInfo;
import org.zkoss.bind.sys.debugger.impl.info.AddCommandBindingInfo;
import org.zkoss.bind.sys.debugger.impl.info.AnnoWarnInfo;
import org.zkoss.bind.sys.debugger.impl.info.CommandInfo;
import org.zkoss.bind.sys.debugger.impl.info.EventInfo;
import org.zkoss.bind.sys.debugger.impl.info.LoadInfo;
import org.zkoss.bind.sys.debugger.impl.info.NotifyChangeInfo;
import org.zkoss.bind.sys.debugger.impl.info.SaveInfo;
import org.zkoss.bind.sys.debugger.impl.info.StackInfo;
import org.zkoss.bind.sys.debugger.impl.info.ValidationInfo;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.util.Clients;
/**
 * 
 * @author dennis
 * @since 6.5.2
 */
public class ClientConsoleExecutionInfoCollector extends AbstractExecutionInfoCollector{

	private boolean _startLine = false;
	@Override
	public void addInfo(JSONObject info) {
		
		String jsonstr = info.toJSONString();
//		System.out.println(">>"+jsonstr);
		StringBuilder sb = new StringBuilder();
		Object type = info.get("type");
		sb.append("var info = ").append(jsonstr).append(";");
		sb.append("if(console && typeof console.log == 'function'){");
		if(!_startLine){
			sb.append("console.log('=======================================');");
			_startLine = true;
		}
		sb.append("console.log('['+info.sid+']");
		int stack = (Integer)info.get("stack");
		for (int i = 0; i < stack; i++) {
			sb.append(" ");
			if(i==stack-1){
				if(StackInfo.TYPE.equals(type)){
					sb.append(" + ");
				}else{
					sb.append(" *");
				}
			}else{
				sb.append(" ");
			}
		}
		
		if(AddBindingInfo.TYPE.equals(type) || AddCommandBindingInfo.TYPE.equals(type)){
			sb.append("ADD-BINDING");
		}
		
		
		if(StackInfo.TYPE.equals(type)){
			sb.append("'+info.name + '\t' +");
		}else{
			sb.append("['+info.type +");
			if(info.containsKey("subtype")){
				sb.append("':'+info.subtype+']\t' +");
			}else{
				sb.append("']\t' +");
			}
		}
		
		if(EventInfo.TYPE.equals(type)){
			sb.append("'['+info.event +']' +");
		}
		else if(LoadInfo.TYPE.equals(type)){
			if(info.containsKey("condition")){
				sb.append("'['+info.condition+']\t' +");
			}
			sb.append("info.fromExpr+' > '+info.toExpr+'\t= ' +");
			if(Boolean.TRUE.equals(info.get("nullval"))){
				sb.append("'NULL' +");
			}else{
				sb.append("info.value +");
			}
		}
		else if(CommandInfo.TYPE.equals(type)){
			if(info.containsKey("event")){
				sb.append("'['+info.event+']\t' +");
			}
			if(info.containsKey("commandExpr")){
				sb.append("info.commandExpr+'\t=' +");
			}
			sb.append("info.command +");
		}
		else if(ValidationInfo.TYPE.equals(type)){
			sb.append("info.validatorExpr +'\t= '+info.validator +'\t result = '+info.result +");
		}
		else if(SaveInfo.TYPE.equals(type)){
			if(info.containsKey("condition")){
				sb.append("'['+info.condition+']\t' +");
			}
			sb.append("info.fromExpr+' > '+info.toExpr+'\t= ' +");
			
			if(Boolean.TRUE.equals(info.get("nullval"))){
				sb.append("'NULL' +");
			}else{
				sb.append("info.value +");
			}
		}
		else if(NotifyChangeInfo.TYPE.equals(type)){
			sb.append("'['+info.base +']['+ info.prop+']'+");
		}
		else if(AddBindingInfo.TYPE.equals(type)){
			if(info.containsKey("condition")){
				sb.append("'['+info.condition+']\t' +");
			}
			sb.append("info.fromExpr+' > '+info.toExpr +");
		}
		else if(AddCommandBindingInfo.TYPE.equals(type)){
			sb.append("'['+info.event +']\t'+ info.commandExpr +");
		}
		else if(AnnoWarnInfo.TYPE.equals(type)){
			sb.append("info.attr +' = @'+ info.anno+'()' +");
		}
		
		if(info.containsKey("widget")){
			sb.append("'\tcomponent ['+info.widget+','+info.uuid+','+info.id+']' + ");
		}
		if(info.containsKey("note")){
			sb.append("'\t'+info.note + ");
		}
		if(info.containsKey("location")){
			sb.append("'\t'+info.location + ");
		}
		sb.append("'');}");
		Clients.evalJavaScript(sb.toString());
	}

}
