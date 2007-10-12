/* PageComponent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 8, 2007 5:48:27 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul;

import org.zkoss.jsf.zul.impl.RootComponent;
import org.zkoss.zk.ui.Execution;

/**
 * Defines a ZK page.
 * It is responsible for handling the lifecycle for ZK components, such
 * as event processing and rendering,
 *
 * <p>All other ZULJSF Component  must be placed inside a {@link org.zkoss.jsf.zul.Page}.
 * Nested page component are not allowed.
 * 
 * @author Dennis.Chen
 *
 */
public class Page extends RootComponent {
	
	private String _style;

	/** Returns the style.
	 * Default: null (no style at all).
	 */
	public String getStyle() {
		return _style;
	}
	/** Sets the style.
	 */
	public void setStyle(String style) {
		_style = style != null && style.length() > 0 ? style: null;
	}
	
	/** Creates and returns the page.
	 */
	protected void init(Execution exec, org.zkoss.zk.ui.Page page) {
		super.init(exec, page);

		page.setStyle(_style);
	}
}
