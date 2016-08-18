/* Idspace.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov 03 12:15:49     2011, Created by benbai

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;

/**
 * Just like DIV tag but implements IdSpace.
 * @since 6.0.0
 * @author benbai
 */
public class Idspace extends Div implements org.zkoss.zk.ui.IdSpace {
	private static final Logger log = LoggerFactory.getLogger(Idspace.class);

	public Idspace() {
		setAttribute("z$is", Boolean.TRUE); //optional but optimized to mean no need to generate z$is since client handles it
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		if (this.getMold() == "nodom") {
			for (Component child = getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child instanceof HtmlBasedComponent && (((HtmlBasedComponent) child).getHflex() != null || ((HtmlBasedComponent) child).getVflex() != null))
					log.warn("You should not use hflex/vflex inside Idspace component with \"nodom\" mold.");
			}
		}
	}
}
