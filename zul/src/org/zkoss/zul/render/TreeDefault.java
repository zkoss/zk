/* TreeDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 7, 2007 8:17:26 AM , Created by robbiecheng
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
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.WriterHelper;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treecols;
import org.zkoss.zul.Treefoot;

/**
 * {@link Tree}'s default mold.
 * 
 * @author robbiecheng
 * 
 * @since 3.0.0
 */
public class TreeDefault implements ComponentRenderer {

	/**
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.tree.Tree"${self.outerAttrs}${self.innerAttrs}>
<c:if test="${!empty self.treecols}">
	<div id="${self.uuid}!head" class="tree-head">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
${z:redraw(self.treecols, null)}
	</table>
	</div>
</c:if>
	<div id="${self.uuid}!body" class="tree-body">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
${z:redraw(self.treechildren, null)}
	</table>
	</div>
<c:if test="${!empty self.treefoot}">
	<div id="${self.uuid}!foot" class="tree-foot">
	<table width="100%" border="0" cellpadding="0" cellspacing="0" style="table-layout:fixed">
${z:redraw(self.treefoot, null)}
	</table>
	</div>
</c:if>
</div>
	 */
	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.render.ComponentRenderer#render(org.zkoss.zk.ui.Component, java.io.Writer)
	 */
	public void render(Component comp, Writer out) throws IOException {
		final WriterHelper wh = new WriterHelper(out);
		final Tree self = (Tree) comp;
		final Treecols tcs = (Treecols) self.getTreecols();
		final Treechildren tc = (Treechildren) self.getTreechildren();
		final Treefoot tf = (Treefoot) self.getTreefoot();
		
		wh.write("<div id=\"" + self.getUuid() + "\" z.type=\"zul.tree.Tree\"" 
				+ self.getOuterAttrs() + self.getInnerAttrs() + ">");
		if(!(self.getTreecols() == null)){
			wh.write("<div id=\"" + self.getUuid() + "!head\" class=\"tree-head\">");
			wh.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">");
			if(tcs != null)
				tcs.redraw(out);		
			wh.write("</table>");
			wh.write("</div>");
		}
		wh.write("<div id=\"" + self.getUuid() + "!body\" class=\"tree-body\">");
		wh.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" );
		if(tc != null)
			tc.redraw(out);		
		wh.write("</table>");
		wh.write("</div>");		
		if(!(self.getTreefoot() == null)){
			wh.write("<div id=\"" + self.getUuid() + "!foot\" class=\"tree-foot\">");
			wh.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"table-layout:fixed\">");
			if(tf != null)
				tf.redraw(out);		
			wh.write("</table>");
			wh.write("</div>");
		}
		wh.write("</div>");
	}

}
