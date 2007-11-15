/* LayoutRegion.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 6, 2007 2:43:34 PM , Created by jumperchen
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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.client.Openable;

/**
 * This class represents a region in a layout manager. This class refer to
 * Ext.LayoutRegion by Ext JS at client side.
 * <p>
 * Events:<br/> onOpen.
 * 
 * @author jumperchen
 */
public abstract class LayoutRegion extends BasicLayoutRegion {

	private boolean _collapsible = false;

	private boolean _collapsed = false;

	private boolean _floatable = true;

	private boolean _alwaysShowTabs = false;

	private boolean _autoScroll = false;

	private boolean _titlebar = false;

	private boolean _animate = false;

	private boolean _autoHide = true;

	private boolean _closeOnTab = false;

	private boolean _hideTabs = false;

	private boolean _resizeTabs = false;

	private boolean _showPin = false;

	private boolean _hideWhenEmpty = false;

	private boolean _disableTabTips = false;

	private int _minTabWidth = 40;

	private int _maxTabWidth = 250;

	private int _preferredTabWidth = 150;

	private String _title = "";

	private String _tabPosition = "bottom";

	private String _collapsedTitle = "";

	private boolean _visible = true;

	// control variable
	private boolean _smartVerify;

	private transient EventListener _smartVerifyListener;

	/**
	 * Returns the margins for the element when collapsed (defaults to:
	 * north/south {top: 2, left: 0, right: 0, bottom: 2} or east/west {top: 0,
	 * left: 2, right: 2, bottom: 0})
	 */
	abstract public int[] getCmargins();

	/**
	 * Sets the margins for the element when collapsed.
	 */
	abstract public void setCmargins(int top, int left, int right, int bottom);

	/**
	 * Returns whether always display tabs even when only 1 panel.
	 * <p>
	 * Default: false.
	 */
	public boolean isAlwaysShowTabs() {
		return _alwaysShowTabs;
	}

	/**
	 * Sets whether always display tabs even when only 1 panel.
	 */
	public void setAlwaysShowTabs(boolean alwaysShowTabs) {
		if (_alwaysShowTabs != alwaysShowTabs) {
			_alwaysShowTabs = alwaysShowTabs;
			if (getChildren().size() == 1)
				invalidate();
		}
	}

	/**
	 * Returns whether animate expand/collapse.
	 * <p>
	 * Default: false.
	 */
	public boolean isAnimate() {
		return _animate;
	}

	/**
	 * Sets whether animate expand/collapse.
	 */
	public void setAnimate(boolean animate) {
		if (_animate != animate) {
			_animate = animate;
			smartUpdate("z.animate", String.valueOf(_animate));
		}
	}

	/**
	 * Returns whether disable autoHide when the mouse leaves the "floated"
	 * region.
	 * <p>
	 * Default: true.
	 */
	public boolean isAutoHide() {
		return _autoHide;
	}

	/**
	 * Sets whether disable autoHide when the mouse leaves the "floated" region.
	 */
	public void setAutoHide(boolean autoHide) {
		if (_autoHide != autoHide) {
			_autoHide = autoHide;
			smartUpdate("z.autoHide", String.valueOf(_autoHide));
		}
	}

	/**
	 * Returns whether enable overflow scrolling.
	 * <p>
	 * Default: false.
	 */
	public boolean isAutoScroll() {
		return _autoScroll;
	}

	/**
	 * Sets whether enable overflow scrolling.
	 */
	public void setAutoScroll(boolean autoScroll) {
		if (_autoScroll != autoScroll) {
			_autoScroll = autoScroll;
			smartUpdate("z.autoScroll", String.valueOf(_autoScroll));
		}
	}

	/**
	 * Returns whether place the close icon on the tabs instead of the region
	 * titlebar.
	 * <p>
	 * Default: false.
	 */
	public boolean isCloseOnTab() {
		return _closeOnTab;
	}

	/**
	 * Sets whether place the close icon on the tabs instead of the region
	 * titlebar.
	 */
	public void setCloseOnTab(boolean closeOnTab) {
		if (_closeOnTab != closeOnTab) {
			_closeOnTab = closeOnTab;
			invalidate();
		}
	}

	/**
	 * Returns whether set the initial display to collapsed.
	 * <p>
	 * Default: false.
	 */
	public boolean isCollapsed() {
		return _collapsed;
	}

	/**
	 * Sets whether set the initial display to collapse.
	 * <p>
	 * <strong>Note:</strong> If the collapsed is true, the
	 * {@link #isChildable()} must be true.
	 * </p>
	 */
	public void setCollapsed(boolean collapsed) {
		if (_collapsed != collapsed) {
			_collapsed = collapsed;
			if (isCollapsible()) {
				smartUpdate("z.collapsed", _collapsed);
			} else {
				invalidate();
				smartVerify();
			}
		}
	}

	/**
	 * Returns whether optional string message to display in the collapsed block
	 * of a north or south region.
	 * <p>
	 * Default: a empty string as "".
	 */
	public String getCollapsedTitle() {
		return _collapsedTitle;
	}

	/**
	 * Sets whether optional string message to display in the collapsed block of
	 * a north or south region.
	 */
	public void setCollapsedTitle(String collapsedTitle) {
		if (collapsedTitle == null)
			collapsedTitle = "";
		if (!_collapsedTitle.equals(collapsedTitle)) {
			_collapsedTitle = collapsedTitle;
			smartUpdate("z.collapsedTitle", _collapsedTitle);
		}
	}

	/**
	 * Returns whether disable collapsing.
	 * <p>
	 * Default: false.
	 */
	public boolean isCollapsible() {
		return _collapsible;
	}

	/**
	 * Sets whether disable collapsing.
	 */
	public void setCollapsible(boolean collapsible) {
		if (_collapsible != collapsible) {
			_collapsible = collapsible;
			invalidate();
		}
	}

	/**
	 * Returns whether disable tab tooltips.
	 * <p>
	 * Default: false.
	 */
	public boolean isDisableTabTips() {
		return _disableTabTips;
	}

	/**
	 * Sets whether disable tab toolitips.
	 */
	public void setDisableTabTips(boolean disableTabTips) {
		if (_disableTabTips != disableTabTips) {
			_disableTabTips = disableTabTips;
			invalidate();
		}
	}

	/**
	 * Returns whether disable floating.
	 * <p>
	 * Default: true.
	 */
	public boolean isFloatable() {
		return _floatable;
	}

	/**
	 * Sets whether disable floating.
	 */
	public void setFloatable(boolean floatable) {
		if (_floatable != floatable) {
			_floatable = floatable;
			invalidate();
		}
	}

	/**
	 * Returns whether hide the tab strip.
	 * <p>
	 * Default: false.
	 */
	public boolean isHideTabs() {
		return _hideTabs;
	}

	/**
	 * Sets whether hide the tab strip.
	 */
	public void setHideTabs(boolean hideTabs) {
		if (_hideTabs != hideTabs) {
			_hideTabs = hideTabs;
			smartUpdate("z.hideTabs", _hideTabs);
		}
	}

	/**
	 * Returns whether hide the region when it has no panels.
	 * <p>
	 * Default: false.
	 */
	public boolean isHideWhenEmpty() {
		return _hideWhenEmpty;
	}

	/**
	 * Sets whether hide the region when it has no panels.
	 */
	public void setHideWhenEmpty(boolean hideWhenEmpty) {
		if (_hideWhenEmpty = hideWhenEmpty) {
			_hideWhenEmpty = hideWhenEmpty;
			smartUpdate("z.hideWhenEmpty", _hideWhenEmpty);
		}
	}

	/**
	 * Returns the margins for the element.
	 * <p>
	 * If {@link #isCollapsed} is true, it will return {@link #getCmargins}
	 * otherwise it will return {@link BasicLayoutRegion#getMargins}.
	 * 
	 * @see BasicLayoutRegion
	 * @see #getCmargins()
	 */
	public int[] getMargins() {
		return _collapsed ? getCmargins() : super.getMargins();
	}

	/**
	 * Returns the minimum tab width.
	 * <p>
	 * Default: 40.
	 */
	public int getMinTabWidth() {
		return _minTabWidth;
	}

	/**
	 * Sets the minimum tab width.
	 */
	public void setMinTabWidth(int minTabWidth) {
		if (_minTabWidth != minTabWidth) {
			_minTabWidth = minTabWidth;
			smartUpdate("z.minTabWidth", _minTabWidth);
		}
	}

	/**
	 * Returns the maximum tab width.
	 * <p>
	 * Default: 250.
	 */
	public int getMaxTabWidth() {
		return _maxTabWidth;
	}

	/**
	 * Sets the maximum tab width.
	 */
	public void setMaxTabWidth(int maxTabWidth) {
		if (_maxTabWidth != maxTabWidth) {
			_maxTabWidth = maxTabWidth;
			smartUpdate("z.maxTabWidth", _maxTabWidth);
		}
	}

	/**
	 * Returns the preferred tab width.
	 * <p>
	 * Default: 150.
	 */
	public int getPreferredTabWidth() {
		return _preferredTabWidth;
	}

	/**
	 * Sets the preferred tab width.
	 */
	public void setPreferredTabWidth(int preferredTabWidth) {
		if (_preferredTabWidth != preferredTabWidth) {
			_preferredTabWidth = preferredTabWidth;
			smartUpdate("z.preferredTabWidth", _preferredTabWidth);
		}
	}

	/**
	 * Returns the whether enable automatic tab resizing.
	 * <p>
	 * Default: false.
	 */
	public boolean isResizeTabs() {
		return _resizeTabs;
	}

	/**
	 * Sets the whether enable automatic tab resizing. This will resize the tabs
	 * so they are all the same size and fit within the space available, similar
	 * to FireFox 1.5 tabs.
	 */
	public void setResizeTabs(boolean resizeTabs) {
		if (_resizeTabs != resizeTabs) {
			_resizeTabs = resizeTabs;
			smartUpdate("z.resizeTabs", _resizeTabs);
		}
	}

	/**
	 * Returns whether show a pin button.
	 * <p>
	 * Default: false.
	 */
	public boolean isShowPin() {
		return _showPin;
	}

	/**
	 * Sets whether show a pin button.
	 */
	public void setShowPin(boolean showPin) {
		if (_showPin != showPin) {
			_showPin = showPin;
			invalidate();
		}
	}

	/**
	 * Returns "top" or "bottom".
	 * <p>
	 * Default: "bottom".
	 */
	public String getTabPosition() {
		return _tabPosition;
	}

	/**
	 * Sets "top" or "bottom".
	 * 
	 * @see BasicLayoutRegion#TAB_POSITION_BOTTOM
	 * @see BasicLayoutRegion#TAB_POSITION_TOP
	 */
	public void setTabPosition(String tabPosition) {
		if (tabPosition == null
				|| (!tabPosition.equals(TAB_POSITION_TOP) && !tabPosition
						.equals(TAB_POSITION_BOTTOM)))
			throw new UiException("Unsupported tabPosition: " + tabPosition);
		if (!_tabPosition.equals(tabPosition)) {
			_tabPosition = tabPosition;
			invalidate();
		}
	}

	/**
	 * Returns the title for the region (overrides panel titles).
	 * <p>
	 * Default: "".
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * Sets the title for the region (overrides panel titles).
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
	 * Returns whether display a title bar.
	 * <p>
	 * Default: false.
	 */
	public boolean isTitlebar() {
		return _titlebar;
	}

	/**
	 * Sets whether display a title bar.
	 */
	public void setTitlebar(boolean titlebar) {
		if (_titlebar != titlebar) {
			_titlebar = titlebar;
			invalidate();
		}
	}

	/**
	 * Sets the tab to hide for the specified panel.
	 */
	public void setHidePanel(Contentpanel panel) {
		if (panel.getParent() != this)
			throw new UiException("Not a child: " + panel);
		if (panel.isVisible())
			panel.setVisible(false);
	}

	/**
	 * Sets the tab to unhide for a previously hidden panel.
	 */
	public void setUnhidePanel(Contentpanel panel) {
		if (panel.getParent() != this)
			throw new UiException("Not a child: " + panel);
		if (!panel.isVisible())
			panel.setVisible(true);
	}

	/**
	 * Returns true if this region is currently visible.
	 */
	public boolean isVisible() {
		return _visible;
	}

	public boolean setVisible(boolean visible) {
		final boolean old = _visible;
		if (old != visible) {
			_visible = visible;
			smartUpdate("z.visible", _visible ? "show" : "hide");
		}
		return old;
	}

	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		appendSkipDefaultAttrs(sb);
		if (!isVisible())
			HTMLs.appendAttribute(sb, "z.visible", "hide");
		appendAsapAttr(sb, Events.ON_OPEN);
		return sb.toString();
	}

	private StringBuffer appendSkipDefaultAttrs(StringBuffer sb) {
		final int[] cmargins = getCmargins();
		final StringBuffer nm = new StringBuffer(64);
		if (getPosition().equals(POSITION_WEST)
				|| getPosition().equals(POSITION_EAST)) {
			if (cmargins[0] != 0 || cmargins[1] != 2 || cmargins[2] != 2
					|| cmargins[3] != 0) {
				HTMLs.appendAttribute(sb, "z.cmargins", cmargins[0] + ","
						+ cmargins[1] + "," + cmargins[2] + "," + cmargins[3]);
				nm.append("cmargins,");
			}
		} else {
			if (cmargins[0] != 2 || cmargins[1] != 0 || cmargins[2] != 0
					|| cmargins[3] != 2) {
				HTMLs.appendAttribute(sb, "z.cmargins", cmargins[0] + ","
						+ cmargins[1] + "," + cmargins[2] + "," + cmargins[3]);
				nm.append("cmargins,");
			}
		}
		if (isAlwaysShowTabs()) {
			HTMLs.appendAttribute(sb, "z.alwaysShowTabs", _alwaysShowTabs);
			nm.append("alwaysShowTabs,");
		}

		if (isAnimate()) {
			HTMLs.appendAttribute(sb, "z.animate", _animate);
			nm.append("animate,");
		}

		if (!isAutoHide()) {
			HTMLs.appendAttribute(sb, "z.autoHide", _autoHide);
			nm.append("autoHide,");
		}

		if (isAutoScroll()) {
			HTMLs.appendAttribute(sb, "z.autoScroll", _autoScroll);
			nm.append("autoScroll,");
		}

		if (isCloseOnTab()) {
			HTMLs.appendAttribute(sb, "z.closeOnTab", _closeOnTab);
			nm.append("closeOnTab,");
		}

		if (isCollapsed()) {
			HTMLs.appendAttribute(sb, "z.collapsed", _collapsed);
			nm.append("collapsed,");
		}

		if (!getCollapsedTitle().equals("")) {
			HTMLs.appendAttribute(sb, "z.collapsedTitle", _collapsedTitle);
			nm.append("collapsedTitle,");
		}

		if (isCollapsible()) {
			HTMLs.appendAttribute(sb, "z.collapsible", _collapsible);
			nm.append("collapsible,");
		}

		if (isDisableTabTips()) {
			HTMLs.appendAttribute(sb, "z.disableTabTips", _disableTabTips);
			nm.append("disableTabTips,");
		}

		if (!isFloatable()) {
			HTMLs.appendAttribute(sb, "z.floatable", _floatable);
			nm.append("floatable,");
		}

		if (isHideTabs()) {
			HTMLs.appendAttribute(sb, "z.hideTabs", _hideTabs);
			nm.append("hideTabs,");
		}

		if (isHideWhenEmpty()) {
			HTMLs.appendAttribute(sb, "z.hideWhenEmpty", _hideWhenEmpty);
			nm.append("hideWhenEmpty,");
		}

		if (getMinTabWidth() != 40) {
			HTMLs.appendAttribute(sb, "z.minTabWidth", _minTabWidth);
			nm.append("minTabWidth,");
		}

		if (getMaxTabWidth() != 250) {
			HTMLs.appendAttribute(sb, "z.maxTabWidth", _maxTabWidth);
			nm.append("maxTabWidth,");
		}

		if (getPreferredTabWidth() != 150) {
			HTMLs
					.appendAttribute(sb, "z.preferredTabWidth",
							_preferredTabWidth);
			nm.append("preferredTabWidth,");
		}

		if (isResizeTabs()) {
			HTMLs.appendAttribute(sb, "z.resizeTabs", _resizeTabs);
			nm.append("resizeTabs,");
		}

		if (isShowPin()) {
			HTMLs.appendAttribute(sb, "z.showPin", _showPin);
			nm.append("showPin,");
		}

		if (!getTabPosition().equals(TAB_POSITION_BOTTOM)) {
			HTMLs.appendAttribute(sb, "z.tabPosition", _tabPosition);
			nm.append("tabPosition,");
		}

		if (!getTitle().equals("")) {
			HTMLs.appendAttribute(sb, "z.title", _title);
			nm.append("title,");
		}

		if (isTitlebar()) {
			HTMLs.appendAttribute(sb, "z.titlebar", _titlebar);
			nm.append("titlebar,");
		}
		if (nm.length() > 0) {
			if (nm.lastIndexOf(",") + 1 == nm.length())
				nm.delete(nm.length() - 1, nm.length());
			appendInitAttr(sb, nm.toString());
		}
		return sb;
	}

	protected void smartVerify() {
		if (_smartVerify)
			return; // already mark smart
		_smartVerify = true;
		if (_smartVerifyListener == null) {
			_smartVerifyListener = new EventListener() {
				public void onEvent(Event event) {
					if (isCollapsed() && !isCollapsible()) {
						throw new UiException(
								"When the collapsed is true, the collapsible must be true!");
					}
					_smartVerify = false;
				}
			};
			addEventListener("onSmartVerify", _smartVerifyListener);
		}
		Events.postEvent("onSmartVerify", this, null);
	}

	// -- Component --//
	public void setParent(Component parent) {
		if (parent != null && !(parent instanceof Borderlayout))
			throw new UiException("Unsupported parent for LayoutRegion: "
					+ parent);
		super.setParent(parent);
	}

	public boolean insertBefore(Component child, Component insertBefore) {
		if (!(child instanceof Contentpanel))
			throw new UiException("Unsupported child for LayoutRegion: "
					+ child);
		Contentpanel panel = (Contentpanel) child;
		if (!panel.isBackground())
			setActivePanel(panel);
		return super.insertBefore(panel, insertBefore);
	}

	// -- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}

	protected class ExtraCtrl extends BasicLayoutRegion.ExtraCtrl implements
			Openable {
		public void setOpenByClient(boolean open) {
			_collapsed = !open;
		}
	}
}
