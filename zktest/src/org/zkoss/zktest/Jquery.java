/* Jquery.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 4, 2009 10:45:32 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.zktest;

/**
 * Jquery client side object.
 * @author jumperchen
 * 
 */
public class Jquery extends ClientWidget {
	/**
	 * The script of get jq by UUID
	 */
	private static String JQ = "jq('%1')";

	public Jquery(String uuid) {
		if (isEmpty(uuid))
			throw new NullPointerException("uuid cannot be null!");
		_out = new StringBuffer(JQ.replace("%1", uuid));
	}
	public Jquery(ClientWidget el) {
		_out = new StringBuffer(JQ.replace("'%1'", el.toString()));
	}
	public Jquery(StringBuffer out, String script) {
		_out = new StringBuffer(out).append(script);
	}
	public Jquery(StringBuffer out) {
		_out = new StringBuffer(out);
	}
	public boolean hasClass(String className) {
		return Boolean.valueOf((ZKTestCase.getCurrent().getEval(_out.toString() + ".hasClass('" + className + "')")));
	}
	public Jquery find(String selector) {
		return new Jquery(_out, ".find('" + selector + "')");
	}
	public ZK zk() {
		return new ZK(_out, ".zk");
	}
}
