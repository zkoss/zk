/* Layout.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug  5 17:12:27 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zul.api;

import org.zkoss.zk.ui.api.HtmlBasedComponent;

/**
 * A layout.
 * @author tomyeh
 * @since 5.0.8
 */
public interface Layout extends HtmlBasedComponent {
	
	/** Returns the spacing between adjacent children, or null if the default
	 * spacing is used.
	 * <p>Default: null (means to use the default spacing).
	 */
	public String getSpacing();
	
	/** Sets the spacing between adjacent children.
	 * @param spacing the spacing (such as "0", "5px", "3pt" or "1em"),
	 * or null to use the default spacing
	 * @see #getSpacing
	 */
	public void setSpacing(String spacing);
}
