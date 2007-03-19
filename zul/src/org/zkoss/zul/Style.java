/* Style.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 20 15:17:40     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;

/**
 * The style component used to specify CSS styles for the owner desktop.
 *
 * <p>Note: a style component can appear anywhere in a ZUML page, but it
 * affects all components in the same desktop.
 *
 * @author tomyeh
 */
public class Style extends AbstractComponent {
	private String _src;

	public Style() {
	}
	/**
	 * @param src the URI of an external style sheet.
	 */
	public Style(String src) {
		setSrc(src);
	}

	/** Returns the URI of an external style sheet.
	 *
	 * <p>Default: null. If null, the children's
	 * (must be instances of {@link Label}) value ({@link Label#getValue})
	 * is used as the content of styles.
	 * If not null, the HTML LINK tag is generated to ask the browser
	 * to load the specified style sheet.
	 *
	 * <p>Note: If not null, the content of children are ignored.
	 */
	public String getSrc() {
		return _src;
	}
	/** Sets the URI of an external style sheet.
	 *
	 * @param src the URI of an external style sheet, or null to use
	 * the content of children ({@link Label#getValue}) instead.
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;
		if (!Objects.equals(_src, src)) {
			_src = src;
			invalidate();
		}
	}

	//Component//
	/** Only {@link Label} children are allowed.
	 */
	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Label))
			throw new UiException("Unsupported child for style: "+child);
		if (super.insertBefore(child, insertBefore)) {
			invalidate();
			return true;
		}
		return false;
	}

	public void redraw(java.io.Writer out) throws java.io.IOException {
		if (_src != null) {
			out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
			out.write(getDesktop().getExecution().encodeURL(_src));
			out.write("\" id=\"");
			out.write(getUuid());
			out.write("\"/>");
		} else {
			out.write("<style type=\"text/css\" id=\"");
			out.write(getUuid());
			out.write("\">\n");
			for (final Iterator it = getChildren().iterator(); it.hasNext();) {
				out.write(((Label)it.next()).getValue());
				out.write('\n');
			}
			out.write("</style>");
		}
	}
}
