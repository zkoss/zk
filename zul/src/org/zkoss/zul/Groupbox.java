/* Groupbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 29 16:55:24     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.render.MultiBranch;
import org.zkoss.zk.ui.ext.client.Openable;
import org.zkoss.zul.impl.XulElement;

/**
 * Groups a set of child elements to have a visual effect.
 *
 * @author tomyeh
 */
public class Groupbox extends XulElement {
	private Caption _caption;
	/** The style used for the content block. */
	private String _cntStyle;
	private Boolean _legend;
	private boolean _open = true, _closable = true;

	/** Returns the caption of this groupbox.
	 */
	public Caption getCaption() {
		return _caption;
	}

	/** Returns whether this groupbox is open.
	 *
	 * <p>Note: the default mold ({@link #getMold}) doesn't support
	 * the open attribute.
	 *
	 * <p>Default: true.
	 */
	public boolean isOpen() {
		return _open;
	}
	/** Opens or closes this groupbox.
	 */
	public void setOpen(boolean open) {
		if (_open != open) {
			_open = open;
			smartUpdate("z.open", _open);
		}
	}

	/** Returns whether user can open or close the group box.
	 * In other words, if false, users are no longer allowed to
	 * change the open status (by clicking on the title).
	 *
	 * <p>Default: true.
	 */
	public boolean isClosable() {
		return _closable;
	}
	/** Sets whether user can open or close the group box.
	 */
	public void setClosable(boolean closable) {
		if (_closable != closable) {
			_closable = closable;
			smartUpdate("z.closable", closable);
		}
	}

	/** Returns the CSS style for the content block of the groupbox.
	 * Used only if {@link #getMold} is not default.
	 */
	public String getContentStyle() {
		return _cntStyle;
	}
	/** Sets the CSS style for the content block of the groupbox.
	 * Used only if {@link #getMold} is not default.
	 *
	 * <p>Default: null.
	 */
	public void setContentStyle(String style) {
		if (!Objects.equals(_cntStyle, style)) {
			_cntStyle = style;
			smartUpdate("z.cntStyle", _cntStyle);
		}
	}
	/** Returns the style class used for the content block of the groupbox.
	 * Used only if {@link #getMold} is not default.
	 *
	 * <p>If {@link #getSclass} is null, "gc-default" is returned,
	 * Otherwise, "gc-<i>sclass</i>",
	 * where <i>sclass</i> is the value returned by {@link #getSclass}.
	 */
	public String getContentSclass() {
		final String sclass = getSclass();
		return sclass == null ? "gc-default": "gc-" + sclass;
	}

	/** Returns whether this groupbox is in the legend mold.
	 * By the legend mold we mean this group box is rendered with
	 * HTML FIELDSET tag.
	 *
	 * <p>Default: the legend mold is assumed if {@link #getMold}
	 * returns "default".
	 *
	 * <p>If it is not the case, you can call {@link #setLegend} to change
	 * it.
	 * @since 3.0.0
	 */
	public boolean isLegend() {
		return _legend != null ?
			_legend.booleanValue(): "default".equals(getMold());
	}
	/** Sets whether this groupbox is in the legend mold.
	 * @see #isLegend
	 * @since 3.0.0
	 */
	public void setLegend(boolean legend) {
		_legend = Boolean.valueOf(legend);
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super.getOuterAttrs());

		appendAsapAttr(sb, Events.ON_OPEN);
		final String clkattrs = getAllOnClickAttrs(false);
		if (clkattrs != null) sb.append(clkattrs);
			//though widget.js handles onclick (if 3d), it is useful
			//to support onClick for groupbox

		if (!_closable)
			sb.append(" z.closable=\"false\"");
		return sb.toString();
	}

	//-- Component --//
	public boolean insertBefore(Component child, Component insertBefore) {
		if (child instanceof Caption) {
			if (_caption != null && _caption != child)
				throw new UiException("Only one caption is allowed: "+this);
			if (!getChildren().isEmpty())
				insertBefore = (Component)getChildren().get(0);
				//always makes caption as the first child
			_caption = (Caption)child;
			invalidate();
		} else if (insertBefore instanceof Caption) {
			throw new UiException("caption must be the first child");
		}
		return super.insertBefore(child, insertBefore);
	}
	public void onChildRemoved(Component child) {
		if (child instanceof Caption) {
			_caption = null;
			invalidate();
		}
		super.onChildRemoved(child);
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements MultiBranch, Openable {
		//-- MultiBranch --//
		public boolean inDifferentBranch(Component child) {
			return child instanceof Caption; //in different branch
		}
		//-- Openable --//
		public void setOpenByClient(boolean open) {
			_open = open;
		}
	}
}
