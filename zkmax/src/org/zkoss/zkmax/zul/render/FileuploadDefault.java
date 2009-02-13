/* FileuploadDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 4:01:36 PM , Created by jumperchen
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
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Fileupload;

/*
 * {@link Fileupload}'s default mold.
 * 
 * @author jumperchen
 * 
 * @since 3.0.0
 */
public class FileuploadDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Fileupload self = (Fileupload) comp;
		final String uuid = self.getUuid();
		final Execution exec = Executions.getCurrent();
		wh.write("<div id=\"").write(uuid).write("\"");
		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
		String hgh = self.getNumber() > 3 ? (self.getNumber() * 16 + 30) + "pt" : "";
		wh.write("<iframe style=\"width:100%;height:").write(hgh)
		  .write("\" frameborder=\"0\" src=\"")
		  .write(exec.encodeURL("~./zul/html/fileupload.html.dsp"))
		  .write("?dtid=").write(self.getDesktop().getId()).write("&amp;uuid=")
		  .write(uuid).write("&amp;max=").write(Integer.toString(self.getNumber()))
		  .write("&amp;native=").write(self.isNative()).writeln("\">")
		  .write("</iframe></div>");
	}

}
