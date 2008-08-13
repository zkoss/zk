/* Detail.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 25, 2008 5:15:48 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
 * <p>Default {@link #getMoldSclass}: z-detail.
 * 
 * <p>Default {@link #getWidth}: 18px. It depends on the width of the icon of
 * the detail.
 * 
 * @author jumperchen
 * @since 3.5.0
 */
public class Detail extends XulElement {
	private static final String DEFAULT_IMAGE = "~./zul/img/grid/row-expand.gif";
	private boolean _open;
	private String _img;
	
	public Detail() {
		setMoldSclass("z-detail");
		setWidth("18px");
	}

	/**
	 * Returns the URI of the button image.
	 */
	public String getImage() {
		return _img != null ?  _img: DEFAULT_IMAGE;
	}
	
	/** 
	 * Sets the URI of the button image.
	 * 
	 * @param img the URI of the button image. If null or empty, the default
	 * URI is used.
	 */
	public void setImage(String img) {
		if (img != null && (img.length() == 0 || DEFAULT_IMAGE.equals(img)))
			img = null;
		if (!Objects.equals(_img, img)) {
			_img = img;
			invalidate();
		}
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
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Row))
			throw new UiException("Unsupported parent for detail: "+parent);
		super.setParent(parent);
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
