/* Applet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Sep 19 17:32:48 TST 2008, Created by davidchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */

package org.zkoss.zul;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zul.impl.XulElement;

/**
 * A generic applet component.
 * 
 * <p>
 * Non XUL extension.
 * 
 * @author Davidchen
 * @since 3.6.0
 */
public class Applet extends XulElement implements DynamicPropertied {
	private String _code = "";
	private String _name = "";
	private Map _params = new HashMap();

	public String getCode() {
		return _code;
	}

	public void setCode(String code) {
		_code = code;
		invalidate();
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
		smartUpdate("z.name", name);
	}

	public void setParams(Map params) {
		_params = new HashMap(params);
		invalidate();
	}

	public Map getParams() {
		return _params;
	}

	public List getParamsHtml() {
		List list = new LinkedList();
		Set keys = _params.entrySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Map.Entry pairs = (Map.Entry) iter.next();
			Object[] value = { pairs.getKey(), pairs.getValue() };
			list.add(value);
		}
		return list;
	}

	public Object getDynamicProperty(String name) {
		return _params.get(name);
	}

	public boolean hasDynamicProperty(String name) {
		return _params.containsKey(name);
	}

	public void setDynamicProperty(String name, Object value)
			throws WrongValueException {
		_params.put(name, value);
	}

	public void invoke(String function) {
		invoke(function, null);
	}

	public void invoke(String function, String[] argument) {
		StringWriter buffer = new StringWriter();
		buffer.write(function + "(");
		if (argument != null && argument.length > 0) {
			buffer.write("\"" + argument[0] + "\"");
			for (int i = 1; i < argument.length; i++) {
				buffer.write(",\"" + argument[i] + "\"");
			}
		}
		buffer.write(");");
		response("ctrl", new AuInvoke(this, "invoke", buffer.toString()));
	}

	public void setField(String field, int value) {
		response("ctrl", new AuInvoke(this, "field", field + "=" + value));
	}

	public void setField(String field, String value) {
		response("ctrl", new AuInvoke(this, "field", field + "=\""
				+ value.toString() + "\""));
	}
	/** No child is allowed.
	 */
	public boolean isChildable() {
		return false;
	}
}
