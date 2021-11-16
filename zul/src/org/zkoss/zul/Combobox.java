/* Combobox.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 15 17:33:01     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import static org.zkoss.lang.Generics.cast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Objects;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.ext.Blockable;
import org.zkoss.zk.ui.sys.BooleanPropertyAccess;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zk.ui.sys.ShadowElementsCtrl;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zk.ui.util.ForEachStatus;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.ext.Selectable;
import org.zkoss.zul.impl.Utils;

/**
 * A combobox.
 *
 * <p>Non-XUL extension. It is used to replace XUL menulist. This class
 * is more flexible than menulist, such as {@link #setAutocomplete}
 * {@link #setAutodrop}.
 *
 * <p>Default {@link #getZclass}: z-combobox.(since 3.5.0)
 *
 * <p>Events: onOpen, onSelect, onAfterRender<br/>
 * Developers can listen to the onOpen event and initializes it
 * when {@link org.zkoss.zk.ui.event.OpenEvent#isOpen} is true, and/or
 * clean up if false.<br/>
 * onAfterRender is sent when the model's data has been rendered.(since 5.0.4)
 *
 * <p>Besides assign a list model, you could assign a renderer
 * (a {@link ComboitemRenderer} instance) to a combobox, such that
 * the combobox will use this renderer to render the data returned by 
 * {@link ListModel#getElementAt}.
 * If not assigned, the default renderer, which assumes a label per
 * combo item, is used.
 * In other words, the default renderer adds a label to
 * a row by calling toString against the object returned
 * by {@link ListModel#getElementAt}. (since 3.0.2)
 * 
 * <p>Note: to have better performance, onOpen is sent only if
 * a non-deferrable event listener is registered
 * (see {@link org.zkoss.zk.ui.event.Deferrable}).
 * 
 * <p>Like {@link Datebox},
 * the value of a read-only comobobox ({@link #isReadonly}) can be changed
 * by dropping down the list and selecting an combo item
 * (though users cannot type anything in the input box).
 *
 * @author tomyeh
 * @see Comboitem
 */
@SuppressWarnings("serial")
public class Combobox extends Textbox {
	private static final Logger log = LoggerFactory.getLogger(Combobox.class);
	private boolean _autodrop, _autocomplete = true, _btnVisible = true, _open;
	//Note: _selItem is maintained loosely, i.e., its value might not be correct
	//unless syncValueToSelection is called. So call getSelectedItem/getSelectedIndex
	//if you want the correct value
	private transient Comboitem _selItem;
	/** The last checked value for selected item.
	 * If null, it means syncValueToSelection is required.
	 */
	private transient String _lastCkVal;
	private ListModel<?> _model;
	/** The submodel used if _model is ListSubModel. */
	private Object[] _subModel;
	private ComboitemRenderer<?> _renderer;
	private transient ListDataListener _dataListener;
	private transient EventListener<InputEvent> _eventListener;
	/**Used to detect whether to sync Comboitem's index later. */
	private boolean _syncItemIndicesLater;
	private String _popupWidth;
	private String _emptySearchMessage;
	private boolean _instantSelect = true;
	private String _iconSclass = "z-icon-caret-down";

	private static final String ATTR_ON_INIT_RENDER = "org.zkoss.zul.Combobox.onInitRender";

	static {
		addClientEvent(Combobox.class, Events.ON_OPEN, CE_IMPORTANT | CE_DUPLICATE_IGNORE);
		addClientEvent(Combobox.class, Events.ON_SELECT, CE_IMPORTANT | CE_DUPLICATE_IGNORE);
	}

	public Combobox() {
	}

	public Combobox(String value) throws WrongValueException {
		this();
		setValue(value);
	}

	protected String coerceToString(Object value) {
		final Constraint constr = getConstraint();
		final String val = super.coerceToString(value);
		if (val.length() > 0 && constr != null && constr instanceof SimpleConstraint
				&& (((SimpleConstraint) constr).getFlags() & SimpleConstraint.STRICT) != 0) {
			for (Iterator<Comboitem> it = getItems().iterator(); it.hasNext();) {
				final String label = ((Comboitem) it.next()).getLabel();
				if (val.equalsIgnoreCase(label))
					return label;
			}
		}
		return val;
	}

	//-- ListModel dependent codes --//
	/** Returns the list model associated with this combobox, or null
	 * if this combobox is not associated with any list data model.
	 * <p> Note: for implementation of auto-complete, the result of {@link #getItemCount()} is a subset of model.
	 * So, if the model implemented {@link ListSubModel} interface, you can't use the index of model to find the comboitem by {@link #getItemAtIndex(int)}.
	 * @since 3.0.2
	 * @see ListSubModel#getSubModel(Object, int)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ListModel<T> getModel() {
		return (ListModel) _model;
	}

	/** Sets the list model associated with this combobox.
	 * If a non-null model is assigned, no matter whether it is the same as
	 * the previous, it will always cause re-render.
	 *
	 * @param model the list model to associate, or null to dissociate
	 * any previous model.
	 * @exception UiException if failed to initialize with the model
	 * @since 3.0.2
	 */
	public void setModel(ListModel<?> model) {
		if (model != null) {
			if (!(model instanceof Selectable))
				throw new UiException(model.getClass() + " must implement " + Selectable.class);

			if (_model != model) {
				if (_model != null) {
					_model.removeListDataListener(_dataListener);
				}
				// ZK-1702: do not clear Comboitems if using Data Binding 1
				if (_model != null
						&& !_model.getClass().getName().equals("org.zkoss.zkplus.databind.BindingListModelList")) {
					// Bug B60-ZK-1202.zul
					// Remove current items anyway, when changing models
					if (!getItems().isEmpty()) {
						getItems().clear();
					}
				}

				_model = model;
				_subModel = null; //clean up (generated later)
				initDataListener();
				setAttribute(Attributes.BEFORE_MODEL_ITEMS_RENDERED, Boolean.TRUE);
			}

			postOnInitRender(null);
			//Since user might setModel and setRender separately or repeatedly,
			//we don't handle it right now until the event processing phase
			//such that we won't render the same set of data twice
			//--
			//For better performance, we shall load the first few row now
			//(to save a round trip)
		} else if (_model != null) {
			_model.removeListDataListener(_dataListener);
			if (_model instanceof ListSubModel)
				removeEventListener(Events.ON_CHANGING, _eventListener);
			_model = null;
			_subModel = null;
			if (!getItems().isEmpty())
				getItems().clear();
		}
	}

	/** Sets empty search message.
	 * This message would be displayed, when no matching results was found.
	 *
	 * @param msg
	 * @since 8.5.1
	 */
	public void setEmptySearchMessage(String msg) {
		if (_emptySearchMessage != msg) {
			_emptySearchMessage = msg;
			smartUpdate("emptySearchMessage", _emptySearchMessage);
		}
	}

	private int INVALIDATE_THRESHOLD = -1;

	private void initDataListener() {
		if (INVALIDATE_THRESHOLD == -1) {
			INVALIDATE_THRESHOLD = Utils.getIntAttribute(this, "org.zkoss.zul.invalidateThreshold", 10, true);
		}
		if (_dataListener == null)
			_dataListener = new ListDataListener() {
				public void onChange(ListDataEvent event) {
					int type = event.getType();
					if (getAttribute(Attributes.BEFORE_MODEL_ITEMS_RENDERED) != null
							&& (type == ListDataEvent.INTERVAL_ADDED || type == ListDataEvent.INTERVAL_REMOVED))
						return;
					final ListModel _model = getModel();
					final int newsz = _model.getSize(), oldsz = getItemCount();
					int min = event.getIndex0(), max = event.getIndex1(), cnt;

					// Bug B30-1906748.zul
					switch (type) {
					case ListDataEvent.SELECTION_CHANGED:
						doSelectionChanged();
						return; //nothing changed so need to rerender
					case ListDataEvent.MULTIPLE_CHANGED:
						return; //nothing to do
					case ListDataEvent.INTERVAL_ADDED:
						cnt = newsz - oldsz;
						if (cnt < 0)
							throw new UiException("Adding causes a smaller list?");
						if (cnt == 0) //no change, nothing to do here
							return;
						if ((oldsz <= 0 || cnt > INVALIDATE_THRESHOLD) && !isOpen())//ZK-2704: don't invalidate when the combobox is open
							invalidate();
						//Also better performance (outer better than remove a lot)
						if (min < 0)
							if (max < 0)
								min = 0;
							else
								min = max - cnt + 1;
						if (min > oldsz)
							min = oldsz;

						final Renderer renderer = new Renderer();
						final Component next = min < oldsz ? getItemAtIndex(min) : null;
						int index = min;
						try {
							ComboitemRenderer cirenderer = null;
							while (--cnt >= 0) {
								if (cirenderer == null)
									cirenderer = (ComboitemRenderer) getRealRenderer();
								Comboitem item = newUnloadedItem(cirenderer);
								insertBefore(item, next);
								renderer.render(item, _model.getElementAt(index), index++);
							}
						} catch (Throwable ex) {
							renderer.doCatch(ex);
						} finally {
							renderer.doFinally();
						}
						break;
					case ListDataEvent.INTERVAL_REMOVED:
						cnt = oldsz - newsz;
						if (cnt < 0)
							throw new UiException("Removal causes a larger list?");
						if (cnt == 0) //no change, nothing to do here
							return;
						if (min >= 0)
							max = min + cnt - 1;
						else if (max < 0)
							max = cnt - 1; //0 ~ cnt - 1			
						if (max > oldsz - 1)
							max = oldsz - 1;

						if ((newsz <= 0 || cnt > INVALIDATE_THRESHOLD) && !isOpen())//ZK-2704: don't invalidate when the combobox is open
							invalidate();
						//Also better performance (outer better than remove a lot)

						//detach from end (due to groupfoot issue)
						Component comp = getItemAtIndex(max);
						while (--cnt >= 0) {
							Component p = comp.getPreviousSibling();
							comp.detach();
							comp = p;
						}
						break;
					default:
						postOnInitRender(null);
					}
				}
			};
		if (_eventListener == null)
			_eventListener = new EventListener<InputEvent>() {
				public void onEvent(InputEvent event) throws Exception {
					if (getModel() instanceof ListSubModel) {
						if (!event.isChangingBySelectBack())
							postOnInitRender(event.getValue());
					}
				}
			};

		_model.addListDataListener(_dataListener);
		if (_model instanceof ListSubModel)
			addEventListener(Events.ON_CHANGING, _eventListener);
	}

	private void doSelectionChanged() {
		final Selectable<Object> smodel = getSelectableModel();
		if (smodel.isSelectionEmpty()) {
			if (_selItem != null)
				setSelectedItem(null);
			return;
		}

		if (_selItem != null && smodel.isSelected(getElementAt(_selItem.getIndex())))
			return; //nothing changed

		int j = 0;
		for (final Comboitem item : getItems()) {
			if (smodel.isSelected(getElementAt(j++))) {
				setSelectedItem(item);
				return;
			}
		}

		//Possible to reach here if _model is ListSubModel, because
		//getText() might be different (so is the list of comboitems).
		if (_model instanceof ListSubModel) {
			//Unfortunately, we can't really fix it because the conversion from
			//Object to String is done by ComboitemRenderer
			//So, we only handle the very simple case
			//(though it could be wrong too -- at least less obvious to users)
			final Object selObj = smodel.getSelection().iterator().next();
			if (selObj instanceof String || selObj == null) {
				setValue((String) selObj);
				postOnInitRender(null); //cause Comboitem to be generated
			}
			return;
		}

		//don't call setSelectedItem(null) either. or, it will clear _value
	}

	private Object getElementAt(int index) {
		return _subModel != null ? _subModel[index] : _model.getElementAt(index);
	}

	@SuppressWarnings("unchecked")
	private Selectable<Object> getSelectableModel() {
		return (Selectable<Object>) _model;
	}

	/** Returns the renderer to render each row, or null if the default
	 * renderer is used.
	 * @since 3.0.2
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ComboitemRenderer<T> getItemRenderer() {
		return (ComboitemRenderer) _renderer;
	}

	/** Sets the renderer which is used to render each row
	 * if {@link #getModel} is not null.
	 *
	 * <p>Note: changing a render will not cause the combobox to re-render.
	 * If you want it to re-render, you could assign the same model again 
	 * (i.e., setModel(getModel())), or fire an {@link ListDataEvent} event.
	 *
	 * @param renderer the renderer, or null to use the default.
	 * @exception UiException if failed to initialize with the model
	 * @since 3.0.2
	 */
	public void setItemRenderer(ComboitemRenderer<?> renderer) {
		_renderer = renderer;
	}

	/** Sets the renderer by use of a class name.
	 * It creates an instance automatically.
	 *@since 3.0.2
	 */
	@SuppressWarnings("rawtypes")
	public void setItemRenderer(String clsnm) throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null)
			setItemRenderer((ComboitemRenderer) Classes.newInstanceByThread(clsnm));
	}

	/** Synchronizes the combobox to be consistent with the specified model.
	 */
	private ListModel<?> syncModel(Object index) {
		ComboitemRenderer<?> renderer = null;
		final ListModel<?> subset = _model instanceof ListSubModel ? ((ListSubModel<?>) _model).getSubModel(index, -1)
				: _model;
		final int newsz = subset.getSize();

		if (!getItems().isEmpty())
			getItems().clear();

		for (int j = 0; j < newsz; ++j) {
			if (renderer == null)
				renderer = getRealRenderer();
			newUnloadedItem(renderer).setParent(this);
		}
		return subset;
	}

	/** Creates an new and unloaded Comboitem. */
	private Comboitem newUnloadedItem(ComboitemRenderer<?> renderer) {
		Comboitem item = null;
		if (renderer instanceof ComboitemRendererExt)
			item = ((ComboitemRendererExt) renderer).newComboitem(this);

		if (item == null) {
			item = new Comboitem();
			item.applyProperties();
		}
		return item;
	}

	/** Handles a private event, onInitRender. It is used only for
	 * implementation, and you rarely need to invoke it explicitly.
	 * @since 3.0.2
	 */
	@SuppressWarnings("rawtypes")
	public void onInitRender(Event data) {
		//Bug #2010389
		removeAttribute(ATTR_ON_INIT_RENDER); //clear syncModel flag
		final Renderer renderer = new Renderer();
		final List<Object> subModel = _model instanceof ListSubModel ? new ArrayList<Object>() : null;
		final ListModel subset = syncModel(data.getData() != null ? data.getData() : getRawText());
		try {
			int pgsz = subset.getSize(), ofs = 0, j = 0;
			for (Comboitem item = getItems().size() <= ofs ? null : getItems().get(ofs), nxt; j < pgsz
					&& item != null; ++j, item = nxt) {
				nxt = (Comboitem) item.getNextSibling(); //store it first
				final int index = j + ofs;
				final Object value = subset.getElementAt(index);
				if (subModel != null)
					subModel.add(value);
				renderer.render(item, value, index);
			}
			if (subModel != null)
				_subModel = subModel.toArray(new Object[subModel.size()]);
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
		Events.postEvent("onInitRenderLater", this, null); // notify databinding load-when. 
		Events.postEvent(ZulEvents.ON_AFTER_RENDER, this, null); // notify the combobox when items have been rendered. 
		removeAttribute(Attributes.BEFORE_MODEL_ITEMS_RENDERED);
	}

	private void postOnInitRender(String idx) {
		//20080724, Henri Chen: optimize to avoid postOnInitRender twice
		if (getAttribute(ATTR_ON_INIT_RENDER) == null) {
			//Bug #2010389
			setAttribute(ATTR_ON_INIT_RENDER, Boolean.TRUE); //flag syncModel
			Events.postEvent("onInitRender", this, idx);
		}
	}

	@SuppressWarnings("rawtypes")
	private static final ComboitemRenderer _defRend = new ComboitemRenderer() {
		public void render(final Comboitem item, final Object data, final int index) {
			final Combobox cb = (Combobox) item.getParent();
			final Template tm = cb.getTemplate("model");
			if (tm == null) {
				item.setLabel(Objects.toString(data));
				item.setValue(data);
			} else {
				final Component[] items = ShadowElementsCtrl
						.filterOutShadows(tm.create(item.getParent(), item, new VariableResolver() {
					public Object resolveVariable(String name) {
						if ("each".equals(name)) {
							return data;
						} else if ("forEachStatus".equals(name)) {
							return new ForEachStatus() {

								public ForEachStatus getPrevious() {
									return null;
								}

								public Object getEach() {
									return getCurrent();
								}

								public int getIndex() {
									return index;
								}

								public Integer getBegin() {
									return 0;
								}

								public Integer getEnd() {
									return cb.getModel().getSize();
								}

								public Object getCurrent() {
									return data;
								}

								public boolean isFirst() {
									return getCount() == 1;
								}

								public boolean isLast() {
									return getIndex() + 1 == getEnd();
								}

								public Integer getStep() {
									return null;
								}

								public int getCount() {
									return getIndex() + 1;
								}
							};
						} else {
							return null;
						}
					}
				}, null));
				if (items.length != 1)
					throw new UiException("The model template must have exactly one item, not " + items.length);

				final Comboitem nci = (Comboitem) items[0];
				if (nci.getValue() == null) //template might set it
					nci.setValue(data);
				item.setAttribute(Attributes.MODEL_RENDERAS, nci);
				//indicate a new item is created to replace the existent one
				item.detach();
			}
		}
	};

	/** Returns the renderer used to render items.
	 */
	@SuppressWarnings("unchecked")
	private <T> ComboitemRenderer<T> getRealRenderer() {
		return _renderer != null ? (ComboitemRenderer<T>) _renderer : _defRend;
	}

	/** Used to render comboitem if _model is specified. */
	private class Renderer implements java.io.Serializable {
		@SuppressWarnings("rawtypes")
		private final ComboitemRenderer _renderer;
		private boolean _rendered, _ctrled;

		private Renderer() {
			_renderer = getRealRenderer();
		}

		@SuppressWarnings("unchecked")
		private void render(Comboitem item, Object value, int index) throws Throwable {
			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl) _renderer).doTry();
				_ctrled = true;
			}

			try {
				try {
					_renderer.render(item, value, index);
				} catch (AbstractMethodError ex) {
					final Method m = _renderer.getClass().getMethod("render",
							new Class<?>[] { Comboitem.class, Object.class });
					m.setAccessible(true);
					m.invoke(_renderer, new Object[] { item, value });
				}
				Object v = item.getAttribute(Attributes.MODEL_RENDERAS);
				if (v != null) //a new item is created to replace the existent one
					item = (Comboitem) v;
			} catch (Throwable ex) {
				try {
					item.setLabel(Exceptions.getMessage(ex));
				} catch (Throwable t) {
					log.error("", t);
				}
				throw ex;
			}
			if (getSelectableModel().isSelected(value))
				setSelectedItem(item);
			_rendered = true;
		}

		private void doCatch(Throwable ex) {
			if (_ctrled) {
				try {
					((RendererCtrl) _renderer).doCatch(ex);
				} catch (Throwable t) {
					throw UiException.Aide.wrap(t);
				}
			} else {
				throw UiException.Aide.wrap(ex);
			}
		}

		private void doFinally() {
			if (_ctrled)
				((RendererCtrl) _renderer).doFinally();
		}
	}

	/** Returns whether to automatically drop the list if users is changing
	 * this text box.
	 * <p>Default: false.
	 */
	public boolean isAutodrop() {
		return _autodrop;
	}

	/** Sets whether to automatically drop the list if users is changing
	 * this text box.
	 */
	public void setAutodrop(boolean autodrop) {
		if (_autodrop != autodrop) {
			_autodrop = autodrop;
			smartUpdate("autodrop", autodrop);
		}
	}

	/** Returns whether to automatically complete this text box
	 * by matching the nearest item ({@link Comboitem}.
	 * It is also known as auto-type-ahead.
	 *
	 * <p>Default: true (since 5.0.0).
	 *
	 * <p>If true, the nearest item will be searched and the text box is
	 * updated automatically.
	 * If false, user has to click the item or use the DOWN or UP keys to
	 * select it back.
	 *
	 * <p>Don't confuse it with the auto-completion feature mentioned by
	 * other framework. Such kind of auto-completion is supported well
	 * by listening to the onChanging event.
	 */
	public boolean isAutocomplete() {
		return _autocomplete;
	}

	/** Sets whether to automatically complete this text box
	 * by matching the nearest item ({@link Comboitem}.
	 */
	public void setAutocomplete(boolean autocomplete) {
		if (_autocomplete != autocomplete) {
			_autocomplete = autocomplete;
			smartUpdate("autocomplete", autocomplete);
		}
	}

	/** Returns whether this combobox is open.
	 *
	 * <p>Default: false.
	 * @since 6.0.0
	 */
	public boolean isOpen() {
		return _open;
	}

	/** Drops down or closes the list of combo items ({@link Comboitem}.
	 * only works while visible
	 * @since 3.0.1
	 * @see #open
	 * @see #close
	 */
	public void setOpen(boolean open) {
		if (isVisible()) {
			if (open)
				open();
			else
				close();
		}
	}

	/** Drops down the list of combo items ({@link Comboitem}.
	 * It is the same as setOpen(true).
	 *
	 * @since 3.0.1
	 */
	public void open() {
		_open = true;
		response("open", new AuInvoke(this, "setOpen", true)); //don't use smartUpdate
	}

	/** Closes the list of combo items ({@link Comboitem} if it was
	 * dropped down.
	 * It is the same as setOpen(false).
	 *
	 * @since 3.0.1
	 */
	public void close() {
		_open = false;
		response("open", new AuInvoke(this, "setOpen", false)); //don't use smartUpdate
	}

	/** Returns whether the button (on the right of the textbox) is visible.
	 * <p>Default: true.
	 */
	public boolean isButtonVisible() {
		return _btnVisible;
	}

	/** Sets whether the button (on the right of the textbox) is visible.
	 */
	public void setButtonVisible(boolean visible) {
		if (_btnVisible != visible) {
			_btnVisible = visible;
			smartUpdate("buttonVisible", visible);
		}
	}

	/**
	 * Returns true if onSelect event is sent as soon as user selects using keyboard navigation.
	 * <p>Default: true
	 *
	 * @since 8.6.1
	 */
	public boolean isInstantSelect() {
		return _instantSelect;
	}

	/**
	 * Sets the instantSelect attribute. When the attribute is true, onSelect event
	 * will be fired as soon as user selects using keyboard navigation.
	 *
	 * If the attribute is false, user needs to press Enter key to finish the selection using keyboard navigation.
	 *
	 * @since 8.6.1
	 */
	public void setInstantSelect(boolean instantSelect) {
		if (_instantSelect != instantSelect) {
			_instantSelect = instantSelect;
			smartUpdate("instantSelect", instantSelect);
		}
	}

	/** Returns a 'live' list of all {@link Comboitem}.
	 * By live we mean you can add or remove them directly with
	 * the List interface.
	 *
	 * <p>Currently, it is the same as {@link #getChildren}. However,
	 * we might add other kind of children in the future.
	 */
	public List<Comboitem> getItems() {
		return cast(getChildren());
	}

	/** Returns the number of items.
	 */
	public int getItemCount() {
		return getItems().size();
	}

	/** Returns the item at the specified index.
	 */
	public Comboitem getItemAtIndex(int index) {
		return getItems().get(index);
	}

	/** Appends an item.
	 */
	public Comboitem appendItem(String label) {
		final Comboitem item = new Comboitem(label);
		item.setParent(this);
		return item;
	}

	/**  Removes the child item in the list box at the given index.
	 * @return the removed item.
	 */
	public Comboitem removeItemAt(int index) {
		final Comboitem item = getItemAtIndex(index);
		removeChild(item);
		return item;
	}

	/** Returns the selected item.
	 * @since 2.4.0
	 */
	public Comboitem getSelectedItem() {
		syncValueToSelection();
		return _selItem;
	}

	/**  Deselects the currently selected items and selects the given item.
	 * <p>Note: if the label of comboitem has the same more than one, the first 
	 * comboitem will be selected at client side, it is a limitation of {@link Combobox}
	 * and it is different from {@link Listbox}.</p>
	 * @since 3.0.2
	 */
	public void setSelectedItem(Comboitem item) {
		if (item != null && item.getParent() != this)
			throw new UiException("Not a child: " + item);

		if (item != _selItem) {
			_selItem = item;
			if (item != null) {
				setValue(item.getLabel());
				smartUpdate("selectedItemUuid_", item.getUuid());
			} else {
				//Bug#2919037: don't call setRawValue(), or the error message will be cleared
				if (_value != null && !"".equals(_value)) {
					_value = "";
					smartUpdate("value", coerceToString(_value));
				}
			}
			_lastCkVal = getValue();
		}
	}

	/** Deselects the currently selected items and selects
	 * the item with the given index.
	 * <p>Note: if the label of comboitem has the same more than one, the first 
	 * comboitem will be selected at client side, it is a limitation of {@link Combobox}
	 * and it is different from {@link Listbox}.</p>
	 * @since 3.0.2
	 */
	public void setSelectedIndex(int jsel) {
		if (jsel >= getItemCount())
			throw new UiException("Out of bound: " + jsel + " while size=" + getItemCount());
		if (jsel < -1)
			jsel = -1;
		setSelectedItem(jsel >= 0 ? getItemAtIndex(jsel) : null);
		//Bug#2919037: setSelectedIndex(-1) shall unselect even with constraint
	}

	/** Returns the index of the selected item, or -1 if not selected.
	 * @since 3.0.1
	 */
	public int getSelectedIndex() {
		syncValueToSelection();
		return _selItem != null ? _selItem.getIndex() : -1;
	}

	/**
	 * @return the width of the popup of this component
	 * @since 8.0.3
     */
	public String getPopupWidth() {
		return _popupWidth;
	}

	/**
	 * Sets the width of the popup of this component.
	 * If the input is a percentage, the popup width will be calculated by multiplying the width of this component with the percentage.
	 * (e.g. if the input string is 130%, and the width of this component is 300px, the popup width will be 390px = 300px * 130%)
	 * Others will be set directly.
	 * @param popupWidth the width of the popup of this component
	 * @since 8.0.3
     */
	public void setPopupWidth(String popupWidth) {
		if (popupWidth != _popupWidth) {
			_popupWidth = popupWidth;
			smartUpdate("popupWidth", popupWidth);
		}
	}

	//-- super --//
	public void setMultiline(boolean multiline) {
		if (multiline)
			throw new UnsupportedOperationException("Combobox doesn't support multiline");
	}

	public void setRows(int rows) {
		if (rows != 1)
			throw new UnsupportedOperationException("Combobox doesn't support multiple rows, " + rows);
	}

	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}

	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 *
	 * <p>If a component requires more client controls, it is suggested to
	 * override {@link #getExtraCtrl} to return an instance that extends from
	 * this class.
	 */
	protected class ExtraCtrl extends Textbox.ExtraCtrl implements Blockable {
		public boolean shallBlock(AuRequest request) {
			// B50-3316103: special case of readonly component: do not block onChange and onSelect
			final String cmd = request.getCommand();
			if (Events.ON_OPEN.equals(cmd))
				return false;
			return isDisabled() || (isReadonly() && Events.ON_CHANGING.equals(cmd))
					|| !Components.isRealVisible(Combobox.this);
		}
	}

	private void syncSelectionToModel() {
		if (_model != null) {
			List<Object> selObjs = new ArrayList<Object>();
			if (_selItem != null)
				selObjs.add(getElementAt(_selItem.getIndex()));
			getSelectableModel().setSelection(selObjs);
		}
	}

	/**
	 * Sets the iconSclass name of this Combobox.
	 * @param iconSclass String
	 * @since 8.6.2
	 */
	public void setIconSclass(String iconSclass) {
		if (!Objects.equals(_iconSclass, iconSclass)) {
			_iconSclass = iconSclass;
			smartUpdate("iconSclass", iconSclass);
		}
	}

	/**
	 * Returns the iconSclass name of this Combobox.
	 * @return the iconSclass name
	 * @since 8.6.2
	 */
	public String getIconSclass() {
		return _iconSclass;
	}

	// super
	public String getZclass() {
		return _zclass == null ? "z-combobox" : _zclass;
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "autodrop", _autodrop);
		if (!_autocomplete)
			renderer.render("autocomplete", false);
		if (!_btnVisible)
			renderer.render("buttonVisible", false);
		if (_selItem != null)
			renderer.render("selectedItemUuid_", _selItem.getUuid());
		if (_popupWidth != null)
			renderer.render("popupWidth", _popupWidth);
		if (_emptySearchMessage != null)
			renderer.render("emptySearchMessage", _emptySearchMessage);
		if (!_instantSelect)
			renderer.render("instantSelect", false);
		if (!"z-icon-caret-down".equals(_iconSclass))
			renderer.render("iconSclass", _iconSclass);
	}

	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link Textbox#service},
	 * it also handles onOpen and onSelect.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_OPEN)) {
			OpenEvent evt = OpenEvent.getOpenEvent(request);
			_open = evt.isOpen();
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_SELECT)) {
			final Set<Comboitem> prevSelectedItems = new LinkedHashSet<Comboitem>();
			Comboitem prevSeld = (Comboitem) request.getDesktop()
					.getComponentByUuidIfAny((String) request.getData().get("prevSeld"));
			// ZK-2089: should skip when selected item is null 
			if (prevSeld != null)
				prevSelectedItems.add(prevSeld);
			SelectEvent<Comboitem, Object> evt = SelectEvent.getSelectEvent(request,
					new SelectEvent.SelectedObjectHandler<Comboitem>() {
						public Set<Object> getObjects(Set<Comboitem> items) {
							if (items == null || items.isEmpty() || _model == null)
								return null;
							Set<Object> objs = new LinkedHashSet<Object>();
							// ZK-5047: we cannot use index here (if it's ListSubModel case)
							if (_model instanceof ListSubModel) {
								for (Comboitem i : items) {
									objs.add(i.getValue());
								}
							} else {
								for (Comboitem i : items) {
									objs.add(_model.getElementAt(i.getIndex()));
								}
							}
							return objs;
						}

						public Set<Comboitem> getPreviousSelectedItems() {
							return prevSelectedItems;
						}

						// in single selection, getPreviousSelectedItems() is same as getPreviousSelectedItems()
						public Set<Comboitem> getUnselectedItems() {
							return getPreviousSelectedItems();
						}

						public Set<Object> getPreviousSelectedObjects() {
							Set<Comboitem> items = getPreviousSelectedItems();
							if (_model == null || items.size() < 1)
								return null;
							else {
								Set<Object> s = new LinkedHashSet<Object>();
								s.add(_model.getElementAt(((Comboitem) items.iterator().next()).getIndex()));
								return s;
							}
						}

						// in single selection, getUnselectedObjects() is same as getPreviousSelectedObjects()
						public Set<Object> getUnselectedObjects() {
							return getPreviousSelectedObjects();
						}
					});
			Comboitem oldSel = _selItem;
			Set<Comboitem> selItems = evt.getSelectedItems();
			_selItem = selItems != null && !selItems.isEmpty() ? (Comboitem) selItems.iterator().next() : null;
			_lastCkVal = getValue(); //onChange is sent before onSelect

			syncSelectionToModel();
			//ZK-1987: Combobox item selection rely items label string
			String val = _lastCkVal;
			if (oldSel != null && !oldSel.equals(_selItem) && oldSel.getLabel().equals(val))
				Events.postEvent(new InputEvent(Events.ON_CHANGE, this, val, val));
			Events.postEvent(evt);
		} else if (cmd.equals(Events.ON_CHANGE)) {
			super.service(request, everError);
			// Bug ZK-1492: synchronize the input value to selection
			syncValueToSelection();
		} else
			super.service(request, everError);
	}

	//-- Component --//
	public void beforeChildAdded(Component newChild, Component refChild) {
		if (!(newChild instanceof Comboitem))
			throw new UiException("Unsupported child for Combobox: " + newChild);
		super.beforeChildAdded(newChild, refChild);
	}

	/** Childable. */
	protected boolean isChildable() {
		return true;
	}

	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		_syncItemIndicesLater = true;
		smartUpdate("repos", true);
	}

	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		_syncItemIndicesLater = true;
		if (child == _selItem) {
			// Bug B60-ZK-1202.zul
			_selItem = null;
			schedSyncValueToSelection();
		}
		smartUpdate("repos", true);
	}

	/*package*/ void syncItemIndices() { //called by Comboitem
		if (_syncItemIndicesLater) {
			_syncItemIndicesLater = false;
			int j = 0;
			for (final Comboitem item : getItems())
				item.setIndexDirectly(j++);
		}
	}

	private void syncValueToSelection() {
		final String value = getValue();
		if (!Objects.equals(_lastCkVal, value)) {
			_lastCkVal = value;
			_selItem = null;
			for (final Comboitem item : getItems()) {
				if (Objects.equals(value, item.getLabel())) {
					_selItem = item;
					break;
				}
			}
			syncSelectionToModel();
		}
	}

	/*package*/ void schedSyncValueToSelection() {
		_lastCkVal = null;
	}

	/*package*/ Comboitem getSelectedItemDirectly() {
		return _selItem;
	}

	//Cloneable//
	@SuppressWarnings("rawtypes")
	public Object clone() {
		final Combobox clone = (Combobox) super.clone();
		clone._selItem = null;
		clone.schedSyncValueToSelection();
		if (clone._model != null) {
			if (clone._model instanceof ComponentCloneListener) {
				final ListModel model = (ListModel) ((ComponentCloneListener) clone._model).willClone(clone);
				if (model != null)
					clone._model = model;
			}
			clone._dataListener = null;
			clone._eventListener = null;
			clone.initDataListener();
		}
		return clone;
	}

	//	Serializable//
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		schedSyncValueToSelection();
		if (_model != null) {
			initDataListener();

			// Map#Entry cannot be serialized, we have to restore them
			if (_model instanceof ListModelMap) {
				for (final Comboitem item : getItems()) {
					item.setValue(getElementAt(item.getIndex()));
				}
			}
		}
	}

	public void sessionWillPassivate(Page page) {
		super.sessionWillPassivate(page);
		willPassivate(_model);
		willPassivate(_renderer);
	}

	public void sessionDidActivate(Page page) {
		super.sessionDidActivate(page);
		didActivate(_model);
		didActivate(_renderer);
	}

	//--ComponentCtrl--//
	private static HashMap<String, PropertyAccess> _properties = new HashMap<String, PropertyAccess>(3);

	static {
		_properties.put("buttonVisible", new BooleanPropertyAccess() {
			public void setValue(Component cmp, Boolean value) {
				((Combobox) cmp).setButtonVisible(value);
			}

			public Boolean getValue(Component cmp) {
				return ((Combobox) cmp).isButtonVisible();
			}
		});
		_properties.put("autocomplete", new BooleanPropertyAccess() {
			public void setValue(Component cmp, Boolean value) {
				((Combobox) cmp).setAutocomplete(value);
			}

			public Boolean getValue(Component cmp) {
				return ((Combobox) cmp).isAutocomplete();
			}
		});
		_properties.put("autodrop", new BooleanPropertyAccess() {
			public void setValue(Component cmp, Boolean value) {
				((Combobox) cmp).setAutodrop(value);
			}

			public Boolean getValue(Component cmp) {
				return ((Combobox) cmp).isAutodrop();
			}
		});
		_properties.put("instantSelect", new BooleanPropertyAccess() {
			public void setValue(Component cmp, Boolean value) {
				((Combobox) cmp).setInstantSelect(value);
			}

			public Boolean getValue(Component cmp) {
				return ((Combobox) cmp).isInstantSelect();
			}
		});
	}

	public PropertyAccess getPropertyAccess(String prop) {
		PropertyAccess pa = _properties.get(prop);
		if (pa != null)
			return pa;
		return super.getPropertyAccess(prop);
	}

	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		if (_model != null) {
			postOnInitRender(null);
			if (_dataListener != null) {
				_model.removeListDataListener(_dataListener);
				_model.addListDataListener(_dataListener);
			}
		}
	}

	public void onPageDetached(Page page) {
		super.onPageDetached(page);
		if (_model != null && _dataListener != null)
			_model.removeListDataListener(_dataListener);
	}
}
