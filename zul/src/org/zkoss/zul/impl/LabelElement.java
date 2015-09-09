/* LabelElement.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 09:45:54     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.util.HashMap;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zk.ui.sys.StringPropertyAccess;

/**
 * A XUL element with a label.
 *
 * @author tomyeh
 */
abstract public class LabelElement extends XulElement {
	/** The label. */
	private String _label = "";

	/** Returns the label (never null).
	 * <p>Default: "".
	 */
	public String getLabel() {
		return _label;
	}
	/** Sets the label.
	 * <p>If label is changed, the whole component is invalidate.
	 * Thus, you want to smart-update, you have to override this method.
	 */
	public void setLabel(String label) {
		if (label == null) label = "";
		if (!Objects.equals(_label, label)) {
			_label = label;
			smartUpdate("label", _label);
		}
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "label", _label);
		renderCrawlable(_label);
	}
	/** Renders the crawlable information.
	 * It is called by {@link #renderProperties},
	 * and designed to be overridden if the deriving class wants to generate
	 * it differently.
	 * <p>Default: <code>org.zkoss.zul.impl.Utils.renderCrawlableText(label)</code>
	 * @since 5.0.5
	 */
	protected void renderCrawlable(String label) throws java.io.IOException {
		org.zkoss.zul.impl.Utils.renderCrawlableText(label);
	}

	//--ComponentCtrl--//
	private static HashMap<String, PropertyAccess> _properties = new HashMap<String, PropertyAccess>(1);
	static {
		_properties.put("label", new StringPropertyAccess() {
			public void setValue(Component cmp, String label) {
				((LabelElement) cmp).setLabel(label);
			}

			public String getValue(Component cmp) {
				return ((LabelElement) cmp).getLabel();
			}
		});
	}

	public PropertyAccess getPropertyAccess(String prop) {
		PropertyAccess pa = _properties.get(prop);
		if (pa != null)
			return pa;
		return super.getPropertyAccess(prop);
	}
}
