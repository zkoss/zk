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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.lang.Classes;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.util.ModificationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.Express;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.impl.InputElement;

/**
 * A Data Binding that associate component+attr to an bean expression.
 *
 * @author Henri
 * @since 3.0.0
 */
public class Binding implements java.io.Serializable {
	private static final long serialVersionUID = 200808191512L;
	private DataBinder _binder;
	private Component _comp;
	private String _attr;
	private String _expression; //the bean expression
	private LinkedHashSet _loadWhenEvents;
	private LinkedHashSet _loadAfterEvents;
	private LinkedHashSet _saveWhenEvents;
	private LinkedHashSet _saveAfterEvents; 
	private boolean _loadable = true;
	private boolean _savable;
	private TypeConverter _converter;
	private String[] _paths; //bean reference path (a.b.c)
	private Map _args; //generic arguments
	
	/** Constrcutor to form a binding between UI component and backend data bean.
	 * @param binder the associated Data Binder.
	 * @param comp The concerned component
	 * @param attr The component attribute
	 * @param expr The bean expression.
	 * @param loadWhenEvents The event set when to load data.
	 * @param saveWhenEvents The event set when to save data.
	 * @param access In the view of UI component: "load" load only, "both" load/save, 
	 *	"save" save only when doing data binding. null means using the default access 
	 *	natural of the component. e.g. Label.expr is "load", but Textbox.expr is "both".
	 * @param converter The converter class used to convert classes between component attribute 
	 * and the associated bean expression. null means using the default class conversion method.
	 */
	/*package*/ Binding(DataBinder binder, Component comp, String attr, String expr, 
		LinkedHashSet loadWhenEvents, LinkedHashSet saveWhenEvents, String access, String converter) {
		this(binder, comp, attr, expr, loadWhenEvents, saveWhenEvents, access, converter, null, null, null);
	}
	
	/** Constrcutor to form a binding between UI component and backend data bean.
	 * @param binder the associated Data Binder.
	 * @param comp The concerned component
	 * @param attr The component attribute
	 * @param expr The bean expression.
	 * @param loadWhenEvents The event set when to load data.
	 * @param saveWhenEvents The event set when to save data.
	 * @param access In the view of UI component: "load" load only, "both" load/save, 
	 *	"save" save only when doing data binding. null means using the default access 
	 *	natural of the component. e.g. Label.expr is "load", but Textbox.expr is "both".
	 * @param converter The converter class used to convert classes between component attribute 
	 * and the associated bean expression. null means using the default class conversion method.
	 * @param args generic arguments
	 * @since 3.1
	 */
	/*package*/ Binding(DataBinder binder, Component comp, String attr, String expr, 
		LinkedHashSet loadWhenEvents, LinkedHashSet saveWhenEvents, String access, String converter, Map args) {
		this(binder, comp, attr, expr, loadWhenEvents, saveWhenEvents, access, converter, args, null, null);
	}
	
	/** Constrcutor to form a binding between UI component and backend data bean.
	 * @param binder the associated Data Binder.
	 * @param comp The concerned component
	 * @param attr The component attribute
	 * @param expr The bean expression.
	 * @param loadWhenEvents The event set when to load data.
	 * @param saveWhenEvents The event set when to save data.
	 * @param access In the view of UI component: "load" load only, "both" load/save, 
	 *	"save" save only when doing data binding. null means using the default access 
	 *	natural of the component. e.g. Label.expr is "load", but Textbox.expr is "both".
	 * @param converter The converter class used to convert classes between component attribute 
	 * and the associated bean expression. null means using the default class conversion method.
	 * @param args generic arguments
	 * @param loadAfterEvents the event set when to load data after
	 * @param saveAfterEvents the event set when to save data after
	 * @since 3.6.1
	 */
	/*package*/ Binding(DataBinder binder, Component comp, String attr, String expr, 
		LinkedHashSet loadWhenEvents, LinkedHashSet saveWhenEvents, String access, String converter, Map args,
		LinkedHashSet loadAfterEvents, LinkedHashSet saveAfterEvents) {
		_binder = binder;
		_comp = comp;
		setAttr(attr);
		setExpression(expr);
		setLoadWhenEvents(loadWhenEvents);
		setLoadAfterEvents(loadAfterEvents);
		setSaveWhenEvents(saveWhenEvents);
		setSaveAfterEvents(saveAfterEvents);
		setAccess(access);
		setConverter(converter);
		setArgs(args);
	}

	/** Gets the associated Data Binder of this Binding.
	 */
	public DataBinder getBinder() {
		return _binder;
	}
	
	/** Gets the associated Component of this Binding.
	 */
	public Component getComponent() {
		return _comp;
	}
	
	/** Set component attribute name.
	 * @param attr component attribute.
	 */	
	/*package*/void setAttr(String attr) {
		_attr = attr;
	}

	/** Get component attribute name.
	 */
	public String getAttr() {
		return _attr;
	}
	
	/*package*/void setArgs(Map args) {
		_args = args;
	}
	
	/** Get generic arguments.
	 * 
	 */
	public Map getArgs() {
		return _args;
	}
	
	/** Set bean expression (a.b.c).
	 */
	/*package*/ void setExpression(String expr) {
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
	
	/**
	 * Internal methods. DO NOT USE THIS. 
	 * Get bean reference paths.
	 */
	/*package*/ String[] getPaths() {
		return _paths;
	}
	
	/** Set save-when event expression.
	 * @param saveWhenEvents the save-when expression.
	 */
	/*package*/ void setSaveWhenEvents(LinkedHashSet saveWhenEvents) {
		_saveWhenEvents = saveWhenEvents;
	}
	
	/** Get save-when event expression.
	 */
	public Set getSaveWhenEvents() {
		return _saveWhenEvents;
	}

	/** Set save-after event expression.
	 * @param saveAfterEvents the save-after expression.
	 */
	/*package*/ void setSaveAfterEvents(LinkedHashSet saveAfterEvents) {
		_saveAfterEvents = saveAfterEvents;
	}
	
	/** Get save-after event expression.
	 */
	public Set getAfterWhenEvents() {
		return _saveAfterEvents;
	}
	
	
	/** Add load-when event expression.
	 * @param loadWhenEvent the load-when expression.
	 */
	/*package*/ void setLoadWhenEvents(LinkedHashSet loadWhenEvents) {
		_loadWhenEvents = loadWhenEvents;
	}
	
	/** Get load-when event expression set.
	 */
	public LinkedHashSet getLoadWhenEvents() {
		return _loadWhenEvents;
	}
	
	/** Add load-after event expression.
	 * 
	 * @param loadAfterEvents the load-after expression.
	 */
	/*package*/ void setLoadAfterEvents(LinkedHashSet loadAfterEvents) {
		_loadAfterEvents = loadAfterEvents;
	}
	
	/** Get load-after event expression set.
	 */
	public LinkedHashSet getLoadAfterEvents() {
		return _loadAfterEvents;
	}

	/** Set accessibility.
	 */
	/*package*/ void setAccess(String access) {
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
		} else if ("none".equals(access)) { 
			_loadable = false;
			_savable = false;
		} else {//unknow access mode
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
	/*package*/ void setConverter(String cvtClsName) {
		if (cvtClsName != null) {
			//bug #2129992
			Class cls = null;
			if (_comp == null || _comp.getPage() == null) {
				try {
					cls = Classes.forNameByThread(cvtClsName);
				} catch (ClassNotFoundException ex) {
					throw UiException.Aide.wrap(ex);
				}
			} else {
				cls = _comp.getPage().getZScriptClass(cvtClsName); 
				if (cls == null) {
					throw UiException.Aide.wrap(new ClassNotFoundException(cvtClsName));
				}
			}
			try {
				_converter = (TypeConverter) cls.newInstance();
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
		if (!isLoadable() 
				|| _attr.startsWith("_") 
				|| DataBinder.isTemplate(comp)
				|| comp == null //bug #1941947 Cannot find associated CollectionItem
				|| comp.getPage() == null) { 
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
		if (!isLoadable() || _attr.startsWith("_") || DataBinder.isTemplate(comp) || comp.getPage() == null) { 
			return; //cannot load, a control attribute, or a detached component, skip!
		}
		myLoadAttribute(comp, bean);
	}
	
	private void myLoadAttribute(Component comp, Object bean) {
		try {
			//since 3.1, 20080416, support bindingArgs for non-supported tag
			//bug #2803575, merge bindingArgs together since a component can have
			//multiple bindings on different attributes.
			Map bindArgs = (Map) comp.getAttribute(DataBinder.ARGS);
			if (bindArgs == null) {
				bindArgs = new HashMap();
				comp.setAttribute(DataBinder.ARGS, bindArgs);
			}
			if (_args != null) {
				bindArgs.putAll(_args);
				comp.setAttribute(_attr+"_"+DataBinder.ARGS, _args);
			}
			
			if (_converter != null) {
				bean = _converter.coerceToUi(bean, comp);
				if (bean == TypeConverter.IGNORE)
					return; //ignore, so don't do Fields.set()
			}
			
			//Bug #1876198 Error msg appears when load page (databind+CustomConstraint)
			//catching WrongValueException no longer works, check special case and 
			//use setRowValue() method directly
			if ((comp instanceof InputElement) && "value".equals(_attr)) {
				Object value = bean;
				try { //Bug 1879389
					final Method m = comp.getClass().getMethod("getValue", null);
					value = Classes.coerce(m.getReturnType(), bean);
				} catch (NoSuchMethodException ex) { //ignore it
				}
				Fields.set(comp, "rawValue", value, _converter == null);
			} else {
				Fields.set(comp, _attr, bean, _converter == null);
			}
		} catch (ClassCastException ex) {
			throw UiException.Aide.wrap(ex);
		} catch (NoSuchMethodException ex) {
			//Bug #1813278, Annotations do not work with xhtml tags
			if (comp instanceof DynamicPropertied) {
				final DynamicPropertied dpcomp = (DynamicPropertied) comp;
 				if (dpcomp.hasDynamicProperty(_attr)) {
					//no way to know destination type of the property, use bean as is
 					dpcomp.setDynamicProperty(_attr, bean);
 				} else {
 					throw UiException.Aide.wrap(ex);
 				}
			} else {
				throw UiException.Aide.wrap(ex);
			}
		} catch (ModificationException ex) {
			throw UiException.Aide.wrap(ex);

		//Bug #1876198 Error msg appears when load page (databind+CustomConstraint)
		//catching WrongValueException no longer works, so mark it out
		/*} catch (WrongValueException ex) {
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
		*/
		}
	}

	/** save into bean value from the attribute of the specified component.
	 * @param comp the component.
	 */
	public void saveAttribute(Component comp) {
		final Object[] vals = getAttributeValues(comp);
		if (vals != null)
			saveAttributeValue(comp, vals, null);
	}
	
	private void saveAttributeValue(Component comp, Object[] vals, List loadOnSaveInfos) {
		if (vals == null) return;
		
		final Object val = vals[0];
		final Object rawval = vals[1];
		_binder.setBeanAndRegisterBeanSameNodes(comp, val, this, _expression, _converter == null, rawval, loadOnSaveInfos);
	}		
	
	/** Get converted value and original value of this Binding.
	 */
	private Object[] getAttributeValues(Component comp) {
		if (!isSavable() || _attr.startsWith("_") || DataBinder.isTemplate(comp) || comp.getPage() == null) { 
			return null; //cannot save, a control attribute, or a detached component, skip!
		}
		Object rawval = null;
		try {
			rawval = Fields.get(comp, _attr);
		} catch (NoSuchMethodException ex) {
			//Bug #1813278, Annotations do not work with xhtml tags
			if (comp instanceof DynamicPropertied) {
				final DynamicPropertied dpcomp = (DynamicPropertied) comp;
 				if (dpcomp.hasDynamicProperty(_attr)) {
 					rawval = dpcomp.getDynamicProperty(_attr);
 				} else {
 					throw UiException.Aide.wrap(ex);
 				}
			} else {
				throw UiException.Aide.wrap(ex);
			}
		}
		try {
			final Object val = (_converter == null) ? rawval : _converter.coerceToBean(rawval, comp);
			return val == TypeConverter.IGNORE ? null : new Object[] {val, rawval};
		} catch (ClassCastException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}		
	
	/*package*/ void registerSaveEvents(Component comp) {
		if (isSavable()) { //bug 1804356
			if (_saveWhenEvents != null) { 
				for(final Iterator it = _saveWhenEvents.iterator(); it.hasNext(); ) {
					final String expr = (String) it.next();
					final Object[] objs =
						ComponentsCtrl.parseEventExpression(comp, expr, comp, false);
					//objs[0] component, objs[1] event name
					final Component target = (Component) objs[0];
					final String evtname = (String) objs[1];
	
					SaveEventListener listener = (SaveEventListener)
						target.getAttribute("zk.SaveEventListener."+evtname);
					if (listener == null) {
						listener = new SaveEventListener();
						target.setAttribute("zk.SaveEventListener."+evtname, listener);
						target.addEventListener(evtname, listener);
					}
					listener.addDataTarget(this, comp);
				}
			}
			if (_saveAfterEvents != null) {
				for(final Iterator it = _saveAfterEvents.iterator(); it.hasNext(); ) {
					final String expr = (String) it.next();
					final Object[] objs =
						ComponentsCtrl.parseEventExpression(comp, expr, comp, false);
					//objs[0] component, objs[1] event name
					final Component target = (Component) objs[0];
					final String evtname = (String) objs[1];
	
					SaveAfterEventListener listener = (SaveAfterEventListener)
						target.getAttribute("zk.SaveAfterEventListener."+evtname);
					if (listener == null) {
						listener = new SaveAfterEventListener();
						target.setAttribute("zk.SaveAfterEventListener."+evtname, listener);
						target.addEventListener(evtname, listener);
						target.addEventListener(evtname+"SaveAfter", listener);
					}
					listener.addDataTarget(this, comp);
				}
			}
		}
	}
	
	/*package*/ void registerLoadEvents(Component comp) {
		if (isLoadable()) { //bug 1804356
			if (_loadWhenEvents != null) {
				for(final Iterator it = _loadWhenEvents.iterator(); it.hasNext(); ) {
					final String expr = (String) it.next();
					final Object[] objs =
						ComponentsCtrl.parseEventExpression(comp, expr, comp, false);
					//objs[0] component, objs[1] event name
					final Component target = (Component) objs[0];
					final String evtname = (String) objs[1];
					
					LoadEventListener listener = (LoadEventListener)
						target.getAttribute("zk.LoadEventListener."+evtname);
					if (listener == null) {
						listener = new LoadEventListener();
						target.setAttribute("zk.LoadEventListener."+evtname, listener);
						target.addEventListener(evtname, listener);
					}
					listener.addDataTarget(this, comp);
				}
			} 
			if (_loadAfterEvents != null) {
				for(final Iterator it = _loadAfterEvents.iterator(); it.hasNext(); ) {
					final String expr = (String) it.next();
					final Object[] objs =
						ComponentsCtrl.parseEventExpression(comp, expr, comp, false);
					//objs[0] component, objs[1] event name
					final Component target = (Component) objs[0];
					final String evtname = (String) objs[1];
					
					LoadAfterEventListener listener = (LoadAfterEventListener)
						target.getAttribute("zk.LoadAfterEventListener."+evtname);
					if (listener == null) {
						listener = new LoadAfterEventListener();
						target.setAttribute("zk.LoadAfterEventListener."+evtname, listener);
						target.addEventListener(evtname, listener);
						target.addEventListener(evtname+"LoadAfter", listener);
					}
					listener.addDataTarget(this, comp);
				}
			}
		}
	}
					
	//-- Object --//
	public String toString() {
		return "[binder:"+_binder+", comp:"+_comp+", attr:"+_attr+", expr:"+_expression
			+", load-when:"+_loadWhenEvents+", save-when:"+_saveWhenEvents
			+", load-after:"+_loadAfterEvents+", save-after:"+_saveAfterEvents
			+", load:"+_loadable+", save:"+_savable+", converter:"+_converter+"]";
	}
	
	private static class BindingInfo implements Serializable {
		private static final long serialVersionUID = 200808191315L;
		private Binding _binding;
		private Component _comp;
		private Object[] _vals;
		
		public BindingInfo(Binding binding, Component comp, Object[] vals) {
			_binding = binding;
			_comp = comp;
			_vals = vals;
		}
		
		public Component getComponent() {
			return _comp;
		}
		
		public Binding getBinding() {
			return _binding;
		}
		
		public Object[] getAttributeValues() {
			return _vals;
		}
	}
	
	private static abstract class BaseEventListener implements EventListener, java.io.Serializable {
		protected List _dataTargets;
		
		public BaseEventListener() {
			_dataTargets = new ArrayList(8);
		}
		
		public void addDataTarget(Binding binding, Component comp) {
			_dataTargets.add(new BindingInfo(binding, comp, null));
		}
	}
	
	private abstract static class BaseLoadEventListener extends BaseEventListener {
		public BaseLoadEventListener() {
			super();
		}
		protected void handleEvent(Event event) {
			for(final Iterator it = _dataTargets.iterator();it.hasNext();) {
				final BindingInfo bi = (BindingInfo) it.next();
				final Component dt = bi.getComponent();
				final Binding binding = bi.getBinding();
				final Component dataTarget = DataBinder.isTemplate(dt) ? 
					DataBinder.lookupClone(event.getTarget(), dt) : dt;
				if (dataTarget != null) {
					binding.loadAttribute(dataTarget);
				}
			}
		}
	}
	
	private static class LoadEventListener extends BaseLoadEventListener implements Express {
		private static final long serialVersionUID = 200808191313L;
		public LoadEventListener() {
			super();
		}
		public void onEvent(Event event) {
			handleEvent(event);
		}
	}
	

	//since 3.6.1
	private static class LoadAfterEventListener extends BaseLoadEventListener {
		private static final long serialVersionUID = 20090423120513L;
		public LoadAfterEventListener() {
			super();
		}
		public void onEvent(Event event) {
			if (event instanceof AfterEvent) {
				handleEvent((Event) event.getData());
			} else { //post AfterEvent to make sure it is called after
				//enforce the event is the last processed
				Events.postEvent(-10100, new AfterEvent(event.getName()+"LoadAfter", event));
			}
		}
	}
	
	//since 3.6.1
	private static class AfterEvent extends Event {
		public AfterEvent(String evtnm, Event event) {
			super(evtnm, event.getTarget(), event);
		}
	}
	
	//since 3.6.1
	private static class SaveAfterEventListener extends BaseSaveEventListener {
		private static final long serialVersionUID = 20090423143051L;
		public SaveAfterEventListener() {
			super();
		}
		public void onEvent(Event event) {
			if (event instanceof AfterEvent) {
				handleEvent((Event) event.getData());
			} else { //post AfterEvent to make sure it is called after
				//enforce the event is the last processed
				Events.postEvent(-10100, new AfterEvent(event.getName()+"SaveAfter", event));
			}
		}
	}
	
	private static class SaveEventListener extends BaseSaveEventListener implements Express {
		private static final long serialVersionUID = 200808191313L;
		public SaveEventListener() {
			super();
		}
		public void onEvent(Event event) {
			handleEvent(event);
		}
	}
	
	private static abstract class BaseSaveEventListener extends BaseEventListener {
		public BaseSaveEventListener() {
			super();
		}
		protected void handleEvent(Event event) {
			final Component target = event.getTarget();
			final List tmplist = new ArrayList(_dataTargets.size());
			
			//fire onSave for each binding
			for(final Iterator it = _dataTargets.iterator();it.hasNext();) {
				final BindingInfo bi = (BindingInfo) it.next();
				final Component dt = bi.getComponent();
				final Binding binding = bi.getBinding();
				final DataBinder binder = binding.getBinder();
				final Component dataTarget = DataBinder.isTemplate(dt) ? 
					DataBinder.lookupClone(target, dt) : dt;
				//bug# 1904389: NullPointerException if save-when in collection binding
				//event.getTarget() might not inside the collection item (i.e. row, listitem, etc.)
				//then binder.lookupClone() will return null dataTarget.
				if (dataTarget != null) {
					final Object[] vals = binding.getAttributeValues(dataTarget);
					if (vals != null) {
						tmplist.add(new BindingInfo(binding, dataTarget, vals));
						Events.sendEvent(new BindingSaveEvent("onBindingSave", dataTarget, target, binding, vals[0]));
					}
				} else {
					//bi.getComponent a template and a null dataTarget, meaning all collection items has to
					//be handled. Search the owner to iterate all cloned items.
					final List clones = scanClones(binder, dt);
					for (final Iterator itc = clones.iterator(); itc.hasNext();) {
						final Component dataTarget1 = (Component)itc.next();
						final Object[] vals = binding.getAttributeValues(dataTarget1);
						if (vals != null) {
							tmplist.add(new BindingInfo(binding, dataTarget1, vals));
							Events.sendEvent(new BindingSaveEvent("onBindingSave", dataTarget1, target, binding, vals[0]));
						}
					}
					
				}
			}
			
			//fire onValidate for target component
			Events.sendEvent(new Event("onBindingValidate", target));
			
			//saveAttribute for each binding
			Component loadOnSaveProxy = null;
			Component dataTarget = null;
			final List loadOnSaveInfos = new ArrayList(tmplist.size());
			for(final Iterator it = tmplist.iterator();it.hasNext();) {
				final BindingInfo bi = (BindingInfo) it.next();
				dataTarget = bi.getComponent();
				final Binding binding = bi.getBinding();
				final Object[] vals = bi.getAttributeValues();
				binding.saveAttributeValue(dataTarget, vals, loadOnSaveInfos);
				if (loadOnSaveProxy == null && dataTarget.isListenerAvailable("onLoadOnSave", true)) {
					loadOnSaveProxy = dataTarget;
				}
			}
			
			//bug #1895856 : data binding LoadOnSave works only on the last save-when component
			//do loadOnSave
			//if (dataTarget != null) {
			//		Events.postEvent(new Event("onLoadOnSave", dataTarget, loadOnSaveInfos));
			//	}
			
			// (use first working dataTarget as proxy)
			if (loadOnSaveProxy != null) {
				Events.postEvent(new Event("onLoadOnSave", loadOnSaveProxy, loadOnSaveInfos));
			}
			
		}

		//scan up the component hierarchy until the real owner is found and collect all cloned components.
		private List scanClones(DataBinder binder, Component comp) {
			if (DataBinder.isTemplate(comp)) {
				final List owners = scanClones(binder, binder.getCollectionOwner(comp)); //recursive
				final List kidowners = new ArrayList(1024);
				for (final Iterator it = owners.iterator(); it.hasNext();) {
					final Component owner = (Component) it.next();
					final CollectionItem decor = binder.getCollectionItemByOwner(owner);
					//backward compatible, CollectionItemEx.getItems() is faster
					if (decor instanceof CollectionItemExt) {
						final CollectionItemExt decorex = (CollectionItemExt) decor;
						for (final Iterator iti = decorex.getItems(owner).iterator(); iti.hasNext();) {
							final Component item = (Component) iti.next();
							kidowners.add(DataBinder.lookupClone(item, comp));
						}
					} else {
						try {
							for (int j = 0; true; ++j) { //iterate until out of bound
								final Component item = decor.getComponentAtIndexByOwner(owner, j);
								kidowners.add(DataBinder.lookupClone(item, comp));
							}
						} catch (IndexOutOfBoundsException ex) {
							//ignore, iterate until out of bound
						}
					}
				}
				return kidowners;
			} else {
				final List owners = new ArrayList(1);
				owners.add(comp);
				return owners;
			}
		}
	}

}
