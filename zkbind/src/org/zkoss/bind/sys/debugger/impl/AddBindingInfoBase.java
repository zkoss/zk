/* AddSaveBindingInfo.java

	Purpose:
		
	Description:
		
	History:
		2013/1/18 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import java.util.Map;

import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;

/**
 * @author dennis
 *
 */
public class AddBindingInfoBase extends ExecutionInfoBase{

	String _location;
	
	protected AddBindingInfoBase(String type,Component comp,String subject,String note){
		super(type,comp,subject,note);
		_location = BinderUtil.hasContext()?BinderUtil.getContext().getCurrentLocationMessage():null;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		if(_location!=null){
			json.put("location", _location);
		}
		return json;
	}

}
