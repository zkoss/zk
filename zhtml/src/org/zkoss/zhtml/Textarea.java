/* Textarea.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:05:13     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import java.lang.Object;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zhtml.impl.TagRenderContext;
import org.zkoss.zhtml.impl.PageRenderer;

/**
 * The TEXTAREA tag.
 *
 * <p>If you instantiate {@link Textarea} directly, you shall use {@link #setValue}
 * to set up the value. You shall not add children to it.
 * 
 * @author tomyeh
 */
public class Textarea extends Input implements AfterCompose {
	private String _value = "";

	static {
		addClientEvent(Textarea.class, Events.ON_CHANGE, 0);
			//don't declare as CE_IMPORTANT since it is not applicable
			//(all zhtml components share the same widget class)
	}

	public Textarea() {
		super("textarea");
	}
	public Textarea(String value) {
		super("textarea");
		_value = value != null ? value: "";
	}

	//@Override
	public void afterCompose() {
		String content = PageRenderer.childrenToContent(this);
		if (content != null)
			setValue(content);
	}
	//@Override
	public void setDynamicProperty(String name, Object value)
	throws WrongValueException {
		if ("value".equals(name)) {
			_value = Objects.toString(value);
			if (_value == null)
				_value = "";
		} else {
			super.setDynamicProperty(name, value);
		}
	}
	//@Override
	public Object getDynamicProperty(String name) {
		return "value".equals(name) ? _value: super.getDynamicProperty(name);
	}
	//@Override
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		render(renderer, "value", _value);
	}
	//@Override
	protected void redrawChildrenDirectly(TagRenderContext rc, Execution exec,
	java.io.Writer out) throws java.io.IOException {
		out.write(_value);
		super.redrawChildrenDirectly(rc, exec, out);
	}
}
