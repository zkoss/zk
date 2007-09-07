/* TimerDefault.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Sep 6, 2007 2:21:06 PM , Created by robbiecheng
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentRenderer;
import org.zkoss.zul.Timer;

/**
 * {@link Timer}'s default mold.
 * @author robbiecheng
 * @since 3.0.0
 */
public class TimerDefault implements ComponentRenderer {
	/**
	 * <c:set var="self" value="${requestScope.arg.self}"/> <span
	 * id="${self.uuid}"
	 * z.type="zul.timer.Timer"${self.outerAttrs}${self.innerAttrs}></span>
	 */
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);		
		final Timer self = (Timer) comp;
		
		wh.write("<span id=\"" + self.getUuid() + "\" z.type=\"zul.timer.Timer\"" + self.getOuterAttrs() + self.getInnerAttrs() + "></span>");

	}

}
