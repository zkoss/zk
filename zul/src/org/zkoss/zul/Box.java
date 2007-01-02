/* Box.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 20 21:51:32     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.impl.XulElement;

/**
 * A box.
 *
 * @author tomyeh
 */
public class Box extends XulElement {
	private String _spacing;
	private String _valign = "top";

	/** Default: vertical ({@link Vbox}).
	 */
	public Box() {
		setMold("vertical");
	}
	/**
	 * @param orient either "horizontal" or "vertical".
	 */
	public Box(String orient) {
		setOrient(orient);
	}

	/** Returns the orient (the same as {@link #getMold}).
	 * <p>Default: "vertical".
	 */
	public String getOrient() {
		return getMold();
	}
	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("orient cannot be "+orient);

		setMold(orient);
	}
	/** Returns the spacing.
	 * <p>Default: null (depending on CSS).
	 */
	public String getSpacing() {
		return _spacing;
	}
	/** Sets the spacing.
	 * @param spacing the spacing (such as "0", "5px", "3pt" or "1em")
	 */
	public void setSpacing(String spacing) {
		if (spacing != null && spacing.length() == 0) spacing = null;
		if (!Objects.equals(_spacing, spacing)) {
			_spacing = spacing;
			invalidate();
		}
	}

	/** Returns the vertical alignment of the adjacent cells of a box.
	 * It is meaningful only if {@link #getOrient} is horizontal (i.e.,
	 * {@link Hbox}).
	 * <p>Default: top.
	 */
	public String getValign() {
		return _valign;
	}
	/** Sets the vertical alignment of the adjacent cells of a box.
	 */
	public void setValign(String valign) {
		if (!Objects.equals(_valign, valign)) {
			_valign = valign;
			smartUpdate("valign", _valign);
		}
	}

	/** Returns the attributes used to wrap the children (never null).
	 * Used only by component development to generate HTML tags.
	 */
	public String getChildExteriorAttrs() {
		final StringBuffer sb = new StringBuffer(32);
		final boolean vert = "vertical".equals(getOrient());
		HTMLs.appendAttribute(sb, "class", vert ? "vbox": "hbox");
		if (_spacing != null) {
			sb.append("style=\"");
			if (vert) sb.append("padding-bottom:").append(_spacing);
			else sb.append("padding-right:").append(_spacing);
			sb.append('"');
		}
		return sb.toString();
	}
	/** Returns the attributes used to wrap splitter (never null).
	 * Used only by component development to generate HTML tags.
	 */
	public String getSplitterExteriorAttrs() {
		final boolean vert = "vertical".equals(getOrient());
		return (vert ? " height": " width") + "=\"8px\"";
	}
	 

	//-- Component --//
	public void onDrawNewChild(Component child, StringBuffer out)
	throws IOException {
		final String chdattrs = child instanceof Splitter ?
			getSplitterExteriorAttrs(): getChildExteriorAttrs();
		if ("vertical".equals(getOrient())) {
			final StringBuffer sb = new StringBuffer(16)
				.append("<tr id=\"").append(child.getUuid())
				.append("!chdextr\"><td ").append(chdattrs).append('>');
			out.insert(0, sb);
			out.append("</td></tr>");
		} else {
			final StringBuffer sb = new StringBuffer(32)
				.append("<td id=\"").append(child.getUuid())
				.append("!chdextr\"");
			if (child instanceof HtmlBasedComponent) {
				final String width = ((HtmlBasedComponent)child).getWidth();
				if (width != null)
					sb.append(" width=\"").append(width).append('"');
			}
			sb.append(chdattrs).append('>');
			out.insert(0, sb);
			out.append("</td>");
		}
	}
}
