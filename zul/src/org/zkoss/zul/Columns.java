/* Columns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:00:29     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.impl.HeadersElement;

/**
 * Defines the columns of a grid.
 * Each child of a columns element should be a {@link Column} element.
 * <p>Default {@link #getZclass}: z-columns.(since 3.5.0)
 *
 * @author tomyeh
 */
public class Columns extends HeadersElement implements org.zkoss.zul.api.Columns {
	private String _mpop = "none";
	private Object _value;
	private boolean _columnshide = true;
	private boolean _columnsgroup = true;
	
	/** Returns the grid that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Grid getGrid() {
		return (Grid)getParent();
	}
	/** Returns the grid that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Grid getGridApi() {
		return getGrid();
	}

	/**
	 * Sets whether to enable hiding of columns with the header context menu.
	 * <p>Note that it is only applied when {@link #getMenupopup()} is auto. 
	 * @since 3.5.0
	 */
	public void setColumnshide(boolean columnshide) {
		if (_columnshide != columnshide) {
			_columnshide = columnshide;
			smartUpdate("columnshide", _columnshide);
		}
	}
	/**
	 * Returns whether to enable hiding of columns with the header context menu.
	 * <p>Default: true.
	 * @since 3.5.0
	 */
	public boolean isColumnshide() {
		return _columnshide;
	}
	/**
	 * Sets whether to enable grouping of columns with the header context menu.
	 * <p>Note that it is only applied when {@link #getMenupopup()} is auto. 
	 * @since 3.5.0
	 */
	public void setColumnsgroup(boolean columnsgroup) {
		if (_columnsgroup != columnsgroup) {
			_columnsgroup = columnsgroup;
			smartUpdate("columnsgroup", _columnsgroup);
		}
	}
	/**
	 * Returns whether to enable grouping of columns with the header context menu.
	 * <p>Default: true.
	 * @since 3.5.0
	 */
	public boolean isColumnsgroup() {
		return _columnsgroup;
	}

	/** Returns the ID of the Menupopup ({@link Menupopup}) that should appear
	 * when the user clicks on the element.
	 *
	 * <p>Default: none (a default menupoppup).
	 * @since 3.5.0
	 */
	public String getMenupopup() {
		return _mpop;
	}
	/** Sets the ID of the menupopup ({@link Menupopup}) that should appear
	 * when the user clicks on the element of each column.
	 *
	 * <p>An onOpen event is sent to the popup menu if it is going to
	 * appear. Therefore, developers can manipulate it dynamically
	 * (perhaps based on OpenEvent.getReference) by listening to the onOpen
	 * event.
	 *
	 * <p>Note: To simplify the use, it ignores the ID space when locating
	 * the component at the client. In other words, it searches for the
	 * first component with the specified ID, no matter it is in 
	 * the same ID space or not.
	 *
	 * <p>If there are two components with the same ID (of course, in
	 * different ID spaces), you can specify the UUID with the following
	 * format:<br/>
	 * <code>uuid(comp_uuid)</code>
	 * 
	 * @param mpop an ID of the menupopup component, "none", or "auto".
	 * 	"none" is assumed by default, "auto" means the menupopup component is 
	 *  created automatically.
	 * @since 3.5.0
	 * @see #setMenupopup(String)
	 */
	public void setMenupopup(String mpop) {
		if (!Objects.equals(_mpop, mpop)) {
			_mpop = mpop;
			smartUpdate("menupopup", mpop);
		}
	}

	/** @deprecated As of release 5.0.0, replaced with {@link #setPopup(Popup)}.
	 */
	public void setPopup(Menupopup mpop) {
		super.setPopup(mpop);
	}
	/** @deprecated As of release 5.0.0, replaced with {@link #setPopup(Popup)}.
	 */
	public void setPopupApi(org.zkoss.zul.api.Menupopup mpopApi) {
		super.setPopup((Popup)mpopApi);		
	}
	
	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		
		if (!_columnsgroup) 
			renderer.render("columnsgroup", false);
		if (!_columnshide) 
			renderer.render("columnshide", false);
		if (!"none".equals(_mpop))
			renderer.render("menupopup", _mpop);
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * @since 3.6.0
	 */
	public Object getValue() {
		return _value;
	}
	/** Sets the value.
	 * @param value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * @since 3.6.0
	 */
	public void setValue(Object value) {
		_value = value;
	}

	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-columns" : _zclass;
	}
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Grid))
			throw new UiException("Unsupported parent for columns: "+parent);
		super.beforeParentChanged(parent);
	}
	
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Column))
			throw new UiException("Unsupported child for columns: "+child);
		super.beforeChildAdded(child, refChild);
	}
}
