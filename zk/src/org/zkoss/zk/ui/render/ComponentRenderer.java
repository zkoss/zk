/* ComponentRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Sep  5 09:17:43     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.render;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.Component;

/**
 * A component renderer is used to render a component.
 * In addition to DSP, JSP and other Servlet, a component can be rendered
 * by use of a {@link ComponentRenderer} instance.
 *
 * <p>When {@link org.zkoss.zk.ui.AbstractComponent#redraw} is called, it
 * retrieves the mold URI by calling {@link org.zkoss.zk.ui.AbstractComponent#getMoldURI}.
 * Then, it returns either an URI or a {@link ComponentRenderer} instance.
 * If URI, the component is rendered by use of {@link org.zkoss.zk.ui.Execution#include}.
 * If {@link ComponentRenderer}, {@link #render} is called directly.
 *
 * <p>Note: an instance of ComponentRenderer is shared among all
 * components of associated types.
 *
 * <p>The use of {@link ComponentRenderer} is mainly to speed up the
 * performance.
 * 
 * @author tomyeh
 * @see Component#redraw
 * @see org.zkoss.zk.ui.AbstractComponent#redraw
 */
public interface ComponentRenderer {
	/** Redraws a component.
	 *
	 * @param comp the component (never null).
	 * @param out the writer to generate the output (never null).
	 */
	public void render(Component comp, Writer out) throws IOException;
}
