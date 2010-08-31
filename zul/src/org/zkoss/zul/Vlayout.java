/* Vlayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 6, 2010 12:46:39 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.HtmlBasedComponent;

/**
 * A vertical layout
 *<p>Default {@link #getZclass}: z-vlayout.
 * @author jumperchen
 * @since 5.0.4
 */
public class Vlayout extends HtmlBasedComponent {

	private String _spacing = "0.3em";
	
	/** Returns the spacing between adjacent children, or null if the default
	 * spacing is used.
	 * <p>Default: 0.3em (means to use the default spacing).
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
		if (spacing == null || spacing.length() == 0) spacing = "0.3em";
		if (!Objects.equals(_spacing, spacing)) {
			_spacing = spacing;
			smartUpdate("spacing", _spacing);
		}
	}
	public String getZclass() {
		return _zclass == null ? "z-vlayout" : _zclass;
	}
	//-- super --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		if (!"0.3em".equals(_spacing))
			render(renderer, "spacing", _spacing);
	}
}
