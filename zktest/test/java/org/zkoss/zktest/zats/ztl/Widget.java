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
package org.zkoss.zktest.zats.ztl;

import org.openqa.selenium.By;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * A simulator of the ZK client widget.
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
	
	public Widget(JQuery jquery) {
		_out = new StringBuffer(WIDGET.replace("'%1'", jquery.toString()));
	}
	public Widget(StringBuffer out) {
		_out = new StringBuffer(out);
	}
	
	public Widget(StringBuffer out, String script) {
		_out = new StringBuffer(out).append(script);
	}

	/**
	 * Sets the boolean value to the evaluated name.
	 * <p>For example,
	 * <p><code>listitem.set("selected", true);</code>
	 * @param name any allowed property of the widget.
	 * @param value true or false.
	 */
	public void set(String name, boolean value) {
		WebDriverTestCase
				.getEval("!!" + _out.toString() + toUpperCase(".set", name) + "(" + value + ")");
	}
	/**
	 * Sets the number value to the evaluated name.
	 * <p>For example,
	 * <p><code>listbox.set("rows", 12);</code>
	 * @param name any allowed property of the widget.
	 * @param value any number.
	 */
	public void set(String name, int value) {
		WebDriverTestCase.getEval("!!" +_out.toString() + toUpperCase(".set", name) + "(" + value + ")");
	}
	/**
	 * Sets the string value to the evaluated name.
	 * <p>For example,
	 * <p><code>grid.set("width", "100px");</code>
	 * @param name any allowed property of the widget.
	 */
	public void set(String name, String value) {
		WebDriverTestCase.getEval("!!" +_out.toString() + toUpperCase(".set", name) + "('" + value + "')");
	}

	/**
	 * Returns the uuid of the widget.
	 */
	public String uuid() {
		if(_uuid == null)
			return _uuid = eval("uuid");
		return _uuid;
	}
	
	/**
	 * Returns the id of the widget.
	 */
	public String id() {
		return eval("id");
	}

	/**
	 * Returns the value of the evaluated name.
	 * <p>For example,
	 * <br/><code>grid.get("width");</code>
	 * <br/>in JavaScript
	 * <br/><code>grid.getWidth();</code>
	 * @param name any allowed property of the widget.
	 */
	public String get(String name) {
		return WebDriverTestCase.getEval(_out.toString() + toUpperCase(".get", name) + "()");
	}

	/**
	 * Returns the boolean value of the evaluated name.
	 * <p>For example,
	 * <br/><code>grid.is("vflex");</code>
	 * <br/>in JavaScript
	 * <br/><code>grid.isVflex();</code>
	 * @param name any allowed property of the widget.
	 */
	public boolean is(String name) {
		return Boolean.valueOf(WebDriverTestCase.getEval(_out.toString() + toUpperCase(".is", name) + "()"));
	}
	/**
	 * Returns the child of the widget from the given name.
	 * <p>
	 * For example,
	 * <p><code>Widget rows = grid.getChild("rows");</code>
	 */
	public Widget getChild(String name) {
		return new Widget(_out, "." + name);
	}
	
	/**
	 * Returns the size of the children.
	 */
	public int nChildren() {
		return WebDriverTestCase.parseInt(WebDriverTestCase.getEval(_out.toString() + ".nChildren"));
	}
	/**
	 * Returns the last child of the widget.
	 */
	public Widget lastChild() {
		return new Widget(_out, ".lastChild");
	}
	/**
	 * Returns the first child of the widget.
	 */
	public Widget firstChild() {
		return new Widget(_out, ".firstChild");
	}
	/**
	 * Returns the next sibling of the widget.
	 */
	public Widget nextSibling() {
		return new Widget(_out, ".nextSibling");
	}
	/**
	 * Returns the previous sibling of the widget.
	 */
	public Widget previousSibling() {
		return new Widget(_out, ".previousSibling");
	}
	/**
	 * Returns the fellow of the widget from the given id.
	 */
	public Widget $f(String id) {
		return new Widget(_out, ".$f('" + id + "', true)");
	}
	/**
	 * Returns the id-space-owner of the widget.
	 */
	public Widget $o() {
		return new Widget(_out, ".$o()");
	}
	/**
	 * Returns the element of the widget.
	 */
	public Element $n() {
		return new Element(_out + ".$n()");
	}
	/**
	 * Returns the sub-element of the widget from the given sub-name.
	 */
	public Element $n(String subname) {
		return new Element(_out + ".$n('"+ subname +"')");
	}
	
	public Element toElement() {
		return $n();
	}
	/**
	 * Detaches the widget
	 */
	public void detach() {
		WebDriverTestCase.eval(_out.toString() + ".detach()");
	}
	
	/**
	 * Returns whether the widget exists or not.
	 */
	public boolean exists() {
		return Boolean.valueOf(WebDriverTestCase.getEval("!!" + _out.toString() + " && !!" + _out.toString() + ".$n()"));
	}
	
	public By toBy() {
		return By.id(uuid());
	}
}
