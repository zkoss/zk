package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zul.Spinner;

/**
 * {@link Spinner}'s default mold.
 * @author gracelin
 * 
 * @since 3.5.0
 */
public class SpinnerDefault implements ComponentRenderer {	
	public void render(Component comp, Writer out) throws IOException {
		final SmartWriter wh = new SmartWriter(out);
		final Execution exec = Executions.getCurrent();		
		final Spinner self = (Spinner) comp;
		
		wh.write("<span id=\"").write(self.getUuid()).write('"')
			.write(self.getOuterAttrs()).write(" z.type=\"zul.spinner.Spinner\" z.combo=\"true\">")
			.write("<input id=\"").write(self.getUuid()).write("!real\" autocomplete=\"off\"")
			.write(" class=\"").write(self.getSclass()).write("inp\"")
			.write(self.getInnerAttrs()).write("/>")
			.write("<span id=\"").write(self.getUuid()).write("!btn\" class=\"z-rbtn\"");

		if (!self.isButtonVisible())
			wh.write(" style=\"display:none\"");

		wh.write("><img class=\"").write(self.getSclass()).write("\"");
		if (self.getImage() != null) 
			wh.write(" style=\"background-image:url(").write(exec.encodeURL(self.getImage()))
				.write(")\"");
		wh.write(" src=\"").write(exec.encodeURL("~./img/spacer.gif")).write("\"").write("\"/></span></span>");
		
	}
}
