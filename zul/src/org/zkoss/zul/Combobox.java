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

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import static org.zkoss.lang.Generics.cast;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Objects;
import org.zkoss.util.logging.Log;
import org.zkoss.xel.VariableResolver;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zk.ui.util.ForEachStatus;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.ext.Blockable;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.ext.Selectable;

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
public class Combobox extends Textbox {
	private static final Log log = Log.lookup(Combobox.class);
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
	private ComboitemRenderer<?>_renderer;
	private transient ListDataListener _dataListener;
	private transient EventListener<InputEvent> _eventListener;
	/**Used to detect whether to syn Comboitem's index later. */
	private boolean _syncItemIndicesLater;

	static {
		addClientEvent(Combobox.class, Events.ON_OPEN, CE_DUPLICATE_IGNORE);
		addClientEvent(Combobox.class, Events.ON_SELECT, CE_IMPORTANT|CE_DUPLICATE_IGNORE);
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
		if (val.length() > 0 && constr != null && 
				constr instanceof SimpleConstraint && (((SimpleConstraint)constr)
						.getFlags() & SimpleConstraint.STRICT) != 0) {
			for (Iterator it = getItems().iterator(); it.hasNext();) {
				final String label = ((Comboitem)it.next()).getLabel();
				if(val.equalsIgnoreCase(label))
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
	@SuppressWarnings("unchecked")
	public <T> ListModel<T> getModel() {
		return (ListModel)_model;
	}
	/** Sets the list model associated with this combobox.
	 * If a non-null model is assigned, no matter whether it is the same as
	 * the previous, it will always cause re-render.
	 *
	 * @param model the list model to associate, or null to dis-associate
	 * any previous model.
	 * @exception UiException if failed to initialize with the model
	 * @since 3.0.2
	 */
	public void setModel(ListModel<?> model) {
		if (model != null) {
			if (!(model instanceof Selectable))
				throw new UiException(model.getClass() + " must implement "+Selectable.class);

			if (_model != model) {
				if (_model != null) {
					_model.removeListDataListener(_dataListener);
				} else if (!getItems().isEmpty()) getItems().clear();
				_model = model;
				_subModel = null; //clean up (generated later)
				initDataListener();
			}

			postOnInitRender(null);
			//Since user might setModel and setRender separately or repeatedly,
			//we don't handle it right now until the event processing phase
			//such that we won't render the same set of data twice
			//--
			//For better performance, we shall load the first few row now
			//(to save a roundtrip)
		} else if (_model != null) {
			_model.removeListDataListener(_dataListener);
			if (_model instanceof ListSubModel)
				removeEventListener(Events.ON_CHANGING, _eventListener);
			_model = null;
			_subModel = null;
			if (!getItems().isEmpty()) getItems().clear();
		}
	}
	
	private void initDataListener() {
		if (_dataListener == null)
			_dataListener = new ListDataListener() {
				public void onChange(ListDataEvent event) {
					// Bug B30-1906748.zul
					switch (event.getType()) {
					case ListDataEvent.SELECTION_CHANGED:
						doSelectionChanged();
						return; //nothing changed so need to rerender
					case ListDataEvent.MULTIPLE_CHANGED:
						return; //nothing to do
					}
					postOnInitRender(null);
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

		if (_selItem != null
		&& smodel.isSelected(getElementAt(_selItem.getIndex())))
			return; //nothing changed

		int j = 0;
		for (final Comboitem item: getItems()) {
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
				setValue((String)selObj);
				postOnInitRender(null); //cause Comboitem to be generated
			}
			return;
		}

		//don't call setSelectedItem(null) either. or, it will clear _value
	}
	private Object getElementAt(int index) {
		return _subModel != null ? _subModel[index]: _model.getElementAt(index);
	}
	@SuppressWarnings("unchecked")
	private Selectable<Object> getSelectableModel() {
		return (Selectable<Object>)_model;
	}

	/** Returns the renderer to render each row, or null if the default
	 * renderer is used.
	 * @since 3.0.2
	 */
	@SuppressWarnings("unchecked")
	public <T> ComboitemRenderer<T> getItemRenderer() {
		return (ComboitemRenderer)_renderer;
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
	public void setItemRenderer(String clsnm)
	throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
	InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null)
			setItemRenderer((ComboitemRenderer)Classes.newInstanceByThread(clsnm));
	}
	
	/** Synchronizes the combobox to be consistent with the specified model.
	 */
	private ListModel<?> syncModel(Object index) {
		ComboitemRenderer<?> renderer = null;
		final ListModel<?> subset = _model instanceof ListSubModel ? 
			((ListSubModel<?>)_model).getSubModel(index, -1) : _model;
		final int newsz = subset.getSize();

		if (!getItems().isEmpty()) getItems().clear();
		
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
			item = ((ComboitemRendererExt)renderer).newComboitem(this);

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
	public void onInitRender(Event data) {
  		//Bug #2010389
		removeAttribute("zul.Combobox.ON_INITRENDER"); //clear syncModel flag
		final Renderer renderer = new Renderer();
		final List<Object> subModel =
			_model instanceof ListSubModel ? new ArrayList<Object>(): null;
		final ListModel subset = syncModel(data.getData() != null ? 
			data.getData() : getRawText());
		try {
			int pgsz = subset.getSize(), ofs = 0, j = 0;
			for (Comboitem item = getItems().size() <= ofs ? null: getItems().get(ofs), nxt;
			j < pgsz && item != null; ++j, item = nxt) {
				nxt = (Comboitem)item.getNextSibling(); //store it first
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
		Events.postEvent("onInitRenderLater", this, null);// notify databinding load-when. 
		Events.postEvent(ZulEvents.ON_AFTER_RENDER, this, null);// notify the combobox when items have been rendered. 
	}
	
	private void postOnInitRender(String idx) {
		//20080724, Henri Chen: optimize to avoid postOnInitRender twice
		if (getAttribute("zul.Combobox.ON_INITRENDER") == null) {
	  		//Bug #2010389
			setAttribute("zul.Combobox.ON_INITRENDER", Boolean.TRUE); //flag syncModel
			Events.postEvent("onInitRender", this, idx);
		}
	}

	private static final ComboitemRenderer _defRend = new ComboitemRenderer() {
		public void render(final Comboitem item, final Object data, final int index) {
			final Combobox cb = (Combobox)item.getParent();
			final Template tm = cb.getTemplate("model");
			if (tm == null) {
				item.setLabel(Objects.toString(data));
				item.setValue(data);
			} else {
				final Component[] items = tm.create(item.getParent(), item,
					new VariableResolver() {
						public Object resolveVariable(String name) {
							if ("each".equals(name)) {
								return data;
							} else if ("forEachStatus".equals(name)) {
								return new ForEachStatus() {
									@Override
									public ForEachStatus getPrevious() {
										return null;
									}
									@Override
									public Object getEach() {
										return data;
									}
									@Override
									public int getIndex() {
										return index;
									}
									@Override
									public Integer getBegin() {
										return 0;
									}
									@Override
									public Integer getEnd() {
										return cb.getModel().getSize();
									}
								};
							} else {
								return null;
							}
						}
					}, null);
				if (items.length != 1)
					throw new UiException("The model template must have exactly one item, not "+items.length);

				final Comboitem nci = (Comboitem)items[0];
				if (nci.getValue() == null) //template might set it
					nci.setValue(data);
				item.setAttribute("org.zkoss.zul.model.renderAs", nci);
					//indicate a new item is created to replace the existent one
				item.detach();
			}
		}
	};
	/** Returns the renderer used to render items.
	 */
	@SuppressWarnings("unchecked")
	private <T> ComboitemRenderer<T> getRealRenderer() {
		return _renderer != null ? _renderer: _defRend;
	}

	/** Used to render comboitem if _model is specified. */
	private class Renderer implements java.io.Serializable {
		private final ComboitemRenderer _renderer;
		private boolean _rendered, _ctrled;

		private Renderer() {
			_renderer = getRealRenderer();
		}
		@SuppressWarnings("unchecked")
		private void render(Comboitem item, Object value, int index)
		throws Throwable {
			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl)_renderer).doTry();
				_ctrled = true;
			}

			try {
				try {
					_renderer.render(item, value, index);
				} catch (AbstractMethodError ex) {
					final Method m = _renderer.getClass()
						.getMethod("render", new Class<?>[] {Comboitem.class, Object.class});
					m.setAccessible(true);
					m.invoke(_renderer, new Object[] {item, value});
				}
				Object v = item.getAttribute("org.zkoss.zul.model.renderAs");
				if (v != null) //a new item is created to replace the existent one
					item = (Comboitem)v;
			} catch (Throwable ex) {
				try {
					item.setLabel(Exceptions.getMessage(ex));
				} catch (Throwable t) {
					log.error(t);
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
					((RendererCtrl)_renderer).doCatch(ex);
				} catch (Throwable t) {
					throw UiException.Aide.wrap(t);
				}
			} else {
				throw UiException.Aide.wrap(ex);
			}
		}
		private void doFinally() {
			if (_ctrled)
				((RendererCtrl)_renderer).doFinally();
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
		if (_open != open) {
			_open = open;
			if (isVisible()) {
				if (open) open();
				else close();
			}
		}
	}
	/** Drops down the list of combo items ({@link Comboitem}.
	 * It is the same as setOpen(true).
	 *
	 * @since 3.0.1
	 */
	public void open() {
		response("open", new AuInvoke(this, "setOpen", true)); //don't use smartUpdate
	}
	/** Closes the list of combo items ({@link Comboitem} if it was
	 * dropped down.
	 * It is the same as setOpen(false).
	 *
	 * @since 3.0.1
	 */
	public void close() {
		response("open", new AuInvoke(this, "setOpen", false));//don't use smartUpdate
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
			throw new UiException("Not a child: "+item);

		if (item != _selItem) {
			_selItem = item;
			if (item != null) {
				setValue(item.getLabel());
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
			throw new UiException("Out of bound: "+jsel+" while size="+getItemCount());
		if (jsel < -1) 
			jsel = -1;
		setSelectedItem(jsel >= 0 ? getItemAtIndex(jsel): null);
			//Bug#2919037: setSelectedIndex(-1) shall unselect even with constraint
	}
	
	/** Returns the index of the selected item, or -1 if not selected.
	 * @since 3.0.1
	 */
	public int getSelectedIndex() {
		syncValueToSelection();
		return _selItem != null ? _selItem.getIndex() : -1;
	}

	//-- super --//
	public void setMultiline(boolean multiline) {
		if (multiline)
			throw new UnsupportedOperationException("Combobox doesn't support multiline");
	}
	public void setRows(int rows) {
		if (rows != 1)
			throw new UnsupportedOperationException("Combobox doesn't support multiple rows, "+rows);
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
			if(Events.ON_OPEN.equals(cmd))
				return false;
			return !Components.isRealVisible(Combobox.this) || isDisabled() || 
				(isReadonly() && Events.ON_CHANGING.equals(cmd));
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

	// super
	public String getZclass() {
		return _zclass == null ? "z-combobox" : _zclass;
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "autodrop", _autodrop);
		if (!_autocomplete)
			renderer.render("autocomplete", false);
		if (!_btnVisible)
			renderer.render("buttonVisible", false);
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
			SelectEvent evt = SelectEvent.getSelectEvent(request, 
					new SelectEvent.SelectedObjectHandler<Comboitem>() {
				public Set<Object> getObjects(Set<Comboitem> items) {
					if (items == null || items.isEmpty() || _model == null)
						return null;
					Set<Object> objs = new LinkedHashSet<Object>();
					for (Comboitem i : items)
						objs.add(_model.getElementAt(i.getIndex()));
					return objs;
				}
			});
			Set selItems = evt.getSelectedItems();
			_selItem = selItems != null && !selItems.isEmpty()?
				(Comboitem)selItems.iterator().next(): null;
			_lastCkVal = getValue(); //onChange is sent before onSelect

			syncSelectionToModel();
			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}

	//-- Component --//
	public void beforeChildAdded(Component newChild, Component refChild) {
		if (!(newChild instanceof Comboitem))
			throw new UiException("Unsupported child for Combobox: "+newChild);
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
		if (child == _selItem)
			schedSyncValueToSelection();
		smartUpdate("repos", true);
	}
	
	/*package*/ void syncItemIndices() { //called by Comboitem
		if (_syncItemIndicesLater) {
			_syncItemIndicesLater = false;
			int j = 0;
			for (final Comboitem item: getItems())
				item.setIndexDirectly(j++);
		}
	}

	private void syncValueToSelection() {
		final String value = getValue();
		if (!Objects.equals(_lastCkVal, value)) {
			_lastCkVal = value;
			_selItem = null;
			for (final Comboitem item: getItems()) {
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
	public Object clone() {
		final Combobox clone = (Combobox)super.clone();
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
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
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

	@Override
	public void sessionWillPassivate(Page page) {
		super.sessionWillPassivate(page);
		willPassivate(_model);
		willPassivate(_renderer);
	}
	@Override
	public void sessionDidActivate(Page page) {
		super.sessionDidActivate(page);
		didActivate(_model);
		didActivate(_renderer);
	}
}
