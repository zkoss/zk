/* Detail.java

	Purpose:
		
	Description:
		
	History:
		Jul 25, 2008 5:15:48 PM , Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.impl.XulElement;

/**
 * The detail component is used to display a detailed section where a master row and 
 * multiple detail rows are on the same row.
 * <p>Available in ZK PE and ZK EE.
 * <p>Event:
 * <ol>
 * <li>onOpen is sent when this component is opened or closed by user.</li>
 * </ol>
 * </p>
 * 
 * <p>Default {@link #getZclass}: z-detail.
 * 
 * <p>Default {@link #getWidth}: 18px. It depends on the width of the icon of
 * the detail.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public class Detail extends XulElement implements org.zkoss.zul.api.Detail {
	private boolean _open;
	/** The style used for the content block. */
	private String _cntStyle;
	/** The style class used for the content block. */
	private String _cntscls;
	
	static {
		addClientEvent(Detail.class, Events.ON_OPEN, CE_IMPORTANT);
	}
	
	public Detail() {
		setWidth("18px");
	}

	/** 
	 * Returns the CSS style for the content block of the window.
	 */
	public String getContentStyle() {
		return _cntStyle;
	}
	/** 
	 * Sets the CSS style for the content block of the window.
	 *
	 * <p>Default: null.
	 */
	public void setContentStyle(String style) {
		if (!Objects.equals(_cntStyle, style)) {
			_cntStyle = style;
			smartUpdate("contentStyle", getContentStyle());
		}
	}

	/** 
	 * Returns the style class used for the content block.
	 */
	public String getContentSclass() {
		return _cntscls;
	}
	/**
	 * Sets the style class used for the content block.
	 */
	public void setContentSclass(String scls) {
		if (!Objects.equals(_cntscls, scls)) {
			_cntscls = scls;
			smartUpdate("contentSclass", _cntscls);
		}
	}

	/**
	 * Sets whether the detail is open.
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;
			smartUpdate("open", _open);
		}
	}
	
	/**
	 * Returns whether the detail is open.
	 */
	public boolean isOpen() {
		return _open;
	}
	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-detail" : _zclass;
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "open", this.isOpen());
		render(renderer, "contentSclass", this.getContentSclass());
		render(renderer, "contentStyle", this.getContentStyle());
	}
	
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Row))
			throw new UiException("Unsupported parent for detail: "+parent);
		super.beforeParentChanged(parent);
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service},
	 * it also handles onOpen.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_OPEN)) {
			OpenEvent evt = OpenEvent.getOpenEvent(request);
			_open = evt.isOpen();
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
}
