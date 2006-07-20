/* Style.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 20 15:17:40     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Iterator;

import com.potix.lang.Objects;

import com.potix.zk.ui.Executions;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.AbstractComponent;
import com.potix.zk.ui.UiException;

/**
 * The style component used to specify CSS styles for the owner desktop.
 *
 * <p>Note: a style component can appear anywhere in a ZUML page, but it
 * affects all components in the same desktop.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
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

	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Label))
			throw new UiException("Unsupported child for style: "+child);
		return super.insertBefore(child, insertBefore);
	}

	public void redraw(java.io.Writer out) throws java.io.IOException {
		if (_src != null) {
			out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
			out.write(getDesktop().getExecution().encodeURL(_src));
			out.write("\"/>");
		} else {
			out.write("<style type=\"text/css\">");
			for (final Iterator it = getChildren().iterator(); it.hasNext();)
				out.write(((Label)it.next()).getValue());
			out.write("</style>");
		}
	}
}
