/* Text.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov 24 15:17:07     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import java.lang.Object;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.xml.XMLs;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ext.Includer;
import org.zkoss.zk.ui.ext.RawId;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zhtml.impl.PageRenderer;
import org.zkoss.zhtml.impl.TagRenderContext;

/**
 * Represents a piece of text (of DOM).
 *
 * @author tomyeh
 */
public class Text extends AbstractComponent implements RawId {
	private String _value = "";
	private boolean _encode = true;

	public Text() {
	}
	public Text(String value) {
		setValue(value);
	}

	/** Returns the value.
	 * <p>Default: "".
	 */
	public String getValue() {
		return _value;
	}
	/** Sets the value.
	 */
	public void setValue(String value) {
		if (value == null)
			value = "";
		if (!Objects.equals(_value, value)) {
			_value = value;
			if (!invalidateParent())
				smartUpdate("value", _value);
			else
				invalidate();
		}
	}

	/** Whether to generate the value directly without ID. */
	private boolean isIdRequired() {
		final Page p = getPage();
		return p == null || !isVisible()
			|| getId().length() > 0 || !isRawLabel(p);
	}
	private static boolean isRawLabel(Page page) {
		final LanguageDefinition langdef =
				page.getLanguageDefinition();
		return langdef != null && langdef.isRawLabel();
	}

	/** Returns whether to encode the text, such as converting &lt;
	 * to &amp;lt;.
	 * <p>Default: true.
	 * @since 5.0.8
	 */
	public boolean isEncode() {
		return _encode;
	}
	/** Sets whether to encode the text, such as converting &lt;
	 * to &amp;lt;.
	 * <p>Default: true.
	 * @since 5.0.8
	 */
	public void setEncode(boolean encode) {
		_encode = encode;
	}

	//-- Component --//
	/** Returns the widget class, "zhtml.Text".
	 * @since 5.0.0
	 */
	public String getWidgetClass() {
		return "zhtml.Text";
	}
	
	private boolean invalidateParent() {
		return !(getParent() instanceof Includer) && !isIdRequired();
	}
	public void setParent(Component parent) {
		final Component old = getParent();
		if (old != null && old != parent && invalidateParent())
			old.invalidate();

		super.setParent(parent);

		// avoid Includer to invalidate itself in an infinite loop
		if (parent != null && old != parent && invalidateParent())
			parent.invalidate();
	}
	public void invalidate() {
		if (!invalidateParent()) super.invalidate();
		else getParent().invalidate();
	}
	public void redraw(Writer out) throws IOException {
		final Execution exec = Executions.getCurrent();
		if (!HtmlPageRenders.isDirectContent(exec)) {
			super.redraw(out);
			return;
		}

		final boolean idRequired = isIdRequired();
		if (idRequired) {
			out.write("<span id=\"");
			out.write(getUuid());
			out.write("\">");
		}

		out.write(_encode ? XMLs.encodeText(_value): _value);

		if (idRequired)
			out.write("</span>");

		final TagRenderContext rc = PageRenderer.getTagRenderContext(exec);
		if (rc != null) {
			rc.renderBegin(this, getClientEvents(), false);
			rc.renderEnd(this);
		}
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws IOException {
		super.renderProperties(renderer);

		render(renderer, "value", _value);
		render(renderer, "idRequired", isIdRequired());
		if (!_encode)
			renderer.render("encode", false);
	}
	protected boolean isChildable() {
		return false;
	}

	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}
	protected class ExtraCtrl implements org.zkoss.zk.ui.ext.render.DirectContent {
	}
}
