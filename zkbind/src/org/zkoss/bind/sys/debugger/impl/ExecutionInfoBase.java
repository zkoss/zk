/* ExecutionInfoBase.java

	Purpose:
		
	Description:
		
	History:
		2013/1/18 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.bind.sys.debugger.ExecutionInfo;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
/**
 * 
 * @author dennis
 *
 */
public class ExecutionInfoBase implements ExecutionInfo{

	Component _comp;
	String _type;
	String _subject;
	String _note;

	protected ExecutionInfoBase(String type,Component comp,String subject,String note){
		_type = type;
		_comp = comp;
		_subject = subject;
		_note = note;
	}
	
	
	@Override
	public Component getComponent() {
		return _comp;
	}

	@Override
	public String getType() {
		return _type;
	}

	@Override
	public String getSubject() {
		return _subject;
	}
	
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		json.put("type", _type);
		if(_comp!=null){
			json.put("widget", _comp.getDefinition().getName());
			json.put("uuid", _comp.getUuid());
			json.put("id", _comp.getId());
		}else{
			json.put("widget", "");
			json.put("uuid", "");
			json.put("id", "");
		}
		json.put("subject", _subject);
		json.put("note", _note);
		return json;
	}

	
	//util method
	protected String toString(Object value, int len){
		String valstr = value==null?null:value.toString();
		if(valstr!=null && valstr.length()>len){
			valstr = valstr.substring(0, len-4)+"...";
		}
		return valstr;
	}
}
