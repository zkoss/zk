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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.Utils;
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
	private static final Logger log = LoggerFactory.getLogger(Toolbar.class);
	private static final String PRELOAD_IMAGE = "org.zkoss.zul.image.preload";
	private String _orient = "horizontal";
	private String _align = "start";
	private boolean _overflowPopup;
	private String _overflowPopupIconSclass = "z-icon-ellipsis-h";
	private String _responsive;

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
		String effResp = getEffectiveResponsive();
		if (effResp != null)
			render(renderer, "responsive", effResp);
	}

	/**
	 * Returns the overflow sclass name of overflow popup icon of this toolbar.
	 * Default: z-icon-ellipsis-h.
	 * @return the overflow popup icon sclass name of this toolbar.
	 * @since 9.6.0
	 */
	public String getOverflowPopupIconSclass() {
		return _overflowPopupIconSclass;
	}

	/**
	 * When {@link #isOverflowPopup} is true, toolbar has a button that shows a popup
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

	/**
	 * Returns the responsive policy.
	 * <p>Default: null (unset — inherits from zk.xml
	 * {@code org.zkoss.zul.toolbar.responsive}, then framework default of no responsive).
	 *
	 * @return the responsive policy, or null if not set.
	 * @since 11.1.0
	 */
	public String getResponsive() {
		return _responsive;
	}

	/**
	 * Sets the responsive policy.
	 * <p>Supported values: {@code "iconOnly"}, {@code "none"}, or null (unset).
	 * <ul>
	 *   <li>{@code "iconOnly"} — while the toolbar is narrower than the framework's
	 *       {@code md} breakpoint (768px), the toolbarbuttons and buttons in it that
	 *       have an icon or image show only it; their labels are visually hidden
	 *       but still name the buttons for assistive technology.</li>
	 *   <li>{@code "none"} — explicitly disables responsive, overriding any
	 *       global setting.</li>
	 *   <li>{@code null} — inherits from the global library property.</li>
	 * </ul>
	 * <p>The width compared with {@code md} is the toolbar's own. A toolbar sized by
	 * its content, such as one in an {@code <hlayout>} or {@code <hbox>}, stays
	 * icon-only once its labels are hidden; give it a {@code width} or {@code hflex}.
	 *
	 * @param responsive the responsive policy
	 * @throws WrongValueException if {@code responsive} is neither
	 *     {@code "iconOnly"}, {@code "none"}, nor {@code null}.
	 * @since 11.1.0
	 */
	public void setResponsive(String responsive) throws WrongValueException {
		if (responsive != null && responsive.isEmpty())
			responsive = null;
		if (!isValidResponsive(responsive))
			throw new WrongValueException(
					"responsive cannot be " + responsive
					+ "; allowed: iconOnly, none");
		if (!Objects.equals(_responsive, responsive)) {
			_responsive = responsive;
			// Send the effective value: clearing the own value falls back to the
			// library property, not to "no responsive".
			smartUpdate("responsive", getEffectiveResponsive());
		}
	}

	private static boolean isValidResponsive(String v) {
		return v == null || "iconOnly".equals(v) || "none".equals(v);
	}

	/**
	 * Returns the effective responsive policy: this component's own value, then
	 * {@code Utils.getStringAttribute} over the component-ancestor chain, page,
	 * desktop and finally the {@code org.zkoss.zul.toolbar.responsive} library
	 * property. An invalid fallback value is logged and clamped to null.
	 */
	/*package*/ String getEffectiveResponsive() {
		if (_responsive != null)
			return _responsive;
		String v = Utils.getStringAttribute(this, "org.zkoss.zul.toolbar.responsive", null, true);
		if (!isValidResponsive(v)) {
			log.warn("Ignoring invalid org.zkoss.zul.toolbar.responsive value: {}; allowed: iconOnly, none", v);
			return null;
		}
		return v;
	}
}
