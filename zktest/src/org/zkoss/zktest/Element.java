/* Element.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 4, 2009 12:34:01 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest;

/**
 * DOM element object.
 * @author jumperchen
 *
 */
public class Element extends ClientWidget {

	public Element(String script) {
		_out = new StringBuffer(script);
	}
	public Element(StringBuffer out) {
		_out = new StringBuffer(out);
	}
	public void set(String name, String value) {
		ZKTestCase.getCurrent().getEval(_out.toString() + "." + name + " = '" + value + "'");
	}
	public void set(String name, boolean value) {
		ZKTestCase.getCurrent().getEval(_out.toString() + "." + name + " = " + value + "");
	}
	public void set(String name, int value) {
		ZKTestCase.getCurrent().getEval(_out.toString() + "." + name + " = " + value + "");
	}
	public String get(String name) {
		return ZKTestCase.getCurrent().getEval(_out.toString() + "." + name);
	}
	public boolean is(String name) {
		return Boolean.valueOf(ZKTestCase.getCurrent().getEval(_out.toString() + "." + name));
	}
}
