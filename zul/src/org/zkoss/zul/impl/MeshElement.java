/* MeshElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Feb 11, 2011 5:48:26 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/


package org.zkoss.zul.impl;

import org.zkoss.lang.Objects;

/**
 * The fundamental class for mesh elements such as {@link org.zkoss.zul.Grid}, {@link org.zkoss.zul.Listbox}, and {@link org.zkoss.zul.Tree}.

 * @author henrichen
 * @since 5.0.6
 */
public class MeshElement extends XulElement implements org.zkoss.zul.impl.api.MeshElement {
	private String _span;
	private boolean _sizedByContent;
	
	public String getSpan() {
		return _span;
	}
	public void setSpan(String span) {
		if (!Objects.equals(_span, span)) {
			_span = span;
			smartUpdate("span", span);
		}
	}
	public void setSpan(boolean span) {
		if ((span && !"true".equals(_span)) || (!span && _span != null && !"false".equals(_span))) {
			_span = span ? "true" : "false";
			smartUpdate("span", span);
		}
	}

	public boolean isSpan() {
		return _span != null && !"false".equals(_span);
	}
	
	public void setSizedByContent(boolean byContent) {
		if(_sizedByContent != byContent) {
			_sizedByContent = byContent;
			smartUpdate("sizedByContent", byContent);
		}
	}

	public boolean isSizedByContent() {
		String s = (String) getAttribute("sized-by-content");
		if (s == null) {
			s = (String) getAttribute("fixed-layout");
			return s != null ? !"true".equalsIgnoreCase(s) : _sizedByContent;
		} else
			return "true".equalsIgnoreCase(s);
	}
	
	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		if (isSizedByContent())
			renderer.render("sizedByContent", true);
		if (_span != null)
			renderer.render("span", _span);
	}
}
