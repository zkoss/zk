/* Radiogroup.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 09:20:41     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.MutableInteger;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zk.ui.util.ForEachStatus;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.ZulEvents;
import org.zkoss.zul.ext.Selectable;
import org.zkoss.zul.impl.XulElement;

/**
 * A radio group.
 *
 * <p>Note: To support the versatile layout, a radio group accepts any kind of
 * children, including {@link Radio}. On the other hand, the parent of
 * a radio, if any, must be a radio group.
 *
 * @author tomyeh
 */
public class Radiogroup extends XulElement {
	private static final Logger log = LoggerFactory.getLogger(Radiogroup.class);
	
	private static final String ZUL_RADIOGROUP_ON_INITRENDER = "zul.Radiogroup.ON_INITRENDER";
	private String _orient = "horizontal";
	/** The name of all child radio buttons. */
	private String _name;
	/** A list of external radio ({@link Radio}) components. */
	private List<Radio> _externs;
	private int _jsel = -1;
	private ListModel<?> _model;
	private RadioRenderer<?> _renderer;
	private transient ListDataListener _dataListener;
	
	static {
		addClientEvent(Radiogroup.class, Events.ON_CHECK, CE_IMPORTANT|CE_REPEAT_IGNORE);
	}
	
	public Radiogroup() {
		_name = genGroupName();
	}

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return _orient;
	}
	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("orient cannot be "+orient);

		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			smartUpdate("orient", _orient);
		}
	}

	/** Returns a readonly list of {@link Radio}.
	 * Note: any update to the list won't affect the state of this radio group.
	 * @since 5.0.4
	 */
	public List<Radio> getItems() {
		//FUTURE: the algorithm is stupid and it shall be similar to Listbox
		//however, it is OK since there won't be many radio buttons in a group
		final List<Radio> items = new ArrayList<Radio>();
		getItems0(this, items);
		if (_externs != null)
			for (Radio radio: _externs) {
				if (!isRedudant(radio))
					items.add(radio);
			}
		return items;
	}
	
	private static void getItems0(Component comp, List<Radio> items) {
		for (Component child: comp.getChildren()) {
			if (child instanceof Radio)
				items.add((Radio)child);
			else if (!(child instanceof Radiogroup)) //skip nested radiogroup
				getItems0(child, items);
		}
	}
	/** Returns the radio button at the specified index.
	 */
	public Radio getItemAtIndex(int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException("Wrong index: "+index);

		final MutableInteger cur = new MutableInteger(0);
		Radio radio = getAt(this, cur, index);
		if (radio != null)
			return radio;
		if (_externs != null)
			for (Radio r: _externs) {
				if (!isRedudant(r) && cur.value++ == index)
					return r;
			}
		throw new IndexOutOfBoundsException(index+" out of 0.."+(cur.value-1));
	}
	private static Radio getAt(Component comp, MutableInteger cur, int index) {
		for (Iterator it = comp.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child instanceof Radio) {
				if (cur.value++ == index)
					return (Radio)child;
			} else if (!(child instanceof Radiogroup)) { //skip nested radiogroup
				Radio r = getAt(child, cur, index);
				if (r != null) return r;
			}
		}
		return null;
	}
	private boolean isRedudant(Radio radio) {
		for (Component p = radio; (p = p.getParent()) != null;)
			if (p instanceof Radiogroup)
				return p == this;
		return false;
	}

	/** Returns the number of radio buttons in this group.
	 */
	public int getItemCount() {
		int sum = countItems(this);
		if (_externs != null)
			for (Radio radio: _externs) {
				if (!isRedudant(radio))
					++sum;
			}
		return sum;
	}
	private static int countItems(Component comp) {
		int sum = 0;
		for (Iterator it = comp.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child instanceof Radio)
				++sum;
			else if (!(child instanceof Radiogroup)) //skip nested radiogroup
				sum += countItems(child);
		}
		return sum;
	}

	/** Returns the index of the selected radio button (-1 if no one is selected).
	 */
	public int getSelectedIndex() {
		return _jsel;
	}
	/** Deselects all of the currently selected radio button and selects
	 * the radio button with the given index.
	 */
	public void setSelectedIndex(int jsel) {
		if (jsel < 0) jsel = -1;
		if (_jsel != jsel) {
			if (jsel < 0) {
				Radio r = getSelectedItem();
				if (r != null)
					r.setSelected(false);
			} else {
				getItemAtIndex(jsel).setSelected(true);
			}
		}
	}
	/** Returns the selected radio button.
	 */
	public Radio getSelectedItem() {
		return _jsel >= 0 ? getItemAtIndex(_jsel): null;
	}
	/**  Deselects all of the currently selected radio buttons and selects
	 * the given radio button.
	 */
	public void setSelectedItem(Radio item) {
		if (item == null) {
			setSelectedIndex(-1);
		} else {
			if (item.getRadiogroup() != this)
				throw new UiException("Not a child: "+item);
			item.setSelected(true);
		}
	}

	/** Appends a radio button.
	 */
	public Radio appendItem(String label, String value) {
		final Radio item = new Radio();
		item.setLabel(label);
		item.setValue(value);
		item.setParent(this);
		return item;
	}
	/**  Removes the child radio button in the radio group at the given index.
	 * @return the removed radio button.
	 */
	public Radio removeItemAt(int index) {
		final Radio item = getItemAtIndex(index);
		if (item != null && !removeExternal(item)) {
			final Component p = item.getParent();
			if (p != null)
				p.removeChild(item);
		}
		return item;
	}

	/** Returns the name of this group of radio buttons.
	 * All child radio buttons shared the same name ({@link Radio#getName}).
	 * <p>Default: automatically generated an unique name
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 */
	public String getName() {
		return _name;
	}
	/** Sets the name of this group of radio buttons.
	 * All child radio buttons shared the same name ({@link Radio#getName}).
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0) name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("name", _name);
		}
	}

	//utilities for radio//
	/** Called when a radio is added to this group.
	 */
	/*package*/ void fixOnAdd(Radio child) {
		if (_jsel >= 0 && child.isSelected()) {
			child.setSelected(false); //it will call fixSelectedIndex
		} else {
			fixSelectedIndex();
		}
	}
	/** Called when a radio is removed from this group.
	 */
	/*package*/ void fixOnRemove(Radio child) {
		if (child.isSelected()) {
			_jsel = -1;
		} else if (_jsel > 0) { //excluding 0
			fixSelectedIndex();
		}
	}
	/** Fix the selected index, _jsel, assuming there are no selected one
	 * before (and excludes) j-the radio button.
	 */
	/*package*/ void fixSelectedIndex() {
		final MutableInteger cur = new MutableInteger(0);
		_jsel = fixSelIndex(this, cur);

		if (_jsel < 0 && _externs != null)
			for (Radio radio: _externs) {
				if (!isRedudant(radio)) {
					if (radio.isSelected()) {
						_jsel = cur.value;
						break; //found
					}
					++cur.value;
				}
			}
	}
	private static int fixSelIndex(Component comp, MutableInteger cur) {
		for (Iterator it = comp.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component)it.next();
			if (child instanceof Radio) {
				if (((Radio)child).isSelected())
					return cur.value;
				++cur.value;
			} else if (!(child instanceof Radiogroup)) { //skip nested radiogroup
				int jsel = fixSelIndex(child, cur);
				if (jsel >= 0)
					return jsel;
			}
		}
		return -1;
	}

	/** Adds an external radio. An external radio is a radio that is NOT a
	 * descendant of the radio group.
	 */
	/*package*/ void addExternal(Radio radio) {
		if (_externs == null)
			_externs = new LinkedList<Radio>();
		_externs.add(radio);
		if (!isRedudant(radio))
			fixOnAdd(radio);
	}
	/** Removes an external radio.
	 */
	/*package*/ boolean removeExternal(Radio radio) {
		if (_externs != null && _externs.remove(radio)) {
			if (!isRedudant(radio))
				fixOnRemove(radio);
			return true;
		}
		return false;
	}
	/** Generates the group name for child radio buttons.
	 */
	private String genGroupName() {
		return Strings.encode(new StringBuffer(16).append("_pg"),
			System.identityHashCode(this)).toString();
	}
	
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		if (_name != null)
			render(renderer, "name", _name);
		if (!"horizontal".equals(_orient))
			render(renderer, "orient", _orient);
	}
	
	
	//-- ListModel dependent codes --//
	/** Returns the list model associated with this radiogroup, or null
	 * if this radiogroup is not associated with any list data model.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public <T> ListModel<T> getModel() {
		return (ListModel)_model;
	}
	/** Sets the list model associated with this radiogroup.
	 * If a non-null model is assigned, no matter whether it is the same as
	 * the previous, it will always cause re-render.
	 *
	 * @param model the list model to associate, or null to dis-associate
	 * any previous model.
	 * @exception UiException if failed to initialize with the model
	 * @since 6.0.0
	 */
	public void setModel(ListModel<?> model) {
		if (model != null) {
			if (!(model instanceof Selectable))
				throw new UiException(model.getClass() + " must implement "+Selectable.class);

			if (_model != model) {
				if (_model != null) {
					_model.removeListDataListener(_dataListener);
					//2012/1/11 TonyQ:Here we only clear children but not external radioss.
				} else if (!getChildren().isEmpty()) getChildren().clear();
				_model = model;
				initDataListener();
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
			_model = null;
			if (!getChildren().isEmpty()) getChildren().clear();
		}
	}
	
	private void initDataListener() {
		if (_dataListener == null){
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
		}

		_model.addListDataListener(_dataListener);
	}
	private void doSelectionChanged() {
		final Selectable<Object> smodel = getSelectableModel();
		if (smodel.isSelectionEmpty()) {
			if (_jsel >= 0)
				setSelectedItem(null);
			return;
		}

		if (_jsel >= 0 && smodel.isSelected(_model.getElementAt(_jsel)))
			return; //nothing changed

		int j = 0;
		for (final Radio item: getItems()) {
			if (smodel.isSelected(_model.getElementAt(j++))) {
				setSelectedItem(item);
				return;
			}
		}
		setSelectedItem(null); //something wrong but be self-protected
	}
	@SuppressWarnings("unchecked")
	private Selectable<Object> getSelectableModel() {
		return (Selectable<Object>)_model;
	}

	private void postOnInitRender(String idx) {
		//20080724, Henri Chen: optimize to avoid postOnInitRender twice
		if (getAttribute(ZUL_RADIOGROUP_ON_INITRENDER) == null) {
	  		//Bug #2010389
			setAttribute(ZUL_RADIOGROUP_ON_INITRENDER, Boolean.TRUE); //flag syncModel
			Events.postEvent("onInitRender", this, idx);
		}
	}
	
	/**
	 * For model rendering
	 * @param data
	 */
	@SuppressWarnings("rawtypes")
	public void onInitRender(Event data) {
  		//Bug #2010389
		removeAttribute(ZUL_RADIOGROUP_ON_INITRENDER); //clear syncModel flag
		final Renderer renderer = new Renderer();
		final ListModel subset = _model;
		try {
			if (!getChildren().isEmpty()) getChildren().clear();
			int pgsz = subset.getSize(), ofs = 0;
			for (int j = 0 ;j < pgsz ; ++j) {
				Radio item = new Radio();
				item.applyProperties();
				item.setParent(this);
				final int index = j + ofs;
				final Object value = subset.getElementAt(index);
				renderer.render(item, value, index);
				Object v = item.getAttribute("org.zkoss.zul.model.renderAs");
				if (v != null) //a new item is created to replace the existent one
					item = (Radio)v;
				if (getSelectableModel().isSelected(value))
					setSelectedItem(item);
			}
		} catch (Throwable ex) {
			renderer.doCatch(ex);
		} finally {
			renderer.doFinally();
		}
		Events.postEvent("onInitRenderLater", this, null);// notify databinding load-when. 
		Events.postEvent(ZulEvents.ON_AFTER_RENDER, this, null);// notify the combobox when items have been rendered. 
	}

	/**
	 * We need to sync model selection to keep select status .
	 * @see Radio#service(org.zkoss.zk.au.AuRequest, boolean)
	 */
	/*package*/ void syncSelectionToModel() {
		if (_model != null) {
			List<Object> selObjs = new ArrayList<Object>();
			if (_jsel >= 0)
				selObjs.add(_model.getElementAt(_jsel));
			getSelectableModel().setSelection(selObjs);
		}
	}
	
	/** Returns the renderer used to render items.
	 */
	@SuppressWarnings("unchecked")
	private <T> RadioRenderer<T> getRealRenderer() {
		return _renderer != null ? _renderer: _defRend;
	}
	
	/** Returns the renderer to render each radio, or null if the default
	 * renderer is used.
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public <T> RadioRenderer<T> getRadioRenderer() {
		return (RadioRenderer<T>)_renderer;
	}
	
	/** Sets the renderer which is used to render each row
	 * if {@link #getModel} is not null.
	 *
	 * <p>Note: changing a render will not cause the radiogroup to re-render.
	 * If you want it to re-render, you could assign the same model again 
	 * (i.e., setModel(getModel())), or fire an {@link ListDataEvent} event.
	 *
	 * @param renderer the renderer, or null to use the default.
	 * @exception UiException if failed to initialize with the model
	 * @since 6.0.0
	 */
	public void setRadioRenderer(RadioRenderer<?> renderer) {
		_renderer = renderer;
	}
	
	/** Sets the renderer by use of a class name.
	 * It creates an instance automatically.
	 *@since 6.0.0
	 */
	public void setRadioRenderer(String clsnm)
	throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
	InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null)
			setRadioRenderer((RadioRenderer)Classes.newInstanceByThread(clsnm));
	}
	
	/**
	 * The default Renderer for model rendering. 
	 */
	@SuppressWarnings("rawtypes")
	private static final RadioRenderer _defRend = new RadioRenderer() {
		public void render(final Radio item,final Object data,final int index) throws Exception {
			final Radiogroup cb = (Radiogroup)item.getParent();
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
					}, null);
				if (items.length != 1)
					throw new UiException("The model template must have exactly one item, not "+items.length);

				final Radio nci = (Radio)items[0];
				if (nci.getValue() == null) //template might set it
					nci.setValue(data);
				item.setAttribute("org.zkoss.zul.model.renderAs", nci);
					//indicate a new item is created to replace the existent one
				item.detach();
			}
			
		}
	};	
	/** Used to render Radio if _model is specified. */
	private class Renderer implements java.io.Serializable {
		@SuppressWarnings("rawtypes")
		private final RadioRenderer _renderer;
		private boolean _rendered, _ctrled;

		private Renderer() {
			_renderer = getRealRenderer();
		}
		@SuppressWarnings("unchecked")
		private void render(Radio item, Object value, int index) throws Throwable {
			if (!_rendered && (_renderer instanceof RendererCtrl)) {
				((RendererCtrl)_renderer).doTry();
				_ctrled = true;
			}

			try {
				_renderer.render(item, value, index);
			} catch (Throwable ex) {
				try {
					item.setLabel(Exceptions.getMessage(ex));
				} catch (Throwable t) {
					log.error("", t);
				}			
				throw ex;
			}
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
	
	
	//Cloneable//
	public Object clone() {
		final Radiogroup clone = (Radiogroup)super.clone();
		fixClone(clone);
		if (clone._model != null) {
			if (clone._model instanceof ComponentCloneListener) {
				final ListModel model = (ListModel) ((ComponentCloneListener) clone._model).willClone(clone);
				if (model != null)
					clone._model = model;
			}
			clone._dataListener = null;
			clone.initDataListener();
		}		
		return clone;
	}
	private static void fixClone(Radiogroup clone) {
		if (clone._name.startsWith("_pg")) clone._name = clone.genGroupName();
	}
	
	//	Serializable//
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		if (_model != null) {
			initDataListener();
		}
	}
	
}
