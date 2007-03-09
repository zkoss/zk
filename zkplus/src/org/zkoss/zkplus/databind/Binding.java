/* Binding.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Feb  1 17:13:40     2007, Created by Henri
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Express;
import org.zkoss.zk.ui.event.EventListener;

import org.zkoss.util.ModificationException;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.reflect.Fields;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * A Data Binding that associate component+attr to an bean expression.
 *
 * @author Henri
 */
/* package */ class Binding {
	private DataBinder _binder;
	private Component _comp;
	private String _attr;
	private String _expression; //the bean expression
	private LinkedHashSet _loadWhenEvents;
	private String _saveWhenEvent;
	private boolean _loadable = true;
	private boolean _savable;
	private TypeConverter _converter;
	private String[] _paths; //bean reference path (a.b.c)
	
	/** Constrcutor to form a binding between UI component and backend data bean.
	 * @param binder the associated Data Binder.
	 * @param comp The concerned component
	 * @param attr The component attribute
	 * @param expr The bean expression.
	 * @param loadWhenEvents The event set when to load data.
	 * @param saveWhenEvent The event when to save data.
	 * @param access In the view of UI component: "load" load only, "both" load/save, 
	 *	"save" save only when doing data binding. null means using the default access 
	 *	natural of the component. e.g. Label.expr is "load", but Textbox.expr is "both".
	 * @param converter The converter class used to convert classes between component attribute 
	 * and the associated bean expression. null means using the default class conversion method.
	 */
	public Binding(DataBinder binder, Component comp, String attr, String expr, 
		LinkedHashSet loadWhenEvents, String saveWhenEvent, String access, String converter) {
		_binder = binder;
		_comp = comp;
		setAttr(attr);
		setExpression(expr);
		setLoadWhenEvents(loadWhenEvents);
		setSaveWhenEvent(saveWhenEvent);
		setAccess(access);
		setConverter(converter);
		
	}

	public DataBinder getBinder() {
		return _binder;
	}
	
	public Component getComponent() {
		return _comp;
	}
	
	/** Set component attribute name.
	 * @param attr component attribute.
	 */	
	public void setAttr(String attr) {
		_attr = attr;
	}

	/** Get component attribute name.
	 */
	public String getAttr() {
		return _attr;
	}
	
	/** Set bean expression (a.b.c).
	 */
	public void setExpression(String expr) {
		_expression = expr;
		_paths = parseBeanExpression(expr);
	}

	//:TODO: handle function parsing	
	private String[] parseBeanExpression(String expr) {
		String[] paths = new String[1];
		paths[0] = expr;
		return paths;
	}

	/** Get bean expression, e.g. a.b.c.
	 */
	public String getExpression() {
		return _expression;
	}
	
	/** Get bean reference path.
	 */
	public String[] getPaths() {
		return _paths;
	}
	
	/** Set save-when event expression.
	 * @param saveWhenEvent the save-when expression.
	 */
	public void setSaveWhenEvent(String saveWhenEvent) {
		if (saveWhenEvent != null) {
			_saveWhenEvent = saveWhenEvent;
		}
	}
	
	/** Get save-when event expression.
	 */
	public String getSaveWhenEvent() {
		return _saveWhenEvent;
	}
	
	/** Add load-when event expression.
	 * @param loadWhenEvent the load-when expression.
	 */
	public void setLoadWhenEvents(LinkedHashSet loadWhenEvents) {
		_loadWhenEvents = loadWhenEvents;
	}
	
	/** Get load-when event expression set.
	 */
	public LinkedHashSet getLoadWhenEvents() {
		return _loadWhenEvents;
	}

	/** Set accessibility.
	 */
	public void setAccess(String access) {
		if (access == null) { //default access to none
			return;
		}
		
		if ("both".equals(access)) {
			_loadable = true;
			_savable = true;
		} else if ("load".equals(access)) {
			_loadable = true;
			_savable = false;
		} else if ("save".equals(access)) {
			_loadable = false;
			_savable = true;
		} else if ("none".equals(access)) { //unknow access mode
			_loadable = false;
			_savable = false;
		} else {
			throw new UiException("Unknown DataBinder access mode. Should be \"both\", \"load\", \"save\", or \"none\": "+access);
		}
	}
	
	/** Whether the binding is loadable.
	 */
	public boolean isLoadable() {
		return _loadable;
	}
	
	/** Whether the binding is savable.
	 */
	public boolean isSavable() {
		return _savable;
	}

	/** Set the {@link TypeConverter}.
	 * @param cvtClsName the converter class name.
	 */
	public void setConverter(String cvtClsName) {
		if (cvtClsName != null) {
			try {
				_converter = (TypeConverter) Classes.newInstanceByThread(cvtClsName);
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
	}
	
	/** Get the {@link TypeConverter}.
	 */
	public TypeConverter getConverter() {
		return _converter;
	}
	
	/** load bean value into the attribute of the specified component.
	 * @param comp the component.
	 */
	public void loadAttribute(Component comp) {
		if (!isLoadable() || _attr.startsWith("_") || _binder.isTemplate(comp) || comp.getPage() == null) { 
			return; //cannot load, a control attribute, or a detached component, skip!
		}
		Object bean = _binder.getBeanAndRegisterBeanSameNodes(comp, _expression);
		myLoadAttribute(comp, bean);
	}

	/** load bean value into the attribute of the specified component.
	 * @param comp the component.
	 * @param bean the bean value.
	 */
	public void loadAttribute(Component comp, Object bean) {
		if (!isLoadable() || _attr.startsWith("_") || _binder.isTemplate(comp) || comp.getPage() == null) { 
			return; //cannot load, a control attribute, or a detached component, skip!
		}
		myLoadAttribute(comp, bean);
	}
	
	private void myLoadAttribute(Component comp, Object bean) {
		try {
			if (_converter != null) {
				bean = _converter.coerceToUi(bean, comp);
			}
			Fields.set(comp, _attr, bean, _converter == null);
		} catch (ClassCastException ex) {
			throw UiException.Aide.wrap(ex);
		} catch (NoSuchMethodException ex) {
			throw UiException.Aide.wrap(ex);
		} catch (ModificationException ex) {
			throw UiException.Aide.wrap(ex);
		} catch (WrongValueException ex) {
			//Bug #1615371, try to use setRawValue()
			if ("value".equals(_attr)) {
				try {
					Fields.set(comp, "rawValue", bean, _converter == null);
				} catch (Exception ex1) {
					//exception
					throw ex;
				}
			} else {
				throw ex;
			}
		}
	}

	/** save into bean value from the attribute of the specified component.
	 * @param comp the component.
	 */
	public void saveAttribute(Component comp) {
		if (!isSavable() || _attr.startsWith("_") || _binder.isTemplate(comp) || comp.getPage() == null) { 
			return; //cannot save, a control attribute, or a detached component, skip!
		}
		try {
			Object rawval = Fields.get(comp, _attr);
			Object val = (_converter == null) ? rawval : _converter.coerceToBean(rawval, comp);
			_binder.setBeanAndRegisterBeanSameNodes(comp, val, this, _expression, _converter == null, rawval);
		} catch (ClassCastException ex) {
			throw UiException.Aide.wrap(ex);
		} catch (NoSuchMethodException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	//:TODO
	public void loadAttribute(Component comp, String expr) {
		//
	}
	//:TODO
	public void saveAttribute(Component comp, String expr) {
		//
	}
	
	public void registerSaveEvent(Component comp) {
		if (_saveWhenEvent != null) {
			final String[] results = splitBeanid(_saveWhenEvent); //[0] bean id or bean path, [1] event name
			final Component target = (Component) ("self".equals(results[0]) ? 
				comp : _binder.lookupBean(comp, results[0]));
			target.addEventListener(results[1], new SaveEventListener(comp));
		}
	}
	
	public void registerLoadEvents(Component comp) {
		if (_loadWhenEvents != null) {
			for(final Iterator it = _loadWhenEvents.iterator(); it.hasNext(); ) {
				String expr = (String) it.next();
				String[] results = splitBeanid(expr); //[0] bean id or bean path, [1] event name
				Component target = (Component) ("self".equals(results[0]) ? 
					comp : _binder.lookupBean(comp, results[0]));
				target.addEventListener(results[1], new LoadEventListener(comp));
			}
		}
	}
					
	//-- Object --//
	public String toString() {
		return "[binder:"+_binder+", comp:"+_comp+", attr:"+_attr+", expr:"+_expression
			+", load-when:"+_loadWhenEvents+", save-when:"+_saveWhenEvent
			+", load:"+_loadable+", save:"+_savable+", converter:"+_converter+"]";
	}

	//split a.b to [a, b]
	private String[] splitBeanid(String expr) {
		String beanid = null;
		String props = null;
		int j = expr.indexOf(".");
		if (j < 0) { //bean only
			beanid = expr;
			props = null;
		} else {
			beanid = expr.substring(0, j);
			props = expr.substring(j+1);
		}
		String[] results = new String[2];
		results[0] = beanid;
		results[1] = props;
		return results;
	}
	
	private abstract class BaseEventListener implements EventListener, Express {
		protected Component _dataTarget;
		
		public BaseEventListener(Component dataTarget) {
			_dataTarget = dataTarget;
		}
		
		public boolean isAsap() {
			return true;
		}
	}
			
	private class LoadEventListener extends BaseEventListener {
		public LoadEventListener(Component dataTarget) {
			super(dataTarget);
		}
		public void onEvent(Event event) {
			final Component dataTarget = _binder.isTemplate(_dataTarget) ? 
				_binder.lookupClone(event.getTarget(), _dataTarget) : _dataTarget;
			if (dataTarget != null) {
				loadAttribute((Component) dataTarget);
			}
		}
	}

	private class SaveEventListener extends BaseEventListener {
		public SaveEventListener(Component dataTarget) {
			super(dataTarget);
		}
		public void onEvent(Event event) {
			final Component dataTarget = _binder.isTemplate(_dataTarget) ? 
				_binder.lookupClone(event.getTarget(), _dataTarget) : _dataTarget;
			if (dataTarget != null) {
				saveAttribute((Component) dataTarget);
			}
		}
	}

}
