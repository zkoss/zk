/* RenderContext.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan  5 11:48:18     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zhtml.impl;

import java.io.StringWriter;

/**
 * The render context used to render the JavaScript part.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class RenderContext {
	/** The writer to output JavaScript codes.
	 */
	public final StringWriter jsout = new StringWriter();
	/** Whether to generate HTML tags directly.
	 *
	 * <p>If true, the HTML tag shall be generated directly to the writer
	 * provided by {@link org.zkoss.zk.ui.sys.ComponentCtrl#redraw},
	 * and generates JavaScript code snippet in {@link #jsout}.
	 *
	 * <p>If false, ZHTML components shall generate properties by use of
	 * {@link org.zkoss.zk.ui.sys.ContentRenderer}.
	 */
	public boolean directContent = true;

	/** Renders the content of {@link #jsout} to the specified writer.
	 * After rendering, {@link #jsout} is reset.
	 */
	public void render(java.io.Writer out) throws java.io.IOException {
		final StringBuffer sb = this.jsout.getBuffer();
		if (sb.length() > 0) {
			sb.setLength(0); //reset
		}
	}
}
