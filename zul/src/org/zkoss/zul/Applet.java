/* Applet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Sep 19 17:32:48 TST 2008, Created by davidchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */

package org.zkoss.zul;

import java.io.StringWriter;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.zkoss.util.Pair;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.DynamicPropertied;

/**
 * A generic applet component.
 * 
 * <p>
 * Non XUL extension.
 * 
 * @author Davidchen
 * @since 3.5.0
 */
//public class Applet extends HtmlBasedComponent implements DynamicPropertied,
//		AfterCompose {
public class Applet extends HtmlBasedComponent implements DynamicPropertied {
	private String _code = "";
	private String _name = "";
	private String _argument = "";
	private Queue callbackQueue = new LinkedList();

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
		smartUpdate("z:name", name);
	}

	public void setParams(Map params) {
		_params = new HashMap(params);
		invalidate();
	}

	public List getParams() {
		List list = new LinkedList();
		Set keys = _params.entrySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Map.Entry pairs = (Map.Entry) iter.next();
			Pair pair = new Pair(pairs.getKey(), pairs.getValue());
			list.add(pair);
		}
		return list;
	}

	public Object getDynamicProperty(String name) {
		// TODO Auto-generated method stub
		return _params.get(name);
	}

	public boolean hasDynamicProperty(String name) {
		// TODO Auto-generated method stub
		return _params.containsKey(name);
	}

	public void setDynamicProperty(String name, Object value)
			throws WrongValueException {
		// TODO Auto-generated method stub
		_params.put(name, value);
	}
//
//	public void invoke(ServerCallback callback, String function,
//			String[] argument) {
//		callbackQueue.add(callback);
//		StringWriter buffer = new StringWriter();
//		buffer.write(function + "(");
//		for (int i = 0; i < argument.length; i++) {
//			buffer.write(argument[i] + ",");
//		}
//		buffer.write(");");
//		response("ctrl", new AuInvoke(this, "invoke", buffer.toString()));
//	}
//
//	public void field(ServerCallback callback, String filedname) {
//		callbackQueue.add(callback);
//		response("ctrl", new AuInvoke(this, "invoke", filedname));
//	}
//
//	/**
//	 * 
//	 * @author davidchen
//	 * 
//	 */
//	public static interface ServerCallback {
//		/**
//		 * 
//		 * @param args
//		 */
//		void doCallback(String[] args);
//	}
//
//	public void afterCompose() {
//		this.addEventListener("onCallback", new EventListener() {
//			public void onEvent(Event event) throws Exception {
//				ServerCallback callback = (ServerCallback) callbackQueue.poll();
//				callback.doCallback(null);
//			}
//		});
//
//	}
}
