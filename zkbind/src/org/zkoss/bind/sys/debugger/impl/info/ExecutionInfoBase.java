/* ExecutionInfoBase.java

	Purpose:
		
	Description:
		
	History:
		2013/1/18 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl.info;

import org.zkoss.bind.impl.BinderUtil;
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
	String _subtype;
	String _note;
	String _location;

	protected ExecutionInfoBase(String type,String subtype,Component comp,String note){
		_type = type;
		_subtype = subtype;
		_comp = comp;
		_note = note;
		
		_location = BinderUtil.hasContext()?BinderUtil.getContext().getCurrentLocationMessage():null;
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
	public String getNote() {
		return _note;
	}

	public String getSubtype() {
		return _subtype;
	}
	
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		putEssential(json,"type", _type);
		if(_comp!=null){
			put(json,"widget", _comp.getDefinition().getName());
			put(json,"uuid", _comp.getUuid());
			put(json,"id", _comp.getId());
		}
		put(json,"subtype", _subtype);
		put(json,"note", toString(_note, 300));
		put(json,"location", _location);
		return json;
	}

	protected static void put(JSONObject json,String prop,Object val){
		if(val!=null){
			json.put(prop, val);
		}
	}
	protected static void putEssential(JSONObject json,String prop,Object val){
		json.put(prop, val);
	}
	
	//utility method
	protected static String toString(Object value, int len){
		String valstr = value==null?null:value.toString();
		if(valstr!=null && valstr.length()>len){
			valstr = valstr.substring(0, len-4)+"...";
		}
		return valstr;
	}

}
