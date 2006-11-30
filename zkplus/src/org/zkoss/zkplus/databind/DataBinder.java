/* DataBinder.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Nov 15 14:24:25     2006, Created by Henri Chen.
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.util.ModificationException;
import org.zkoss.lang.Classes;
import org.zkoss.lang.reflect.Fields;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.lang.reflect.Method;

/**
 * The DataBinder used for binding ZK UI component and the backend data bean.
 *
 * @author Henri Chen
 */
public class DataBinder {
	private Map _bindings = new LinkedHashMap(29); //(comp, (attr, Binding))
	private Map _beans = new HashMap(5); //(beanid, bean)
	private static Map _converterMap = new HashMap(5); //(converterClassName, converter)
	
	/** Binding bean to UI component. This is the same as {@link #addBinding(comp, attr, expression, null, null)}. 
	 * @param comp The component to be associated.
	 * @param attr The attribute of the component to be associated.
	 * @param expression The expression to associate the data bean.
	 */
	public void addBinding(Component comp, String attr, String expression) {
		addBinding(comp, attr, expression, null, null);
	}

	/** Binding bean to UI component. 
	 * @param comp The component to be associated.
	 * @param attr The attribute of the component to be associated.
	 * @param expression The expression to associate the data bean.
	 * @param access In the view of UI component: "ro" read only, "rw" read write, "wo" write only when doing
	 * data binding. null means using the default access natural of the component. e.g. Label is "ro", 
	 * but Textbox is "rw".
	 * @param converter The converter class used to convert classes between component and the associated bean.
	 * null means using the default class conversion method.
	 */
	public void addBinding(Component comp, String attr, String expression, String access, String converter) {
		Map attrMap = (Map) _bindings.get(comp);
		if (attrMap == null) {
			attrMap = new LinkedHashMap(3);
			_bindings.put(comp, attrMap);
		}
		attrMap.put(attr, new Binding(comp, attr, expression, access, converter));
	}
	
	/** Bind a real bean object to the specified beanid. You might not need to call this method because this
	 * DataBinder would look up the variable via the {@link org.zkoss.zk.ui.Component#getVariable()} method
	 * if it cannot find the specified bean via the given beanid.
	 *
	 * @param beanid The bean id used in data binding.
	 * @param bean The real bean object to be associated with the bean id.
	 */
	public void bindBean(String beanid, Object bean) {
		_beans.put(beanid, bean);
	}
	
	/** Load value from the data bean property to a specified UI component. 
	 * @param comp the UI component to be loaded value.
	 */
	public void loadComponent(Component comp) {
		final Map attrMap = (Map) _bindings.get(comp);
		if (attrMap == null) { //nothing to do
			return;
		}
		loadUi(attrMap);
	}
	
	/** Save value from a specified UI component to a data bean property. 
	 * @param comp the UI component used to save value into backend data bean.
	 */
	public void saveComponent(Component comp) {
		final Map attrMap = (Map) _bindings.get(comp);
		if (attrMap == null) { //nothing to do
			return;
		}
		saveUi(attrMap);
	}
	
	/** Load all value from data beans to UI components. */
	public void loadAll() {
		for (final Iterator it = _bindings.values().iterator(); it.hasNext(); ) {
			final Map attrMap = (Map) it.next();
			loadUi(attrMap);
		}
	}
	
	/** Save all values from UI components to beans. */
	public void saveAll() {
		for (final Iterator it = _bindings.values().iterator(); it.hasNext(); ) {
			final Map attrMap = (Map) it.next();
			saveUi(attrMap);
		}
	}
	
	private void loadUi(Map attrMap) {
		for (final Iterator it = attrMap.values().iterator(); it.hasNext(); ) {
			final Binding binding = (Binding) it.next();
			if (binding.canRead()) {
				binding.loadAttr();
			}
		}
	}
			
	private void saveUi(Map attrMap) {
		for (final Iterator it = attrMap.values().iterator(); it.hasNext(); ) {
			final Binding binding = (Binding) it.next();
			if (binding.canWrite()) {
				binding.saveAttr();
			}
		}
	}

	private static TypeConverter lookupConverter(String clsName) {
		TypeConverter converter = (TypeConverter) _converterMap.get(clsName);
		if (converter == null) {
			try {
				converter = (TypeConverter) Classes.newInstanceByThread(clsName);
				_converterMap.put(clsName, converter);
			} catch (java.lang.reflect.InvocationTargetException ex) {
				throw UiException.Aide.wrap(ex.getCause());
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
		return converter;
	}
	
	//A binding association class.
	private class Binding {
		private Component _comp;
		private String _attr;
		private String _expression;
		private String _beanid;
		private String _props; //a.b.c
		private boolean _canRead;
		private boolean _canWrite;
		private TypeConverter _converter;
		
		/** Construtor to form a binding between UI component and backend data bean.
		 * @param comp The component to be associated.
		 * @param attr The attribute of the component to be associated.
		 * @param expression The expression to associate the data bean.
		 * @param access In the view of UI component: "ro" read only, "rw" read write, "wo" write only when doing
		 * data binding. null means using the default access natural of the attribute of the component. 
		 * e.g. Label.value is "ro" while Textbox.value is "rw".
		 * @param converter The converter class used to convert classes between component and the associated bean.
		 * null means using the default class conversion method.
		 */
		private Binding(Component comp, String attr, String expression, String access, String converter) {
			_comp = comp;
			_attr = attr;
			_expression = expression;
			parseExpression(expression);

			if (access == null) {
				access = decideAccessibility();
			}
			
			if ("rw".equals(access)) {
				_canRead = true;
				_canWrite = true;
			} else if ("ro".equals(access)) {
				_canRead = true;
			} else if ("wo".equals(access)) {
				_canWrite = true;
			} else { //unknow access mode
				throw new UiException("Unknown DataBinder access mode. Should be \"rw\", \"ro\", or \"wo\": "+access);
			}
			
			if (converter != null) {
				_converter = lookupConverter(converter);
			}
		}
		
		private String decideAccessibility() {
			if ((_comp instanceof org.zkoss.zul.impl.InputElement && "value".equals(_attr))
				|| (_comp instanceof org.zkoss.zul.Checkbox && "checked".equals(_attr))
				|| (_comp instanceof org.zkoss.zul.Listbox && "model".equals(_attr))
				|| (_comp instanceof org.zkoss.zul.Slider && "curpos".equals(_attr))
				|| (_comp instanceof org.zkoss.zul.Calendar && "value".equals(_attr)))
				return "rw";
			else
				return "ro";
		}
		
		private boolean canRead() {
			return _canRead;
		}
		
		private boolean canWrite() {
			return _canWrite;
		}
		
		private void saveAttr() {
			try {
				Object bean = lookupBean(_beanid);
				if (bean == null) {
					throw new UiException("Cannot find the specified bean: "+_beanid+" in "+_expression);
				}
				Object val = Fields.getField(_comp, _attr);
				if (_converter != null) {
					val = _converter.coerceToBean(val);
				}
				if (_props == null) { //assign back to where bean is stored
					if (_beans.containsKey(_beanid)) {
						_beans.put(_beanid, val);
					} else {
						_comp.setVariable(_beanid, val, false);
					}
				} else {
					Fields.setField(bean, _props, val, _converter == null);
				}
			} catch (ClassCastException ex) {
				throw UiException.Aide.wrap(ex);
			} catch (NoSuchMethodException ex) {
				throw UiException.Aide.wrap(ex);
			} catch (ModificationException ex) {
				throw UiException.Aide.wrap(ex);
			}
		}

		private void loadAttr() {
			try {
				Object bean = lookupBean(_beanid);
				if (bean == null) {
					throw new UiException("Cannot find the specified bean: "+_beanid+" in "+_expression);
				}
				Object val = _props == null ? bean : Fields.getField(bean, _props);
				if (_converter != null) {
					val = _converter.coerceToUi(val);
				}
				Fields.setField(_comp, _attr, val, _converter == null);
			} catch (ClassCastException ex) {
				throw UiException.Aide.wrap(ex);
			} catch (NoSuchMethodException ex) {
				throw UiException.Aide.wrap(ex);
			} catch (ModificationException ex) {
				throw UiException.Aide.wrap(ex);
			}
		}

		private Object lookupBean(String beanid) {
			Object bean = null;
			if (_beans.containsKey(beanid)) {
				bean = _beans.get(beanid);
			} else {
				bean = _comp.getVariable(beanid, false);
			}
			return bean;
		}

		private void parseExpression(String expr) {
			String beanid = null;
			String props = null;
			int j = expr.indexOf(".");
			if (j < 0) { //bean only
				_beanid = expr;
				_props = null;
			} else {
				_beanid = expr.substring(0, j);
				_props = expr.substring(j+1);
			}
		}		
	}
}
