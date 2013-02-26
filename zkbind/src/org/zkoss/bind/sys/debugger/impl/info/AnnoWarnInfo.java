/* WarnInfo.java

	Purpose:
		
	Description:
		
	History:
		2013/1/18 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger.impl.info;

import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;

/**
 * @author dennis
 *
 */
public class AnnoWarnInfo extends ExecutionInfoBase{

	public static final String TYPE = "anno-warn";
	
	String _attr;
	String _anno;
	
	public AnnoWarnInfo(Component comp,String attr,String anno,String note) {
		super(TYPE, null,comp, note);
		_attr = attr;
		_anno = anno;
	}
	
	public JSONObject toJSON(){
		JSONObject json = super.toJSON();
		putEssential(json,"attr", _attr);
		putEssential(json,"anno", _anno);
		return json;
	}

}
