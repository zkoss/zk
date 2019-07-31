/* DataBinder.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Feb  1 18:27:18     2007, Created by Henri
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import static org.zkoss.lang.Generics.cast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Primitives;
import org.zkoss.lang.reflect.Fields;
import org.zkoss.zk.scripting.HierachicalAware;
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;

/**
 * The DataBinder used for binding ZK UI component and the backend data bean.
 *
 * @author Henri Chen
 * @deprecated As of release 7.0.0, replace with new ZK binding.
 */
public class DataBinder implements java.io.Serializable {
	public static final String LOAD_ON_SAVE_TRIGGER_COMPONENT = "zkoss.DataBinder.LOAD_ON_SAVE_TRIGGER_COMPONENT";
	private static final long serialVersionUID = 200808191508L;
	public static final String NULLIFY = "none"; //used to nullify default configuration
	public static final String ARGS = "bindingArgs"; //extra arguments specified in annotation
	public static final String VARNAME = "zkplus.databind.VARNAME"; //_var name
	public static final String TEMPLATEMAP = "zkplus.databind.TEMPLATEMAP"; // template -> clone
	public static final String TEMPLATE = "zkplus.databind.TEMPLATE"; //clone -> template
	private static final String OWNER = "zkplus.databind.OWNER"; //the collection owner of the template component
	private static final String ITEM = "zkplus.databind.ITEM"; //the collection item of the template component
	private static final String IAMOWNER = "zkplus.databind.IAMOWNER"; //I am the collection owner
	private static final String HASTEMPLATEOWNER = "zkplus.databind.HASTEMPLATEOWNER"; //whether has template owner (collection in collection)
	private static final Object NA = new Object();

	private Map<Component, Map<String, Binding>> _compBindingMap = new LinkedHashMap<Component, Map<String, Binding>>(
			29); //(comp, Map(attr, Binding))
	private Map<String, Object> _beans = new HashMap<String, Object>(29); //bean local to this DataBinder
	private Map<Object, Set<Object>> _beanSameNodes = new HashMap<Object, Set<Object>>(29); //(bean, Set(BindingNode)) bean same nodes, diff expression but actually hold the same bean
	private BindingNode _pathTree = new BindingNode("/", false, "/", false); //path dependency tree.
	private boolean _defaultConfig = true; //whether load default configuration from lang-addon.xml
	private boolean _init; //whether this databinder is initialized. 
	private EventListener _listener = new LoadOnSaveEventListener();
	//Databinder is init automatically when saveXXX or loadXxx is called

	protected Map<String, CollectionItem> _collectionItemMap = new HashMap<String, CollectionItem>(3);
	protected Map<String, CollectionItem> _collectionOwnerMap = new HashMap<String, CollectionItem>(3); //bug#1950313 F - 1764967 bug

	private boolean _loadOnSave = true; //whether firing the onLoadOnSave event to automate the loadOnSave operation 

	/**
	 * Sets whether this DataBinder shall do load-on-save automatically.
	 * @param b true to have this DataBinder shall do load-on-save automatically.
	 * @since 5.0.4
	 */
	public void setLoadOnSave(boolean b) {
		_loadOnSave = b;
	}

	/**
	 * Returns whether this DataBinder shall do load-on-save automatically(default is true).
	 * @return whether this DataBinder shall do load-on-save automatically(default is true).
	 * @since 5.0.4
	 */
	public boolean isLoadOnSave() {
		return _loadOnSave;
	}

	/** Binding bean to UI component. This is the same as 
	 * addBinding(Component comp, String attr, String expr, (List)null, (List)null, (String)null, (String)null). 
	 * @param comp The component to be associated.
	 * @param attr The attribute of the component to be associated.
	 * @param expr The expression to associate the data bean.
	 */
	public void addBinding(Component comp, String attr, String expr) {
		addBinding(comp, attr, expr, (List<String>) null, (List<String>) null, null, null);
	}

	/** Binding bean to UI component. 
	 * @param comp The component to be associated.
	 * @param attr The attribute of the component to be associated.
	 * @param expr The expression to associate the data bean.
	 * @param loadWhenEvents The event list when to load data.
	 * @param saveWhenEvent The event when to save data.
	 * @param access In the view of UI component: "load" load only, 
	 * "both" load/save, "save" save only when doing
	 * data binding. null means using the default access natural of the component. 
	 * e.g. Label.value is "load", but Textbox.value is "both".
	 * @param converter The converter class used to convert classes between component 
	 *  and the associated bean. null means using the default class conversion method.
	 */
	public void addBinding(Component comp, String attr, String expr, String[] loadWhenEvents, String saveWhenEvent,
			String access, String converter) {
		List<String> loadEvents = null;
		if (loadWhenEvents != null && loadWhenEvents.length > 0) {
			loadEvents = new ArrayList<String>(loadWhenEvents.length);
			for (int j = 0; j < loadWhenEvents.length; ++j) {
				loadEvents.add(loadWhenEvents[j]);
			}
		}
		addBinding(comp, attr, expr, loadEvents, saveWhenEvent, access, converter);
	}

	/** Binding bean to UI component. 
	 * @param comp The component to be associated.
	 * @param attr The attribute of the component to be associated.
	 * @param expr The expression to associate the data bean.
	 * @param loadWhenEvents The event list when to load data.
	 * @param saveWhenEvents The event when to save data.
	 * @param access In the view of UI component: "load" load only, 
	 * "both" load/save, "save" save only when doing
	 * data binding. null means using the default access natural of the component. 
	 * e.g. Label.value is "load", but Textbox.value is "both".
	 * @param converter The converter class used to convert classes between component 
	 *  and the associated bean. null means using the default class conversion method.
	 * @since 3.0.0
	 */
	public void addBinding(Component comp, String attr, String expr, String[] loadWhenEvents, String[] saveWhenEvents,
			String access, String converter) {
		List<String> loadEvents = null;
		if (loadWhenEvents != null && loadWhenEvents.length > 0) {
			loadEvents = new ArrayList<String>(loadWhenEvents.length);
			for (int j = 0; j < loadWhenEvents.length; ++j) {
				loadEvents.add(loadWhenEvents[j]);
			}
		}
		List<String> saveEvents = null;
		if (saveWhenEvents != null && saveWhenEvents.length > 0) {
			saveEvents = new ArrayList<String>(saveWhenEvents.length);
			for (int j = 0; j < saveWhenEvents.length; ++j) {
				saveEvents.add(saveWhenEvents[j]);
			}
		}
		addBinding(comp, attr, expr, loadEvents, saveEvents, access, converter);
	}

	/** Binding bean to UI component. 
	 * @param comp The component to be associated.
	 * @param attr The attribute of the component to be associated.
	 * @param expr The expression to associate the data bean.
	 * @param loadWhenEvents The event list when to load data.
	 * @param saveWhenEvent The event when to save data.
	 * @param access In the view of UI component: "load" load only, 
	 * "both" load/save, "save" save only when doing
	 * data binding. null means using the default access natural of the component. 
	 * e.g. Label.value is "load", but Textbox.value is "both".
	 * @param converter The converter class used to convert classes between component 
	 *  and the associated bean. null means using the default class conversion method.
	 */
	public void addBinding(Component comp, String attr, String expr, List<String> loadWhenEvents, String saveWhenEvent,
			String access, String converter) {
		List<String> saveEvents = new ArrayList<String>();
		saveEvents.add(saveWhenEvent);
		addBinding(comp, attr, expr, loadWhenEvents, saveEvents, access, converter);
	}

	/** Binding bean to UI component. 
	 * @param comp The component to be associated.
	 * @param attr The attribute of the component to be associated.
	 * @param expr The expression to associate the data bean.
	 * @param loadWhenEvents The event list when to load data.
	 * @param saveWhenEvents The event list when to save data.
	 * @param access In the view of UI component: "load" load only, 
	 * "both" load/save, "save" save only when doing
	 * data binding. null means using the default access natural of the component. 
	 * e.g. Label.value is "load", but Textbox.value is "both".
	 * @param converter The converter class used to convert classes between component 
	 *  and the associated bean. null means using the default class conversion method.
	 * @since 3.0.0
	 */
	public void addBinding(Component comp, String attr, String expr, List<String> loadWhenEvents,
			List<String> saveWhenEvents, String access, String converter) {
		addBinding(comp, attr, expr, loadWhenEvents, saveWhenEvents, access, converter, null, null, null);
	}

	/** Binding bean to UI component. 
	 * @param comp The component to be associated.
	 * @param attr The attribute of the component to be associated.
	 * @param expr The expression to associate the data bean.
	 * @param loadWhenEvents The event list when to load data.
	 * @param saveWhenEvents The event list when to save data.
	 * @param access In the view of UI component: "load" load only, 
	 * "both" load/save, "save" save only when doing
	 * data binding. null means using the default access natural of the component. 
	 * e.g. Label.value is "load", but Textbox.value is "both".
	 * @param converter The converter class used to convert classes between component 
	 *  and the associated bean. null means using the default class conversion method.
	 * @param args generic argument map for each binding.
	 * @since 3.5.0
	 */
	/*	public void addBinding(Component comp, String attr, String expr,
				List<String> loadWhenEvents, List<String> saveWhenEvents, String access, String converter, Map args) {
			addBinding(comp, attr, expr, loadWhenEvents, saveWhenEvents, access, converter, args, null, null);
		}
	*/
	/** Binding bean to UI component. 
	 * @param comp The component to be associated.
	 * @param attr The attribute of the component to be associated.
	 * @param expr The expression to associate the data bean.
	 * @param loadWhenEvents The event list when to load data.
	 * @param saveWhenEvents The event list when to save data.
	 * @param access In the view of UI component: "load" load only, 
	 * "both" load/save, "save" save only when doing
	 * data binding. null means using the default access natural of the component. 
	 * e.g. Label.value is "load", but Textbox.value is "both".
	 * @param converter The converter class used to convert classes between component 
	 *  and the associated bean. null means using the default class conversion method.
	 * @param args generic argument map for each binding.
	 * @param loadAfterEvents the event list when to load data after.
	 * @param saveAfterEvents the event list when to save data after.
	 * @since 3.6.1
	 */
	public void addBinding(Component comp, String attr, String expr, List<String> loadWhenEvents,
			List<String> saveWhenEvents, String access, String converter, Map<Object, Object> args,
			List<String> loadAfterEvents, List<String> saveAfterEvents) {
		//since 3.1, 20080416, Henri Chen: add a generic arguments map (string, string)

		//Since 3.0, 20070726, Henri Chen: we accept "each" to replace "_var" in collection data binding
		//Before 2.4.1
		//<a:bind _var="person">
		//<listitem...>
		//After 2.5
		//<listitem self="@{bind(each='person')}"...>
		//or <listitem self="@{each='person'}"...>
		if ("each".equals(attr)) {
			attr = "_var";
		}

		if (isDefaultConfig()) { //use default binding configuration
			//handle default-bind defined in lang-addon.xml
			Object[] objs = loadPropertyAnnotation(comp, attr, "default-bind");

			/* logically impossible to hold "expr" in default binding 
			if (expr == null && objs[0] != null) {
				expr = (String) objs[0];
			}
			*/

			if (loadWhenEvents == null && objs[1] != null) {
				loadWhenEvents = cast((List) objs[1]);
			}
			if (saveWhenEvents == null && objs[2] != null) {
				saveWhenEvents = cast((List) objs[2]);
			}
			if (access == null && objs[3] != null) {
				access = (String) objs[3];
			}
			if (converter == null && objs[4] != null) {
				converter = (String) objs[4];
			}
			if (args == null && objs[5] != null) {
				args = cast((Map) objs[5]);
			}
			if (loadAfterEvents == null && objs[6] != null) {
				loadAfterEvents = cast((List) objs[6]);
			}
			if (saveAfterEvents == null && objs[7] != null) {
				saveAfterEvents = cast((List) objs[7]);
			}
		}

		//nullify check
		LinkedHashSet<String> loadEvents = null;
		if (loadWhenEvents != null && loadWhenEvents.size() > 0) {
			loadEvents = new LinkedHashSet<String>(loadWhenEvents.size());
			for (String event : loadWhenEvents) {
				if (NULLIFY.equals(event)) {
					loadEvents.clear();
				} else {
					loadEvents.add(event);
				}
			}
			if (loadEvents.isEmpty()) {
				loadEvents = null;
			}
		}

		LinkedHashSet<String> lafterEvents = null;
		if (loadAfterEvents != null && loadAfterEvents.size() > 0) {
			lafterEvents = new LinkedHashSet<String>(loadAfterEvents.size());
			for (String event : loadAfterEvents) {
				if (NULLIFY.equals(event)) {
					lafterEvents.clear();
				} else {
					lafterEvents.add(event);
				}
			}
			if (lafterEvents.isEmpty()) {
				lafterEvents = null;
			}
		}

		LinkedHashSet<String> saveEvents = null;
		if (saveWhenEvents != null && saveWhenEvents.size() > 0) {
			saveEvents = new LinkedHashSet<String>(saveWhenEvents.size());
			for (String event : saveWhenEvents) {
				if (NULLIFY.equals(event)) {
					saveEvents.clear();
				} else {
					saveEvents.add(event);
				}
			}
			if (saveEvents.isEmpty()) {
				saveEvents = null;
			}
		}

		LinkedHashSet<String> safterEvents = null;
		if (saveAfterEvents != null && saveAfterEvents.size() > 0) {
			safterEvents = new LinkedHashSet<String>(saveAfterEvents.size());
			for (String event : saveAfterEvents) {
				if (NULLIFY.equals(event)) {
					safterEvents.clear();
				} else {
					safterEvents.add(event);
				}
			}
			if (safterEvents.isEmpty()) {
				safterEvents = null;
			}
		}

		//bug 2129730. Comment out the following to solve this bug
		//		if (NULLIFY.equals(access)) {
		//			access = null;
		//		}

		if (NULLIFY.equals(converter)) {
			converter = null;
		}

		Map<String, Binding> attrMap = _compBindingMap.get(comp);
		if (attrMap == null) {
			attrMap = new LinkedHashMap<String, Binding>(3);
			_compBindingMap.put(comp, attrMap);
		}

		if (attrMap.containsKey(attr)) { //override
			final Binding binding = attrMap.get(attr);
			binding.setExpression(expr);
			binding.setLoadWhenEvents(loadEvents);
			binding.setLoadAfterEvents(lafterEvents);
			binding.setSaveWhenEvents(saveEvents);
			binding.setSaveAfterEvents(safterEvents);
			binding.setAccess(access);
			binding.setConverter(converter);
		} else {
			attrMap.put(attr, new Binding(this, comp, attr, expr, loadEvents, saveEvents, access, converter, args,
					lafterEvents, safterEvents));
		}
	}

	/** Remove the binding associated with the attribute of the component.
	 * @param comp The component to be removed the data binding association.
	 * @param attr The attribute of the component to be removed the data binding association.
	 */
	public void removeBinding(Component comp, String attr) {
		//bug #2928837 Cannot remove bindable collection item from the DataBinder
		if (isClone(comp)) {
			comp = (Component) comp.getAttribute(TEMPLATE);
		}
		Map<String, Binding> attrMap = _compBindingMap.get(comp);
		if (attrMap != null) {
			attrMap.remove(attr);
		}
	}

	/** Given component and attr, return the associated {@link Binding}.
	 * @param comp the concerned component
	 * @param attr the concerned attribute
	 */
	public Binding getBinding(Component comp, String attr) {
		if (isClone(comp)) {
			comp = (Component) comp.getAttribute(TEMPLATE);
		}
		Map<String, Binding> attrMap = _compBindingMap.get(comp);
		return attrMap != null ? attrMap.get(attr) : null;
	}

	/** Given component, return the associated list of {@link Binding}s.
	 * @param comp the concerned component
	 */
	public Collection<Binding> getBindings(Component comp) {
		if (isClone(comp)) {
			comp = (Component) comp.getAttribute(TEMPLATE);
		}
		Map<String, Binding> attrMap = _compBindingMap.get(comp);
		return attrMap != null ? attrMap.values() : null;
	}

	/** Return all Bindings covered by this DataBinder
	 * @return all Bindings covered by this DataBinder.
	 * @since 3.5.2
	 */
	public Collection<Binding> getAllBindings() {
		final List<Binding> bindings = new ArrayList<Binding>(_compBindingMap.size() * 2);
		for (Map<String, Binding> map : _compBindingMap.values()) {
			bindings.addAll(map.values());
		}
		return bindings;
	}

	/** Whether this component associated with any bindings.
	 */
	public boolean existsBindings(Component comp) {
		if (isClone(comp)) {
			comp = (Component) comp.getAttribute(TEMPLATE);
		}
		return _compBindingMap.containsKey(comp);
	}

	/** Whether this component and attribute associated with a binding.
	 */
	public boolean existBinding(Component comp, String attr) {
		if (isClone(comp)) {
			comp = (Component) comp.getAttribute(TEMPLATE);
		}
		if (_compBindingMap.containsKey(comp)) {
			Map<String, Binding> attrMap = _compBindingMap.get(comp);
			return attrMap.containsKey(attr);
		}
		return false;
	}

	/** Whether use the default binding configuration.
	 */
	public boolean isDefaultConfig() {
		return _defaultConfig;
	}

	/** Whether use the default binding configuration.
	 */
	public void setDefaultConfig(boolean b) {
		_defaultConfig = b;
	}

	/** Bind a real bean object to the specified beanid. You might not need to call this method because this
	 * DataBinder would look up the variable via the {@link org.zkoss.zk.ui.Component#getAttributeOrFellow} method
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
		if (isTemplate(comp) || comp.getPage() == null) {
			return; //skip detached component
		}
		init();
		Binding binding = getBinding(comp, attr);
		if (binding != null) {
			binding.loadAttribute(comp);
		}
	}

	/** Save value from a specified attribute of the UI component to a data bean property.
	 * @param comp the UI component used to save value into backend data bean.
	 * @param attr the UI component attribute used to save value into backend data bean.
	 */
	public void saveAttribute(Component comp, String attr) {
		if (isTemplate(comp) || comp.getPage() == null) {
			return; //skip detached component
		}
		init();
		Binding binding = getBinding(comp, attr);
		if (binding != null) {
			binding.saveAttribute(comp);
		}
	}

	/** Load values from the data bean properties to all attributes of a specified UI component. 
	 * @param comp the UI component to be loaded value.
	 */
	public void loadComponent(Component comp) {
		init();
		if (loadComponent0(comp)) { //component detached, skip
			return;
		}

		//load kids of this component
		for (final Iterator it = comp.getChildren().iterator(); it.hasNext();) {
			loadComponent((Component) it.next()); //recursive
		}
	}

	private boolean loadComponent0(Component comp) {
		if (isTemplate(comp) || comp.getPage() == null) {
			return true; //skip detached component
		}
		final Collection bindings = getBindings(comp);
		if (bindings != null) {
			loadAttrs(comp, bindings);
		}
		return false;
	}

	/** Save values from all attributes of a specified UI component to data bean properties.
	 * @param comp the UI component used to save value into backend data bean.
	 */
	public void saveComponent(Component comp) {
		if (isTemplate(comp) || comp.getPage() == null) {
			return; //skip detached component
		}
		init();
		Collection bindings = getBindings(comp);
		if (bindings != null) {
			saveAttrs(comp, bindings);
		}

		//save kids of this component
		for (final Iterator it = comp.getChildren().iterator(); it.hasNext();) {
			saveComponent((Component) it.next()); //recursive
		}
	}

	/** Load all value from data beans to UI components. */
	public void loadAll() {
		init();
		for (Component comp : _compBindingMap.keySet()) {
			loadComponent0(comp);
		}
	}

	/** Save all values from UI components to beans. */
	public void saveAll() {
		init();
		for (Component comp : _compBindingMap.keySet()) {
			saveComponent(comp);
		}
	}

	private void loadAttrs(Component comp, Collection attrs) {
		for (final Iterator it = attrs.iterator(); it.hasNext();) {
			Binding binding = (Binding) it.next();
			binding.loadAttribute(comp);
		}
	}

	private void saveAttrs(Component comp, Collection attrs) {
		for (final Iterator it = attrs.iterator(); it.hasNext();) {
			Binding binding = (Binding) it.next();
			binding.saveAttribute(comp);
		}
	}

	//[0] expr, [1] loadWhenEvents, [2] saveWhenEvents, [3] access, [4] converter, [5] args, [6] loadAfterEvents, [7] saveAfterEvents
	protected Object[] loadPropertyAnnotation(Component comp, String propName, String bindName) {
		ComponentCtrl compCtrl = (ComponentCtrl) comp;
		Annotation ann = compCtrl.getAnnotation(propName, bindName);
		if (ann != null) {
			final Map<String, String[]> attrs = ann.getAttributes(); //(tag, tagExpr)
			List<String> loadWhenEvents = null;
			List<String> saveWhenEvents = null;
			List<String> loadAfterEvents = null;
			List<String> saveAfterEvents = null;
			String access = null;
			String converter = null;
			String expr = null;
			Map<Object, Object> args = null;
			for (Map.Entry<String, String[]> entry : attrs.entrySet()) {
				String tag = entry.getKey();
				String[] tagval = entry.getValue();
				String tagExpr;

				// ZK-1928: Converter Override (default-binding) causing "Array of attribute values not allowed "
				//				if (tagval.length != 1)
				//					throw new UiException("Array of attribute values not allowed, "+Objects.toString(tagval));

				// get the overridden one.
				tagExpr = tagval[tagval.length - 1];

				if ("save-when".equals(tag)) {
					saveWhenEvents = parseExpression(tagExpr, ",");
				} else if ("load-after".equals(tag)) {
					loadAfterEvents = parseExpression(tagExpr, ",");
				} else if ("access".equals(tag)) {
					access = tagExpr;
				} else if ("converter".equals(tag)) {
					converter = tagExpr;
				} else if ("load-when".equals(tag)) {
					loadWhenEvents = parseExpression(tagExpr, ",");
				} else if ("save-after".equals(tag)) {
					saveAfterEvents = parseExpression(tagExpr, ",");
				} else if ("value".equals(tag)) {
					expr = tagExpr;
				} else {
					if (args == null) {
						args = new HashMap<Object, Object>();
					}
					args.put(tag, tagExpr);
				}
			}
			return new Object[] { expr, loadWhenEvents, saveWhenEvents, access, converter, args, loadAfterEvents,
					saveAfterEvents };
		}
		return new Object[8];
	}

	//late init
	protected void init() {
		if (!_init) {
			_init = true;

			// init CollectionItem
			initCollectionItem();

			//setup all added bindings
			final Set<String> varnameSet = new HashSet<String>();
			final LinkedHashSet<Component> toBeDetached = new LinkedHashSet<Component>();
			for (Entry<Component, Map<String, Binding>> me : _compBindingMap.entrySet()) {
				final Component comp = me.getKey();
				final Map<String, Binding> attrMap = me.getValue();
				final Collection<Binding> bindings = attrMap.values();

				//_var special case; meaning a template component
				if (attrMap.containsKey("_var")) {
					comp.setAttribute(ITEM, comp);
					final Component owner = getComponentCollectionOwner(comp);
					//bug#1888911 databind and Grid in Grid not work when no _var in inner Grid
					owner.setAttribute(IAMOWNER, Boolean.TRUE);
					setupTemplateComponent(comp, owner); //setup as template components
					String varname = (attrMap.get("_var")).getExpression();
					varnameSet.add(varname);
					comp.setAttribute(VARNAME, varname);
					setupBindingRenderer(comp); //setup binding renderer
					toBeDetached.add(comp);
				}

				if (bindings != null) {
					//construct the path dependent tree
					setupPathTree(bindings, varnameSet);

					//register save-when event
					registerSaveEvents(comp, bindings);

					//register load-when events
					registerLoadEvents(comp, bindings);
				}
			}

			//detach template components so they will not interfere the visual part
			for (Component comp : toBeDetached) {
				comp.detach();
			}
		}
	}

	private void initCollectionItem() {
		addCollectionItem(Listitem.class, Listbox.class, new ListitemCollectionItem());
		addCollectionItem(Row.class, Grid.class, new RowCollectionItem());
		addCollectionItem(Comboitem.class, Combobox.class, new ComboitemCollectionItem());
	}

	/**
	 * Adds a CollectionItem for the specified item and owner component;
	 * e.g. Listitem and Listbox, Row and Grid, Comoboitem and Combobox.
	 * @see CollectionItem
	 * @since 3.0.5
	 * 
	 * @param item the item class
	 * @param owner the owner class
	 * @param decor the associated CollectionItem decorator
	 */
	public void addCollectionItem(Class item, Class owner, CollectionItem decor) {
		_collectionItemMap.put(item.getName(), decor);
		_collectionOwnerMap.put(owner.getName(), decor);
	}

	//get Collection owner of a given collection item.
	private Component getComponentCollectionOwner(Component comp) {
		CollectionItem decor = getBindingCollectionItem(comp);
		return decor.getComponentCollectionOwner(comp);
	}

	/**
	 * Returns a CollectionItem by the comp accordingly.
	 * @see CollectionItem
	 * @since 3.0.0
	 */
	protected CollectionItem getBindingCollectionItem(Component comp) {
		String name = comp.getClass().getName();
		if (comp instanceof Listitem) {
			name = Listitem.class.getName();
		} else if (comp instanceof Row) {
			name = Row.class.getName();
		} else if (comp instanceof Comboitem) {
			name = Comboitem.class.getName();
		}
		CollectionItem decorName = _collectionItemMap.get(name);
		if (decorName != null) {
			return decorName;
		} else {
			throw new UiException("Cannot find associated CollectionItem:" + comp);
		}
	}

	//Get CollectionItem per the given owner.
	//@since 3.0.4 
	//@since 3.0.5, bug#1950313 F - 1764967 bug
	/*package*/ CollectionItem getCollectionItemByOwner(Component comp) {
		final CollectionItem decorName = myGetCollectionItemByOwner(comp);
		if (decorName == null) {
			throw new UiException("Cannot find associated CollectionItem by owner: " + comp);
		}
		return decorName;
	}

	private CollectionItem myGetCollectionItemByOwner(Component comp) {
		String name = comp.getClass().getName();
		if (comp instanceof Listbox) {
			name = Listbox.class.getName();
		} else if (comp instanceof Grid) {
			name = Grid.class.getName();
		} else if (comp instanceof Combobox) {
			name = Combobox.class.getName();
		}
		CollectionItem decorName = _collectionOwnerMap.get(name);
		return decorName;
	}

	//get Collection owner of a given collection item.
	/*package*/ Component getCollectionOwner(Component comp) {
		if (isTemplate(comp)) {
			return (Component) comp.getAttribute(OWNER);
		}
		return getComponentCollectionOwner(comp);
	}

	//since 3.1
	//get associated clone of a given bean and template component
	private Component getCollectionItem(Component comp, Object bean, boolean isCollectionItem) {
		final Component[] comps = getCollectionItems(comp, bean, isCollectionItem);

		return comps.length == 0 ? null : comps[0];
	}

	//since 3.1
	//get associated clone components of a given bean and template component
	//note that same bean can be used in multiple components
	//assume comp is template component
	private Component[] getCollectionItems(Component comp, Object bean, boolean isCollectionItem) {
		Component owner = getCollectionOwner(comp);
		Component item = (Component) comp.getAttribute(DataBinder.ITEM);
		//For backward compatible, if by owner failed, try again with by item
		CollectionItem decor = myGetCollectionItemByOwner(owner);
		if (decor == null) {
			decor = getBindingCollectionItem(item);
		}
		final ListModel xmodel = decor.getModelByOwner(owner);
		if (isCollectionItem && comp == item) {
			if (xmodel instanceof BindingListModelExt && !((BindingListModelExt) xmodel).isDistinct()) {
				final BindingListModelExt model = (BindingListModelExt) xmodel;
				final int[] indexes = model.indexesOf(bean);
				final int sz = indexes.length;
				final List<Component> comps = new ArrayList<Component>(sz);
				for (int j = 0; j < sz; ++j) {
					final Component xcomp = lookupClone(decor.getComponentAtIndexByOwner(owner, indexes[j]), comp);
					if (xcomp != null)
						comps.add(xcomp);
				}
				return comps.toArray(new Component[comps.size()]);
			} else if (xmodel instanceof BindingListModel) {
				final BindingListModel model = (BindingListModel) xmodel;
				int index = model.indexOf(bean);
				if (index >= 0) {
					final Component xcomp = lookupClone(decor.getComponentAtIndexByOwner(owner, index), comp);
					return xcomp != null ? new Component[] { xcomp } : new Component[0];
				}
			}
		} else {
			//though the comp is in collection but the binding does not relate to _var, 
			//have to scan through the whole cloned children items   
			final int sz = xmodel.getSize();
			final List<Component> comps = new ArrayList<Component>(sz);
			if (decor instanceof CollectionItemExt) {
				final List items = ((CollectionItemExt) decor).getItems(owner);
				for (final Iterator it = items.iterator(); it.hasNext();) {
					final Component cloneitem = (Component) it.next();
					final Component xcomp = lookupClone(cloneitem, comp);
					if (xcomp != null)
						comps.add(xcomp);
				}
			} else {
				for (int j = 0; j < sz; ++j) {
					final Component xcomp = lookupClone(decor.getComponentAtIndexByOwner(owner, j), comp);
					if (xcomp != null)
						comps.add(xcomp);
				}
			}
			return comps.toArray(new Component[sz]);
		}
		return new Component[0];
	}

	//set the binding renderer for the template listitem component
	private void setupBindingRenderer(Component comp) {
		getBindingCollectionItem(comp).setupBindingRenderer(comp, this);
	}

	private void setupPathTree(Collection<Binding> bindings, Set<String> varnameSet) {
		for (Binding binding : bindings) {
			String[] paths = binding.getPaths();
			for (int j = 0; j < paths.length; ++j) {
				final String path = paths[j];
				_pathTree.addBinding(path, binding, varnameSet);
			}
		}
	}

	private void registerSaveEvents(Component comp, Collection bindings) {
		for (final Iterator it = bindings.iterator(); it.hasNext();) {
			final Binding binding = (Binding) it.next();
			binding.registerSaveEvents(comp);
		}
	}

	private void registerLoadEvents(Component comp, Collection bindings) {
		for (final Iterator it = bindings.iterator(); it.hasNext();) {
			final Binding binding = (Binding) it.next();
			binding.registerLoadEvents(comp);
		}
	}

	//whether exists the specified bean in this DataBinder.
	/* package */ boolean existsBean(String beanid) {
		return _beans.containsKey(beanid);
	}

	//get a bean by the beanid from this Data binder
	/* package */ Object getBean(String beanid) {
		return _beans.get(beanid);
	}

	//set a bean into this Data binder
	/* package */ void setBean(String beanid, Object bean) {
		_beans.put(beanid, bean);
	}

	/**
	 * Sets up the specified comp and its descendants to be as template (or not)
	 */
	public void setupTemplateComponent(Component comp, Object owner) {
		mySetupTemplateComponent(comp, owner, comp);
	}

	private void mySetupTemplateComponent(Component comp, Object owner, Component item) {
		if (existsBindings(comp)) {
			if (comp.getAttribute(OWNER) != null) {
				comp.setAttribute(HASTEMPLATEOWNER, Boolean.TRUE); //owner is a template
			}
			comp.setAttribute(OWNER, owner);
			comp.setAttribute(ITEM, item);
		}

		for (Component c : comp.getChildren()) {
			mySetupTemplateComponent(c, owner, item); //recursive
		}
	}

	//parse token and return as a List of String
	/* package */ static List<String> parseExpression(String expr, String separator) {
		if (expr == null) {
			return null;
		}
		List<String> results = new ArrayList<String>(6);
		while (true) {
			int j = expr.indexOf(separator);
			if (j < 0) {
				results.add(expr.trim());
				return results;
			}
			results.add(expr.substring(0, j).trim());

			if (expr.length() <= (j + 1)) {
				return results;
			}
			expr = expr.substring(j + 1);
		}
	}

	//whether a collection owner component. (e.g. Grid, Listbox with _var)
	/*package*/ static boolean isCollectionOwner(Component owner) {
		return owner.getAttribute(IAMOWNER) != null;
	}

	//whether a component is a binding template rather than a real component
	/* package */ static boolean isTemplate(Component comp) {
		//bug #1941947 Cannot find associated CollectionItem error
		return comp != null && comp.getAttribute(OWNER) != null;
	}

	//whether a cloned component from the template.
	/* package */ static boolean isClone(Component comp) {
		//bug #1813055  Multiple listboxes with same selectedItem causes NPE
		return comp != null && (comp.getAttribute(TEMPLATE) instanceof Component);
	}

	//Returns template component of a given clone component
	/* package */ static Component getComponent(Component clone) {
		return (Component) clone.getAttribute(TEMPLATE);
	}

	//whether has template owner (collection in collection)
	/* package */ static boolean hasTemplateOwner(Component comp) {
		//bug #1813055  Multiple listboxes with same selectedItem causes NPE
		return comp != null && (comp.getAttribute(HASTEMPLATEOWNER) != null);
	}

	//set a bean to SameNode Set 
	/* package */ void setBeanSameNodes(Object bean, Set<Object> set) {
		_beanSameNodes.put(bean, set);
	}

	//get SameNode Set of the given bean
	/* package */ Set<Object> getBeanSameNodes(Object bean) {
		return _beanSameNodes.get(bean);
	}

	//remove SameNode set of the given bean
	/* package */ Set<Object> removeBeanSameNodes(Object bean) {
		return _beanSameNodes.remove(bean);
	}

	/** traverse the path nodes and return the final bean.
	 */
	/* package */ Object getBeanAndRegisterBeanSameNodes(Component comp, String path) {
		return myGetBeanWithExpression(comp, path, true);
	}

	/* package */ Object getBeanWithExpression(Component comp, String path) {
		return myGetBeanWithExpression(comp, path, false);
	}

	private Object myGetBeanWithExpression(Component comp, String path, boolean registerNode) {
		Object bean = null;
		BindingNode currentNode = _pathTree;
		final List nodeids = parseExpression(path, ".");
		final Iterator it = nodeids.iterator();
		if (it != null && it.hasNext()) {
			String nodeid = (String) it.next();
			currentNode = currentNode.getKidNode(nodeid);
			if (currentNode == null) {
				throw new UiException("Cannot find the specified databind bean expression:" + path);
			}
			bean = lookupBean(comp, nodeid);
			if (registerNode) {
				registerBeanNode(bean, currentNode);
			}
		} else {
			throw new UiException("Incorrect format of databind bean expression:" + path);
		}

		while (bean != null && it.hasNext()) {
			String nodeid = (String) it.next();
			currentNode = currentNode.getKidNode(nodeid);
			if (currentNode == null) {
				throw new UiException("Cannot find the specified databind bean expression:" + path);
			}
			bean = fetchValue(bean, currentNode, nodeid, registerNode);
		}

		return bean;
	}

	private Object fetchValue(Object bean, BindingNode node, String nodeid, boolean registerNode) {
		if (bean != null) {
			//feature#1766905 Binding to Map
			//bug# 2630168, check Map case first and avoid throw unnecessary exception
			if (bean instanceof Map) { //regret the change for bug#2987511(follow the EL spec)
				bean = ((Map) bean).get(nodeid);
			} else {
				try {
					bean = Fields.get(bean, nodeid);
				} catch (NoSuchMethodException ex) {
					//bug# 2932475
					//SameNode algorithm not good enough. LoadOnSave might load 
					//implicit objects with same name(but different instance)
					//have to ignore the exception

					//ignore the exception
					//throw UiException.Aide.wrap(ex);
				}
			}
		}
		if (registerNode) {
			registerBeanNode(bean, node);
		}
		return bean;
	}

	@SuppressWarnings("unchecked")
	/* package */ void setBeanAndRegisterBeanSameNodes(Component comp, Object val, Binding binding, String path,
			boolean autoConvert, Object rawval, List<Object> loadOnSaveInfos, String triggerEventName) {
		Object orgVal = null;
		Object bean = null;
		BindingNode currentNode = _pathTree;
		boolean refChanged = false; // whether this setting change the reference
		String beanid = null;
		final List nodeids = parseExpression(path, ".");
		final List<BindingNode> nodes = new ArrayList<BindingNode>(nodeids.size());
		final Iterator it = nodeids.iterator();
		if (it != null && it.hasNext()) {
			beanid = (String) it.next();
			currentNode = currentNode.getKidNode(beanid);
			if (currentNode == null) {
				throw new UiException("Cannot find the specified databind bean expression:" + path);
			}
			nodes.add(currentNode);
			bean = lookupBean(comp, beanid);
		} else {
			throw new UiException("Incorrect format of databind bean expression:" + path);
		}

		if (!it.hasNext()) { //assign back to where bean is stored
			orgVal = bean;
			if (Objects.equals(orgVal, val)) {
				return; //same value, no need to do anything
			}
			if (existsBean(beanid)) {
				setBean(beanid, val);
			} else if (!setZScriptVariable(comp, beanid, val)) {
				comp.getSpaceOwner().setAttribute(beanid, val, true);
			}
			refChanged = true;
		} else {
			if (bean == null) {
				return; //no bean to set value, skip
			}
			int sz = nodeids.size() - 2; //minus first and last beanid in path
			for (; bean != null && it.hasNext() && sz > 0; --sz) {
				beanid = (String) it.next();
				currentNode = currentNode.getKidNode(beanid);
				if (currentNode == null) {
					throw new UiException("Cannot find the specified databind bean expression:" + path);
				}
				nodes.add(currentNode);
				// Bug B50-3183438: Access to bean shall be consistent
				if (bean instanceof Map) {
					bean = ((Map) bean).get(beanid); //feature#1766905 Binding to Map
				} else {
					try {
						bean = Fields.get(bean, beanid);
					} catch (NoSuchMethodException ex) {
						throw UiException.Aide.wrap(ex);
					}
				}
			}
			if (bean == null) {
				return; //no bean to set value, skip
			}
			beanid = (String) it.next();
			// Bug B50-3183438: Access to bean shall be consistent
			if (bean instanceof Map)
				((Map) bean).put(beanid, val); //feature#1766905 Binding to Map
			else {
				try {
					orgVal = Fields.get(bean, beanid);
					if (Objects.equals(orgVal, val))
						return; //same value, no need to do anything
					Fields.set(bean, beanid, val, autoConvert);
				} catch (NoSuchMethodException ex) {
					throw UiException.Aide.wrap(ex);
				}
			}

			if (!isPrimitive(val) && !isPrimitive(orgVal)) { //val is a bean (null is not primitive)
				currentNode = currentNode.getKidNode(beanid);
				if (currentNode == null) {
					throw new UiException("Cannot find the specified databind bean expression:" + path);
				}
				nodes.add(currentNode);
				bean = orgVal;
				refChanged = true;
			}
		}

		if (val != null) {
			if (refChanged && !binding.isLoadable() && binding.isSavable()) { //the sameNodes should change accordingly.
				registerBeanNode(val, currentNode);
			}
			//20070309, Henri Chen: Tricky. 
			//When loading page, listbox.selectedItem == null. The _var Listitem will not be able to 
			//associate with the selectedItem (no way to associate via null bean). When end user then 
			//select one Listitem, we have to force such association.
			if (rawval instanceof Component) {
				Binding varbinding = getBinding((Component) rawval, "_var");
				if (varbinding != null) {
					registerBeanNode(val, currentNode);
					getBeanAndRegisterBeanSameNodes((Component) rawval, varbinding.getExpression());
				}
			}
		}

		//TODO:Henri Chen: Is it possible to make the loadOnSave event to be called once only for a
		//setXxx. So avoid load a node several times?

		//register "onLoadSave" listener to this component if have not done so.
		if (!comp.isListenerAvailable("onLoadOnSave", true)) {
			comp.addEventListener("onLoadOnSave", _listener);
		}

		Object[] loadOnSaveInfo = new Object[] { this, currentNode, binding, (refChanged ? val : bean),
				Boolean.valueOf(refChanged), nodes, comp, triggerEventName };
		if (loadOnSaveInfos != null) {
			loadOnSaveInfos.add(loadOnSaveInfo);
		} else if (isLoadOnSave()) { //feature#2990932, allow disable load-on-save mechanism
			//do loadOnSave immediately
			Events.postEvent(new Event("onLoadOnSave", comp, loadOnSaveInfo));
		}
	}

	private void registerBeanNode(Object bean, BindingNode node) {
		if (isPrimitive(bean)) {
			return;
		}
		final Set<Object> nodeSameNodes = node.getSameNodes();
		final Set<Object> binderSameNodes = getBeanSameNodes(bean);
		//variable node(with _var) is special. Assume selectedItem then _var. 
		//e.g. a Listitem but no selectedItem yet
		if (node.isVar() && binderSameNodes == null) {
			return;
		}

		if (!nodeSameNodes.contains(bean)) {
			//remove the old bean
			for (final Iterator it = nodeSameNodes.iterator(); it.hasNext();) {
				final Object elm = it.next();
				if (!(elm instanceof BindingNode)) {
					it.remove();
					removeBeanSameNodes(elm); //remove the binderSameNodes of the original bean
					break;
				}
			}
			//add the new bean if not null
			if (bean != null) {
				nodeSameNodes.add(bean);
			}
		}

		if (binderSameNodes == null) {
			if (bean != null) {
				setBeanSameNodes(bean, nodeSameNodes);
			}
		} else {
			node.mergeAndSetSameNodes(binderSameNodes);
		}

	}

	private boolean isPrimitive(Object bean) {
		//String is deemed as primitive and null is not primitive
		return (bean instanceof String) || (bean != null && Primitives.toPrimitive(bean.getClass()) != null)
				|| (bean instanceof Date) || (bean instanceof Number);
	}

	/** Sets the variable to all loaded interpreters, if it was defined in
	 * the interpreter.
	 *
	 * @return whether it is set to the interpreter
	 */
	private boolean setZScriptVariable(Component comp, String beanid, Object val) {
		//for all loaded interpreters, assign val to beanid
		boolean found = false;
		for (final Iterator it = comp.getPage().getLoadedInterpreters().iterator(); it.hasNext();) {
			final Interpreter ip = (Interpreter) it.next();
			if (ip instanceof HierachicalAware) {
				final HierachicalAware ha = (HierachicalAware) ip;
				if (ha.containsVariable(comp, beanid)) {
					ha.setVariable(comp, beanid, val);
					found = true;
				}
			} else if (ip.containsVariable(beanid)) {
				ip.setVariable(beanid, val);
				found = true;
			}
		}
		return found;
	}

	/*package*/ Object lookupBean(Component comp, String beanid) {
		//fetch the bean object
		Object bean = null;

		//bug#1871833: Data binder should read "self".
		if ("self".equals(beanid)) {
			return comp;
		}
		if (isClone(comp)) {
			bean = myLookupBean1(comp, beanid);
			if (bean != NA) {
				return bean;
			}
		}
		if (existsBean(beanid)) {
			bean = getBean(beanid);
		} else if (beanid.startsWith("/")) { //a absolute component Path: // or /
			bean = Path.getComponent(beanid);
		} else if (beanid.startsWith(".")) { //a relative component Path: ./ or ../
			bean = Path.getComponent(comp.getSpaceOwner(), beanid);
		} else {
			//VariableResolver would need such "self" information when doing
			//variable resolving
			final Page page = comp.getPage();
			if (page != null) { //Bug #2823591, try to "load" into a detached(no page) component and NPE
				//bug #2932475, NoSuchMethodException in DataBinder (SpaceOwner-Mixup)
				bean = Components.getImplicit(comp, beanid);
				//bug #2945974
				//dirty patch
				if ("param".equals(beanid) && bean != null) {
					bean = new HashMap<Object, Object>(cast((Map) bean));
				}
				if (bean == null) {
					bean = page.getZScriptVariable(comp, beanid);
					if (bean == null) {
						final Object self = page.getAttribute("self");
						try {
							page.setAttribute("self", comp);
							bean = comp.getAttributeOrFellow(beanid, true);
							if (bean == null)
								bean = page.getXelVariable(null, null, beanid, true);
						} finally {
							if (self == null) {
								page.removeAttribute("self");
							} else {
								page.setAttribute("self", self);
							}
						}
					}
				}
			}
		}
		return bean;
	}

	//given a beanid and a template, return the associated bean
	//return NA if cannot find it
	private Object myLookupBean1(Component comp, String beanid) {
		Map templatemap = (Map) comp.getAttribute(TEMPLATEMAP);
		return myLookupBean2(beanid, templatemap);
	}

	private Object myLookupBean2(String beanid, Map templatemap) {
		if (templatemap != null) {
			if (templatemap.containsKey(beanid)) { //got it
				return templatemap.get(beanid);
			} else { //search up the parent templatemap
				templatemap = (Map) templatemap.get(TEMPLATEMAP);
				return myLookupBean2(beanid, templatemap); //recursive
			}
		}
		return NA; //not available
	}

	//given a clone and a template, return the associated clone of that template.
	/*package*/ static Component lookupClone(Component srcClone, Component srcTemplate) {
		if (isTemplate(srcTemplate) && srcClone != null) {
			Map templatemap = (Map) srcClone.getAttribute(TEMPLATEMAP);
			return myLookupClone(srcTemplate, templatemap);
		}
		return null;
	}

	private static Component myLookupClone(Component srcTemplate, Map templatemap) {
		if (templatemap != null) {
			if (templatemap.containsKey(srcTemplate)) { //got it
				return (Component) templatemap.get(srcTemplate);
			} else { //search up the parent templatemap
				templatemap = (Map) templatemap.get(TEMPLATEMAP);
				return myLookupClone(srcTemplate, templatemap); //recursive
			}
		}
		return null;
	}

	// Given parentNode, path, and level, return associate same kid nodes of parent
	// a1.b.c -> a2.b.c, a3.b.c, ...
	private Set<Object> getAssociateSameNodes(BindingNode parentNode, String path, int level) {
		final List nodeids = DataBinder.parseExpression(path, ".");
		final int sz = nodeids.size();
		final List subids = nodeids.subList(sz - level, sz);

		for (final Iterator it = parentNode.getSameNodes().iterator(); it.hasNext();) {
			Object obj = it.next();
			if (!(obj instanceof BindingNode)) {
				break;
			}
		}

		//for each same node, find the associated kid node
		final Set<Object> assocateSameNodes = new HashSet<Object>();
		for (final Iterator it = parentNode.getSameNodes().iterator(); it.hasNext();) {
			//locate the associate kid node
			BindingNode currentNode = null;
			final Object obj = it.next();
			if (!(obj instanceof BindingNode) || currentNode == parentNode) {
				continue;
			}

			currentNode = (BindingNode) obj;
			for (final Iterator itx = subids.iterator(); itx.hasNext();) {
				final String nodeid = (String) itx.next();
				currentNode = currentNode.getKidNode(nodeid);
				if (currentNode == null) {
					break;
				}
			}

			if (currentNode != null) {
				if (!currentNode.isVar()) {
					assocateSameNodes.add(currentNode);
				} else { //a var node, special case, find the var root
					Component varRootComp = getVarRootComponent(currentNode);
					assocateSameNodes.add(new Object[] { currentNode, varRootComp });
				}
			}
		}
		return assocateSameNodes;
	}

	private Component getVarRootComponent(BindingNode node) {
		final BindingNode varRootNode = node.getRootNode(_pathTree);

		Object bean = null;
		for (final Iterator it = varRootNode.getSameNodes().iterator(); it.hasNext();) {
			Object obj = it.next();
			if (!(obj instanceof BindingNode)) {
				bean = obj;
				break;
			}
		}

		Component comp = null;
		for (final Iterator itx = varRootNode.getBindings().iterator(); itx.hasNext();) {
			Binding binding = (Binding) itx.next();
			if ("_var".equals(binding.getAttr())) {
				comp = binding.getComponent();
				break;
			}
		}

		return comp == null ? null : getCollectionItem(comp, bean, /* isCollectionItem */ true);
	}

	private class LoadOnSaveEventListener implements EventListener, java.io.Serializable {
		private static final long serialVersionUID = 200808191508L;

		public LoadOnSaveEventListener() {
		}

		//-- EventListener --//
		public void onEvent(Event event) {
			final Set<BindingNode> walkedNodes = new HashSet<BindingNode>(32);
			final Set<Dual> loadedComps = new HashSet<Dual>(32 * 2);

			Object obj = event.getData();
			if (obj instanceof List) {
				for (final Iterator it = ((List) obj).iterator(); it.hasNext();) {
					final Object[] data = (Object[]) it.next();
					doLoad(data, walkedNodes, loadedComps);
				}
			} else {
				doLoad((Object[]) obj, walkedNodes, loadedComps);
			}
		}

		private void doLoad(Object[] data, Set<BindingNode> walkedNodes, Set<Dual> loadedComps) {
			if (!data[0].equals(DataBinder.this)) {
				return; //not for this DataBinder, skip
			}
			final BindingNode node = (BindingNode) data[1]; //to be loaded nodes
			final Binding savebinding = (Binding) data[2]; //to be excluded binding
			final Object bean = data[3]; //saved bean
			final boolean refChanged = ((Boolean) data[4]).booleanValue(); //whether bean itself changed
			final List<BindingNode> nodes = cast((List) data[5]); //the complete nodes along the path to the node
			final Component savecomp = (Component) data[6]; //saved comp that trigger this load-on-save event
			final String triggerEventName = (String) data[7]; //event that trigger the save
			if (savecomp != null) {
				final Execution exec = Executions.getCurrent();
				final Object old = exec.getAttribute(LOAD_ON_SAVE_TRIGGER_COMPONENT);
				exec.setAttribute(LOAD_ON_SAVE_TRIGGER_COMPONENT, new Object[] { savecomp, triggerEventName });
				try {
					loadAllNodes(bean, node, savecomp, savebinding, refChanged, nodes, walkedNodes, loadedComps);
				} finally {
					exec.setAttribute(LOAD_ON_SAVE_TRIGGER_COMPONENT, old);
				}
			}
		}

		/** Load all associated BindingNodes below the given nodes (depth first traverse).
		 */
		private void loadAllNodes(Object bean, BindingNode node, Component collectionComp, Binding savebinding,
				boolean refChanged, List<BindingNode> nodes, Set<BindingNode> walkedNodes, Set<Dual> loadedComps) {
			myLoadAllNodes(bean, node, new Component[] { collectionComp }, walkedNodes, savebinding, loadedComps,
					refChanged);

			//for each ancestor, find associated same nodes			
			if (!nodes.isEmpty()) {
				final String path = node.getPath();
				int level = 1;
				for (final ListIterator<BindingNode> it = nodes.listIterator(nodes.size() - 1); it
						.hasPrevious(); ++level) {
					final BindingNode parentNode = it.previous();
					final Set<Object> associateSameNodes = getAssociateSameNodes(parentNode, path, level);
					for (Object obj : associateSameNodes) {
						if (obj instanceof BindingNode) {
							BindingNode samenode = (BindingNode) obj;
							myLoadAllNodes(bean, samenode, new Component[] { collectionComp }, walkedNodes, savebinding,
									loadedComps, refChanged);
						} else {
							BindingNode samenode = (BindingNode) ((Object[]) obj)[0];
							Component varRootComp = (Component) ((Object[]) obj)[1];
							myLoadAllNodes(bean, samenode, new Component[] { varRootComp }, walkedNodes, savebinding,
									loadedComps, refChanged);
						}
					}
				}
			}
		}

		//since 3.1, 20080416, Henri Chen: support one object multiple collection items of ListModel
		private void myLoadAllNodes(Object bean, BindingNode node, Component[] collectionComps,
				Set<BindingNode> walkedNodes, Binding savebinding, Set<Dual> loadedComps, boolean refChanged) {
			if (walkedNodes.contains(node)) {
				return; //already walked, skip
			}
			//mark as walked already
			walkedNodes.add(node);

			//the component might have been removed
			if (collectionComps.length == 0) {
				return;
			}

			//loading component associated with the node, return related collection items
			//since 3.1, 20080416, Henri Chen: support one object multiple collection items of ListModel
			final int sz = collectionComps.length;
			Component[][] kidCollectionCompsArray = new Component[sz][];
			for (int j = 0; j < sz; ++j) {
				kidCollectionCompsArray[j] = loadAllBindings(bean, node, collectionComps[j], savebinding, loadedComps,
						refChanged);
			}

			//walk all kid nodes
			for (final Iterator it = node.getKidNodes().iterator(); it.hasNext();) {
				final BindingNode kidnode = (BindingNode) it.next();
				final Object kidbean = fetchValue(bean, kidnode, kidnode.getNodeId(), true);
				for (int j = 0; j < sz; ++j) {
					myLoadAllNodes(kidbean, kidnode, kidCollectionCompsArray[j], walkedNodes, savebinding, loadedComps,
							true); //recursive
				}
			}

			//walk all same nodes (different expression but keep same bean)
			for (Object obj : new ArrayList<Object>(node.getSameNodes())) {
				if (obj instanceof BindingNode) {
					final BindingNode samenode = (BindingNode) obj;
					if (node == samenode) {
						continue;
					}
					if (samenode.isVar()) { // -> var node
						//var node must traverse from the root 
						//even a root, must make sure the samebean (could be different)
						//even the same bean, if a inner var root(collection in collection), not a real root
						if (!samenode.isRoot() || !isSameBean(samenode, bean) || samenode.isInnerCollectionNode()) {
							continue;
						}
					} else if (node.isVar() && !isSameBean(samenode, bean)) { //var -> !var, must same bean
						continue;
					}
					myLoadAllNodes(bean, samenode, collectionComps, walkedNodes, savebinding, loadedComps, refChanged); //recursive
				}
			}
		}

		//load each binding of the node and return nearest collection item Components (i.e. Listitem)
		private Component[] loadAllBindings(Object bean, BindingNode node, Component collectionComp,
				Binding savebinding, Set<Dual> loadedComps, boolean refChanged) {
			final Collection bindings = node.getBindings();
			Component[] collectionComps = null;
			for (final Iterator it = bindings.iterator(); it.hasNext();) {
				final Binding binding = (Binding) it.next();

				if (loadedComps.contains(new Dual(collectionComp, binding))) {
					continue;
				}
				loadedComps.add(new Dual(collectionComp, binding));

				// bug#1775051: a multiple selection Listbox. When onSelect and loadOnSave cause 
				// setSelectedItem (loading) to be called and cause deselection of other multiple 
				// selected items. Must skip such case.

				// save binding that cause this loadOnSave, no need to load again.
				if (binding == savebinding) {
					continue;
				}

				final String attr = binding.getAttr();
				final boolean isCollectionItem = "_var".equals(attr);
				final Component comp = binding.getComponent();

				//since 3.1, 20080416, Henri Chen: support one object maps to multiple item of a ListModel
				if (isTemplate(comp)) { //a template component, locate the listitem
					Component[] clonecomps = new Component[0];
					if (isClone(collectionComp)) { //A listbox in listbox
						Component clonecomp = lookupClone(collectionComp, comp);
						if (clonecomp == null) { //the comp is in another Listbox?
							if (isCollectionItem) {
								clonecomps = getCollectionItems(comp, bean, isCollectionItem);
							} else {
								throw new UiException("Cannot find associated CollectionItem=" + comp + ", binding="
										+ binding + ", collectionComp=" + collectionComp);
							}
						} else {
							clonecomps = new Component[] { clonecomp };
						}
					} else {
						clonecomps = getCollectionItems(comp, bean, isCollectionItem);
					}
					if (refChanged) {
						for (int j = 0; j < clonecomps.length; ++j) {
							final Component clonecomp = clonecomps[j];
							binding.loadAttribute(clonecomp);
						}
					}
					//special case, collection items are found, 
					//use it to handle the rest of the bindings in this node
					if (isCollectionItem) {
						collectionComps = clonecomps;
						for (int j = 0; j < collectionComps.length; ++j) {
							//recursive exclude this _var binding
							loadAllBindings(bean, node, collectionComps[j], binding, loadedComps, refChanged);
						}
						break;
					}
				} else {
					if (refChanged) {
						binding.loadAttribute(comp);
					}
				}
			}
			return collectionComps == null ? new Component[] { collectionComp } : collectionComps;
		}

		private boolean isSameBean(BindingNode node, Object bean) {
			final Collection bindings = node.getBindings();
			if (bindings.isEmpty()) {
				return true;
			}
			final Component comp = ((Binding) bindings.iterator().next()).getComponent();
			if (isTemplate(comp)) {
				return true;
			}
			final Object nodebean = getBeanWithExpression(comp, node.getPath());
			return Objects.equals(nodebean, bean);
		}

		private class Dual implements java.io.Serializable {
			private static final long serialVersionUID = 200808191743L;
			private Component _comp;
			private Binding _binding;

			public Dual(Component comp, Binding binding) {
				_comp = comp;
				_binding = binding;
			}

			public int hashCode() {
				return (_comp == null ? 0 : _comp.hashCode()) ^ (_binding == null ? 0 : _binding.hashCode());
			}

			public boolean equals(Object other) {
				if (this == other)
					return true;
				if (other instanceof Dual) {
					final Dual o = (Dual) other;
					return o._comp == _comp && o._binding == _binding;
				}
				return false;
			}
		}
	}

	//feature #3026221: Databinder shall fire onCreate when cloning each items
	/*package*/ static void postOnCreateEvents(Component item) {
		for (final Iterator it = item.getChildren().iterator(); it.hasNext();) {
			final Component child = (Component) it.next();
			postOnCreateEvents(child); //recursive
		}
		if (Events.isListened(item, Events.ON_CREATE, false)) {
			Events.postEvent(new CreateEvent(Events.ON_CREATE, item, null));
		}
	}
}
