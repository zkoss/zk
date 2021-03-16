/* Toolbar.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 23 11:33:31     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.XulElement;

/**
 * A toolbar.
 * 
 * <p>Mold:
 * <ol>
 * <li>default</li>
 * <li>panel: since 3.5.0, this mold is used for {@link Panel} component as its
 * foot toolbar.</li>
 * </ol>
 * <p>Default {@link #getZclass}: z-toolbar
 *  
 * @author tomyeh
 */
public class Toolbar extends XulElement {
	private static final String PRELOAD_IMAGE = "org.zkoss.zul.image.preload";
	private String _orient = "horizontal";
	private String _align = "start";
	private boolean _overflowPopup;
	private String _overflowPopupIconSclass = "z-icon-ellipsis-h";

	public Toolbar() {
	}

	/**
	 * @param orient either "horizontal" or "vertical".
	 */
	public Toolbar(String orient) {
		this();
		setOrient(orient);
	}

	/** 
	 * Returns the alignment of any children added to this toolbar. Valid values
	 * are "start", "end" and "center".
	 * <p>Default: "start"
	 * @since 3.5.0
	 */
	public String getAlign() {
		return _align;
	}

	/**
	 * Sets the alignment of any children added to this toolbar. Valid values
	 * are "start", "end" and "center".
	 * <p>Default: "start", if null, "start" is assumed.
	 * 
	 * @since 3.5.0
	 */
	public void setAlign(String align) {
		if (align == null)
			align = "start";
		if (!"start".equals(align) && !"center".equals(align) && !"end".equals(align))
			throw new WrongValueException("align cannot be " + align);
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}

	/*package*/ boolean inPanelMold() {
		return "panel".equals(getMold());
	}

	// super
	public String getZclass() {
		return _zclass == null ? "z-toolbar" : _zclass;
	}

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return _orient;
	}

	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("orient cannot be " + orient);

		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			smartUpdate("orient", _orient);
		}
	}

	/**
	 * Return whether toolbar has a button that shows a popup
	 * which contains those content weren't able to fit in the toolbar.
	 * If overflowPopup is false, toolbar will display multiple rows when content is wider than toolbar.
	 * Default: false.
	 *
	 * @return whether toolbar has a button that shows a popup
	 * @since 8.6.0
	 */
	public boolean isOverflowPopup() {
		return _overflowPopup;
	}

	/**
	 * Set whether toolbar has a button that shows a popup
	 * which contains those content weren't able to fit in the toolbar.
	 * If overflowPopup is false, toolbar will display multiple rows when content is wider than toolbar.
	 *
	 * @param overflowPopup whether toolbar has a button that shows a popup
	 * @since 8.6.0
	 */
	public void setOverflowPopup(boolean overflowPopup) {
		if (_overflowPopup != overflowPopup) {
			_overflowPopup = overflowPopup;
			smartUpdate("overflowPopup", _overflowPopup);
			if (_overflowPopup)
				setAttribute(PRELOAD_IMAGE, "true");
			else
				removeAttribute(PRELOAD_IMAGE);
		}
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		if (!"horizontal".equals(_orient))
			render(renderer, "orient", _orient);
		if (!"start".equals(_align))
			render(renderer, "align", _align);
		render(renderer, "overflowPopup", _overflowPopup);
		if (!"z-icon-ellipsis-h".equals(_overflowPopupIconSclass))
			render(renderer, "overflowPopupIconSclass", getOverflowPopupIconSclass());
	}

	/**
	 * @return the overflow popup icon sclass name of this toolbar.
	 * Default: z-icon-ellipsis-h.
	 * @since 9.6.0
	 */
	public String getOverflowPopupIconSclass() {
		return _overflowPopupIconSclass;
	}

	/**
	 * When {@link #setOverflowPopup} is true, toolbar has a button that shows a popup
	 * users can customize the overflow popup icon.
	 * Default: z-icon-ellipsis-h.
	 * @param overflowPopupIconSclass refer to FontAwesome Cheatsheet.
	 * @since 9.6.0
	 */
	public void setOverflowPopupIconSclass(String overflowPopupIconSclass) {
		if (!Objects.equals(_overflowPopupIconSclass, overflowPopupIconSclass)) {
			_overflowPopupIconSclass = overflowPopupIconSclass;
			smartUpdate("overflowPopupIconSclass", _overflowPopupIconSclass);
		}
	}
}
