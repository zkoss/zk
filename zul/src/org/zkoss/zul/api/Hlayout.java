/* Hlayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 12, 2010 8:49:04 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul.api;

import org.zkoss.zk.ui.api.HtmlBasedComponent;

/**
 * A horizontal layout
 * @author jumperchen
 * @since 5.0.4
 */
public interface Hlayout extends HtmlBasedComponent {
	
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
