/* Tabbox.java

	Purpose:

	Description:

	History:
		Tue Jul 12 10:42:31     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.io.Serializables;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Deferrable;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.ext.Selectable;
import org.zkoss.zul.impl.TabboxEngine;
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
 * <p>
 * Besides creating {@link Tab}  and {@link Tabpanel} programmatically, you could
 * assign a data model (a {@link ListModel} to a Tabbox via {@link #setModel(ListModel)}
 * and then the tabbox will retrieve data via {@link ListModel#getElementAt} when
 * necessary. (since 7.0.0) [ZK EE]
 *
 * <p>
 * Besides assign a list model, you could assign a renderer (a
 * {@link TabboxRenderer} instance) to a Tabbox, such that the Tabbox will
 * use this renderer to render the data returned by
 * {@link ListModel#getElementAt}. If not assigned, the default renderer, which
 * assumes a label per Tab and Tabpanel, is used. In other words, the default renderer
 * adds a label to a Tab and Tabpanel by calling toString against the object returned by
 * {@link ListModel#getElementAt}  (since 7.0.0) [ZK EE]
 *
 * <p>To retrieve what are selected in Tabbox with a {@link Selectable}
 * {@link ListModel}, you shall use {@link Selectable#getSelection} to get what
 * is currently selected object in {@link ListModel} rather than using
 * {@link Tabbox#getSelectedTab()}. That is, you shall operate on the data of
 * the {@link ListModel} rather than on the {@link Tab} of the {@link Tabbox}
 * if you use the {@link Selectable} {@link ListModel}.  (since 7.0.0) [ZK EE]
 *
 * <pre><code>
 * Set selection = ((Selectable)getModel()).getSelection();
 * </code></pre>
 * 
 * @author tomyeh
 */
public class Tabbox extends XulElement {
	private transient Tabs _tabs;
	private transient Toolbar _toolbar;
	private transient Tabpanels _tabpanels;
	private transient Tab _seltab;
	private String _panelSpacing;
	private String _orient = "top";
	private boolean _tabscroll = true;
	private boolean _maximalHeight = false;
	/** The event listener used to listen onSelect for each tab. */
	/* package */transient EventListener<Event> _listener;

	private transient ListModel<?> _model;
	private transient ListDataListener _dataListener;
	private transient TabboxRenderer<?> _renderer;
	private transient TabboxEngine _engine;

	public Tabbox() {
		init();
	}

	private void init() {
		_listener = new Listener();
	}

	/** Returns the implementation tabbox engine.
	 * @exception UiException if failed to load the engine.
	 * @since 7.0.0
	 */
	public TabboxEngine getEngine() throws UiException {
		if (_engine == null)
			_engine = newTabboxEngine();
		return _engine;
	}

	/** Sets the tabbox engine for {@link ListModel}
	 * @since 7.0.0
	 */
	public void setEngine(TabboxEngine engine) {
		if (_engine != engine) {
			_engine = engine;
		}

		//Always redraw, if any
		postOnInitRender();
	}

	/** Instantiates the default tabbox engine.
	 * It is called, if {@link #setEngine} is not called with non-null
	 * engine.
	 *
	 * <p>By default, it looks up the library property called
	 * org.zkoss.zul.tabbox.engine.class.
	 * If found, the value is assumed to be
	 * the class name of the tabbox engine (it must implement
	 * {@link TabboxEngine}).
	 * If not found, {@link UiException} is thrown.
	 *
	 * <p>Derived class might override this method to provide your
	 * own default class.
	 *
	 * @exception UiException if failed to instantiate the engine
	 * @since 7.0.0
	 */
	protected TabboxEngine newTabboxEngine() throws UiException {
		final String PROP = "org.zkoss.zul.tabbox.engine.class";
		final String klass = Library.getProperty(PROP);
		if (klass == null)
			throw new UiException("Library property,  " + PROP + ", required");

		final Object v;
		try {
			v = Classes.newInstanceByThread(klass);
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
		if (!(v instanceof TabboxEngine))
			throw new UiException(TabboxEngine.class + " must be implemented by " + v);
		return (TabboxEngine) v;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Returns the selectable model, if any.
	 * @since 7.0.0
	 */
	public Selectable<Object> getSelectableModel() {
		return (Selectable<Object>) _model;
	}

	private static boolean disableFeature() {
		return !WebApps.getFeature("ee");
	}

	/**
	 * Sets the list model associated with this t. If a non-null model
	 * is assigned, no matter whether it is the same as the previous, it will
	 * always cause re-render. [ZK EE]
	 * 
	 * @param model
	 *            the list model to associate, or null to dissociate any
	 *            previous model.
	 * @exception UiException
	 *                if failed to initialize with the model
	 * @since 7.0.0
	 */
	public void setModel(ListModel<?> model) {
		if (disableFeature())
			throw new IllegalAccessError("ZK EE version only!");
		if (model != null) {
			if (!(model instanceof Selectable))
				throw new UiException(model.getClass() + " must implement " + Selectable.class);

			if (_model != model) {
				if (_model != null) {
					_model.removeListDataListener(_dataListener);
				}
				_model = model;
				_seltab = null;
				initDataListener();
				postOnInitRender();
			}
		} else if (_model != null) {
			_model.removeListDataListener(_dataListener);
			_model = null;
			invalidate();
		}
	}

	private void initDataListener() {
		if (_dataListener == null)
			_dataListener = new ListDataListener() {
				public void onChange(ListDataEvent event) {
					getEngine().doDataChange(Tabbox.this, event);
				}
			};
		_model.addListDataListener(_dataListener);
	}

	/**
	 * Returns the renderer to render each tab and tabpanel, or null if the default renderer
	 * is used.
	 * @since 7.0.0
	 */
	@SuppressWarnings("unchecked")
	public <T> TabboxRenderer<T> getTabboxRenderer() {
		return (TabboxRenderer<T>) _renderer;
	}

	/**
	 * Sets the renderer which is used to render each tab and tabpanel if {@link #getModel}
	 * is not null. [ZK EE]
	 * 
	 * <p>
	 * Note: changing a render will not cause the tabbox to re-render. If you
	 * want it to re-render, you could assign the same model again (i.e.,
	 * setModel(null) and than setModel(oldModel)), or fire an {@link ListDataEvent} event.
	 * 
	 * @param renderer
	 *            the renderer, or null to use the default.
	 * @exception UiException
	 *                if failed to initialize with the model
	 * @since 7.0.0
	 */
	public void setTabboxRenderer(TabboxRenderer<?> renderer) {
		if (disableFeature())
			throw new IllegalAccessError("ZK EE version only!");
		if (_renderer != renderer) {
			_renderer = renderer;
			postOnInitRender();
		}
	}

	/**
	 * Sets the renderer by use of a class name. It creates an instance
	 * automatically. [ZK EE]
	 * 
	 * @since 7.0.0
	 * @see #setTabboxRenderer(TabboxRenderer)
	 */
	public void setTabboxRenderer(String clsnm) throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null)
			setTabboxRenderer((TabboxRenderer<?>) Classes.newInstanceByThread(clsnm));
	}

	public void onInitRender() {
		if (disableFeature())
			throw new IllegalAccessError("ZK EE version only!");
		removeAttribute(TabboxEngine.ATTR_ON_INIT_RENDER_POSTED);
		doInitRenderer();
		invalidate();
	}

	private void doInitRenderer() {
		if (disableFeature())
			throw new IllegalAccessError("ZK EE version only!");
		getEngine().doInitRenderer(this);
		Events.postEvent(ZulEvents.ON_AFTER_RENDER, this, null); // notify the tabbox when items have been rendered.

	}

	/**
	 * Component internal use only.
	 * @since 7.0.0
	 */
	public void postOnInitRender() {
		if (getAttribute(TabboxEngine.ATTR_ON_INIT_RENDER_POSTED) == null) {
			setAttribute(TabboxEngine.ATTR_ON_INIT_RENDER_POSTED, Boolean.TRUE);
			Events.postEvent("onInitRender", this, null);
		}
	}

	/**
	 * Returns the model associated with this selectbox, or null if this
	 * selectbox is not associated with any list data model.
	 */
	@SuppressWarnings("unchecked")
	public <T> ListModel<T> getModel() {
		return (ListModel<T>) _model;
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
	 * Returns the tabpanels that this tabbox owns.
	 */
	public Tabpanels getTabpanels() {
		return _tabpanels;
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
	 * Sets whether to enable the tab scrolling.
	 * When enabled, if tab list is wider than tab bar, left, right arrow will appear.
	 * @since 3.5.0
	 */
	public void setTabscroll(boolean tabscroll) {
		if (_tabscroll != tabscroll) {
			_tabscroll = tabscroll;
			smartUpdate("tabscroll", _tabscroll);
		}
	}

	/**
	 * Returns whether to use maximum height of all tabpanel in initial phase.
	 * <p> 
	 * Default: false.
	 * @since 7.0.0
	 */
	public boolean isMaximalHeight() {
		return _maximalHeight;
	}

	/**
	 * Sets whether to use maximum height of all tabpanel in initial phase.
	 * <p>
	 * The Client ROD feature will be disabled if it is set to true.
	 * @since 7.0.0
	 */
	public void setMaximalHeight(boolean maximalHeight) {
		if (_maximalHeight != maximalHeight) {
			_maximalHeight = maximalHeight;
			smartUpdate("maximalHeight", _maximalHeight);
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
		if (j >= 0)
			setSelectedTab((Tab) tabs.getChildren().get(j));
		else
			setSelectedTab((Tab) tabs.getFirstChild()); // keep the first one selected.
	}

	/**
	 * Returns the selected tab panel.
	 */
	public Tabpanel getSelectedPanel() {
		return _seltab != null ? _seltab.getLinkedPanel() : null;
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
	 * Returns the selected tab.
	 */
	public Tab getSelectedTab() {
		return _seltab;
	}

	/**
	 * Sets the selected tab.
	 */
	public void setSelectedTab(Tab tab) {
		selectTabDirectly(tab, false);
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

			try {
				// avoid recursive invoking
				if (getAttribute(TabboxEngine.ATTR_CHANGING_SELECTION) == null) {
					setAttribute(TabboxEngine.ATTR_CHANGING_SELECTION, Boolean.TRUE);
					_seltab = tab;
					_seltab.setSelectedDirectly(true);
					if (byClient && _model != null) {
						Selectable<Object> sm = getSelectableModel();
						if (!sm.isSelected(_model.getElementAt(_seltab.getIndex()))) {
							sm.clearSelection();
							sm.addToSelection(_model.getElementAt(_seltab.getIndex()));
						}
					}
				}
			} finally {
				removeAttribute(TabboxEngine.ATTR_CHANGING_SELECTION);
			}
			if (!byClient)
				smartUpdate("selectedTab", _seltab);
		}
	}

	/**
	 * Returns the orient.
	 *
	 * <p>
	 * Default: "top".
	 *
	 * <p>
	 * Note: only the default mold supports it (not supported if accordion).
	 */
	public String getOrient() {
		return this._orient;
	}

	/**
	 * Sets the mold.
	 *
	 * @param mold default , accordion and accordion-lite
	 *
	 */
	public void setMold(String mold) {
		if (isVertical()) {
			if (mold.startsWith("accordion")) {
				throw new WrongValueException("Unsupported vertical orient in mold : " + mold);
			} else {
				super.setMold(mold);
			}
		} else {
			super.setMold(mold);
		}
	}

	/**
	 * Sets the orient.
	 *
	 * @param orient either "top", "left", "bottom or "right".
	 * @since 7.0.0 "horizontal" is renamed to "top" and "vertical" is renamed to "left".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"top".equals(orient) && !"bottom".equals(orient)
				&& !"vertical".equals(orient) && !"right".equals(orient) && !"left".equals(orient))
			throw new WrongValueException("Unknow orient : " + orient);
		if (inAccordionMold())
			throw new WrongValueException("Unsupported vertical orient in mold : " + getMold());
		if (!Objects.equals(_orient, orient)) {
			if ("horizontal".equals(orient))
				this._orient = "top";
			else if ("vertical".equals(orient))
				this._orient = "left";
			else
				this._orient = orient;
			smartUpdate("orient", _orient);
		}
	}

	/**
	 * Returns whether it is a horizontal tabbox.
	 *
	 * @since 3.0.3
	 */
	public boolean isHorizontal() {
		String orient = getOrient();
		return "horizontal".equals(orient) || "top".equals(orient) || "bottom".equals(orient);
	}

	/**
	 * Returns whether it is the top orientation.
	 * @since 7.0.0
	 */
	public boolean isTop() {
		String orient = getOrient();
		return "horizontal".equals(orient) || "top".equals(orient);
	}

	/**
	 * Returns whether it is the bottom orientation.
	 * @since 7.0.0
	 */
	public boolean isBottom() {
		return "bottom".equals(getOrient());
	}

	/**
	 * Returns whether it is a vertical tabbox.
	 *
	 * @since 3.0.3
	 */
	public boolean isVertical() {
		String orient = getOrient();
		return "vertical".equals(orient) || "right".equals(orient) || "left".equals(orient);
	}

	/**
	 * Returns whether it is the left orientation.
	 *
	 * @since 7.0.0
	 */
	public boolean isLeft() {
		String orient = getOrient();
		return "vertical".equals(orient) || "left".equals(orient);
	}

	/**
	 * Returns whether it is the right orientation.
	 *
	 * @since 7.0.0
	 */
	public boolean isRight() {
		return "right".equals(getOrient());
	}

	public String getZclass() {
		return _zclass == null ? "z-tabbox" : _zclass;
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
				for (Iterator<Component> it = _tabs.getChildren().iterator(); it.hasNext();) {
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
		} else if (_tabpanels == child) {
			_tabpanels = null;
		} else if (_toolbar == child) {
			_toolbar = null;
		}
		super.onChildRemoved(child);
	}

	/** Removes _listener from all {@link Tab} instances. */
	private void removeTabsListeners() {
		if (_tabs != null) {
			for (Iterator<Component> it = _tabs.getChildren().iterator(); it.hasNext();) {
				final Tab tab = (Tab) it.next();
				tab.removeEventListener(Events.ON_SELECT, _listener);
			}
		}
	}

	/** Adds _listener to all {@link Tab} instances. */
	private void addTabsListeners() {
		if (_tabs != null) {
			for (Iterator<Component> it = _tabs.getChildren().iterator(); it.hasNext();) {
				final Tab tab = (Tab) it.next();
				tab.addEventListener(Events.ON_SELECT, _listener);
			}
		}
	}

	protected void clearSelectedTab() {
		_seltab = null;
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
		for (Iterator<Component> it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Tabs) {
				_tabs = (Tabs) child;
				for (Iterator<Component> e = _tabs.getChildren().iterator(); e.hasNext();) {
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
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();

		willSerialize(_model);
		Serializables.smartWrite(s, _model);
		willSerialize(_renderer);
		Serializables.smartWrite(s, _renderer);
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_model = (ListModel) s.readObject();
		didDeserialize(_model);
		_renderer = (TabboxRenderer) s.readObject();
		didDeserialize(_renderer);

		init();
		afterUnmarshal(-1);

		if (_model != null) {
			initDataListener();
		}
	}

	private class Listener implements EventListener<Event>, Deferrable {
		public void onEvent(Event event) {
			Events.sendEvent(Tabbox.this, event);
		}

		public boolean isDeferrable() {
			return !Events.isListened(Tabbox.this, Events.ON_SELECT, true);
		}
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		if (_panelSpacing != null)
			render(renderer, "panelSpacing", _panelSpacing);
		if (!isTop())
			render(renderer, "orient", _orient);
		if (!_tabscroll)
			renderer.render("tabscroll", _tabscroll);
		if (_maximalHeight) {
			renderer.render("z$rod", false);
			renderer.render("maximalHeight", _maximalHeight);
		}
	}
}
