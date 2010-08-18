/* Hlayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 6, 2010 11:34:20 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.HtmlBasedComponent;


/**
 * A horizontal layout
 *<p>Default {@link #getZclass}: z-hlayout.
 * @author jumperchen
 * @since 5.0.4
 */
public class Hlayout extends HtmlBasedComponent {

	private String _spacing;
	
	/** Returns the spacing between adjacent children, or null if the default
	 * spacing is used.
	 * <p>Default: null (means to use the default spacing).
	 */
	public String getSpacing() {
		return _spacing;
	}
	/** Sets the spacing between adjacent children.
	 * @param spacing the spacing (such as "0", "5px", "3pt" or "1em"),
	 * or null to use the default spacing
	 * @see #getSpacing
	 */
	public void setSpacing(String spacing) {
		if (spacing != null && spacing.length() == 0) spacing = null;
		if (!Objects.equals(_spacing, spacing)) {
			_spacing = spacing;
			smartUpdate("spacing", _spacing);
		}
	}
	public String getZclass() {
		return _zclass == null ? "z-hlayout" : _zclass;
	}
}
