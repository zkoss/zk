/* Widget.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 4, 2009 12:17:52 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest;

/**
 * The client side object of ZK Widget.
 * @author jumperchen
 *
 */
public class Widget extends ClientWidget {
	/**
	 * The script of how get widget by UUID
	 */
	private static String WIDGET = "zk.Widget.$('%1')";

	private String _uuid;
	public Widget(String uuid) {
		if (isEmpty(uuid))
			throw new NullPointerException("uuid cannot be null!");
		_uuid = uuid;
		_out = new StringBuffer(WIDGET.replace("%1", uuid));
	}
	public Widget(StringBuffer out) {
		_out = new StringBuffer(out);
	}
	
	public Widget(StringBuffer out, String script) {
		_out = new StringBuffer(out).append(script);
	}
	
	public void set(String name, boolean value) {
		ZKTestCase.getCurrent().getEval(_out.toString() + toUpperCase(".set", name) + "(" + value + ")");
	}
	public void set(String name, int value) {
		ZKTestCase.getCurrent().getEval(_out.toString() + toUpperCase(".set", name) + "(" + value + ")");
	}
	public void set(String name, String value) {
		ZKTestCase.getCurrent().getEval(_out.toString() + toUpperCase(".set", name) + "('" + value + "')");
	}
	
	public String uuid() {
		return _uuid == null ? eval(".uuid") : _uuid;
	}
	
	public String id() {
		return eval(".id");
	}
	
	public String get(String name) {
		return ZKTestCase.getCurrent().getEval(_out.toString() + toUpperCase(".get", name) + "()");
	}

	public boolean is(String name) {
		return Boolean.valueOf(ZKTestCase.getCurrent().getEval(_out.toString() + toUpperCase(".is", name) + "()"));
	}
	public Widget lastChild() {
		return new Widget(_out, ".lastChild");
	}
	public Widget firstChild() {
		return new Widget(_out, ".firstChild");
	}
	public Widget nextSibling() {
		return new Widget(_out, ".nextSibling");
	}
	public Widget previousSibling() {
		return new Widget(_out, ".previousSibling");
	}
	public Element $n() {
		return new Element(_out + ".$n()");
	}
	public Element $n(String subNode) {
		return new Element(_out + ".$n('"+ subNode +"')");
	}
}
