/* AudioDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 6, 2007 1:53:25 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Audio;

/**
 * {@link Audio}'s default mold.
 * 
 * @author jumperchen
 * @since 3.0.0
 */
public class AudioDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Audio self = (Audio) comp;
		wh.write("<embed id=\"").write(self.getUuid()).write("\"").write(
				self.getOuterAttrs()).write(self.getInnerAttrs()).write(
				" mastersound z.type=\"zul.audio.Audio\"/>");
	}

}
