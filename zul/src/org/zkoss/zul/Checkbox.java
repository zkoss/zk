/* Checkbox.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 16 23:45:45     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.*;

import org.zkoss.zul.impl.LabelImageElement;

/**
 * A checkbox.
 *
 * <p>Event:
 * <ol>
 * <li>org.zkoss.zk.ui.event.CheckEvent is sent when a checkbox
 * is checked or unchecked by user.</li>
 * </ol>
 *
 * @author tomyeh
 */
public class Checkbox extends LabelImageElement implements org.zkoss.zul.api.Checkbox {
	/** The name. */
	private String _name;
	private int _tabindex = -1;
	/** Whether it is checked. */
	/*package*/ boolean _checked;
	private boolean _disabled;
	
	static {
		addClientEvent(Checkbox.class, Events.ON_CHECK, CE_IMPORTANT);
		addClientEvent(Checkbox.class, Events.ON_FOCUS, 0);
		addClientEvent(Checkbox.class, Events.ON_BLUR, 0);
	}
	
	public Checkbox() {
	}
	public Checkbox(String label) {
		super(label);
	}
	public Checkbox(String label, String image) {
		super(label, image);
	}

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 */
	public boolean isDisabled() {
		return _disabled;
	}
	/** Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
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
		if (_checked != checked) {
			_checked = checked;
			smartUpdate("checked", _checked);
		}
	}

	/** Returns the name of this component.
	 * <p>Default: null.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 */
	public String getName() {
		return _name;
	}
	/** Sets the name of this component.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 *
	 * @param name the name of this component.
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0) name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("name", _name);
		}
	}

	/** Returns the tab order of this component.
	 * <p>Default: -1 (means the same as browser's default).
	 */
	public int getTabindex() {
		return _tabindex;
	}
	/** Sets the tab order of this component.
	 */
	public void setTabindex(int tabindex) throws WrongValueException {
		if (_tabindex != tabindex) {
			_tabindex = tabindex;
			if (tabindex < 0) smartUpdate("tabindex", (Object)null);
			else smartUpdate("tabindex", _tabindex);
		}
	}

	//-- super --//
	/** Returns the Style of checkbox label
	 *
	 * <p>Default: "z-checkbox"
	 * <p>Since 3.5.1
	 * 
	 */
	public String getZclass() {
		return _zclass == null ? "z-checkbox" : _zclass;
	}
	
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		if (_tabindex >= 0)
			renderer.render("tabindex", _tabindex);

		render(renderer, "disabled", _disabled);
		render(renderer, "name", _name);
		if (_checked)
			render(renderer, "checked", _checked);

		org.zkoss.zul.impl.Utils.renderCrawlableText(getLabel());
	}
	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link LabelImageElement#service},
	 * it also handles onCheck.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CHECK)) {
			CheckEvent evt = CheckEvent.getCheckEvent(request);
			_checked = evt.isChecked();
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
}
