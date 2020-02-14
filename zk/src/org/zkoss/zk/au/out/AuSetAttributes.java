/* AuSetAttributes.java

	Purpose:
		
	Description:
		
	History:
		Wed Feb 12 15:48:05     2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.au.out;

import java.util.Date;
import java.util.List;

import org.zkoss.json.JSONArray;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.DeferredValue;
import org.zkoss.zk.ui.Component;

/**
 * A response to set successive attributes of the specified component at the client.
 * <p>data[0]: the uuid of the component<br/>
 * data[1]: the array of attribute name and attribute value
 *
 * @since 9.0.1
 * @author rudyhuang
 * @see org.zkoss.zk.au.out.AuSetAttribute
 */
public class AuSetAttributes extends AuResponse {
	/**
	 * Construct an instance for miscellaneous values, such as Boolean,
	 * Integer and so on.
	 */
	public AuSetAttributes(Component comp, AuSetAttribute... attrs) {
		super("setAttrs", comp, attrs);
	}

	@Override
	public List<Object> getEncodedData() {
		if (_data == null)
			return null;

		final JSONArray encdata = new JSONArray();
		encdata.add(new JSONComponent((Component) getDepends()));
		final JSONArray subdata = new JSONArray();
		for (AuSetAttribute setAttr : (AuSetAttribute[]) _data) {
			Object[] setAttrData = setAttr.getRawData();
			for (int j = 1, len = setAttrData.length; j < len; ++j) { // skip 0
				Object d = setAttrData[j];
				if (d instanceof DeferredValue)
					d = ((DeferredValue) d).getValue();
				if (d instanceof Component)
					d = new JSONComponent((Component) d);
				if (d instanceof Date)
					d = new JSONDate((Date) d);
				subdata.add(d);
			}
		}
		encdata.add(subdata);
		return encdata;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(60).append("[cmd=").append(_cmd);
		if (_data != null && _data.length > 0) {
			sb.append(", {");
			for (AuSetAttribute d : (AuSetAttribute[]) _data) {
				sb.append(d);
			}
			sb.append('}');
		}
		return sb.append(']').toString();
	}
}
