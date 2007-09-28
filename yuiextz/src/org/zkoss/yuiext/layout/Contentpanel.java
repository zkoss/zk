/* Contentpanel.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 6, 2007 1:59:45 PM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.yuiext.layout;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * A basic ContentPanel element. This class refer to Ext.ContentPanel by Ext JS
 * at client side.
 * <p>
 * Events:<br/> onClose.
 * 
 * @author jumperchen
 * 
 */
public class Contentpanel extends LayoutBasedComponent {

	private boolean _visible = true;

	private boolean _fitToFrame = false;

	private boolean _fitContainer = false;

	private boolean _closable = false;

	private boolean _background = false;

	private boolean _autoScroll = false;

	private Component _resizeEl;

	private String _title = "";

	/**
	 * Returns whether scroll overflow in this panel.
	 * <p>
	 * Default: false.
	 */
	public boolean isAutoScroll() {
		return _autoScroll;
	}

	/**
	 * Sets whether scroll overflow in this panel (use with fitToFrame).
	 */
	public void setAutoScroll(boolean autoScroll) {
		if (_autoScroll != autoScroll) {
			_autoScroll = autoScroll;
			smartUpdate("z.autoScroll", _autoScroll);
		}
	}

	/**
	 * Returns ture if the panel should not be activated when it is added.
	 * <p>
	 * Default: false.
	 */
	public boolean isBackground() {
		return _background;
	}

	/**
	 * Sets ture if the panel should not be activated when it is added.
	 * <p>
	 * <strong>Note:</strong>The method only is effected in initialization.
	 */
	public void setBackground(boolean background) {
		if (_background != background) {
			_background = background;
		}
	}

	/**
	 * Returns true if the panel can be closed/removed.
	 * <p>
	 * Default: false.
	 */
	public boolean isClosable() {
		return _closable;
	}

	/**
	 * Sets whether to show a close button on the title bar. If closable, a
	 * button is displayed and the onClose event is sent if an user clicks the
	 * button.
	 * 
	 * <p>
	 * Default: false.
	 * 
	 * <p>
	 * You can intercept the default behavior by either overriding
	 * {@link #onClose}, or listening the onClose event.
	 * 
	 * <p>
	 * Note: the close button won't be displayed if no title or caption at all.
	 */
	public void setClosable(boolean closable) {
		if (_closable != closable) {
			_closable = closable;
			final Component region = getParent();
			if (region != null)
				region.invalidate();
		}
	}

	/**
	 * Returns true when using fitToFrame and resizeEl, you can also fit the
	 * parent container.
	 * <p>
	 * Default: false.
	 */
	public boolean isFitContainer() {
		return _fitContainer;
	}

	/**
	 * Sets true when using fitToFrame and resizeEl, you can also fit the parent
	 * container.
	 */
	public void setFitContainer(boolean fitContainer) {
		if (_fitContainer != fitContainer) {
			_fitContainer = fitContainer;
			smartUpdate("z.fitContainer", _fitContainer);
		}
	}

	/**
	 * Returns true for this panel to adjust its size to fit when the region
	 * resizes.
	 * <p>
	 * Default: false.
	 */
	public boolean isFitToFrame() {
		return _fitToFrame;
	}

	/**
	 * Sets true for this panel to adjust its size to fit when the region
	 * resizes.
	 */
	public void setFitToFrame(boolean fitToFrame) {
		if (_fitToFrame != fitToFrame) {
			_fitToFrame = fitToFrame;
			smartUpdate("z.fitToFrame", _fitToFrame);
		}
	}

	/**
	 * Returns an element to resize if fitToFrame is true (instead of this
	 * panel's element).
	 */
	public Component getResizeEl() {
		return _resizeEl;
	}

	/**
	 * Sets an element to resize if fitToFrame is true (instead of this panel's
	 * element).
	 */
	public void setResizeEl(Component resizeEl) {
		if (resizeEl != null && _resizeEl != resizeEl) {
			_resizeEl = resizeEl;
			smartUpdate("z.resizeEl", _resizeEl.getUuid());
		}
	}

	/**
	 * Returns the title for this panel.
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * Sets the title for this panel.
	 */
	public void setTitle(String title) {
		if (title == null)
			title = "";
		if (!_title.equals(title)) {
			_title = title;
			smartUpdate("z.title", _title);
		}
	}

	/**
	 * Returns true if this panel is currently visible.
	 */
	public boolean isVisible() {
		return _visible;
	}

	/**
	 * Sets whether the tab to hide/unhide for the specified panel.
	 */
	public boolean setVisible(boolean visible) {
		final boolean old = _visible;
		if (old != visible) {
			_visible = visible;
			final Component cmp = getParent();
			if (cmp != null && cmp instanceof LayoutRegion) {
				cmp.smartUpdate(_visible ? "z.unhidePanel" : "z.hidePanel",
						getUuid());
			}
		}
		return old;
	}

	/**
	 * Process the onClose event sent when the close button is pressed.
	 * <p>
	 * Default: detach itself.
	 */
	public void onClose() {
		detach();
	}

	// -- Component --//
	public void invalidate() {
		getParent().invalidate();
	}

	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof LayoutRegion))
			throw new UiException("Unsupported parent for Contentpanel: "
					+ parent);
		super.setParent(parent);
	}

	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		final StringBuffer nm = new StringBuffer(32);
		HTMLs.appendAttribute(sb, "z.lid", getParent().getParent().getUuid());
		if (isAutoScroll()) {
			HTMLs.appendAttribute(sb, "z.autoScroll", _autoScroll);
			nm.append("autoScroll,");
		}
		if (isBackground()) {
			HTMLs.appendAttribute(sb, "z.background", _background);
			nm.append("background,");
		}
		if (isClosable()) {
			HTMLs.appendAttribute(sb, "z.closable", _closable);
			nm.append("closable,");
		}
		if (isFitContainer()) {
			HTMLs.appendAttribute(sb, "z.fitContainer", _fitContainer);
			nm.append("fitContainer,");
		}
		if (isFitToFrame()) {
			HTMLs.appendAttribute(sb, "z.fitToFrame", _fitToFrame);
			nm.append("fitToFrame,");
		}
		if (getResizeEl() != null) {
			HTMLs.appendAttribute(sb, "z.resizeEl", _resizeEl.getUuid());
			nm.append("resizeEl,");
		}
		if (!getTitle().equals("")) {
			HTMLs.appendAttribute(sb, "z.title", _title);
			nm.append("title,");
		}
		if (!isVisible()) {
			final Component cmp = getParent();
			if (cmp != null && cmp instanceof LayoutRegion) {
				cmp.smartUpdate(_visible ? "z.unhidePanel" : "z.hidePanel",
						getUuid());
			}
		}
		if (nm.length() > 0) {
			if (nm.lastIndexOf(",") + 1 == nm.length())
				nm.delete(nm.length() - 1, nm.length());
			appendInitAttr(sb, nm.toString());
		}
		if (_closable)
			sb.append(" z.closable=\"true\"");
		return sb.toString();
	}

	public boolean insertBefore(Component child, Component insertBefore) {
		if (getChildren().size() > 0)
			throw new UiException("Only one child is allowed: " + this);
		return super.insertBefore(child, insertBefore);
	}

}
