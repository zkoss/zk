/* ServerConsoleExecutionInfoCollector.java

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

/**
 * 
 * @author dennis
 * @since 6.5.2
 */
public class DefaultExecutionInfoCollector extends AbstractExecutionInfoCollector {

	private boolean _startLine = false;
	@Override
	public void addInfo(JSONObject info) {
		
		Object type = info.get("type");
		if(!_startLine){
			out("=======================================");
			_startLine = true;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("["+info.get("sid")+"]");
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
			sb.append(info.get("name")+"\t ");
		}else{
			sb.append("["+info.get("type")+(info.containsKey("subtype")?":"+info.get("subtype"):"")+"]\t");
		}
		
		if(EventInfo.TYPE.equals(type)){
			sb.append("["+info.get("event") +"]");
		}
		else if(LoadInfo.TYPE.equals(type)){
			if(info.containsKey("condition")){
				sb.append("["+info.get("condition")+"]\t");
			}
			sb.append(info.get("fromExpr")+" > "+info.get("toExpr")+"\t");
			if(Boolean.TRUE.equals(info.get("nullval"))){
				sb.append("NULL");
			}else{
				sb.append(info.get("value"));
			}
			
		}
		else if(CommandInfo.TYPE.equals(type)){
			if(info.containsKey("event")){
				sb.append("["+info.get("event")+"]\t");
			}
			if(info.containsKey("commandExpr")){
				sb.append("["+info.get("commandExpr")+"]\t");
			}
			sb.append(info.get("command"));
		}
		else if(ValidationInfo.TYPE.equals(type)){
			sb.append(info.get("validatorExpr")+"\t"+info.get("validator")+"\t result = "+info.get("result"));
		}
		else if(SaveInfo.TYPE.equals(type)){
			if(info.containsKey("condition")){
				sb.append("["+info.get("condition")+"]\t");
			}
			sb.append(info.get("fromExpr")+" > "+info.get("toExpr")+"\t");
			if(Boolean.TRUE.equals(info.get("nullval"))){
				sb.append("NULL");
			}else{
				sb.append(info.get("value"));
			}
		}
		else if(NotifyChangeInfo.TYPE.equals(type)){
			sb.append("["+info.get("base")+"]["+info.get("prop")+"]");
		}
		else if(AddBindingInfo.TYPE.equals(type)){
			if(info.containsKey("condition")){
				sb.append("["+info.get("condition")+"]\t");
			}
			sb.append(info.get("fromExpr")+" > "+info.get("toExpr"));
		}
		else if(AddCommandBindingInfo.TYPE.equals(type)){
			sb.append("["+info.get("event")+"]\t"+info.get("commandExpr"));
		}
		else if(AnnoWarnInfo.TYPE.equals(type)){
			sb.append(info.get("attr")+" = @"+info.get("anno")+"()");
		}

		if(info.containsKey("widget")){
			sb.append("\t<"+info.get("widget")+" uuid=\""+info.get("uuid")+"\" id=\""+info.get("id")+"\" />");
		}
		if(info.containsKey("note")){
			sb.append("\t"+info.get("note"));
		}
		if(info.containsKey("location")){
			sb.append("\t"+info.get("location"));
		}
		out(sb.toString());
	}

	protected void out(String string) {
		System.out.println(string);
	}

}
