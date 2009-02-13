/* Tabbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:42:31     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
import org.zkoss.zk.ui.ext.render.ChildChangedAware;
import org.zkoss.zk.au.out.AuInvoke;

import org.zkoss.zul.impl.XulElement;

/**
 * A tabbox.
 *
 * <p>Event:
 * <ol>
 * <li>org.zkoss.zk.ui.event.SelectEvent is sent when user changes
 * the tab.</li>
 * </ol>
 *
 * <p>Mold:
 * <dl>
 * <dt>default</dt>
 * <dd>The default tabbox.</dd>
 * <dt>accordion</dt>
 * <dd>The accordion tabbox.</dd>
 * </dl>
 *
 * <p>Note: {@link #getSclass} also controls {@link Tabs} and {@link Tab}.
 * Refer to {@link #getTabLook} for details.
 *
 * @author tomyeh
 */
public class Tabbox extends XulElement {
	private transient Tabs _tabs;
	private transient Tabpanels _tabpanels;
	private transient Tab _seltab;
	private String _panelSpacing;
	private String _orient = "horizontal";
	/** The event listener used to listen onSelect for each tab. */
	/*package*/ transient EventListener _listener;

	public Tabbox() {
		init();
	}
	private void init() {
		_listener = new Listener();
	}

	/** Returns whether it is in the accordion mold.
	 */
	/*package*/ boolean inAccordionMold() {
		return "accordion".equals(getMold());
	}
	/** Returns the tabs that this tabbox owns.
	 */
	public Tabs getTabs() {
		return _tabs;
	}
	/** Returns the tabpanels that this tabbox owns.
	 */
	public Tabpanels getTabpanels() {
		return _tabpanels;
	}

	/** Returns the spacing between {@link Tabpanel}.
	 * This is used by certain molds, such as accordion.
	 * <p>Default: null (no spacing).
	 */
	public String getPanelSpacing() {
		return _panelSpacing;
	}
	/** Sets the spacing between {@link Tabpanel}.
	 * This is used by certain molds, such as accordion.
	 */
	public void setPanelSpacing(String panelSpacing) {
		if (panelSpacing != null && panelSpacing.length() == 0)
			panelSpacing = null;

		if (!Objects.equals(_panelSpacing, panelSpacing)) {
			_panelSpacing = panelSpacing;
			invalidate();
		}
	}

	/** Returns the selected index.
	 */
	public int getSelectedIndex() {
		return _seltab != null ? _seltab.getIndex(): -1;
	}
	/*** Sets the selected index.
	 */
	public void setSelectedIndex(int j) {
		final Tabs tabs = getTabs();
		if (tabs == null)
			throw new IllegalStateException("No tab at all");
		setSelectedTab((Tab)tabs.getChildren().get(j));
	}

	/** Returns the selected tab panel.
	 */
	public Tabpanel getSelectedPanel() {
		return _seltab != null ? _seltab.getLinkedPanel(): null;
	}
	/** Sets the selected tab panel.
	 */
	public void setSelectedPanel(Tabpanel panel) {
		if (panel != null && panel.getTabbox() != this)
			throw new UiException("Not a child: "+panel);
		final Tab tab = panel.getLinkedTab();
		if (tab != null)
			setSelectedTab(tab);
	}
	/** Returns the selected tab.
	 */
	public Tab getSelectedTab() {
		return _seltab;
	}
	/** Sets the selected tab.
	 */
	public void setSelectedTab(Tab tab) {
		selectTabDirectly(tab, false);
	}
	/** Sets the selected tab. */
	/*packge*/ void selectTabDirectly(Tab tab, boolean byClient) {
		if (tab == null)
			throw new IllegalArgumentException("null tab");
		if (tab.getTabbox() != this)
			throw new UiException("Not my child: "+tab);
		if (tab != _seltab) {
			if (_seltab != null)
				_seltab.setSelectedDirectly(false);

			_seltab = tab;
			_seltab.setSelectedDirectly(true);
			if (!byClient)
				response("sel", new AuInvoke(_seltab, "selTab"));
		}
	}

	/** Returns the orient.
	 *
	 * <p>Default: "horizontal".
	 *
	 * <p>Note: only the default mold supports it (not supported if accordion).
	 */
	public String getOrient() {
		return _orient;
	}
	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException(orient);

		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			invalidate();
		}
	}
	/** Returns whether it is a horizontal tabbox.
	 * @since 3.0.3
	 */
	public boolean isHorizontal() {
		return "horizontal".equals(getOrient());
	}
	/** Returns whether it is a vertical tabbox.
	 * @since 3.0.3
	 */
	public boolean isVertical() {
		return "vertical".equals(getOrient());
	}

	/** Returns the look of the {@link Tab} and {@link Tabbox}.
	 * It is, in fact, a portion of the style class that are used to
	 * generate the style of {@link Tabs} and {@link Tab}.
	 *
	 * <p>If the style class ({@link #getSclass}) of this tab box is not
	 * defined and the mold is default,
	 * "tab-3d" and "tab-v3d" are returned for horizontal and vertical
	 * orient, respectively.
	 * If the style class not defined and the mold is accordion,
	 * "tabaccd-3d" and "tabaccd-v3d" returned (note: accordion doesn't support vertical yet).
	 *
	 * <p>If the style class is defined, say "lite",
	 * then this method return "tab-lite" and "tab-vlite" for
	 * horizontal and vertical orient, respectively, and "tabacc-lite" for horizontal accordion.
	 *
	 * <p>If the mold is not "default" nor "accordion", this method returns
	 * "tab" + getMold() + "-" + (vertical ? 'v': '') + getSclass().
	 *
	 * <p>With this method, {@link Tab} and {@link Tabpanel} generate
	 * the style class accordingly. For example, if the mold is "default"
	 * and the style class not defined, then
	 * "tab-3d-tl-sel" for the top-left corner of the selected tab,
	 * "tab-3d-tm-uns" for the top-middle border of the
	 * non-selected tab, and so on.
	 *
	 * @since 3.0,0
	 */
	public String getTabLook() {
		final String mold = getMold();
		String prefix = "default".equals(mold) ? "tab-":
				"accordion".equals(mold) ? "tabaccd-": "tab" + mold + '-';

		if ("vertical".equals(_orient))
			prefix += 'v';

		final String scls = getSclass();
		return scls != null && scls.length() > 0 ? prefix + scls: prefix + "3d";
	}

	//-- Component --//
	/** Auto-creates {@link Tabpanel} and select one of tabs if necessary.
	 */
	public void onCreate() {
		if (_tabs != null) {
			final int sz = _tabs.getChildren().size();
			if (_tabpanels == null)
				insertBefore(new Tabpanels(), null);
			for (int n = _tabpanels.getChildren().size(); n < sz; ++n)
				_tabpanels.insertBefore(new Tabpanel(), null);
			if (sz > 0 && _seltab == null)
				setSelectedTab((Tab)_tabs.getFirstChild());
		}
	}
	public boolean insertBefore(Component child, Component insertBefore) {
		if (child instanceof Tabs) {
			if (_tabs != null && _tabs != child)
				throw new UiException("Only one tabs is allowed: "+this);

			_tabs = (Tabs)child;
			for (Iterator it = _tabs.getChildren().iterator(); it.hasNext();) {
				final Tab tab = (Tab)it.next();
				if (tab.isSelected()) {
					_seltab = tab;
					break;
				}
			}

			addTabsListeners();
		} else if (child instanceof Tabpanels) {
			if (_tabpanels != null && _tabpanels != child)
				throw new UiException("Only one tabpanels is allowed: "+this);
			_tabpanels = (Tabpanels)child;
		} else {
			throw new UiException("Unsupported child for tabbox: "+child);
		}
		if (super.insertBefore(child, insertBefore)) {
			invalidate(); //due to DSP might implemented diff for children order
			return true;
		}
		return false;
	}
	public void onChildRemoved(Component child) {
		if (child instanceof Tabs) {
			removeTabsListeners();
			_tabs = null;
			_seltab = null;
		} else if (child instanceof Tabpanels) {
			_tabpanels = null;
		}
		super.onChildRemoved(child);
	}
	/** Removes _listener from all {@link Tab} instances. */
	private void removeTabsListeners() {
		if (_tabs != null) {
			for (Iterator it = _tabs.getChildren().iterator(); it.hasNext();) {
				final Tab tab = (Tab)it.next();
				tab.removeEventListener(Events.ON_SELECT, _listener);
			}
		}
	}
	/** Adds _listener to all {@link Tab} instances. */
	private void addTabsListeners() {
		if (_tabs != null) {
			for (Iterator it = _tabs.getChildren().iterator(); it.hasNext();) {
				final Tab tab = (Tab)it.next();
				tab.addEventListener(Events.ON_SELECT, _listener);
			}
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		appendAsapAttr(sb, Events.ON_RIGHT_CLICK);
			//no z.dbclk/z.lfclk since it is covered by both Tab and Tabpanel

		if (isVertical())
			HTMLs.appendAttribute(sb, "z.orient", "v");
		if (_tabs != null && !inAccordionMold())
			HTMLs.appendAttribute(sb, "z.tabs", _tabs.getUuid());
		return sb.toString();
	}

	//Cloneable//
	public Object clone() {
		final Tabbox clone = (Tabbox)super.clone();

		clone.removeTabsListeners();
		clone.init();

		int cnt = 0;
		if (clone._tabs != null) ++cnt;
		if (clone._tabpanels != null) ++cnt;
		if (cnt > 0) clone.afterUnmarshal(cnt);

		return clone;
	}
	private void afterUnmarshal(int cnt) {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Tabs) {
				_tabs = (Tabs)child;
				for (Iterator e = _tabs.getChildren().iterator();
				e.hasNext();) {
					final Tab tab = (Tab)e.next();
					if (tab.isSelected()) {
						_seltab = tab;
						break;
					}
				}
				if (--cnt == 0) break;
			} else if (child instanceof Tabpanels) {
				_tabpanels = (Tabpanels)child;
				if (--cnt == 0) break;
			}
		}

		addTabsListeners();
	}

	//-- Serializable --//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();
		afterUnmarshal(-1);
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements ChildChangedAware {
		//ChildChangedAware//
		public boolean isChildChangedAware() {
			return !inAccordionMold();
				//we have to adjust the width of last cell
		}
	}

	private class Listener implements EventListener, Deferrable {
		public void onEvent(Event event) {
			Events.sendEvent(Tabbox.this, event);
		}
		public boolean isDeferrable() {
			return !Events.isListened(Tabbox.this, Events.ON_SELECT, true);
		}
	}
}
