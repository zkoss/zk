/* RenderContext.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan  5 11:48:18     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zhtml.impl;

import java.io.StringWriter;

import org.zkoss.zk.ui.Component;

/**
 * The render context used to render the additional part (JavaScript
 * code snippet).
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class RenderContext {
	/** The writer to output JavaScript codes.
	 */
	private final StringWriter _jsout = new StringWriter();

	/** Whether to generate HTML tags directly.
	 *
	 * <p>If true, the HTML tag shall be generated directly to the writer
	 * provided by {@link org.zkoss.zk.ui.sys.ComponentCtrl#redraw},
	 * and generates JavaScript code snippet in {@link #renderBegin}.
	 *
	 * <p>If false, ZHTML components shall generate properties by use of
	 * {@link org.zkoss.zk.ui.sys.ContentRenderer}.
	 */
	public boolean directContent = true;

	/** Completes the rendering by returning what are generated
	 * by {@link #renderBegin} and {@link #renderEnd} (never null).
	 * After rendering, the context is reset.
	 */
	public String complete() {
		final StringBuffer sb = _jsout.getBuffer();
		if (sb.length() > 0) {
			sb.insert(0, "<script>\nzkblbg(true);try{");
			sb.append("\n}finally{zkble();}</script>");
			final String txt = sb.toString();
			sb.setLength(0); //reset
			return txt;
		}
		return "";
	}
	/** Renders the beginning JavaScript code snippet for the component.
	 * It must be called before rendering the children.
	 *
	 * @param lookup whether to look up instead of creating a widget.
	 * Specifies true if the widget is created somewhere else.
	 */
	public void renderBegin(Component comp, boolean lookup) {
		_jsout.write("\nzkb2('");
		_jsout.write(comp.getUuid());
		final String wgtcls = lookup ? "zk.RefWidget": comp.getWidgetClass();
		if (!"zhtml.Widget".equals(wgtcls)) {
			_jsout.write("','");
			_jsout.write(wgtcls);
		}
		_jsout.write("');");
	}
	/** Renders the ending JavaScript code snippet for the component.
	 * It must be called after rendering the children.
	 */
	public void renderEnd(Component comp) {
		_jsout.write("zke();");
	}
}
