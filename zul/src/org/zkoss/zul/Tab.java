/* Tab.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:43:18     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * A tab.
 * <p>
 * Default {@link #getZclass}: z-tab. (since 3.5.0)
 * 
 * <h3>Support child component</h3>
 * {@link Caption} child component is allowed.
 * [ZK EE]
 * [Since 6.5.0]
 * 
 * @author tomyeh
 */
public class Tab extends LabelImageElement {

	private static final Logger log = LoggerFactory.getLogger(Tab.class);
			
	private boolean _selected;
	/** Whether to show a close button. */
	private boolean _closable;

	private boolean _disabled;
	private transient Caption _caption;
	private transient Object _value;
	
	static {
		addClientEvent(Tab.class, Events.ON_CLOSE, 0);
		addClientEvent(Tab.class, Events.ON_SELECT, CE_IMPORTANT);
	}
	public Tab() {}

	public Tab(String label) {
		super(label);
	}

	public Tab(String label, String image) {
		super(label, image);
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * @since 7.0.0
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T)_value;
	}
	/** Sets the value.
	 * @param value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * @since 7.0.0
	 */
	public <T> void setValue(T value) {
		if (!Objects.equals(_value, value)) {
			_value = value;
		}
	}
	
	/** Returns the caption of this tab.
	 * @since 6.5.0
	 */
	public Caption getCaption() {
		return _caption;
	}
	
	//-- super --//
	public void setWidth(String width) {
		Tabbox tb = getTabbox();
		if (tb != null && tb.isVertical())
			throw new UnsupportedOperationException("Set Tabs' width instead");
		super.setWidth(width);
	}
	
	/**
	 * Returns whether this tab is closable. If closable, a button is displayed
	 * and the onClose event is sent if an user clicks the button.
	 * <p>
	 * Default: false.
	 */
	public boolean isClosable() {
		return _closable;
	}

	/**
	 * Sets whether this tab is closable. If closable, a button is displayed and
	 * the onClose event is sent if an user clicks the button.
	 * <p>
	 * Default: false.
	 * <p>
	 * You can intercept the default behavior by either overriding
	 * {@link #onClose}, or listening the onClose event.
	 * 
	 * <p>If {@link Tabbox#getModel()} is assigned, there is no an action to do with {@link #onClose},
	 * i.e. developer has to listen onClose event to delete that item in model not
	 * component itself. (since 7.0.0)
	 */
	public void setClosable(boolean closable) {
		if (_closable != closable) {
			_closable = closable;
			smartUpdate("closable", _closable);
		}
	}

	/**
	 * Process the onClose event sent when the close button is pressed.
	 * <p>
	 * Default: invoke {@link #close} to detach itself and the corresponding {@link Tabpanel}.
	 */
	public void onClose() {
		close();
	}

	/** Closes this tab and the linked tabpanel.
	 * This method detaches this component and the linked {@link Tabpanel}), only if
	 * {@link Tabbox#getModel()} is null. (since 7.0.0)
	 * @since 5.0.0
	 */
	public void close() {
		if (_selected) {
			final Tab tab = selectNextTab();
			if (tab != null) {
				final Set<Tab> selItems = new HashSet<Tab>(2);
				selItems.add(tab);
				Events.postEvent(new SelectEvent<Tab, Object>(Events.ON_SELECT, tab, selItems));
			}
		}
		Tabbox tabbox = getTabbox();
		
		// Nothing to do according to ZK-2027 issue, let application developer to do so.
		if (tabbox != null && tabbox.getModel() != null)
			return;
		
		//Cache panel before detach , or we couldn't get it after tab is detached.
		final Tabpanel panel = getLinkedPanel();
		
		detach();

		if (panel != null) {
			// B60-ZK-1160: Exception when closing tab with included content
			// Must clean up included content before detaching tab panel
			Component include = panel.getFirstChild();
			if (include instanceof Include) {
				include.detach();
			}
			panel.detach();
		}
	}
	
	private Tab selectNextTab() {
		for (Tab tab = (Tab) getNextSibling(); tab != null; tab = (Tab) tab.getNextSibling())
			if (!tab.isDisabled()) {
				tab.setSelected(true);
				return tab;
			}
		for (Tab tab = (Tab) getPreviousSibling(); tab != null; tab = (Tab) tab.getPreviousSibling())
			if (!tab.isDisabled()) {
				tab.setSelected(true);
				return tab;
			}
		return null;
	}
	
	/**
	 * Returns the tabbox owns this component.
	 */
	public Tabbox getTabbox() {
		final Tabs tabs = (Tabs) getParent();
		return tabs != null ? tabs.getTabbox() : null;
	}

	/**
	 * Returns the panel associated with this tab.
	 */
	public Tabpanel getLinkedPanel() {
		final int j = getIndex();
		if (j >= 0) {
			final Tabbox tabbox = getTabbox();
			if (tabbox != null) {
				final Tabpanels tabpanels = tabbox.getTabpanels();
				if (tabpanels != null && tabpanels.getChildren().size() > j)
					return (Tabpanel) tabpanels.getChildren().get(j);
			}
		}
		return null;
	}

	/**
	 * Returns whether this tab is selected.
	 */
	public boolean isSelected() {
		return _selected;
	}

	/**
	 * Sets whether this tab is selected.
	 */
	public void setSelected(boolean selected) {
		final Tabbox tabbox = (Tabbox) getTabbox();
		if (tabbox != null) {
			// Note: we don't update it here but let its parent does the job
			if(selected){  //Note that if already selected , tabbox will ignore it.
				tabbox.setSelectedTab(this);
			}else if(tabbox.getSelectedTab() == this){ //selected false and selected
				
				//clean selected tab , not set any others selected , if user call setSelected(false) manually , 
				//they should set another tab to be selected or no any tab will be selected.
				tabbox.clearSelectedTab();   
				_selected = false;
			}
		} else if(_selected != selected){
			_selected = selected;				
			smartUpdate("selected", _selected);
		}
	}

	/**
	 * Returns whether this tab is disabled.
	 * <p>
	 * Default: false.
	 * 
	 * @since 3.0.0
	 */
	public boolean isDisabled() {
		return _disabled;
	}

	/**
	 * Sets whether this tab is disabled. If a tab is disabled, then it cann't
	 * be selected or closed by user, but it still can be controlled by server
	 * side program.
	 * 
	 * @since 3.0.0
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}

	/**
	 * Updates _selected directly without updating the client.
	 */
	/* package */void setSelectedDirectly(boolean selected) {
		_selected = selected;
	}

	/**
	 * Returns the index of this panel, or -1 if it doesn't belong to any tabs.
	 */
	public int getIndex() {
		final Tabs tabs = (Tabs) getParent();
		if (tabs == null)
			return -1;
		int j = 0;
		for (Iterator<Component> it = tabs.getChildren().iterator();; ++j)
			if (it.next() == this)
				return j;
	}

	// -- super --//
	public String getZclass() {
		return _zclass == null ? "z-tab" : _zclass;
	}

	// -- Component --//
	/**
	 * Child is allowed, {@link Caption} only.
	 * @since 6.5.0
	 */
	protected boolean isChildable() {
		return true;
	}
	
	public void beforeChildAdded(Component child, Component refChild) {
		if (child instanceof Caption) {
			if (_caption != null && _caption != child)
				throw new UiException("Only one caption is allowed: "+this);
			super.beforeChildAdded(child, refChild);
		} else if (child instanceof Label) {// backward compatible
			super.beforeChildAdded(child, refChild);
		} else
			throw new UiException("Only caption is allowed: "+this);
	}
	
	// backward compatible
	private transient Label _tmpLabel;
	/**
	 * Internal use only
	 * @since 6.5.0
	 */
	public void onCreate(Event evt) {
		if (_tmpLabel != null) {
			setLabel(_tmpLabel.getValue());
			removeChild(_tmpLabel);
		}
		_tmpLabel = null;
	}
	
	public boolean insertBefore(Component child, Component refChild) {
		if (child instanceof Caption) {
			refChild = getFirstChild();
				//always makes caption as the first child
			if (super.insertBefore(child, refChild)) {
				_caption = (Caption)child;
				invalidate();
				return true;
			}
			return false;
		} else if (child instanceof Label) {// backward compatible
			_tmpLabel = (Label)child;
			log.warn("Please use Tab#setLabel(msg) instead! ["+this+"]");
		}
		return super.insertBefore(child, refChild);
		
	}
	
	public void onChildRemoved(Component child) {
		if (child instanceof Caption) {
			_caption = null;
			invalidate();
		}
		super.onChildRemoved(child);
	}


	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Tabs))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}
	
	//Cloneable//
	public Object clone() {
		final Tab clone = (Tab)super.clone();
		if (clone._caption != null) clone.afterUnmarshal();
		return clone;
	}
	private void afterUnmarshal() {
		for (Iterator<Component> it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Caption) {
				_caption = (Caption)child;
				break;
			}
		}
	}

	//Serializable//
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		afterUnmarshal();
	}

	// -- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link LabelImageElement#service},
	 * it also handles onSelect.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_SELECT)) {
			final Tabbox tabbox = getTabbox();
			final Set<Tab> prevSeldItems = new LinkedHashSet<Tab>();
			if (tabbox.getSelectedTab() != null)
				prevSeldItems.add(tabbox.getSelectedTab());
			final SelectEvent<Tab, Object> evt = SelectEvent.getSelectEvent(request,
					new SelectEvent.SelectedObjectHandler<Tab>() {
				public Set<Object> getObjects(Set<Tab> items) {
					if (items == null || items.isEmpty() || tabbox.getModel() == null)
						return null;
					Set<Object> objs = new LinkedHashSet<Object>();
					ListModel<Object> model = tabbox.getModel();
					for (Tab i : items)
						objs.add(model.getElementAt(i.getIndex()));
					return objs;
				}

				public Set<Tab> getPreviousSelectedItems() {
					return prevSeldItems;
				}
			});
			
			final Set<Tab> selItems = evt.getSelectedItems();
			if (selItems == null || selItems.size() != 1)
				throw new UiException("Exactly one selected tab is required: " + selItems); // debug purpose
			
			if (tabbox != null)
				tabbox.selectTabDirectly((Tab) selItems.iterator().next(), true);

			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);
		if (_disabled)
			render(renderer, "disabled", _disabled);
		if (_selected)
			render(renderer, "selected", _selected);
		if (_closable)
			render(renderer, "closable", _closable);
	}
}
