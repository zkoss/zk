/* Selectbox.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 30 10:53:25 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;

/**
 * A light weight dropdown list.
 * @author jumperchen
 * @since 6.0.0
 */
public class Selectbox extends HtmlBasedComponent {

	private String _name;
	private boolean _disabled;
	private int _jsel = -1;
	private int _tabindex;
	private transient ListModel _model;
	private transient ListDataListener _dataListener;
	private transient OptionRenderer _renderer;
	private static final String ATTR_ON_INIT_RENDER_POSTED = "org.zkoss.zul.onInitLaterPosted";
	
	static {
		addClientEvent(Selectbox.class, Events.ON_SELECT, CE_DUPLICATE_IGNORE|CE_IMPORTANT);
		addClientEvent(Selectbox.class, Events.ON_FOCUS, CE_DUPLICATE_IGNORE);
		addClientEvent(Selectbox.class, Events.ON_BLUR, CE_DUPLICATE_IGNORE);
	}
	/**
	 * Returns the index of the selected item (-1 if no one is selected).
	 */
	public int getSelectedIndex() {
		return _jsel;
	}
	
	/**
	 * Selects the item with the given index.
	 */
	public void setSelectedIndex(int jsel) {
		if (jsel < -1)
			jsel = -1;
		if (jsel != _jsel) {
			_jsel = jsel;
			smartUpdate("selectedIndex", jsel);
		}
	}

	/**
	 * Returns the tab order of this component.
	 * <p>
	 * Default: 0 (means the same as browser's default).
	 */
	public int getTabindex() {
		return _tabindex;
	}

	/**
	 * Sets the tab order of this component.
	 */
	public void setTabindex(int tabindex) throws WrongValueException {
		if (_tabindex != tabindex) {
			_tabindex = tabindex;
			smartUpdate("tabindex", tabindex);
		}
	}
	/**
	 * Returns the renderer to render each item, or null if the default renderer
	 * is used.
	 */
	public OptionRenderer getItemRenderer() {
		return _renderer;
	}

	/**
	 * Sets the renderer which is used to render each item if {@link #getModel}
	 * is not null.
	 *
	 * <p>
	 * Note: changing a render will not cause the selectbox to re-render. If you
	 * want it to re-render, you could assign the same model again (i.e.,
	 * setModel(getModel())), or fire an {@link ListDataEvent} event.
	 *
	 * @param renderer
	 *            the renderer, or null to use the default.
	 * @exception UiException
	 *                if failed to initialize with the model
	 */
	public void setItemRenderer(OptionRenderer renderer) {
		if (_renderer != renderer) {
			_renderer = renderer;
			invalidate();
		}
	}

	/**
	 * Sets the renderer by use of a class name. It creates an instance
	 * automatically.
	 */
	public void setItemRenderer(String clsnm) throws ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException,
			InstantiationException, java.lang.reflect.InvocationTargetException {
		if (clsnm != null)
			setItemRenderer((OptionRenderer) Classes
					.newInstanceByThread(clsnm));
	}

	/**
	 * Returns whether it is disabled.
	 * <p>
	 * Default: false.
	 */
	public boolean isDisabled() {
		return _disabled;
	}

	
	protected boolean isChildable() {
		return false;
	}
	/**
	 * Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}

	/**
	 * Returns the name of this component.
	 * <p>
	 * Default: null.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Sets the name of this component.
	 * <p>
	 * The name is used only to work with "legacy" Web application that handles
	 * user's request by servlets. It works only with HTTP/HTML-based browsers.
	 * It doesn't work with other kind of clients.
	 * <p>
	 * Don't use this method if your application is purely based on ZK's
	 * event-driven model.
	 * 
	 * @param name
	 *            the name of this component.
	 */
	public void setName(String name) {
		if (name != null && name.length() == 0)
			name = null;
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("name", name);
		}
	}

	private void initDataListener() {
		if (_dataListener == null)
			_dataListener = new ListDataListener() {
				public void onChange(ListDataEvent event) {
					postOnInitRender();
				}
			};
		_model.addListDataListener(_dataListener);
	}
	
	/**
	 * Sets the list model associated with this selectbox. If a non-null model is
	 * assigned, no matter whether it is the same as the previous, it will
	 * always cause re-render.
	 *
	 * @param model
	 *            the list model to associate, or null to dis-associate any
	 *            previous model.
	 * @exception UiException
	 *                if failed to initialize with the model
	 */
	public void setModel(ListModel model) {
		if (model != null) {
			if (_model != model) {
				if (_model != null) {
					_model.removeListDataListener(_dataListener);
				}
				_model = model;
				initDataListener();
				postOnInitRender();
			}
		} else if (_model != null) {
			_model.removeListDataListener(_dataListener);
			_model = null;
			invalidate();
		}
	}

	public void onInitRender() {
		removeAttribute(ATTR_ON_INIT_RENDER_POSTED);
		invalidate();
	}
	private void postOnInitRender() {
		if (getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
			setAttribute(ATTR_ON_INIT_RENDER_POSTED, Boolean.TRUE);
			Events.postEvent("onInitRender", this, null);
		}
	}
	
	/**
	 * Returns the model associated with this selectbox, or null if this selectbox
	 * is not associated with any list data model.
	 */
	public ListModel getModel() {
		return _model;
	}

	public OptionRenderer getRealRenderer() {
		final OptionRenderer renderer = getItemRenderer();
		return renderer != null ? renderer : _defRend; 
	}
	
	private static final OptionRenderer _defRend = new OptionRenderer() {
		public String render(Object data) {
			return Objects.toString(data);
		}
	};
	// -- ComponentCtrl --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "name", _name);
		render(renderer, "disabled", isDisabled());
		renderer.render("selectedIndex", _jsel);
		
		if (_tabindex != 0)
			renderer.render("tabindex", _tabindex);
		
		if (_model != null) {
	        String[] data = new String[_model.getSize()];
	        OptionRenderer render = getRealRenderer();

			try {
				for (int i = 0; i < _model.getSize(); i++)
					data[i] = render.render(_model.getElementAt(i));
			} catch (Exception e) {
				throw UiException.Aide.wrap(e);
			}
			
			render(renderer, "items", data);
		}
	}

	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_SELECT)) {
			_jsel = ((Integer)request.getData().get("")).intValue();
			Events.postEvent(new Event(Events.ON_SELECT, this, request.getData().get("")));
		}
	}
	// Cloneable//
	public Object clone() {
		final Selectbox clone = (Selectbox) super.clone();
		if (clone._model != null) {
			// we use the same data model but we have to create a new listener
			clone._dataListener = null;
			clone.initDataListener();
		}
		return clone;
	}


	// -- Serializable --//
	// NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		s.defaultWriteObject();

		willSerialize(_model);
		s.writeObject(_model instanceof java.io.Serializable
				|| _model instanceof java.io.Externalizable ? _model : null);
		willSerialize(_renderer);
		s.writeObject(_renderer instanceof java.io.Serializable
				|| _renderer instanceof java.io.Externalizable ? _renderer
				: null);
	}

	private synchronized void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_model = (ListModel) s.readObject();
		didDeserialize(_model);
		_renderer = (OptionRenderer) s.readObject();
		didDeserialize(_renderer);
		if (_model != null) {
			initDataListener();
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
}
