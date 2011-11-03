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

/**
 * A horizontal layout
 *<p>Default {@link #getZclass}: z-hlayout.
 * @author jumperchen
 * @since 5.0.4
 */
public class Hlayout extends Layout {
	/** The vertical-align property used for the inner children.
	 * @since 6.0.0
	 */
	private String _valign = ""; // middle, bottom

	/** Returns the valign.
	 * <p>Default: empty.
	 */
	public String getValign() {
		return _valign;
	}
	/** Sets the valign.
	 */
	public void setValign(String valign) {
		if (valign == null)
			valign = "";
		if (!Objects.equals(_valign, valign)) {
			_valign = valign;
			smartUpdate("valign", valign);
		}
	}
	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "valign", _valign);
	}
	public String getZclass() {
		return _zclass == null ? "z-hlayout" : _zclass;
	}
}
