/* WarnInfo.java

	Purpose:
		
	Description:
		
	History:
		2013/1/18 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl;

import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;

/**
 * @author dennis
 *
 */
public class WarnInfo extends ExecutionInfoBase{

	String _attr;
	String _anno;
	
	public WarnInfo(Component comp,String subject, String attr,String anno,String note) {
		super("warn-info", comp, subject, note);
		_attr = attr;
		_anno = anno;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		json.put("attr", _attr);
		json.put("anno", _anno);
		return json;
	}

}
