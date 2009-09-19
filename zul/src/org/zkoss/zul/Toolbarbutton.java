/* Toolbarbutton.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 23 11:33:45     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;

/**
 * A tool button.
 *
 * <p>The default CSS class is "z-toolbar-button".
 *
 * <p>Non-xul extension: Toolbarbutton supports {@link #getHref}. If {@link #getHref}
 * is not null, the onClick handler is ignored and this element is degenerated
 * to HTML's A tag.
 * <p>Default {@link #getZclass}: z-toolbar-button.(since 3.5.0)
 * <p>If {@link #getMold()} is <code>anchor</code> mold,
 * {@link #getZclass}: z-toolbar-anchor.(since 5.0.0)
 *
 * @author tomyeh
 */
public class Toolbarbutton extends Button implements org.zkoss.zul.api.Toolbarbutton {
	private String _mold = null;
	public Toolbarbutton() {
	}
	public Toolbarbutton(String label) {
		super(label);
	}
	public Toolbarbutton(String label, String image) {
		super(label, image);
	}
	
	public void setUpload(String upload) {
		if (upload != null
		&& (upload.length() == 0 || "false".equals(upload)))
			upload = null;
		if (!Objects.equals(upload, _upload)) {
			_upload = upload;
			smartUpdate("upload", _upload);
		}
	}
	
	// super
	public String getZclass() {
		return _zclass == null ? 
					"default".equals(getMold()) ? "z-toolbar-button" : "z-toolbar-anchor"
					: _zclass;
	}
	public void setMold(String mold) {
		super.setMold(mold);
		_mold = mold;
	}
	
	public String getMold() {
		if (_mold != null)
			return super.getMold();
		Component parent = getParent();
		return parent != null && parent instanceof Toolbar ? "default" : "anchor";
	}
}
