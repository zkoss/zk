/* ZK.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 4, 2009 6:25:17 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest;

/**
 * ZK client side object.
 * @author jumperchen
 *
 */
public class ZK extends ClientWidget {
	/**
	 * The script of get jq by UUID
	 */
	private static String ZK = "zk('%1')";

	public ZK(String uuid) {
		if (isEmpty(uuid))
			throw new NullPointerException("uuid cannot be null!");
		_out = new StringBuffer(ZK.replace("%1", uuid));
	}
	public ZK(ClientWidget el) {
		_out = new StringBuffer(ZK.replace("'%1'", el.toString()));
	}
	public ZK(StringBuffer out, String script) {
		_out = new StringBuffer(out).append(script);
	}
	public ZK(StringBuffer out) {
		_out = new StringBuffer(out);
	}
	public String[] revisedOffset() {
		return ZKTestCase.getCurrent().getEval(_out.toString() + ".revisedOffset()").split(",");
	}
	public int revisedWidth(int size) {
		return Integer.parseInt(ZKTestCase.getCurrent().getEval(_out.toString() + ".revisedWidth("+ size +")"));
	}
	public int revisedHeight(int size) {
		return Integer.parseInt(ZKTestCase.getCurrent().getEval(_out.toString() + ".revisedHeight("+ size +")"));
	}
	public Jquery jq() {
		return new Jquery(_out, ".jq");
	}
}
