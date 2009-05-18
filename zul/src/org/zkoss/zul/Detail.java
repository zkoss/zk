/* Detail.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 25, 2008 5:15:48 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Openable;
import org.zkoss.zul.impl.XulElement;

/**
 * The detail component is used to display a detailed section where a master row and 
 * multiple detail rows are on the same row.
 * 
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
			smartUpdate("z.cntStyle", getContentStyle());
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
			smartUpdate("z.cntScls", _cntscls);
		}
	}
	/**
	 * @deprecated As of release 3.5.2
	 */
	public String getImage() {
		return null;
	}
	
	/** 
	 * @deprecated As of release 3.5.2
	 */
	public void setImage(String img) {
	}
	
	/**
	 * Sets whether the detail is open.
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;
			smartUpdate("z.open", _open);
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
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Row))
			throw new UiException("Unsupported parent for detail: "+parent);
		super.beforeParentChanged(parent);
	}
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		appendAsapAttr(sb, Events.ON_OPEN);
		if (_open)
			HTMLs.appendAttribute(sb, "z.open", _open);
		return sb.toString();
	}
	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements Openable {
		//-- Openable --//
		public void setOpenByClient(boolean open) {
			_open = open;
		}
	}
}
