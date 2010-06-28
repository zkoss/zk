/* Tabbox.java

	Purpose:

	Description:

	History:
		Tue Jul 12 10:42:31     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Deferrable;

import org.zkoss.zul.impl.XulElement;

/**
 * A tabbox.
 *
 * <p>
 * Event:
 * <ol>
 * <li>org.zkoss.zk.ui.event.SelectEvent is sent when user changes the tab.</li>
 * </ol>
 *
 * <p>
 * Mold:
 * <dl>
 * <dt>default</dt>
 * <dd>The default tabbox.</dd>
 * <dt>accordion</dt>
 * <dd>The accordion tabbox.</dd>
 * </dl>
 *
 * <p>{@link Toolbar} only works in the horizontal default mold and
 * the {@link #isTabscroll()} to be true. (since 3.6.3)
 *  
 * <p>Default {@link #getZclass}: z-tabbox. (since 3.5.0)
 *
 * @author tomyeh
 */
public class Tabbox extends XulElement implements org.zkoss.zul.api.Tabbox {
	private transient Tabs _tabs;
	private transient Toolbar _toolbar;
	private transient Tabpanels _tabpanels;
	private transient Tab _seltab;
	private String _panelSpacing;
	private String _orient = "horizontal";
	private boolean _tabscroll = true;
	/** The event listener used to listen onSelect for each tab. */
	/* package */transient EventListener _listener;

	public Tabbox() {
		init();
	}

	private void init() {
		_listener = new Listener();
	}

	/**
	 * Returns whether it is in the accordion mold.
	 */
	/* package */boolean inAccordionMold() {
		return getMold().startsWith("accordion");
	}

	/**
	 * Returns the tabs that this tabbox owns.
	 */
	public Tabs getTabs() {
		return _tabs;
	}

	/**
	 * Returns the auxiliary toolbar that this tabbox owns.
	 * 
	 * @since 3.6.3
	 */
	public Toolbar getToolbar() {
		return _toolbar;
	}
	/**
	 * Returns the tabs that this tabbox owns.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tabs getTabsApi() {
		return getTabs();
	};
	/**
	 * Returns the tabpanels that this tabbox owns.
	 */
	public Tabpanels getTabpanels() {
		return _tabpanels;
	}
	/**
	 * Returns the tabpanels that this tabbox owns.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tabpanels getTabpanelsApi() {
		return getTabpanels();
	}
	/**
	 * Returns whether the tab scrolling is enabled.
	 * Default: true.
	 * @since 3.5.0
	 */
	public boolean isTabscroll() {
		return _tabscroll;
	}

	/**
	 * Sets whether to eable the tab scrolling
	 * @since 3.5.0
	 */
	public void setTabscroll(boolean tabscroll) {
		if (_tabscroll != tabscroll) {
			_tabscroll = tabscroll;
			smartUpdate("tabscroll", _tabscroll);
		}
	}

	/**
	 * Returns the spacing between {@link Tabpanel}. This is used by certain
	 * molds, such as accordion.
	 * <p>
	 * Default: null (no spacing).
	 */
	public String getPanelSpacing() {
		return _panelSpacing;
	}

	/**
	 * Sets the spacing between {@link Tabpanel}. This is used by certain molds,
	 * such as accordion.
	 */
	public void setPanelSpacing(String panelSpacing) {
		if (panelSpacing != null && panelSpacing.length() == 0)
			panelSpacing = null;

		if (!Objects.equals(_panelSpacing, panelSpacing)) {
			_panelSpacing = panelSpacing;
			smartUpdate("panelSpacing", _panelSpacing);
		}
	}

	/**
	 * Returns the selected index.
	 */
	public int getSelectedIndex() {
		return _seltab != null ? _seltab.getIndex() : -1;
	}

	/**
	 * Sets the selected index.
	 */
	public void setSelectedIndex(int j) {
		final Tabs tabs = getTabs();
		if (tabs == null)
			throw new IllegalStateException("No tab at all");
		setSelectedTab((Tab) tabs.getChildren().get(j));
	}

	/**
	 * Returns the selected tab panel.
	 */
	public Tabpanel getSelectedPanel() {
		return _seltab != null ? _seltab.getLinkedPanel() : null;
	}
	/**
	 * Returns the selected tab panel.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tabpanel getSelectedPanelApi() {
		return getSelectedPanel();
	}
	/**
	 * Sets the selected tab panel.
	 */
	public void setSelectedPanel(Tabpanel panel) {
		if (panel == null)
			throw new IllegalArgumentException("null tabpanel");
		if (panel.getTabbox() != this)
			throw new UiException("Not a child: " + panel);
		final Tab tab = panel.getLinkedTab();
		if (tab != null)
			setSelectedTab(tab);
	}
	/**
	 * Sets the selected tab panel.
	 * @param panelApi assume as a {@link org.zkoss.zul.Tabpanel}
	 * @since 3.5.2
	 */
	public void setSelectedPanelApi(org.zkoss.zul.api.Tabpanel panelApi) {
		Tabpanel panel = (Tabpanel) panelApi;
		setSelectedPanel(panel);
	}

	/**
	 * Returns the selected tab.
	 */
	public Tab getSelectedTab() {
		return _seltab;
	}
	/**
	 * Returns the selected tab.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tab getSelectedTabApi() {
		return getSelectedTab();
	}
	/**
	 * Sets the selected tab.
	 */
	public void setSelectedTab(Tab tab) {
		selectTabDirectly(tab, false);
	}

	/**
	 * Sets the selected tab.
	 * @param tabApi assume as a {@link org.zkoss.zul.Tab}
	 * @since 3.5.2
	 */
	public void setSelectedTabApi(org.zkoss.zul.api.Tab tabApi) {
		Tab tab = (Tab) tabApi;
		setSelectedTab(tab);
	}

	/** Sets the selected tab. */
	/* packge */void selectTabDirectly(Tab tab, boolean byClient) {
		if (tab == null)
			throw new IllegalArgumentException("null tab");
		if (tab.getTabbox() != this)
			throw new UiException("Not my child: " + tab);
		if (tab != _seltab) {
			if (_seltab != null)
				_seltab.setSelectedDirectly(false);

			_seltab = tab;
			_seltab.setSelectedDirectly(true);
			if (!byClient)
				smartUpdate("selectedTab", _seltab.getUuid());
		}
	}

	/**
	 * Returns the orient.
	 *
	 * <p>
	 * Default: "horizontal".
	 *
	 * <p>
	 * Note: only the default mold supports it (not supported if accordion).
	 */
	public String getOrient() {
		return _orient;
	}
	/**
	 * Sets the mold.
	 *
	 * @param mold default , accordion and accordion-lite
	 *
	 */
	public void setMold(String mold){
		if (isVertical()){
			if (mold.startsWith("accordion")){
				throw new WrongValueException("Unsupported vertical orient in mold : "+mold);
			}else{
				super.setMold(mold);
			}
		}else{
			super.setMold(mold);
		}
	}
	/**
	 * Sets the orient.
	 *
	 * @param orient
	 *            either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("Unknow orient : " + orient);
		if (inAccordionMold())
			throw new WrongValueException("Unsupported vertical orient in mold : "+getMold());
		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			smartUpdate("orient", _orient);
		}
	}

	/**
	 * Returns whether it is a horizontal tabbox.
	 *
	 * @since 3.0.3
	 */
	public boolean isHorizontal() {
		return "horizontal".equals(getOrient());
	}

	/**
	 * Returns whether it is a vertical tabbox.
	 *
	 * @since 3.0.3
	 */
	public boolean isVertical() {
		return "vertical".equals(getOrient());
	}

	public String getZclass() {
		return  _zclass == null ? "z-tabbox" + (inAccordionMold() ? "-" + getMold() : isVertical() ? "-ver" : "") :
			_zclass;
	}

	// -- Component --//
	public void beforeChildAdded(Component child, Component refChild) {
		if (child instanceof Toolbar) {
			if (_toolbar != null && _toolbar != child)
				throw new UiException("Only one Toolbar is allowed: " + this);
		} else if (child instanceof Tabs) {
			if (_tabs != null && _tabs != child)
				throw new UiException("Only one tabs is allowed: " + this);
		} else if (child instanceof Tabpanels) {
			if (_tabpanels != null && _tabpanels != child)
				throw new UiException("Only one tabpanels is allowed: " + this);
		} else {
			throw new UiException("Unsupported child for tabbox: " + child);
		}
		super.beforeChildAdded(child, refChild);
	}
	public boolean insertBefore(Component child, Component refChild) {
		if (child instanceof Tabs) {
			if (super.insertBefore(child, refChild)) {
				_tabs = (Tabs) child;
				for (Iterator it = _tabs.getChildren().iterator(); it.hasNext();) {
					final Tab tab = (Tab) it.next();
					if (tab.isSelected()) {
						_seltab = tab;
						break;
					}
				}

				addTabsListeners();
				return true;
			}
		} else if (child instanceof Tabpanels) {
			if (super.insertBefore(child, refChild)) {
				_tabpanels = (Tabpanels) child;
				return true;
			}
		} else if (child instanceof Toolbar) {
			if (super.insertBefore(child, refChild)) {
				_toolbar = (Toolbar) child;
				return true;
			}
		} else {
			return super.insertBefore(child, refChild);
				//impossible but make it more extensible
		}
		return false;
	}

	public void onChildRemoved(Component child) {
		if (_tabs == child) {
			removeTabsListeners();
			_tabs = null;
			_seltab = null;
		} else if (_tabpanels ==  child) {
			_tabpanels = null;
		} else if (_toolbar == child) {
			_toolbar = null;
		}
		super.onChildRemoved(child);
	}

	/** Removes _listener from all {@link Tab} instances. */
	private void removeTabsListeners() {
		if (_tabs != null) {
			for (Iterator it = _tabs.getChildren().iterator(); it.hasNext();) {
				final Tab tab = (Tab) it.next();
				tab.removeEventListener(Events.ON_SELECT, _listener);
			}
		}
	}

	/** Adds _listener to all {@link Tab} instances. */
	private void addTabsListeners() {
		if (_tabs != null) {
			for (Iterator it = _tabs.getChildren().iterator(); it.hasNext();) {
				final Tab tab = (Tab) it.next();
				tab.addEventListener(Events.ON_SELECT, _listener);
			}
		}
	}

	// Cloneable//
	public Object clone() {
		final Tabbox clone = (Tabbox) super.clone();

		clone.removeTabsListeners();
		clone.init();

		int cnt = 0;
		if (clone._tabs != null)
			++cnt;
		if (clone._toolbar != null)
			++cnt;
		if (clone._tabpanels != null)
			++cnt;
		if (cnt > 0)
			clone.afterUnmarshal(cnt);

		return clone;
	}

	private void afterUnmarshal(int cnt) {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Tabs) {
				_tabs = (Tabs) child;
				for (Iterator e = _tabs.getChildren().iterator(); e.hasNext();) {
					final Tab tab = (Tab) e.next();
					if (tab.isSelected()) {
						_seltab = tab;
						break;
					}
				}
				if (--cnt == 0)
					break;
			} else if (child instanceof Toolbar) {
				_toolbar = (Toolbar) child;
				if (--cnt == 0)
					break;
			} else if (child instanceof Tabpanels) {
				_tabpanels = (Tabpanels) child;
				if (--cnt == 0)
					break;
			}
		}

		addTabsListeners();
	}

	// -- Serializable --//
	private synchronized void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();
		afterUnmarshal(-1);
	}

	private class Listener implements EventListener, Deferrable {
		public void onEvent(Event event) {
			Events.sendEvent(Tabbox.this, event);
		}

		public boolean isDeferrable() {
			return !Events.isListened(Tabbox.this, Events.ON_SELECT, true);
		}
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);
		if (_panelSpacing != null )
			render(renderer, "panelSpacing", _panelSpacing);
		if (!"horizontal".equals(_orient))
			render(renderer, "orient", _orient);
		if (!_tabscroll)
			renderer.render("tabscroll", _tabscroll);
	}
}
