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

import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Express;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.metainfo.Annotation; 

import org.zkoss.zul.impl.InputElement;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Calendar;
import org.zkoss.zul.ListitemRenderer;

import org.zkoss.util.ModificationException;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.reflect.Fields;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.lang.reflect.Method;

/**
 * The DataBinder used for binding ZK UI component and the backend data bean.
 *
 * @author Henri Chen
 */
public class DataBinder {
	private Map _compBindingMap = new LinkedHashMap(29); //(comp, Map(attr, Binding))
	private Map _beanBindingMap = new HashMap(29); //(beanid, Set(Binding))
	private Map _sameBeanidMap = new HashMap(5); //(bean, Set(beanid)) and (beanid, Set(beanid));
	private Map _cloneMap = new HashMap(255); //(cloned item target, Map(template, clone))
	private Map _beans = new HashMap(5); //(beanid, bean)
//	private Node _dependency = new Node(); //(beanid+prop) dependency tree.
	
	private boolean _init; //whether this databinder is initialized. 
							//Databinder is init automatically when saveXXX or loadXxx is called
	private static Map _converterMap = new HashMap(5); //(converterClassName, converter)
	private static final String VAR = "org.koss.zkplus.databind.VAR"; //the template variable name
	private static final String INDEXITEM = "org.koss.zkplus.databind.INDEXITEM"; //the listitem with index
	private static final String TEMPLATE = "org.koss.zkplus.databind.TEMPLATE"; //the template
	private static final String ISTEMPLATE = "org.koss.zkplus.databind.ISTEMPLATE"; //whether a template
	private static final Object NA = new Object();
	
	/** Binding bean to UI component. This is the same as addBinding(comp, attr, value, null, null). 
	 * @param comp The component to be associated.
	 * @param attr The attribute of the component to be associated.
	 * @param value The expression to associate the data bean.
	 */
	public void addBinding(Component comp, String attr, String value) {
		addBinding(comp, attr, value, null, null, null, null);
	}

	/** Binding bean to UI component. 
	 * @param comp The component to be associated.
	 * @param attr The attribute of the component to be associated.
	 * @param value The expression to associate the data bean.
	 * @param loadWhenEvents The event list when to load data.
	 * @param saveWhenEvent The event when to save data.
	 * @param access In the view of UI component: "load" load only, "both" load/save, "save" save only when doing
	 * data binding. null means using the default access natural of the component. e.g. Label.value is "load", 
	 * but Textbox.value is "both".
	 * @param converter The converter class used to convert classes between component and the associated bean.
	 * null means using the default class conversion method.
	 */
	public void addBinding(Component comp, String attr, String value,
		String[] loadWhenEvents, String saveWhenEvent, String access, String converter) {
			
		//handle default-bind defined in lang-addon.xml
		Object[] objs = loadPropertyAnnotation(comp, attr, "default-bind");
		/* logically impossible to hold "value" in default binding 
		if (value == null) {
			value = (String) objs[0];
		}
		if (loadWhenEvents == null) {
			loadWhenEvents = (String[]) objs[1];
		}
		*/
		if (saveWhenEvent == null) {
			saveWhenEvent = (String) objs[2];
		}
		if (access == null) {
			access = (String) objs[3];
		}
		if (converter == null) {
			converter = (String) objs[4];
		}
		Map attrMap = (Map) _compBindingMap.get(comp);
		if (attrMap == null) {
			attrMap = new LinkedHashMap(3);
			_compBindingMap.put(comp, attrMap);
		}
		if (attrMap.containsKey(attr)) { //override and merge
			final Binding binding = (Binding) attrMap.get(attr);
			binding.setValue(value);
			binding.addLoadWhenEvents(loadWhenEvents);
			binding.setSaveWhenEvent(saveWhenEvent);
			binding.setAccess(access);
			binding.setConverter(converter);
		} else {
			if (objs[1] != null) { //default-bind
				loadWhenEvents = mergeStringArray((String[])objs[1], loadWhenEvents);
			}
			attrMap.put(attr, new Binding(comp, attr, value, loadWhenEvents, saveWhenEvent, access, converter));
		}
	}
	
	/** Remove the binding associated with the attribute of the component.
	 * @param comp The component to be removed the data binding association.
	 * @param attr The attribute of the component to be removed the data binding association.
	 */
	public void removeBinding(Component comp, String attr) {
		Map attrMap = (Map) _compBindingMap.get(comp);
		if (attrMap != null) {
			attrMap.remove(attr);
		}
	}

	//[0] value, [1] loadWhenEvents, [2] saveWhenEvent, [3] access, [4] converter
	protected Object[] loadPropertyAnnotation(Component comp, String propName, String bindName) {
		ComponentCtrl compCtrl = (ComponentCtrl) comp;
		Annotation ann = compCtrl.getAnnotation(propName, bindName);
		if (ann != null) {
			final Map attrs = ann.getAttributes(); //(tag, tagExpr)
			String[] loadWhenEvents = null;
			String saveWhenEvent = null;
			String access = null;
			String converter = null;
			String expr = null;
			for (final Iterator it = attrs.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				String tag = (String) entry.getKey();
				String tagExpr = (String) entry.getValue();
	
				if ("save-when".equals(tag)) {
					saveWhenEvent = tagExpr;
				} else if ("access".equals(tag)) {
					access = tagExpr;
				} else if ("converter".equals(tag)) {
					converter = tagExpr;
				} else if ("load-when".equals(tag)) {
					loadWhenEvents = parseExpression(tagExpr, ",");
				} else if ("value".equals(tag)) {
					expr = tagExpr;
				}
			}
			return new Object[] {expr, loadWhenEvents, saveWhenEvent, access, converter};
		}
		return new Object[5];
	}
	
	/** Bind a real bean object to the specified beanid. You might not need to call this method because this
	 * DataBinder would look up the variable via the {@link org.zkoss.zk.ui.Component#getVariable} method
	 * if it cannot find the specified bean via the given beanid.
	 *
	 * @param beanid The bean id used in data binding.
	 * @param bean The real bean object to be associated with the bean id.
	 */
	public void bindBean(String beanid, Object bean) {
		_beans.put(beanid, bean);
	}

	/** Load value from the data bean property to a specified attribute of the UI component.
	 * @param comp the UI component to be loaded value.
	 * @param attr the UI component attribute to be loaded value.
	 */
	public void loadAttribute(Component comp, String attr) {
		//skip detached component
		if (comp.getPage() != null) {
			init();
			final Component template = getTemplateComponent(comp);
			Map attrMap = (Map) (template != null ? _compBindingMap.get(template) : _compBindingMap.get(comp));
			if (attrMap != null) {
				Binding binding = (Binding) attrMap.get(attr);
				if (binding != null) {
					binding.loadAttribute(comp);
				}
			}
		}
	}			

	/** Save value from a specified attribute of the UI component to a data bean property.
	 * @param comp the UI component used to save value into backend data bean.
	 * @param attr the UI component attribute used to save value into backend data bean.
	 */
	public void saveAttribute(Component comp, String attr) {
		//skip detached component
		if (comp.getPage() != null) {
			init();
			final Component template = getTemplateComponent(comp);
			Map attrMap = (Map) (template != null ? _compBindingMap.get(template) : _compBindingMap.get(comp));
			if (attrMap != null) {
				Binding binding = (Binding) attrMap.get(attr);
				if (binding != null) {
					binding.saveAttribute(comp);
				}
			}
		}
	}
	
	/** Load values from the data bean properties to all attributes of a specified UI component. 
	 * @param comp the UI component to be loaded value.
	 */
	public void loadComponent(Component comp) {
		//skip detached component
		if (comp.getPage() != null) {
			init();
			final Component template = getTemplateComponent(comp);
			Map attrMap = (Map) (template != null ? _compBindingMap.get(template) : _compBindingMap.get(comp));
			if (attrMap != null) {
				loadAttrs(comp, attrMap.values());
			}
			
			//load kids of this component
			for(final Iterator it = comp.getChildren().iterator(); it.hasNext();) {
				loadComponent((Component) it.next()); //recursive
			}
		}
	}
	
	/** Save values from all attributes of a specified UI component to data bean properties. 
	 * @param comp the UI component used to save value into backend data bean.
	 */
	public void saveComponent(Component comp) {
		//skip detached component
		if (comp.getPage() != null) {
			init();
			final Component template = getTemplateComponent(comp);
			Map attrMap = (Map) (template != null ? _compBindingMap.get(template) : _compBindingMap.get(comp));
			if (attrMap != null) {
				saveAttrs(comp, attrMap.values());
			}

			//save kids of this component
			for(final Iterator it = comp.getChildren().iterator(); it.hasNext();) {
				saveComponent((Component) it.next()); //recursive
			}
		}
	}
	
	/** Load all value from data beans to UI components. */
	public void loadAll() {
		init();
		for (final Iterator it = _compBindingMap.keySet().iterator(); it.hasNext(); ) {
			final Component comp = (Component) it.next();
			if (isTemplate(comp)) { //do via model and renderer, so skip
				continue;
			}
			loadComponent(comp);
		}
	}
	
	/** Save all values from UI components to beans. */
	public void saveAll() {
		init();
		for (final Iterator it = _compBindingMap.keySet().iterator(); it.hasNext(); ) {
			final Component comp = (Component) it.next();
			if (isTemplate(comp)) { //do via model and renderer, so skip
				continue;
			}
			saveComponent(comp);
		}
	}

	private void loadAttrs(Component comp, Collection attrs) {
		for(final Iterator it = attrs.iterator(); it.hasNext();) {
			Binding binding = (Binding) it.next();
			binding.loadAttribute(comp);
		}
	}	

	private void saveAttrs(Component comp, Collection attrs) {
		for(final Iterator it = attrs.iterator(); it.hasNext();) {
			Binding binding = (Binding) it.next();
			binding.saveAttribute(comp);
		}
	}

	//late init
	protected void init() {
		if (!_init) {
			_init = true;
			
			//process template data binding
			List detachs = new ArrayList(_compBindingMap.size());
			for(final Iterator it = _compBindingMap.keySet().iterator(); it.hasNext();) {
				final Component comp = (Component) it.next();
				final Map attrMap = (Map) _compBindingMap.get(comp);
				
				//_var special case; meaning a template component
				if (attrMap.containsKey("_var")) {
					comp.setAttribute(VAR, ((Binding)attrMap.get("_var")).getBeanid());
					setupTemplate(comp, Boolean.TRUE);
					setupRenderer(comp);
					detachs.add(comp);
				}

				//register event handler
				registerEvents(comp, attrMap.values());
				
				//register beanid -> Binding(s) map
				registerBeanidBinding(comp, attrMap.values());
			}
			
			for(final Iterator it = detachs.iterator(); it.hasNext(); ) {
				final Component comp = (Component) it.next();
				comp.detach();
			}
		}
	}
	
	private void registerEvents(Component comp, Collection attrs) {
		for(final Iterator it = attrs.iterator(); it.hasNext();) {
			Binding binding = (Binding) it.next();
			binding.registerSaveEventListener(comp);
			binding.registerLoadEventListeners(comp);
		}
	}
	
	private void registerBeanidBinding(Component comp, Collection attrs) {
		for(final Iterator it = attrs.iterator(); it.hasNext();) {
			Binding binding = (Binding) it.next();
			if (binding.canLoad()) {
				final String beanid = binding.getBeanid();
				Set bindings = (Set) _beanBindingMap.get(beanid);
				if (bindings == null) {
					bindings = new LinkedHashSet(32);
					_beanBindingMap.put(beanid, bindings);
				}
				bindings.add(binding);
			}
		}
	}
	
//vv----------------------------------------
//:TODO: The following code is Component type tightly coupled, should change to use interface...

	//20061205, Henri Chen: Tightly coupled with Component type
	private void setupRenderer(Component template) {
		if (template instanceof Listitem) {
			final Listitem li = (Listitem) template;
			final Listbox lbx = li.getListbox();
			if (lbx.getItemRenderer() == null) {
				lbx.setItemRenderer(new Renderer(li));
			}
		}
	}
	
	//20061205, Henri Chen: Tightly coupled with Component type
	//get index of the specified component, if not a indexitem, return -1.
	private Object getListModelItem(Component comp, String beanid) {
		final Component indexitem = (Component) comp.getAttribute(INDEXITEM);
		if (indexitem != null && indexitem instanceof Listitem) {
			final Listitem li = (Listitem) indexitem;
			final String var = (String)getTemplateComponent(li).getAttribute(VAR);
			if (beanid.equals(var)) {
				final Listbox lbx = li.getListbox();
				final ListModel model = lbx.getModel();
				final int index = li.getIndex();
				return model.getElementAt(index);
			} else {
				return NA;
			}
		}
		throw new UiException("Unsupported collection item component for DataBinder: "+comp);
	}
	
//^^----------------------------------------------
	//get associated template component of a cloned component
	private Component getTemplateComponent(Component comp) {
		return (Component) comp.getAttribute(TEMPLATE);
	}
	
	//set associated template component of a cloned component
	private void setTemplateComponent(Component comp, Component template) {
		comp.setAttribute(TEMPLATE, template);
	}

	//whether a cloned item
	private boolean isClone(Component comp) {
		return getTemplateComponent(comp) != null;
	}

	//whether the specified component a template component
	private boolean isTemplate(Component comp) {
		return comp.getAttribute(ISTEMPLATE) == Boolean.TRUE;
	}

	//set the specified comp and its decendents to be template (or not)
	private void setupTemplate(Component comp, Boolean b) {
		comp.setAttribute(ISTEMPLATE, b);
		List kids = comp.getChildren();
		for(final Iterator it = kids.iterator(); it.hasNext(); ) {
			setupTemplate((Component) it.next(), b); //recursive
		}
	}

	//given reference cloned component, return assoicated cloned component of _comp
	private Object lookupComponentByReference(Object bean, Component ref, Component comp) {
		if (isTemplate(comp)) {
			final Component item = (Component) ref.getAttribute(INDEXITEM);
			final Map compMap = (Map) (item != null ? _cloneMap.get(item) : _cloneMap.get(bean));
			//ref could have not been rendered, so the associate _cloneMap has not been established.
			if (compMap != null) {
				return (Component) compMap.get(comp);
			} else {
				return NA;
			}
		} else {
			return comp;
		}
	}

	//parse token and return as a String[]
	protected String[] parseExpression(String expr, String seperator) {
		List list = myParseExpression(expr, seperator);
		if (list == null) {
			return null;
		}
		String[] results = new String[list.size()];
		int j = 0;
		for(final Iterator it = list.iterator(); it.hasNext(); ++j) {
			String result = (String) it.next();
			if (result != null) {
				result  = result.trim();
				if (result.length() == 0)
					result = null;
			}
			if (j == 0 && result == null) {
				return null;
			}
			results[j] = result;
		}
		return results;
	}
	
	private List myParseExpression(String expr, String separator) {
		if (expr == null) {
			return null;
		}
		List results = new ArrayList(5);
		while(true) {
			int j = expr.indexOf(separator);
			if (j < 0) {
				results.add(expr);
				return results;
			}
			results.add(expr.substring(0, j));

			if (expr.length() <= (j+1)) {
				return results;
			}
			expr = expr.substring(j+1);
		}
	}

	//assume no state is stored in TypeConverter
	private static TypeConverter lookupConverter(String clsName) {
		TypeConverter converter = (TypeConverter) _converterMap.get(clsName);
		if (converter == null) {
			try {
				converter = (TypeConverter) Classes.newInstanceByThread(clsName);
				_converterMap.put(clsName, converter);
			} catch (java.lang.reflect.InvocationTargetException ex) {
				throw UiException.Aide.wrap(ex);
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
		return converter;
	}

	private String[] mergeStringArray(String[] s1, String[] s2) {
		if (s2 == null) {
			return s1;
		}
		int sz1 = s1 == null ? 0 : s1.length;
		int sz2 = s2.length;
		String[] merge = new String[sz1 + sz2];
		
		if (sz1 > 0) {
			System.arraycopy(s1, 0, merge, 0, sz1);
		}
		if (sz2 > 0) {
			System.arraycopy(s2, 0, merge, sz1, sz2);
		}
		return merge;
	}
	
	//A binding association class.
	private class Binding {
		private Component _comp;
		private String _attr;
		private String _value;
		private String _beanid;
		private String _props; //a.b.c
		private String[] _loadWhenEvents;
		private String _saveWhenEvent;
		private boolean _canLoad = true; //default access to "load"
		private boolean _canSave;
		private TypeConverter _converter;
		
		/** Construtor to form a binding between UI component and backend data bean.
		 * @param comp The concerned component
		 * @param attr The attribute of the component to be associated.
		 * @param value The expression to associate the data bean.
		 * @param loadWhenEvents The event list when to load data.
		 * @param saveWhenEvent The event when to save data.
		 * @param access In the view of UI component: "load" load only, "both" load/save, "save" save only when doing
		 * data binding. null means using the default access natural of the component. e.g. Label.value is "load", 
		 * but Textbox.value is "both".
		 * @param converter The converter class used to convert classes between component and the associated bean.
		 * null means using the default class conversion method.
		 */
		private Binding(Component comp, String attr, String value, 
			String[] loadWhenEvents, String saveWhenEvent, String access, String converter) {
				
			_comp = comp;
			setAttr(attr);
			setValue(value);
			addLoadWhenEvents(loadWhenEvents);
			setSaveWhenEvent(saveWhenEvent);
			setAccess(access);
			setConverter(converter);
		}
		
		private void setAttr(String attr) {
			_attr = attr;
		}
		
		private void setValue(String value) {
			if (value != null) {
				String[] results = splitBeanid(value);
				_beanid = results[0];
				_props = results[1];
			}
		}
		
		private void setSaveWhenEvent(String saveWhenEvent) {
			if (saveWhenEvent != null) {
				_saveWhenEvent = saveWhenEvent;
			}
		}
		
		private void addLoadWhenEvents(String[] loadWhenEvents) {
			_loadWhenEvents = mergeStringArray(_loadWhenEvents, loadWhenEvents);
		}

		private void setAccess(String access) {
			if (access == null) { //default access to load
				return;
			}
			
			if ("both".equals(access)) {
				_canLoad = true;
				_canSave = true;
			} else if ("load".equals(access)) {
				_canLoad = true;
				_canSave = false;
			} else if ("save".equals(access)) {
				_canLoad = false;
				_canSave = true;
			} else if ("none".equals(access)) { //unknow access mode
				_canLoad = false;
				_canSave = false;
			} else {
				throw new UiException("Unknown DataBinder access mode. Should be \"both\", \"load\", \"save\", or \"none\": "+access);
			}
		}
		
		private void setConverter(String converter) {
			if (converter != null) {
				_converter = lookupConverter(converter);
			}
		}			
		
		private String getBeanid() {
			return _beanid;
		}
		
		private String getValue() {
			return _beanid+(_props == null ? "" : ("."+_props));
		}
		
		private String getAttr() {
			return _attr;
		}
		
		//register events when to load component value from the bean
		private void registerLoadEventListeners(Component comp) {
			if (_loadWhenEvents != null) {
				for(int j = 0; j < _loadWhenEvents.length; ++j) {
					String expr = _loadWhenEvents[j];
					String[] results = splitBeanid(expr); //[0] bean id or bean path, [1] event name
					Component target = (Component) ("self".equals(results[0]) ? comp : lookupBean(comp, results[0]));
					registerLoadEventListener(results[1], target, comp);
				}
			}
		}

		//register events when to save component value to the bean.
		private void registerSaveEventListener(Component comp) {
			if (_saveWhenEvent != null) {
				String expr = _saveWhenEvent;
				String[] results = splitBeanid(expr); //[0] bean id or bean path, [1] event name
				Component target = (Component) ("self".equals(results[0]) ? comp : lookupBean(comp, results[0]));
				registerSaveEventListener(results[1], target, comp);
			}
		}

		private void registerLoadEventListener(String eventName, Component target, Component dataTarget) {
			target.addEventListener(eventName, new LoadEventListener(dataTarget));
		}

		private void registerSaveEventListener(String eventName, Component target, Component dataTarget) {
			target.addEventListener(eventName, new SaveEventListener(dataTarget));
		}
		
		private boolean canLoad() {
			return _canLoad;
		}
		
		private boolean canSave() {
			return _canSave;
		}

		private void loadAttributeByReference(Component comp, Object bean) {
			final Object clone = lookupComponentByReference(bean, comp, _comp);
			if (clone != NA) {
				loadAttribute((Component)clone);
			}
		}
		
		//link same beanid together
		private void registerSameBeanid(Object bean) {
			if (!canLoad()) {
				return;
			}
			//register beanid
			Set beanidSet = null;
			beanidSet = (Set) _sameBeanidMap.get(bean);
			if (beanidSet == null) {
				beanidSet = (Set) _sameBeanidMap.get(_beanid);
			}
			if (beanidSet == null) {
				beanidSet = new HashSet(5);
			}
			if (bean != null) {
				_sameBeanidMap.put(bean, beanidSet);
			}
			_sameBeanidMap.put(_beanid, beanidSet);
			beanidSet.add(_beanid);
		}			
		
		private void loadAttribute(Component comp) {
			if (!canLoad()) { //cannot load , skip
				return;
			}
			myLoadAttribute(comp);
		}
		private void myLoadAttribute(Component comp) {
			if ( _attr.startsWith("_") || comp.getPage() == null) { //cannot load or a control attribute, skip
				return;
			}
			Object val = null;
			try {
				Object bean = lookupBean(comp, _beanid);

				registerSameBeanid(bean);
								
				val = (_props == null || bean == null) ? bean : Fields.getField(bean, _props);
				if (_converter != null) {
					val = _converter.coerceToUi(val, comp);
				}
				Fields.setField(comp, _attr, val, _converter == null);
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
						Fields.setField(comp, "rawValue", val, _converter == null);
					} catch (Exception ex1) {
						//exception
						throw ex;
					}
				} else {
					throw ex;
				}
			}
		}
					
		private void saveAttribute(Component comp) {
			if (!canSave()) { //cannot save, skip
				return;
			}
			mySaveAttribute(comp);
		}
		
		private void mySaveAttribute(Component comp) {
			if (_attr.startsWith("_") || comp.getPage() == null) { //cannot save or a control attribute, skip
				return;
			}
			try {
				final Object bean = lookupBean(comp, _beanid);
				Object val = Fields.getField(comp, _attr);
				if (_converter != null) {
					val = _converter.coerceToBean(val, comp);
				}

				if (_props == null) { //assign back to where bean is stored
					if (!isClone(comp)) {
						if (_beans.containsKey(_beanid)) {
							_beans.put(_beanid, val);
						} else {
							comp.setVariable(_beanid, val, false);
						}
					}
					
					//load what is changed
					//bean == val, nothing to load
					//bean != null && val == null, get list of bean(remove list of bean), load the list, .
					//bean == null && val != null, get list of beanid, load the list, register binding with val, add val as binding key.
					//bean != null && val != null, get list of bean, load the list, register binding with val, change binding key from bean to val.
					if (bean != val) {
						if (val == null) {
							final Set beanidSet = (Set) _sameBeanidMap.remove(bean);
							loadDependencies(comp, val, beanidSet);
						} else {
							Set beanidSet = (Set) _sameBeanidMap.get(bean == null ? _beanid : bean);
							if (beanidSet != null) {
								//merge beanidSet
								final Set valBeanidSet = (Set) _sameBeanidMap.get(val);
								if (valBeanidSet != null) {
									valBeanidSet.addAll(beanidSet);
									beanidSet = valBeanidSet;
								} else {
									_sameBeanidMap.put(val, beanidSet);
								}
							}
							loadDependencies(comp, val, beanidSet);
							registerSameBeanid(val);
						}
					}
				} else if (bean != null) {
					Fields.setField(bean, _props, val, _converter == null);
					final Set beanidSet = (Set) _sameBeanidMap.get(bean);
					loadDependencies(comp, bean, beanidSet);
					registerSameBeanid(bean);
				} else {
					throw new UiException("saveAttribute: Cannot find the specified bean: "+_beanid+" in "+getValue());
				}
/*				
				final String value = getValue();
				final String[] keys = parseExpression(value, ".");
				final List bindings = _dependency.getAllBindings(keys);
				for(final Iterator it = bindings.iterator(); it.hasNext();) {
					final Binding binding = (Binding) it.next();
					if (binding == this) { //don't load that just save, skip
						continue;
					}
					binding.loadAttributeByReference(comp);
				}
*/
			} catch (ClassCastException ex) {
				throw UiException.Aide.wrap(ex);
			} catch (NoSuchMethodException ex) {
				throw UiException.Aide.wrap(ex);
			} catch (ModificationException ex) {
				throw UiException.Aide.wrap(ex);
			}
		}

		private void loadDependencies(Component comp, Object bean, Set beanidSet) {
			//automatically load back dependent component
			if (bean instanceof Component) {
				loadComponent((Component) bean);
			}
			
			//same beanid must be load back
			if (beanidSet != null) {
				for(final Iterator its = beanidSet.iterator(); its.hasNext();) {
					final String beanid = (String) its.next();
					final Set bindings = (Set) _beanBindingMap.get(beanid);
					if (bindings != null) {
						for(final Iterator it = bindings.iterator(); it.hasNext();) {
							final Binding binding = (Binding) it.next();
							if (binding == this) { //don't load that just save, skip
								continue;
							}
							binding.loadAttributeByReference(comp, bean);
						}
					}
				}
			}
		}
		
		private Object lookupBean(Component comp, String beanid) {
			//check collection template special case first
			Object bean = NA;
			if (isClone(comp)) {
				bean = getListModelItem(comp, beanid);
			}
			
			if (bean == NA) { //not available 
				//fetch the bean object
				if (_beans.containsKey(beanid)) {
					bean = _beans.get(beanid);
				} else if (beanid.startsWith("/")) { //a component Path in ID Space
					bean = Path.getComponent(beanid);	
				} else {
					bean = comp.getVariable(beanid, false);
				}
			}

			return bean;
		}

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
			
/*			public Component getDataTarget() {
				return _dataTarget;
			}
*/			
			public boolean isAsap() {
				return true;
			}
		}
			
		private class LoadEventListener extends BaseEventListener {
			public LoadEventListener(Component dataTarget) {
				super(dataTarget);
			}
			public void onEvent(Event event) {
				final Object dataTarget = lookupComponentByReference(null, event.getTarget(), _dataTarget);
				if (dataTarget != NA) {
					myLoadAttribute((Component )dataTarget);
				}
			}
		}

		private class SaveEventListener extends BaseEventListener {
			public SaveEventListener(Component dataTarget) {
				super(dataTarget);
			}
			public void onEvent(Event event) {
				final Object dataTarget = lookupComponentByReference(null, event.getTarget(), _dataTarget);
				if (dataTarget != NA) {
					mySaveAttribute((Component )dataTarget);
				}
			}
		}
	}

	private class Renderer implements org.zkoss.zul.ListitemRenderer {
		private Listitem _template;
		
		private Renderer(Listitem template) {
			_template = template;
		}
	    public void render(Listitem item, java.lang.Object bean) {
			//clone from template
	        final Listitem clone = (Listitem)_template.clone();
	        setupTemplate(clone, null); //not template for cloned component tree
	        
	        //copy children into item from clone
	        final List clonekids = clone.getChildren();

	        item.getChildren().clear();
	        //addAll will cause kids to move parent and thus change clonekids, must make a copy
	        item.getChildren().addAll(new ArrayList(clonekids)); 
	        
	        //setup the clone listitem and collect comp table for template
	        Map compMap = new HashMap(7);
	        setupClone(item, _template, item, compMap);
	        
	        //setup cloneMap for tempalte
	        _cloneMap.put(item, compMap);
	        if (bean != null) {
	        	_cloneMap.put(bean, compMap);
	        }

	        //apply the data binding
	        loadComponent(item);
	    }
	
		//link cloned to template, collection Save & Load Event mapping information, remove id
		private void setupClone(Component comp, Component template, Component item, Map compMap) {
			if (_compBindingMap.containsKey(template)) { //as long as there is a binding
				compMap.put(template, comp);
			}
			setTemplateComponent(comp, template);
			comp.setAttribute(INDEXITEM, item);
			comp.setId("@"+comp.getUuid()); //init id to @uuid to avoid duplicate id issue
			
	        //setup clone kids
	        final Iterator itt = template.getChildren().iterator();
	        final Iterator itc = comp.getChildren().iterator();
	        while (itt.hasNext()) {
	        	final Component t = (Component) itt.next();
	        	final Component c = (Component) itc.next();
	        	setupClone(c, t, item, compMap);	//recursive
	        }
	    }
	}
	/*
	//dependency Node, when a bean saveXXX, the associate loadXXX should be called to reflect the UI component
	private static class Node {
		private List _bindingList;
		private Map _kids = new LinkedHashMap(7);
		
		public void add(String[] keys, Binding binding) {
			insert(keys, binding, 0);
		}
		
		public List getAllBindings(String[] keys) {
			final Node node = locate(keys, 0);
			if (node != null) {
				final List bindings = new ArrayList(32);
				node.collectBindings(bindings);
				return bindings;
			}
			return null;
		}

		private void addBinding(Binding binding) {
			if (_bindingList == null) {
				_bindingList = new ArrayList(8);
			}
			_bindingList.add(binding);
		}
		
		private void insert(String[] keys, Binding binding, int level) {
			final String key = keys[level];
			Node next = getKidNode(key);
			if (next == null) {
				next = new Node();
				addKidNode(key, next);
			}
			if (keys.length <= (level+1)) {
				next.addBinding(binding);
			} else {
				next.insert(keys, binding, level+1); //recursive
			}
		}
		
		private Node locate(String[] keys, int level) {
			final String key = keys[level];
			Node next = getKidNode(key);
			if (keys.length <= (level+1) || next == null) {
				return next;
			} else {
				return next.locate(keys, level+1); //recursive
			}
		}
		
		private void collectBindings(List list) {
			if (_bindingList != null) {
				list.addAll(_bindingList);
			}
			for(final Iterator it = _kids.values().iterator(); it.hasNext(); ) {
				final Node kid = (Node) it.next();
				kid.collectBindings(list); //recursive
			}
		}
		
		private void addKidNode(String key, Node node) {
			_kids.put(key, node);
		}
		
		private Node getKidNode(String key) {
			return (Node) _kids.get(key);
		}
	}	
	*/
}
