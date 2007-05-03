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

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.metainfo.Annotation; 
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.Express;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Interpreter;

import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListModel;

import org.zkoss.util.ModificationException;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Primitives;
import org.zkoss.lang.reflect.Fields;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * The DataBinder used for binding ZK UI component and the backend data bean.
 *
 * @author Henri Chen
 */
public class DataBinder {
	/*package*/ static final String NULLIFY = "none"; //used to nullify default configuration
	/*package*/ static final String VARNAME = "zkplus.databind.VARNAME"; //_var name
	/*package*/ static final String TEMPLATEMAP = "zkplus.databind.TEMPLATEMAP"; // template -> clone
	/*package*/ static final String TEMPLATE = "zkplus.databind.TEMPLATE"; //clone -> template
	private static final String OWNER = "zkplus.databind.OWNER"; //the collection owner of the template component
	private static final String HASTEMPLATEOWNER = "zkplus.databind.HASTEMPLATEOWNER"; //whether has template owner (collection in collection)
	private static final Object NA = new Object();
	private static final String ASSIGNVAR = "var_tmp_assignment"; //the temp var name for assignment script

	private Map _compBindingMap = new LinkedHashMap(29); //(comp, Map(attr, Binding))
	private Map _beans = new HashMap(29); //bean local to this DataBinder
	private Map _beanSameNodes = new HashMap(29); //(bean, Set(BindingNode)) bean same nodes, diff expression but actually hold the same bean
	private BindingNode _pathTree = new BindingNode("/", false, "/", false); //path dependency tree.
	private Set _pageSet = new HashSet(1); //page that associated with this DataBinder
	private boolean _defaultConfig = true; //whether load default configuration from lang-addon.xml
	private boolean _init; //whether this databinder is initialized. 
		//Databinder is init automatically when saveXXX or loadXxx is called

	/** Binding bean to UI component. This is the same as 
	 * addBinding(Component comp, String attr, String expr, (List)null, (String)null, (String)null, (String)null). 
	 * @param comp The component to be associated.
	 * @param attr The attribute of the component to be associated.
	 * @param expr The expression to associate the data bean.
	 */
	public void addBinding(Component comp, String attr, String expr) {
		addBinding(comp, attr, expr, (List)null, null, null, null);
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
	public void addBinding(Component comp, String attr, String expr,
		String[] loadWhenEvents, String saveWhenEvent, String access, String converter) {
		List loadEvents = null;
		if (loadWhenEvents != null && loadWhenEvents.length > 0) {
			loadEvents = new ArrayList(loadWhenEvents.length);
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
	 * @param saveWhenEvent The event when to save data.
	 * @param access In the view of UI component: "load" load only, 
	 * "both" load/save, "save" save only when doing
	 * data binding. null means using the default access natural of the component. 
	 * e.g. Label.value is "load", but Textbox.value is "both".
	 * @param converter The converter class used to convert classes between component 
	 *  and the associated bean. null means using the default class conversion method.
	 */
	public void addBinding(Component comp, String attr, String expr,
		List loadWhenEvents, String saveWhenEvent, String access, String converter) {
			
		//add EventListener if not have done so
		Page page = comp.getPage();
		if (!_pageSet.contains(page)) {
			_pageSet.add(page);
			page.addEventListener("onLoadOnSave", new LoadOnSaveEventListener(this));
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
				loadWhenEvents = (List) objs[1];
			}
			if (saveWhenEvent == null && objs[2] != null) {
				saveWhenEvent = (String) objs[2];
			}
			if (access == null && objs[3] != null) {
				access = (String) objs[3];
			}
			if (converter == null && objs[4] != null) {
				converter = (String) objs[4];
			}
		}
	
		//nullify check
		boolean nullify = false;
		LinkedHashSet loadEvents = null;
		if (loadWhenEvents != null && loadWhenEvents.size() > 0) {
			loadEvents = new LinkedHashSet(loadWhenEvents.size());
			for(final Iterator it = loadWhenEvents.iterator(); it.hasNext();) {
				final String event = (String) it.next();
				if (NULLIFY.equals(event)) {
					loadEvents.clear();
					nullify = true;
				} else {
					nullify = false;
					loadEvents.add(event);
				}
			}
			if (loadEvents.isEmpty()) { 
				loadEvents = null;
			}
		}

		if (NULLIFY.equals(saveWhenEvent)) {
			saveWhenEvent = null;
		}
		
		if (NULLIFY.equals(access)) {
			access = null;
		}
		
		if (NULLIFY.equals(converter)) {
			converter = null;
		}
		
		Map attrMap = (Map) _compBindingMap.get(comp);
		if (attrMap == null) {
			attrMap = new LinkedHashMap(3);
			_compBindingMap.put(comp, attrMap);
		}
			
		if (attrMap.containsKey(attr)) { //override
			final Binding binding = (Binding) attrMap.get(attr);
			binding.setExpression(expr);
			binding.setLoadWhenEvents(loadEvents);
			binding.setSaveWhenEvent(saveWhenEvent);
			binding.setAccess(access);
			binding.setConverter(converter);
		} else {
			attrMap.put(attr, new Binding(this, comp, attr, expr, loadEvents, saveWhenEvent, access, converter));
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

	/** Given component and attr, return the associated {@link Binding}.
	 * @param comp the concerned component
	 * @param attr the concerned attribute
	 */
	public Binding getBinding(Component comp, String attr) {
		if (isClone(comp)) {
			comp = (Component) comp.getAttribute(TEMPLATE);
		}
		Map attrMap = (Map) _compBindingMap.get(comp);
		return attrMap != null ?  (Binding) attrMap.get(attr) : null;
	}

	/** Given component, return the associated list of {@link Binding}s.
	 * @param comp the concerned component
	 */
	public Collection getBindings(Component comp) {
		if (isClone(comp)) {
			comp = (Component) comp.getAttribute(TEMPLATE);
		}
		Map attrMap = (Map)_compBindingMap.get(comp);
		return attrMap != null ?  (Collection) attrMap.values() : null;
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
			Map attrMap = (Map) _compBindingMap.get(comp);
			return attrMap.containsKey(attr);
		}
		return false;
	}		
	
	/** Whether use the default binding configuration.
	 */
	public boolean isDefaultConfig(){
		return _defaultConfig;
	}
	
	/** Whether use the default binding configuration.
	 */
	public void setDefaultConfig(boolean b) {
		_defaultConfig = b;
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
		if (isTemplate(comp) || comp.getPage() == null) {
			return; //skip detached component
		}
		init();
		Collection bindings = getBindings(comp);
		if (bindings != null) {
			loadAttrs(comp, bindings);
		}
			
		//load kids of this component
		for(final Iterator it = comp.getChildren().iterator(); it.hasNext();) {
			loadComponent((Component) it.next()); //recursive
		}
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
		for(final Iterator it = comp.getChildren().iterator(); it.hasNext();) {
			saveComponent((Component) it.next()); //recursive
		}
	}
	
	/** Load all value from data beans to UI components. */
	public void loadAll() {
		init();
		for (final Iterator it = _compBindingMap.keySet().iterator(); it.hasNext(); ) {
			final Component comp = (Component) it.next();
			loadComponent(comp);
		}
	}
	
	/** Save all values from UI components to beans. */
	public void saveAll() {
		init();
		for (final Iterator it = _compBindingMap.keySet().iterator(); it.hasNext(); ) {
			final Component comp = (Component) it.next();
			saveComponent(comp);
		}
	}

	private void loadAttrs(String expr, Collection attrs) {
		for(final Iterator it = attrs.iterator(); it.hasNext();) {
			Binding binding = (Binding) it.next();
			Component comp = binding.getComponent();
			binding.loadAttribute(comp, expr);
		}
	}	

	private void saveAttrs(String expr, Collection attrs) {
		for(final Iterator it = attrs.iterator(); it.hasNext();) {
			Binding binding = (Binding) it.next();
			Component comp = binding.getComponent();
			binding.saveAttribute(comp, expr);
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

	//[0] expr, [1] loadWhenEvents, [2] saveWhenEvent, [3] access, [4] converter
	protected Object[] loadPropertyAnnotation(Component comp, String propName, String bindName) {
		ComponentCtrl compCtrl = (ComponentCtrl) comp;
		Annotation ann = compCtrl.getAnnotation(propName, bindName);
		if (ann != null) {
			final Map attrs = ann.getAttributes(); //(tag, tagExpr)
			List loadWhenEvents = null;
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
	
	//late init
	protected void init() {
		if (!_init) {
			_init = true;

			//setup all added bindings
			final Set varnameSet = new HashSet();
			final LinkedHashSet toBeDetached = new LinkedHashSet();
			for(final Iterator it = _compBindingMap.entrySet().iterator(); it.hasNext(); ) {
				final Entry me = (Entry) it.next();
				final Component comp = (Component) me.getKey();
				final Map attrMap = (Map) me.getValue();
				final Collection bindings = attrMap.values();
				
				//_var special case; meaning a template component
				if (attrMap.containsKey("_var")) {
					setupTemplateComponent(comp, getComponentCollectionOwner(comp)); //setup as template components
					String varname = ((Binding)attrMap.get("_var")).getExpression();
					varnameSet.add(varname);
					comp.setAttribute(VARNAME, varname);
					setupBindingRenderer(comp); //setup binding renderer
					toBeDetached.add(comp);
				}

				if (bindings != null) {
					//construct the path dependant tree
					setupPathTree(bindings, varnameSet);
				
					//register save-when event
					registerSaveEvent(comp, bindings);
					
					//register load-when events
					registerLoadEvents(comp, bindings);
				}
			}
            
			//detach template components so they will not interfer the visual part
			for(final Iterator it = toBeDetached.iterator(); it.hasNext(); ) {
				final Component comp = (Component) it.next();
				comp.detach();
			}
		}
	}
	
	//Tightly coupled to Component
	//get Collection owner of a given collection item.
	private static Component getComponentCollectionOwner(Component comp) {
		if (comp instanceof Listitem) {
			return ((Listitem)comp).getListbox();
		}
		if (comp instanceof Row) {
			return ((Row)comp).getGrid();
		}
		throw new UiException("Collection Databinder now supports Listbox and Grid only! CollectionItem:"+comp);
	}

	//get Collection owner of a given collection item.
	private static Component getCollectionOwner(Component comp) {
		if (isTemplate(comp)) {
			return (Component) comp.getAttribute(OWNER);
		}
		return getComponentCollectionOwner(comp);
	}
	
	//get associated clone of a given bean and template component
	private static Component getCollectionItem(Component comp, Object bean) {
		Component owner = getCollectionOwner(comp);
		
		assert !isTemplate(owner) : "collection in collection, should never occure! comp:" + comp + ", bean:" + bean+", owner: "+owner;
		
		if (owner instanceof Listbox) {
			final Listbox lbx = (Listbox) owner;
  		final ListModel xmodel = lbx.getModel();
  		if (xmodel instanceof BindingListModel) {
  			final BindingListModel model = (BindingListModel) xmodel;
  			int index = model.indexOf(bean);
  			if (index >= 0) {
    			Listitem li = (Listitem) lbx.getItemAtIndex(index);
    			return lookupClone(li, comp);
    		}
    	}
    	return null;
    } 
		if (owner instanceof Grid) {
			final Grid grid = (Grid) owner;
  		final ListModel xmodel = grid.getModel();
  		if (xmodel instanceof BindingListModel) {
  			final BindingListModel model = (BindingListModel) xmodel;
  			int index = model.indexOf(bean);
  			if (index >= 0) {
    			Row row = (Row) grid.getRows().getChildren().get(index);
    			return lookupClone(row, comp);
    		}
    	}
    	return null;
    } 

		throw new UiException("Collection Databinder now supports Listbox and Grid only! CollectionItem:"+comp+", CollectionOwner:"+owner);
	}
  		
	//set the binding renderer for the template listitem component (Tigthly couple to a component)
	private void setupBindingRenderer(Component comp) {
		if (comp instanceof Listitem) {
			final Listitem li = (Listitem)comp;
			final Listbox lbx = li.getListbox();
			if (lbx.getItemRenderer() == null) {
				lbx.setItemRenderer(new BindingListitemRenderer(li, this));
			}
			return;
		}
		if (comp instanceof Row) {
			final Row row = (Row)comp;
			final Grid grid = row.getGrid();
			if (grid.getRowRenderer() == null) {
				grid.setRowRenderer(new BindingRowRenderer(row, this));
			}
			return;
		}
		throw new UiException("Collection Databinder now supports Listbox and Grid only! CollectionItem:"+comp);
	}
	//^^ above Tightly coupled to component
	
	private void setupPathTree(Collection bindings, Set varnameSet) {
		for(final Iterator it = bindings.iterator(); it.hasNext(); ) {
			final Binding binding = (Binding) it.next();
			String[] paths = binding.getPaths();
			for(int j = 0; j < paths.length; ++j) {
				final String path = (String) paths[j];
				_pathTree.addBinding(path, binding, varnameSet);
			}
		}
	}
	
	private void registerSaveEvent(Component comp, Collection bindings) {
		for(final Iterator it = bindings.iterator(); it.hasNext(); ) {
			final Binding binding = (Binding) it.next();
			binding.registerSaveEvent(comp);
		}
	}

	private void registerLoadEvents(Component comp, Collection bindings) {
		for(final Iterator it = bindings.iterator(); it.hasNext(); ) {
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
	
	//setup the specified comp and its decendents to be as template (or not)
	/* package */ void setupTemplateComponent(Component comp, Object owner) {
		if (existsBindings(comp)) {
			if (comp.getAttribute(OWNER) != null) {
				comp.setAttribute(HASTEMPLATEOWNER, Boolean.TRUE); //owner is a template
			}
			comp.setAttribute(OWNER, owner);
		}
		List kids = comp.getChildren();
		for(final Iterator it = kids.iterator(); it.hasNext(); ) {
			setupTemplateComponent((Component) it.next(), owner); //recursive
		}
	}
	
	//parse token and return as a List of String
	/* package */ static List parseExpression(String expr, String separator) {
		if (expr == null) {
			return null;
		}
		List results = new ArrayList(5);
		while(true) {
			int j = expr.indexOf(separator);
			if (j < 0) {
				results.add(expr.trim());
				return results;
			}
			results.add(expr.substring(0, j).trim());

			if (expr.length() <= (j+1)) {
				return results;
			}
			expr = expr.substring(j+1);
		}
	}

	//whether a component is a binding template rather than a real component
	/* package */ static boolean isTemplate(Component comp) {
		return comp.getAttribute(OWNER) != null;
	}
	
	//whether a cloned component from the template.
	/* package */ static boolean isClone(Component comp) {
		return (comp.getAttribute(TEMPLATE) instanceof Component);
	}
	
	//whether has template owner (collection in collection)
	/* package */ static boolean hasTemplateOwner(Component comp) {
		return (comp.getAttribute(HASTEMPLATEOWNER) != null);
	}
	
	//set a bean to SameNode Set 
	/* package */ void setBeanSameNodes(Object bean, Set set) {
		_beanSameNodes.put(bean, set);
	}
	
	//get SameNode Set of the given bean
	/* package */ Set getBeanSameNodes(Object bean) {
		return (Set) _beanSameNodes.get(bean);
	}
	
	//remove SameNode set of the given bean
	/* package */ Set removeBeanSameNodes(Object bean) {
		return (Set) _beanSameNodes.remove(bean);
	}

	/** traverse the path nodes and return the final bean.
	 */
	/* package */ Object getBeanAndRegisterBeanSameNodes(Component comp, String path) {
		return myGetBeanWithExpression(comp, path, true);
	}
	
	private Object getBeanWithExpression(Component comp, String path) {
		return myGetBeanWithExpression(comp, path, false);
	}
	
	private Object myGetBeanWithExpression(Component comp, String path, boolean registerNode) {
		Object bean = null;
		BindingNode currentNode = _pathTree;
		final List nodeids = parseExpression(path, ".");
		final Iterator it = nodeids.iterator();
		if (it != null && it.hasNext()) {
			String nodeid = (String) it.next();
			currentNode = (BindingNode) currentNode.getKidNode(nodeid);
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
		
		while(bean != null && it.hasNext()) {
			String nodeid = (String) it.next();
			currentNode = (BindingNode) currentNode.getKidNode(nodeid);
			if (currentNode == null) {
				throw new UiException("Cannot find the specified databind bean expression:" + path);
			}
			try {
				bean = Fields.get(bean, nodeid);
			} catch (NoSuchMethodException ex) {
				throw UiException.Aide.wrap(ex);
			}
			if (registerNode) {
				registerBeanNode(bean, currentNode);
			}
		}

		return bean;
	}
	
	/* package */ void setBeanAndRegisterBeanSameNodes(Component comp, Object val, Binding binding, String path, boolean autoConvert, Object rawval) {
		Object orgVal = null;
		Object bean = null;
		BindingNode currentNode = _pathTree;
		boolean refChanged = false; //wether this setting change the reference
		String beanid = null;
		final List nodeids = parseExpression(path, ".");
		final Iterator it = nodeids.iterator();
		if (it != null && it.hasNext()) {
			beanid = (String) it.next();
			currentNode = (BindingNode) currentNode.getKidNode(beanid);
			if (currentNode == null) {
				throw new UiException("Cannot find the specified databind bean expression:" + path);
			}
			bean = lookupBean(comp, beanid);
		} else {
			throw new UiException("Incorrect format of databind bean expression:" + path);
		}
		
		if (!it.hasNext()) { //assign back to where bean is stored
			orgVal = bean;
			if(Objects.equals(orgVal, val)) {
				return; //same value, no need to do anything
			}
			if (existsBean(beanid)) {
				setBean(beanid, val);
			} else if (comp.containsVariable(beanid, false)) {
				comp.setVariable(beanid, val, false);
			} else {
				setZScriptVariable(comp, beanid, val);
			}
			refChanged = true;
		} else {
			if (bean == null) {
				return; //no bean to set value, skip
			}
			int sz = nodeids.size() - 2; //minus first and last beanid in path
			for(;bean != null && it.hasNext() && sz > 0; --sz) {
				beanid = (String) it.next();
				currentNode = (BindingNode) currentNode.getKidNode(beanid);
				if (currentNode == null) {
					throw new UiException("Cannot find the specified databind bean expression:" + path);
				}
				try {
					bean = Fields.get(bean, beanid);
				} catch (NoSuchMethodException ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
			if (bean == null) {
				return; //no bean to set value, skip
			}
			try {
				beanid = (String) it.next();
				orgVal = Fields.get(bean, beanid);
				if(Objects.equals(orgVal, val)) {
					return; //same value, no need to do anything
				}
				Fields.set(bean, beanid, val, autoConvert);
				if (!isPrimitive(val) && !isPrimitive(orgVal)) { //val is a bean (null is not primitive)
					currentNode = (BindingNode) currentNode.getKidNode(beanid);
					if (currentNode == null) {
						throw new UiException("Cannot find the specified databind bean expression:" + path);
					}
					bean = orgVal;
					refChanged = true;
				}
			} catch (NoSuchMethodException ex) {
				throw UiException.Aide.wrap(ex);
			} catch (ModificationException ex) {
				throw UiException.Aide.wrap(ex);
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
			
		//All kid nodes should be reloaded 
		Events.postEvent(new Event("onLoadOnSave", comp, new Object[] {this, currentNode, binding, (refChanged ? val : bean), Boolean.valueOf(refChanged)}));
	}
	
	private void registerBeanNode(Object bean, BindingNode node) {
		if (isPrimitive(bean)) {
			return;
		}
		final Set nodeSameNodes = node.getSameNodes();
		final Set binderSameNodes = getBeanSameNodes(bean);
		//variable node(with _var) is special. Assume selectedItem then _var. 
		//e.g. a Listitem but no selectedItem yet
		if (node.isVar() && binderSameNodes == null) {
			return;
		}
		
		if (!nodeSameNodes.contains(bean)) {
			//remove the old bean
			for(final Iterator it = nodeSameNodes.iterator(); it.hasNext();) {
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
		return (bean instanceof String) || (bean != null && Primitives.toPrimitive(bean.getClass()) != null);
	}
	
	//Very tricky implementation, assume "=" the assignment operator for all interpreters.
	//All loaded interperter will be set a zscript variable
	private void setZScriptVariable(Component comp, String beanid, Object val) {
		final Page page = comp.getPage();
		//put val into Page's name space with a strange variable name.
		page.setVariable(ASSIGNVAR, val);
		
		//for all loaded interperter, let interpret the assignment script
		final String script = beanid + " = "+ ASSIGNVAR;
		final Namespace ns = page.getNamespace();
		final Collection interpreters = page.getLoadedInterpreters();
		for(final Iterator it = interpreters.iterator(); it.hasNext();) {
			final Interpreter interpreter = (Interpreter) it.next();
			if ("ruby".equalsIgnoreCase(interpreter.getLanguage())) {
				interpreter.interpret("$"+script, ns);
			} else {
				interpreter.interpret(script, ns);
			}
		}
	}

	/*package*/ Object lookupBean(Component comp, String beanid) {
		//fetch the bean object
		Object bean = null;
		if (isClone(comp)) {
			bean = myLookupBean1(comp, beanid);
			if (bean != NA) {
				return bean;
			}
		}
		if (existsBean(beanid)) {
			bean = getBean(beanid);
		} else if (beanid.startsWith("/")) { //a component Path in ID Space
			bean = Path.getComponent(beanid);	
		} else if (comp.containsVariable(beanid, false)) {
			bean = comp.getVariable(beanid, false);
		} else {
			final Page page = comp.getPage();
			if (page != null)
				bean = page.getZScriptVariable(comp.getNamespace(), beanid);
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
		if (isTemplate(srcTemplate)) {
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

	
	private static class LoadOnSaveEventListener implements EventListener {
		private DataBinder _binder;
		
		public LoadOnSaveEventListener(DataBinder binder) {
			_binder = binder;
		}
		
		//-- EventListener --//
		public void onEvent(Event event) {
			Object[] data = (Object[]) event.getData();
			if (!data[0].equals(_binder)) {
				return; //not for this DataBinder, skip
			}
			final BindingNode node = (BindingNode) data[1]; //to be loaded nodes
			final Binding savebinding = (Binding) data[2]; //to be excluded binding
			final Object bean = data[3]; //saved bean
			final boolean refChanged = ((Boolean)data[4]).booleanValue(); //whether bean itself changed
			final Component savecomp = event.getTarget(); //saved comp that trigger this load-on-save event
			if (savecomp != null) {
				loadAllNodes(bean, node, savecomp, savebinding, refChanged);
			}
		}
		
		public boolean isAsap() {
			return true;
		}

		/** Load all associated BindingNodes below the given nodes (depth first traverse).
		 */
		private void loadAllNodes(Object bean, BindingNode node, Component collectionComp, Binding savebinding, boolean refChanged) {
			Set walkedNodes = new HashSet(23);
			Set loadedBindings = new HashSet(23*2);
			myLoadAllNodes(bean, node, collectionComp, walkedNodes, savebinding, loadedBindings, refChanged);
			
		}
		
		private void myLoadAllNodes(Object bean, BindingNode node, Component collectionComp,
		Set walkedNodes, Binding savebinding, Set loadedBindings, boolean refChanged) {
			if (walkedNodes.contains(node)) {
				return; //already walked, skip
			}
			
			//mark as walked already
			walkedNodes.add(node);

			//the component might have been removed
			if (collectionComp == null) {
				return;
			}
			//loading
			collectionComp = loadBindings(bean, node, collectionComp, savebinding, loadedBindings, refChanged);
			
			for(final Iterator it = node.getKidNodes().iterator(); it.hasNext();) {
				final BindingNode kidnode = (BindingNode) it.next();
				final Object kidbean = fetchValue(bean, kidnode, kidnode.getNodeId());
				myLoadAllNodes(kidbean, kidnode, collectionComp, walkedNodes, savebinding, loadedBindings, true); //recursive
			}
			
			for(final Iterator it = node.getSameNodes().iterator(); it.hasNext();) {
				final Object obj = it.next();
				if (obj instanceof BindingNode) {
					final BindingNode samenode = (BindingNode) obj;
					if (node == samenode) {
						continue;
					}
					if (samenode.isVar()) { // -> var node
						//var node must traverse from the root 
						//even a root, must make sure the samebean (could be diff)
						//even the same bean, if a inner var root(collection in collection), not a real root
						if (!samenode.isRoot() || !isSameBean(samenode, bean) || samenode.isInnerCollectionNode()) { 
							continue;
						}
					} else if (node.isVar() && !isSameBean(samenode, bean)) { //var -> !var, must same bean
						continue;
					}
					myLoadAllNodes(bean, samenode, collectionComp, walkedNodes, savebinding, loadedBindings, refChanged); //recursive
				}
			}
		}
	
		private Object fetchValue(Object bean, BindingNode node, String nodeid) {
			if (bean != null) {
				try {
					bean = Fields.get(bean, nodeid);
				} catch (NoSuchMethodException ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
			_binder.registerBeanNode(bean, node);
			return bean;
		}
		
		//return nearest collection item Component (i.e. ListItem)
		private Component loadBindings(Object bean, BindingNode node, Component collectionComp, 
		Binding savebinding, Set loadedBindings, boolean refChanged) {
			final Collection bindings = node.getBindings();
			for(final Iterator it = bindings.iterator(); it.hasNext();) {
				final Binding binding = (Binding) it.next();
				if (loadedBindings.contains(binding)) {
					continue;
				}
				loadedBindings.add(binding);
				/* save then load might change the format, so still load back
				if (binding == savebinding) {
					continue;
				}
				*/
				Component comp = binding.getComponent();
				if (isTemplate(comp)) { //a template component, locate the listitem
					if (isClone(collectionComp)) {
						comp = lookupClone(collectionComp, comp);
					} else {
						comp = getCollectionItem(comp, bean);
					}
					if ("_var".equals(binding.getAttr())) {
						collectionComp = comp;
					}
				}
				
				if (refChanged) {
					binding.loadAttribute(comp, bean);
				}
			}
			return collectionComp;
		}
		private boolean isSameBean(BindingNode node, Object bean) {	
			final Collection bindings = node.getBindings();
			if (bindings.isEmpty()) {
				return true;
			}
			final Component comp = ((Binding)bindings.iterator().next()).getComponent();
			if (isTemplate(comp)) {
				return true;
			}
			final Object nodebean = _binder.getBeanWithExpression(comp, node.getPath());
			return Objects.equals(nodebean, bean);
		}
	}
}
