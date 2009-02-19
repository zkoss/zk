/* Label.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun  8 18:53:53     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.xml.XMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;

import org.zkoss.zul.impl.XulElement;

/**
 * A label.
 * 
 * @author tomyeh
 */
public class Label extends XulElement implements org.zkoss.zul.api.Label {
	private String _value = "";
	private int _maxlength;
	private boolean _multiline, _pre;

	public Label() {
	}
	public Label(String value) {
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
		if (value == null) value = "";
		if (!Objects.equals(_value, value)) {
			_value = value;
			smartUpdate("value", _value);
		}
	}

	/** Returns the maximal length of the label.
	 * <p>Default: 0 (means no limitation)
	 */
	public int getMaxlength() {
		return _maxlength;
	}
	/** Sets the maximal length of the label.
	 */
	public void setMaxlength(int maxlength) {
		if (maxlength < 0) maxlength = 0;
		if (_maxlength != maxlength) {
			_maxlength = maxlength;
			smartUpdate("maxlength", _maxlength);
		}
	}
	/** Returns whether to preserve the new line and the white spaces at the
	 * begining of each line.
	 */
	public boolean isMultiline() {
		return _multiline;
	}
	/** Sets whether to preserve the new line and the white spaces at the
	 * begining of each line.
	 */
	public void setMultiline(boolean multiline) {
		if (_multiline != multiline) {
			_multiline = multiline;
			smartUpdate("multiline", _multiline);
		}
	}
	/** Returns whether to preserve the white spaces, such as space,
	 * tab and new line.
	 *
	 * <p>It is the same as style="white-space:pre". However, IE has a bug when
	 * handling such style if the content is updated dynamically.
	 * Refer to Bug 1455584.
	 *
	 * <p>Note: the new line is preserved either {@link #isPre} or
	 * {@link #isMultiline} returns true.
	 * In other words, <code>pre</code> implies <code>multiline</code>
	 */
	public boolean isPre() {
		return _pre;
	}
	/** Sets whether to preserve the white spaces, such as space,
	 * tab and new line.
	 */
	public void setPre(boolean pre) {
		if (_pre != pre) {
			_pre = pre;
			smartUpdate("pre", _pre);
		}
	}
	/** @deprecated As of release 5.0.0, use CSS instead.
	 */
	public boolean isHyphen() {
		return false;
	}
	/** @deprecated As of release 5.0.0, use CSS instead.
	 *
	 * <p>Use the CSS style called "word-wrap: word-break"
	 * to have similar effect.
	 * Unfortunately, word-wrap is not applicable to
	 * FF and Opera(it works fine with IE and Safari).
	 */
	public void setHyphen(boolean hyphen) {
	}

	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
	private static boolean isRawLabel(Component comp) {
		final LanguageDefinition langdef =
			comp.getDefinition().getLanguageDefinition();
		return langdef != null && langdef.isRawLabel();
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws IOException {
		super.renderProperties(renderer);

		if (_maxlength > 0) renderer.render("maxlength", _maxlength);
		render(renderer, "multiline", _multiline);
		render(renderer, "pre", _pre);

		if (_value.length() > 0) {
			boolean outed = false;
			final HtmlPageRenders.RenderContext rc =
				HtmlPageRenders.getRenderContext(null);
			if (rc != null && rc.crawlable) {
				rc.extra.write("<div id=\"");
				rc.extra.write(getUuid());
				rc.extra.write("\">");
				rc.extra.write(XMLs.encodeText(_value));
					//encode required since it might not be valid HTML
				rc.extra.write("</div>\n");
				outed = true;
			}
			if (outed) renderer.render("z_ea", "$value"); //decode required
			else render(renderer, "value", _value); //no need to encode
		}
	}
	public String getZclass() {
		return _zclass == null ? "z-label" : _zclass;
	}
	/** No child is allowed.
	 */
	protected boolean isChildable() {
		return false;
	}
}
