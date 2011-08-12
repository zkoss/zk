/* Layout.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug  5 17:10:51 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.HtmlBasedComponent;

/**
 * A layout.
 * @author tomyeh
 * @since 5.0.8
 */
public class Layout extends HtmlBasedComponent {
	private String _spacing = "0.3em";
	
	/** Returns the spacing between adjacent children, or null if the default
	 * spacing is used.
	 * <p>Default: 0.3em (means to use the default spacing).
	 */
	public String getSpacing() {
		return _spacing;
	}
	/** Sets the spacing between adjacent children.
	 * @param spacing the spacing (such as "0", "5px", "3pt" or "1em").
	 * If the spacing is set to "auto", null, or empty (""), 
	 * the DOM style is left intact, so the spacing can be customized from CSS.
	 * @see #getSpacing
	 */
	public void setSpacing(String spacing) {
		if (spacing != null && spacing.length() == 0) spacing = null;
		if (!Objects.equals(_spacing, spacing)) {
			_spacing = spacing;
			smartUpdate("spacing", _spacing);
		}
	}

	//-- super --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		if (!"0.3em".equals(_spacing))
			renderer.render("spacing", _spacing);
	}
}
