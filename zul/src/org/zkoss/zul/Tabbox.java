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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Objects;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Deferrable;
import org.zkoss.zk.ui.util.ForEachStatus;
import org.zkoss.zk.ui.util.Template;

import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.ext.Selectable;
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
public class Tabbox extends XulElement {
	private static final Logger log = LoggerFactory.getLogger(Tabbox.class);
	
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
	private static final String ATTR_ON_INIT_RENDER_POSTED = "org.zkoss.zul.onInitLaterPosted";
	private static final String ATTR_CHANGING_SELECTION = "org.zkoss.zul.tabbox.changingSelection";
	
	public Tabbox() {
		init();
	}

	private void init() {
		_listener = new Listener();
	}

	@SuppressWarnings("unchecked")
	/*package*/ Selectable<Object> getSelectableModel() {
		return (Selectable<Object>)_model;
	}

	/**
	 * Sets the list model associated with this t. If a non-null model
	 * is assigned, no matter whether it is the same as the previous, it will
	 * always cause re-render.
	 * 
	 * @param model
	 *            the list model to associate, or null to dissociate any
	 *            previous model.
	 * @exception UiException
	 *                if failed to initialize with the model
	 * @since 7.0.0
	 */
	public void setModel(ListModel<?> model) {
		if (model != null) {
			if (!(model instanceof Selectable))
				throw new UiException(model.getClass() + " must implement "+Selectable.class);

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
	private void doSelectionChanged() {
		final Selectable<Object> smodel = getSelectableModel();
		if (smodel.isSelectionEmpty()) {
			if (_seltab != null)
				setSelectedIndex(0);
			return;
		}

		if (_seltab != null && smodel.isSelected(_model.getElementAt(_seltab.getIndex())))
			return; //nothing changed

		for (int i = 0, sz = _model.getSize(); i < sz; i++) {
			if (smodel.isSelected(_model.getElementAt(i))) {
				setSelectedIndex(i);
				return; //done
			}
		}
		setSelectedIndex(0); //just in case
	}

	private void initDataListener() {
		if (_dataListener == null)
			_dataListener = new ListDataListener() {
				public void onChange(ListDataEvent event) {
					switch (event.getType()) {
					case ListDataEvent.SELECTION_CHANGED:
						if (getAttribute(ATTR_CHANGING_SELECTION) == null
								&& getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null /* Bug ZK-2011*/)
							doSelectionChanged();
						return; //nothing changed so need to rerender
					case ListDataEvent.MULTIPLE_CHANGED:
						return; //nothing to do
					}
					postOnInitRender();
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
		return (TabboxRenderer) _renderer;
	}

	/**
	 * Sets the renderer which is used to render each tab and tabpanel if {@link #getModel}
	 * is not null.
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
		if (_renderer != renderer) {
			_renderer = renderer;
			postOnInitRender();
		}
	}

	/**
	 * Sets the renderer by use of a class name. It creates an instance
	 * automatically.
	 * 
	 * @since 7.0.0
	 * @see #setTabboxRenderer(TabboxRenderer)
	 */
	public void setTabboxRenderer(String clsnm) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null)
			setTabboxRenderer((TabboxRenderer) Classes.newInstanceByThread(clsnm));
	}
	
	@SuppressWarnings("unchecked")
	public void onInitRender() {
		removeAttribute(ATTR_ON_INIT_RENDER_POSTED);
		doInitRenderer();
		invalidate();
	}
	
	/**
	 * Returns the empty tab for model to use.
	 * @since 7.0.0
	 */
	protected Tab newUnloadedTab() {
		Tab tab = new Tab();
		tab.applyProperties();
		return tab;
	}

	/**
	 * Returns the empty tabpanel for model to use.
	 * @since 7.0.0
	 */
	protected Tabpanel newUnloadedTabpanel() {
		Tabpanel tab = new Tabpanel();
		tab.applyProperties();
		return tab;
	}
	
	private void doInitRenderer() {
		final Renderer renderer = new Renderer();
		try {
			if (getTabs() == null)
				new Tabs().setParent(this);
			else
				getTabs().getChildren().clear();
			
			if (getTabpanels() == null)
				new Tabpanels().setParent(this);
			else
				getTabpanels().getChildren().clear();
			
			for (int i = 0, j = _model.getSize(); i < j; i++) {
				renderer.render(this, _model.getElementAt(i), i);
			}
			
			if (_seltab == null && _model.getSize() > 0) {// select the first one
				setSelectedTab((Tab)getTabs().getFirstChild());
			}
				
		} catch (Throwable ex) {
			log.error("", ex);
		}
		Events.postEvent(ZulEvents.ON_AFTER_RENDER, this, null);// notify the tabbox when items have been rendered.
		
	}

	private void postOnInitRender() {
		if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
			setAttribute(ATTR_ON_INIT_RENDER_POSTED, Boolean.TRUE);
			Events.postEvent("onInitRender", this, null);
		}
	}

	/**
	 * Returns the model associated with this selectbox, or null if this
	 * selectbox is not associated with any list data model.
	 */
	@SuppressWarnings("unchecked")
	public <T> ListModel<T> getModel() {
		return (ListModel) _model;
	}

	@SuppressWarnings("unchecked")
	public <T> TabboxRenderer<T> getRealRenderer() {
		final TabboxRenderer renderer = getTabboxRenderer();
		return renderer != null ? renderer : _defRend;
	}



	/** Used to render listitem if _model is specified. */
	/* package */class Renderer { // use package for easy to call (if override)
		private final TabboxRenderer _renderer;

		/* package */Renderer() {
			_renderer = (TabboxRenderer) getRealRenderer();
		}

		/* package */@SuppressWarnings("unchecked")
		void render(Tabbox tabbox, Object value, int index) throws Throwable {
			
			final boolean selected = ((Selectable) _model).isSelected(value);
			Tab tab = newUnloadedTab();
			tabbox.getTabs().appendChild(tab);
			try {
				_renderer.renderTab(tab, value, index);
				Object v = tab.getAttribute("org.zkoss.zul.model.renderAs");
				if (v != null)
					tab = (Tab)v;
			} catch (Throwable ex) {
				try {
					tab.setLabel(Exceptions.getMessage(ex));
				} catch (Throwable t) {
					log.error("", t);
				}
				throw ex;
			}
			
			if (selected)
				setSelectedTab(tab);
			
			Tabpanel tabpanel = newUnloadedTabpanel();
			tabbox.getTabpanels().appendChild(tabpanel);
			
			try {
				_renderer.renderTabpanel(tabpanel, value, index);
				Object v = tab.getAttribute("org.zkoss.zul.model.renderAs");
				if (v != null)
					tabpanel = (Tabpanel)v;
			} catch (Throwable ex) {
				try {
					tabpanel.appendChild(new Label(Exceptions.getMessage(ex)));
				} catch (Throwable t) {
					log.error("", t);
				}
				throw ex;
			}
			
		}
	}
	private static final TabboxRenderer<Object> _defRend = new TabboxRenderer<Object>() {
		
		public void renderTab(Tab tab, final Object data, final int index) throws Exception {
			final Tabbox tabbox = tab.getTabbox();
			final Template tm = tabbox.getTemplate("model:tab");
			if (tm == null)
				tab.setLabel(Objects.toString(data));
			else {
				final Component[] items = tm.create(tabbox.getTabs(), tab,
						new VariableResolver() {
							public Object resolveVariable(String name) {
								if ("each".equals(name)) {
									return data;
								} else if ("forEachStatus".equals(name)) {
									return new ForEachStatus() {
										
										public ForEachStatus getPrevious() {
											return null;
										}
										
										public Object getEach() {
											return data;
										}
										
										public int getIndex() {
											return index;
										}
										
										public Integer getBegin() {
											return 0;
										}
										
										public Integer getEnd() {
											return ((Tabbox)tabbox).getModel().getSize();
										}
									};
								} else {
									return null;
								}
							}
						}, null);
				if (items.length != 1)
					throw new UiException(
							"The model template must have exactly one item, not "
									+ items.length);
				if (!(items[0] instanceof Tab))
					throw new UiException(
							"The model template can only support Tab component, not "
									+ items[0]);

				final Tab ntab = (Tab)items[0];
				if (ntab.getValue() == null) //template might set it
					ntab.setValue(data);
				tab.setAttribute("org.zkoss.zul.model.renderAs", ntab);
					//indicate a new item is created to replace the existent one
				tab.detach();
			}
		}

		public void renderTabpanel(Tabpanel tabpanel, final Object data, final int index)
				throws Exception {
			final Tabbox tabbox = tabpanel.getTabbox();
			final Template tm = tabbox.getTemplate("model:tabpanel");
			if (tm == null)
				tabpanel.appendChild(new Label(Objects.toString(data)));
			else {
				final Component[] items = tm.create(tabbox.getTabpanels(), tabpanel,
						new VariableResolver() {
							public Object resolveVariable(String name) {
								if ("each".equals(name)) {
									return data;
								} else if ("forEachStatus".equals(name)) {
									return new ForEachStatus() {
										
										public ForEachStatus getPrevious() {
											return null;
										}
										
										public Object getEach() {
											return data;
										}
										
										public int getIndex() {
											return index;
										}
										
										public Integer getBegin() {
											return 0;
										}
										
										public Integer getEnd() {
											return ((Tabbox)tabbox).getModel().getSize();
										}
									};
								} else {
									return null;
								}
							}
						}, null);
				if (items.length != 1)
					throw new UiException(
							"The model template must have exactly one item, not "
									+ items.length);
				if (!(items[0] instanceof Tabpanel))
					throw new UiException(
							"The model template can only support Tabpanel component, not "
									+ items[0]);

				final Tabpanel ntabpanel = (Tabpanel)items[0];
				tabpanel.setAttribute("org.zkoss.zul.model.renderAs", ntabpanel);
					//indicate a new item is created to replace the existent one
				tabpanel.detach();
			}
		}
	};
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
	 * Returns whether to use maximum height of all tabpanel in initial phase or not.
	 * <p> 
	 * Default: false.
	 * @since 7.0.0
	 */
	public boolean isMaximalHeight() {
		return _maximalHeight;
	}

	/**
	 * Sets whether to use maximum height of all tabpanel in initial phase or not.
	 * <p>
	 * The Client ROD feature will be disabled if it is set to true.
	 * @param boolean maximalHeight
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
			setSelectedTab((Tab)tabs.getFirstChild()); // keep the first one selected.
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
				if (getAttribute(ATTR_CHANGING_SELECTION) == null) {
					setAttribute(ATTR_CHANGING_SELECTION, Boolean.TRUE);
					_seltab = tab;
					_seltab.setSelectedDirectly(true);
					if (byClient && _model != null) {
						Selectable sm = getSelectableModel();
						if (!sm.isSelected(_model.getElementAt(_seltab.getIndex()))) {
							sm.clearSelection();
							sm.addToSelection(_model.getElementAt(_seltab.getIndex()));
						}
					}
				}
			} finally {
				removeAttribute(ATTR_CHANGING_SELECTION);
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
	 * @param orient either "top", "left", "bottom or "right".
	 * @since 7.0.0 "horizontal" is renamed to "top" and "vertical" is renamed to "left".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"top".equals(orient) && !"bottom".equals(orient) 
				&& !"vertical".equals(orient) && !"right".equals(orient) && !"left".equals(orient))
			throw new WrongValueException("Unknow orient : " + orient);
		if (inAccordionMold())
			throw new WrongValueException("Unsupported vertical orient in mold : "+getMold());
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
	
	protected void clearSelectedTab(){
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
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();
		afterUnmarshal(-1);
	}

	private class Listener implements EventListener<Event>, Deferrable {
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
