package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zkex.zul.Jasperreport;

public class JasperreportDefault implements ComponentRenderer {

	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Jasperreport self = (Jasperreport) comp;
		wh.write("<iframe id=\"").write(self.getUuid())
			.write("\" z.type=\"zul.widget.Ifr\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.writeln(">\n</iframe>");
			//z.type same as iframe, because it, like iframe, might contain
			//xml, pdf... and we got a few browser bugs to workaround
	}
}
