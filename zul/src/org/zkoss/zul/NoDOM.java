/* NoDOM.java

	Purpose:

	Description:

	History:
		Tue Jul 19 15:21:48 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;

/**
 * A no-dom component with comment scope
 * @since 8.0.3
 * @author jameschu
 */
public class NoDOM extends AbstractComponent {
	private static final Logger log = LoggerFactory.getLogger(NoDOM.class);

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		for (Component child = getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child instanceof HtmlBasedComponent && (((HtmlBasedComponent) child).getHflex() != null || ((HtmlBasedComponent) child).getVflex() != null))
				log.warn("Using hflex/vflex inside NoDOM component is not supported");
		}
	}
}