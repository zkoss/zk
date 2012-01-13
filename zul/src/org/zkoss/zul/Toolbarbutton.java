/* Toolbarbutton.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 23 11:33:45     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ContentRenderer;

/**
 * A toolbar button.
 *
 * <p>Non-xul extension: Toolbarbutton supports {@link #getHref}. If {@link #getHref}
 * is not null, the onClick handler is ignored and this element is degenerated
 * to HTML's A tag.
 *
 * <p>Default {@link #getZclass}: z-toolbarbutton.(since 5.0.0)
 *
 * @author tomyeh
 */
public class Toolbarbutton extends Button {
	
	static {
		addClientEvent(Toolbarbutton.class, Events.ON_CHECK, CE_IMPORTANT);
	}
	
	private boolean _checked ;
	
	public Toolbarbutton() {
	}
	public Toolbarbutton(String label) {
		super(label);
	}
	public Toolbarbutton(String label, String image) {
		super(label, image);
	}

	protected void renderProperties(ContentRenderer renderer)
			throws IOException {
		super.renderProperties(renderer);
		render(renderer, "checked", isChecked());
	}
	
	@Override
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CHECK)) {
			CheckEvent evt = CheckEvent.getCheckEvent(request);
			_checked = evt.isChecked();
			
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
	/** Returns whether it is checked.
	 * <p>Default: false.
	 */
	public boolean isChecked() {
		return _checked;
	}
	/** Sets whether it is checked.
	 */
	public void setChecked(boolean checked) {
		if (_checked  != checked) {
			_checked = checked;
			smartUpdate("checked", isChecked());
		}
	}
	
	// super
	public String getZclass() {
		return _zclass == null ? "z-toolbarbutton" : _zclass;
	}
}
