/* Components.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 13 20:55:18     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.zkoss.idom.Document;
import org.zkoss.lang.Classes;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.xel.Evaluator;

/**
 * Utilities to access {@link Component}.
 *
 * @author tomyeh
 */
public class Components {
	private static final Log log = Log.lookup(Components.class);
	protected Components() {}

	/** Sorts the components in the list.
	 *
	 * <p>Note: you cannot use Collections.sort to sort
	 * {@link Component#getChildren} because Collections.sort might cause
	 * some replicated item in the list.
	 * @see #sort(List, int, int, Comparator)
	 */
	public static void sort(List list, Comparator cpr) {
		sort(list, 0, list.size(), cpr);
	}

	/** Replaces a component with another.
	 * @param oldc the component to remove.
	 * @param newc the component to add
	 * @exception IllegalArgumentException if oldc's parent and page are
	 * both null.
	 * @since 3.5.2
	 */
	public static void replace(Component oldc, Component newc) {
		final Component p = oldc.getParent(),
			sib = oldc.getNextSibling();
		if (p != null) {
			oldc.detach();
			p.insertBefore(newc, sib);
		} else {
			final Page page = oldc.getPage();
			if (page == null)
				throw new IllegalArgumentException("Neither child nor attached, "+oldc);
			oldc.detach();
			if (newc.getParent() != null)
				newc.detach();
			newc.setPageBefore(page, sib);
		}
	}
	/** Replaces all children of the specified component.
	 * It is the same as
	 * <pre><code>parent.getChildren().clear();
	 *parent.getChildren().addAll(newChildren);
	 *</code></pre>
	 * @since 3.5.2
	 */
	public static
	void replaceChildren(Component parent, Collection newChildren) {
		final Collection children = parent.getChildren();
		children.clear();
		children.addAll(newChildren);
	}
	/**
	 * Sorts the components in the list.
	 * @param list the list to be sorted
	 * @param from the index of the first element (inclusive) to be sorted
	 * @param to the index of the last element (exclusive) to be sorted
	 * @param cpr the comparator to determine the order of the list.
	 * @since 3.5.0
	 */
	public static void sort(List list, int from, int to, Comparator cpr) {
		final Object ary[] = CollectionsX.toArray(list, from, to);
		Arrays.sort(ary, cpr);

		ListIterator it = list.listIterator(from);
		int j = 0, k = to - from;
		for (; it.hasNext() && --k >= 0; ++j) {
			if (it.next() != ary[j]) {
				it.remove();

				if (it.hasNext() && --k >= 0) {
					if (it.next() == ary[j]) continue;
					it.previous();
					++k;
				}
				break;
			}
		}
		while (it.hasNext() && --k >= 0) {
			it.next();
			it.remove();
		}
		for (; j < ary.length; ++j)
			list.add(from + j, ary[j]);
	}
	/** Tests whether node1 is an ancessor of node 2.
	 * If node1 and node2 is the same, true is returned.
	 */
	public static boolean isAncestor(Component node1, Component node2) {
		for (; node2 != null; node2 = node2.getParent()) {
			if (node1 == node2)
				return true;
		}
		return false;
	}

	/** Removes all children of the specified component.
	 */
	public static void removeAllChildren(Component comp) {
		final List children = comp.getChildren();
		if (children.isEmpty()) return;

		for (Iterator it = new ArrayList(children).iterator(); it.hasNext();)
			((Component)it.next()).setParent(null); //detach
	}

	/** Returns whether this component is real visible (all its parents
	 * are visible).
	 * @see Component#isVisible
	 */
	public static boolean isRealVisible(Component comp) {
		for (; comp != null; comp = comp.getParent())
			if (!comp.isVisible())
				return false;
		return true;
	}
	/** Returns a collection of visible children.
	 * <p>The performance of the returned collection's size() is NO GOOD.
	 */
	public static Collection getVisibleChildren(Component comp) {
		final Collection children = comp.getChildren();
		return new AbstractCollection() {
			public int size() {
				int size = 0;
				for (Iterator it = children.iterator(); it.hasNext();) {
					if (((Component)it.next()).isVisible())
						++size;
				}
				return size;
			}
			public Iterator iterator() {
				return new Iterator() {
					final Iterator _it = children.iterator();
					Component _next;
					public boolean hasNext() {
						if (_next != null) return true;
						_next = getNextVisible(false);
						return _next != null;
					}
					public Object next() {
						if (_next != null) {
							final Component c = _next;
							_next = null;
							return c;
						}
						return getNextVisible(true);
					}
					public void remove() {
						throw new UnsupportedOperationException();
					}
					private Component getNextVisible(boolean blind) {
						while (blind || _it.hasNext()) {
							final Component c = (Component)_it.next();
							if (c.isVisible())
								return c;
						}
						return null;
					}
				};
			}
		};
	}

	/** Converts a string to an integer that can be used to access
	 * {@link Component#getAttribute(String, int)}
	 */
	public static final int getScope(String scope) {
		if ("component".equals(scope)) return Component.COMPONENT_SCOPE;
		if ("space".equals(scope)) return Component.SPACE_SCOPE;
		if ("page".equals(scope)) return Component.PAGE_SCOPE;
		if ("desktop".equals(scope)) return Component.DESKTOP_SCOPE;
		if ("session".equals(scope)) return Component.SESSION_SCOPE;
		if ("application".equals(scope)) return Component.APPLICATION_SCOPE;
		if ("request".equals(scope)) return Component.REQUEST_SCOPE;
		throw new IllegalArgumentException("Unknown scope: "+scope);
	}
	/** Converts an integer to the string representing the scope.
	 * @param scope one of {@link Component#COMPONENT_SCOPE},
	 * {@link Component#SPACE_SCOPE}, {@link Component#PAGE_SCOPE}, 
	 * {@link Component#DESKTOP_SCOPE}, {@link Component#SESSION_SCOPE},
	 * {@link Component#REQUEST_SCOPE}, and {@link Component#APPLICATION_SCOPE}.
	 */
	public static final String scopeToString(int scope) {
		switch (scope) {
		case Component.COMPONENT_SCOPE: return "component";
		case Component.SPACE_SCOPE: return "space";
		case Component.PAGE_SCOPE: return "page";
		case Component.DESKTOP_SCOPE: return "desktop";
		case Component.SESSION_SCOPE: return "session";
		case Component.APPLICATION_SCOPE: return "application";
		case Component.REQUEST_SCOPE: return "request";
		}
		throw new IllegalArgumentException("Unknown scope: "+scope);
	}

	/** Returns whether an ID is generated automatically.
	 */
	public static final boolean isAutoId(String id) {
		return org.zkoss.zk.ui.sys.ComponentsCtrl.isAutoId(id);
	}

	/** Converts a component to a path (relavant to another component).
	 * It is usefully to implement a serializable component that contains
	 * a reference to another component. In this case, we can not
	 * serializes the reference directly (otherwise, another component will
	 * be created, when deserialized).
	 *
	 * <p>Rather, it is better to store the path related, and then restore
	 * it back to a component by calling {@link #pathToComponent}.
	 *
	 * @param comp the component to be converted to path. It cannot be null.
	 * @param ref the component used to generated the path from.
	 * It cannot be null.
	 * @return the path. Notice that you have to use {@link #pathToComponent}
	 * to convert it back.
	 * @exception UnsupportedOperationException if we cannot find a path
	 * to the component to write.
	 * @since 3.0.0
	 */
	public static final String componentToPath(Component comp, Component ref) {
		//Implementation Note:
		//The path being written is a bit different to Path, if ref
		//is not an space owner
		//For example, if comp is the space owner, "" is written.
	 	//If comp is the same as ref, "." is written.
	 	if (comp == null) {
	 		return null;
	 	} else if (comp == ref) {
	 		return ".";
		} else {
			final String id = comp.getId();
			if (!(comp instanceof IdSpace) && isAutoId(id))
				throw new UnsupportedOperationException("comp must be assigned with ID or a space owner: "+comp);

			final StringBuffer sb = new StringBuffer(128);
			for (IdSpace space = ref.getSpaceOwner();;) {
				if (comp == space) {
					return sb.toString(); //could be ""
						//we don't generate id to make it work even if
						//its ID is changed
				} else if (space.getFellowIfAny(id) == comp) {
					if (sb.length() > 0) sb.append('/');
					return sb.append(id).toString();
				}

				if (sb.length() > 0) sb.append('/');
				sb.append("..");

				final Component parent =
					space instanceof Component ?
						((Component)space).getParent(): null;
				if (parent == null)
					throw new UnsupportedOperationException(
						"Unable to locate "+comp+" from "+ref);
				space = parent.getSpaceOwner();
			}
		}
	}
	/** Converts a path, generated by {@link #componentToPath}, to
	 * a component.
	 *
	 * @param ref the component used to generated the path from.
	 * It cannot be null. It is the same as the one when calling
	 * {@link #componentToPath}.
	 * @since 3.0.0
	 */
	public static final Component pathToComponent(String path, Component ref) {
		if (path == null) {
			return null;
		} else if (".".equals(path)) {
			return ref;
		} else if ("".equals(path)) {
			final IdSpace owner = ref.getSpaceOwner();
			if (!(owner instanceof Component))
				throw new IllegalStateException("The component is moved after serialized: "+ref);
			return (Component)owner;
		}
		return Path.getComponent(ref.getSpaceOwner(), path);
	}
	/** Wire fellow components and space owner ancestors of the specified 
	 * Id space into a controller Java object. This implementation checks the 
	 * setXxx() method names first then the
	 * field names. If a setXxx() method name matches the id of a fellow or
	 * space owner ancestors and with correct 
	 * argument type, the found method is called with the fellow component as the 
	 * argument. If no proper setXxx() method then search the field of the 
	 * controller object for a matched field with name equals to the fellow 
	 * component's id and proper type. Then the fellow component 
	 * is assigned as the value of the matched field.
	 * 
	 * <p>Note that fellow components are looked up first, then the space owner
	 * ancestors<p>
	 * <p>since 3.5.2, the controller would be assigned as a variable of the given idspace 
	 * per the naming convention composed of the idspace id and controller Class name. e.g.
	 * if the idspace id is "xwin" and the controller class is 
	 * org.zkoss.MyController, then the variable name would be "xwin$MyController"</p>
	 * 
	 * <p>This is useful in writing controller code in MVC design practice. You
	 * can wire the components into the controller object per the
	 * component's id and do whatever you like.</p>
	 *
	 * <p>Since 3.6.0, for Groovy or other environment that
	 * '$' is not applicable, you can invoke {@link #wireFellows(IdSpace,Object,char)}
	 * to use '_' as the separator.
	 * 
	 * @param idspace the id space to be bound
	 * @param controller the controller Java object to be injected the fellow components.
	 * @since 3.0.6
	 */
	public static final void wireFellows(IdSpace idspace, Object controller) {
		new Wire(controller).wireFellows(idspace);
	}
	/** Wire fellow components and space owner with a custom separator.
	 * The separator is used to separate the component ID and additional
	 * information, such as event name.
	 * By default, it is '$'. However, for Groovy or other environment that
	 * '$' is not applicable, you can invoke this method to use '_' as
	 * the separator.
	 * @see #wireFellows(IdSpace, Object)
	 * @since 3.6.0
	 */
	public static final
	void wireFellows(IdSpace idspace, Object controller, char separator) {
		new Wire(controller, separator).wireFellows(idspace);
	}
		
	/** <p>Wire accessible variable objects of the specified component into a 
	 * controller Java object. This implementation checks the 
	 * setXxx() method names first then the field names. If a setXxx() method 
	 * name matches the name of the resolved variable object with correct 
	 * argument type and the associated field value is null, then the method is 
	 * called with the resolved variable object as the argument. 
	 * If no proper setXxx() method then search the 
	 * field name of the controller object. If the field name matches the name
	 * of the resolved variable object with correct field type and null field
	 * value, the field is then assigned the resolved variable object.
	 * </p> 
	 * 
	 * <p>since 3.5.2, the controller would be assigned as a variable of the given component
	 * per the naming convention composed of the component id and controller Class name. e.g.
	 * if the component id is "xwin" and the controller class is 
	 * org.zkoss.MyController, then the variable name would be "xwin$MyController"</p>
	 *
	 * <p>This is useful in writing controller code in MVC design practice. You
	 * can wire the embedded objects, components, and accessible variables into 
	 * the controller object per the components' id and variables' name and do 
	 * whatever you like.
	 * </p>
	 
	 * <p>Since 3.6.0, for Groovy or other environment that
	 * '$' is not applicable, you can invoke {@link #wireVariables(Component,Object,char)}
	 * to use '_' as the separator.
	 * 
	 * @param comp the reference component to wire variables
	 * @param controller the controller Java object to be injected the 
	 * accessible variable objects.
	 * @see org.zkoss.zk.ui.util.GenericAutowireComposer
	 * @since 3.0.6
	 */
	public static final void wireVariables(Component comp, Object controller) {
		new Wire(controller).wireVariables(comp);
	}
	/** Wire accessible variable objects of the specified component with a custom separator.
	 * The separator is used to separate the component ID and additional
	 * information, such as event name.
	 * By default, it is '$'. However, for Groovy or other environment that
	 * '$' is not applicable, you can invoke this method to use '_' as
	 * the separator.
	 * @see #wireVariables(Component, Object)
	 * @since 3.6.0
	 */
	public static final
	void wireVariables(Component comp, Object controller, char separator) {
		new Wire(controller, separator).wireVariables(comp);
	}
	
	/** <p>Wire accessible variables of the specified page into a 
	 * controller Java object. This implementation checks the 
	 * setXxx() method names first then the field names. If a setXxx() method 
	 * name matches the name of the resolved variable object with correct 
	 * argument type and the associated field value is null, then the method is 
	 * called with the resolved variable object as the argument. 
	 * If no proper setXxx() method then search the 
	 * field name of the controller object. If the field name matches the name
	 * of the resolved variable object with correct field type and null field
	 * value, the field is then assigned the resolved variable object.</p> 
	 *
	 * <p>since 3.5.2, the controller would be assigned as a variable of the given page 
	 * per the naming convention composed of the page id and controller Class name. e.g.
	 * if the page id is "xpage" and the controller class is 
	 * org.zkoss.MyController, then the variable name would be "xpage$MyController"</p>
	 *
	 * <p>Since 3.0.8, if the method name of field name matches the ZK implicit
	 * object name, ZK implicit object will be wired in, too.</p> 
	 * <p>This is useful in writing controller code in MVC design practice. You
	 * can wire the embedded objects, components, and accessible variables into 
	 * the controller object per the component's id and variable name and do 
	 * whatever you like.
	 * </p>
	 * 
	 * <p>Since 3.6.0, for Groovy or other environment that
	 * '$' is not applicable, you can invoke {@link #wireVariables(Page,Object,char)}
	 * to use '_' as the separator.
	 * 
	 * @param page the reference page to wire variables
	 * @param controller the controller Java object to be injected the fellow components.
	 * @since 3.0.6
	 */
	public static final void wireVariables(Page page, Object controller) {
		new Wire(controller).wireVariables(page);
	}
	/** Wire accessible variable objects of the specified page with a custom separator.
	 * The separator is used to separate the component ID and additional
	 * information, such as event name.
	 * By default, it is '$'. However, for Groovy or other environment that
	 * '$' is not applicable, you can invoke this method to use '_' as
	 * the separator.
	 * @see #wireVariables(Page, Object)
	 * @since 3.6.0
	 */
	public static final
	void wireVariables(Page page, Object controller, char separator) {
		new Wire(controller, separator).wireVariables(page);
	}

	/** Wire controller as a variable objects of the specified component with a custom separator.
	 * The separator is used to separate the component ID and the controller.
	 * By default, it is '$'. However, for Groovy or other environment that
	 * '$' is not applicable, you can invoke {@link #wireController(Component, Object, char)}
	 * to use '_' as the separator.
	 * @since 3.6.1
	 */
	public static final
	void wireController(Component comp, Object controller) {
		new Wire(controller).wireController(comp, comp.getId());
	}
	
	/** Wire controller as a variable objects of the specified component with a custom separator.
	 * The separator is used to separate the component ID and the controller.
	 * By default, it is '$'. However, for Groovy or other environment that
	 * '$' is not applicable, you can invoke this method to use '_' as
	 * the separator.
	 * @since 3.6.1
	 */
	public static final
	void wireController(Component comp, Object controller, char separator) {
		new Wire(controller, separator).wireController(comp, comp.getId());
	}
	
	/** <p>Adds forward conditions to myid source component so onXxx source 
	 * event received by 
	 * myid component can be forwarded to the specified target 
	 * component with the target event name onXxx$myid.</p> 
	 * <p>The controller is a POJO file with onXxx$myid methods (the event handler 
	 * codes). This utility method search such onXxx$myid methods and adds 
	 * forward condition to the source myid component looked up by   
	 * {@link Component#getVariable} of the specified component, so you 
	 * don't have to specify in zul file the "forward" attribute one by one. 
	 * If the source component cannot be looked up or the object looked up is 
	 * not a component, this method will log the error and ignore it.
	 * </p>
	 * <p>since 3.0.8, cascade '$' will add Forwards cascadely. E.g. define method 
	 * onClick$btn$w1 in window w2. This method will add a forward on the button 
	 * "btn.onClick=w1.onClick$btn" and add another forward on the window w1
	 * "w1.onClick$btn=w2.onClick$btn$w1"</p>
	 * 
	 * <p>Since 3.6.0, for Groovy or other environment that
	 * '$' is not applicable, you can invoke {@link #addForwards(Component,Object,char)}
	 * to use '_' as the separator.
	 * 
	 * @param comp the targetComponent
	 * @param controller the controller code with onXxx$myid event handler methods
	 * @since 3.0.7
	 */
	public static void addForwards(Component comp, Object controller) {
		addForwards(comp, controller, '$');
	}
	/** Adds forward conditions to the specified component with a custom separator.
	 * The separator is used to separate the component ID and additional
	 * information, such as event name.
	 * By default, it is '$'. However, for Groovy or other environment that
	 * '$' is not applicable, you can invoke this method to use '_' as
	 * the separator.
	 * @see #addForwards(Component, Object)
	 * @since 3.6.0
	 */
	public static
	void addForwards(Component comp, Object controller, char separator) {
		final Class cls = controller.getClass();
		final Method[] mtds = cls.getMethods();
		for (int j = 0; j < mtds.length; ++j) {
			final Method md = mtds[j];
			String mdname = md.getName();
			if (mdname.length() > 5 && mdname.startsWith("on") 
			&& Character.isUpperCase(mdname.charAt(2))) {
				Component xcomp = comp;
				int k = 0;
				do { //handle cascade $. e.g. onClick$btn$win1
					k = mdname.lastIndexOf(separator);
					if (k >= 3) { //found '$'
						final String srcevt = mdname.substring(0, k);
						if ((k+1) < mdname.length()) {
							final String srccompid = mdname.substring(k+1);
							final Object srccomp = xcomp.getVariable(srccompid, false);
							if (srccomp == null || !(srccomp instanceof Component)) {
								if (log.debugable()) {
									log.debug("Cannot find the associated component to forward event: "+mdname);
								}
								break;
							} else {
								((Component)srccomp).addForward(srcevt, xcomp, mdname);
								xcomp = (Component) srccomp;
								mdname = srcevt;
							}
						} else {
							throw new UiException("Illegal event method name(component id not specified or consecutive '"+separator+"'): "+md.getName());
						}
					}
				} while (k >= 3);
			}
		}
	}
	
	/**
	 * Returns whether the given id is an implicit ZK object id.
	 * 
	 * @param id Component id
	 * @return whether the given name is a implicit object.
	 * @since 3.5.2
	 */
	public static boolean isImplicit(String id) {
		return IMPLICIT_NAMES.contains(id);
	}

	private static final Set IMPLICIT_NAMES = new HashSet();
	static {  
		IMPLICIT_NAMES.add("application");
		IMPLICIT_NAMES.add("applicationScope");
		IMPLICIT_NAMES.add("arg");
		IMPLICIT_NAMES.add("componentScope");
		IMPLICIT_NAMES.add("desktop");
		IMPLICIT_NAMES.add("desktopScope");
		IMPLICIT_NAMES.add("execution");
		IMPLICIT_NAMES.add("event"); //since 3.6.1, #bug 2681819: normal page throws exception after installed zkspring
		IMPLICIT_NAMES.add("self");
		IMPLICIT_NAMES.add("session");
		IMPLICIT_NAMES.add("sessionScope");
		IMPLICIT_NAMES.add("spaceOwner");
		IMPLICIT_NAMES.add("spaceScope");
		IMPLICIT_NAMES.add("page");
		IMPLICIT_NAMES.add("pageScope");
		IMPLICIT_NAMES.add("requestScope");
		IMPLICIT_NAMES.add("param");
	}
	
	/** Internal Use only. 
	 * @since 3.6.0
	 */
	public static Object getImplicit(Component comp, String fdname) {
		//initialize implicit objects
		if ("self".equals(fdname))
			return comp;
		if ("spaceOwner".equals(fdname))
			return comp.getSpaceOwner();
		if ("page".equals(fdname))
			return comp.getPage();
		if ("desktop".equals(fdname))
			return comp.getDesktop();
		if ("session".equals(fdname))
			return comp.getDesktop().getSession();
		if ("application".equals(fdname))
			return comp.getDesktop().getWebApp();
		if ("componentScope".equals(fdname))
			return comp.getAttributes();
		if ("spaceScope".equals(fdname)) {
			final IdSpace spaceOwner = comp.getSpaceOwner();
			return (spaceOwner instanceof Page) ? 
					comp.getPage().getAttributes() : ((Component)spaceOwner).getAttributes();
		}
		if ("pageScope".equals(fdname))
			return comp.getPage().getAttributes();
		if ("desktopScope".equals(fdname))
			return comp.getDesktop().getAttributes();
		if ("sessionScope".equals(fdname))
			return comp.getDesktop().getSession().getAttributes();
		if ("applicationScope".equals(fdname))
			return comp.getDesktop().getWebApp().getAttributes();
		if ("requestScope".equals(fdname))
			return REQUEST_SCOPE_PROXY;
		if ("execution".equals(fdname))
			return EXECUTION_PROXY;
		if ("arg".equals(fdname))
			return ARG_PROXY;
		if ("param".equals(fdname))
			return PARAM_PROXY;
		//20090314, Henri Chen: No way to suppport "event" with an event proxy becuase org.zkoss.zk.Event is not an interface
		return null;
	}

	/** Internal Use only. 
	 * @since 3.6.0
	 */
	public static Object getImplicit(Page page, String fdname) {
		//initialize implicit objects
		if ("self".equals(fdname))
			return page;
		if ("spaceOwner".equals(fdname))
			return page;
		if ("page".equals(fdname))
			return page;
		if ("desktop".equals(fdname))
			return page.getDesktop();
		if ("session".equals(fdname))
			return page.getDesktop().getSession();
		if ("application".equals(fdname))
			return page.getDesktop().getWebApp();
		if ("componentScope".equals(fdname))
			return new HashMap(0);
		if ("spaceScope".equals(fdname))
			return page.getAttributes();
		if ("pageScope".equals(fdname))
			return page.getAttributes();
		if ("desktopScope".equals(fdname))
			return page.getDesktop().getAttributes();
		if ("sessionScope".equals(fdname))
			return page.getDesktop().getSession().getAttributes();
		if ("applicationScope".equals(fdname))
			return page.getDesktop().getWebApp().getAttributes();
		if ("requestScope".equals(fdname))
			return REQUEST_SCOPE_PROXY;
		if ("execution".equals(fdname))
			return EXECUTION_PROXY;
		if ("arg".equals(fdname))
			return EXECUTION_PROXY.getArg();
		if ("param".equals(fdname))
			return PARAM_PROXY;
		//20090314, Henri Chen: No way to suppport "event" with an event proxy becuase org.zkoss.zk.Event is not an interface
		return null;
	}
	
	/**
	 * Utility class for wiring variables
	 * @author henrichen
	 * @since 3.0.8
	 */
	private static class Wire {
		private final Object _controller;
		private final Set _injected;
		private final Map _fldMaps;
		private final char _separator;
		
		public Wire(Object controller) {
			this(controller, '$');
		}
		public Wire(Object controller, char separator) {
			_controller = controller;
			_separator = separator;
			_injected = new HashSet();
			_fldMaps = new LinkedHashMap(64);
			
			Class cls = _controller.getClass();
			do {
				Field[] flds = cls.getDeclaredFields();
				for (int j = 0; j < flds.length; ++j) {
					final Field fd = flds[j];
					final String fdname = fd.getName();
					if (!_fldMaps.containsKey(fdname))
						_fldMaps.put(fdname, fd);
				}
				cls = cls.getSuperclass();
			} while (cls != null && !Object.class.equals(cls));
		}
		/**
		 * Inject controller as variable of the specified component. You can
		 * then access the controller with the name pattern of 
		 * id + separator + "composer" 
		 * or id + separator + controller's class name.
		 * e.g. if the given id is "xwin" and the controller class name is "org.zkoss.MyController"
		 * then you can access the controller with the name of "xwin$MyController" or "xwin$composer".
		 *   
		 * @param comp component to be assigned the variable
		 * @param id the id used in controller name pattern
		 * @since 3.6.1
		 */
		public void wireController(Component comp, String id) {
			//feature #2778513, support {id}$composer name
			final String composerid =  id + _separator + "composer";
			if (!comp.containsVariable(composerid, true)) {
				comp.setVariable(composerid, _controller, true);
			}
			comp.setVariable(varname(id, _controller.getClass()), _controller, true);
		}
		
		public void wireController(Page page, String id) {
			final String composerid =  id + _separator + "composer";
			if (!page.containsVariable(composerid)) {
				page.setVariable(composerid, _controller);
			}
			page.setVariable(varname(id, _controller.getClass()), _controller);
		}
		
		public void wireFellows(IdSpace idspace) {
			//inject fellows
			final Collection fellows = idspace.getFellows();
			for(final Iterator it = fellows.iterator(); it.hasNext();) {
				final Component xcomp = (Component) it.next();
				injectFellow(xcomp);
			}
			//inject space owner ancestors
			IdSpace xidspace = idspace;
			if (xidspace instanceof Component) {
				wireController((Component)xidspace, ((Component)idspace).getId());
				while (true) {
					final Component parent = ((Component)xidspace).getParent();
					if (parent == null) {//hit page
						injectFellow(((Component)xidspace).getPage());
						break;
					}
					xidspace = parent.getSpaceOwner();
					injectFellow(xidspace);
				}
			} else {
				wireController((Page)xidspace, ((Component)idspace).getId());
				injectFellow((Page) idspace);
			}
		}
		public void wireVariables(Page page) {
			wireController(page, page.getId());
			myWireVariables(page);
		}
		public void wireVariables(Component comp) {
			wireController(comp, comp.getId());
			myWireVariables(comp);
		}
		private void myWireVariables(Object x) {
			wireImplicit(x);
			wireOthers(x);
		}
		private void wireImplicit(Object x) {
			for (final Iterator it= IMPLICIT_NAMES.iterator(); it.hasNext();) {
				final String fdname = (String) it.next();
				//we cannot inject event proxy because it is not an Interface
				if ("event".equals(fdname)) { 
					continue;
				}
				final Object arg = myGetImplicit(x, fdname);
				injectByName(arg, fdname);
			}
		}
		private void wireOthers(Object x) {
			//check methods
			final Class cls = _controller.getClass();
			Method[] mtds = cls.getMethods();
			for (int j = 0; j < mtds.length; ++j) {
				final Method md = mtds[j];
				final String mdname = md.getName();
				if (mdname.length() > 3 && mdname.startsWith("set") 
				&& Character.isUpperCase(mdname.charAt(3))) {
					final String fdname = Classes.toAttributeName(mdname);
					if (!_injected.contains(fdname)) { //if not injected yet
						final Class[] parmcls = md.getParameterTypes();
						if (parmcls.length == 1) {
							if (containsVariable(x, fdname)) {
								final Object arg = getVariable(x, fdname);
								final Class argcls = arg == null ? null : arg.getClass();
								injectByMethod(md, parmcls[0], argcls, arg, fdname);
							}
						}
					}
				}
			}

			//check fields
			for (final Iterator it=_fldMaps.entrySet().iterator();it.hasNext();) {
				final Entry entry = (Entry) it.next();
				final String fdname = (String) entry.getKey();
				if (!_injected.contains(fdname)) { //if not injected by setXxx yet
					if (containsVariable(x, fdname)) {
						final Object arg = getVariable(x, fdname);
						final Class argcls = arg == null ? null : arg.getClass();
						final Field fd = (Field) entry.getValue();
						injectField(arg, argcls, fd);
					}
				}
			}
		}

		private boolean containsVariable(Object x, String fdname) {
			//#feature 2770471 GenericAutowireComposer shall support wiring ZScript varible
			if (x instanceof Page) {
				final Page pg = (Page) x;
				return pg.getZScriptVariable(fdname) != null
					|| pg.containsVariable(fdname);
			} else {
				final Component cmp = (Component) x;
				return cmp.getPage().getZScriptVariable(cmp, fdname) != null
					|| cmp.containsVariable(fdname, false);
			}
		}
		
		private Object getVariable(Object x, String fdname) {
			//#feature 2770471 GenericAutowireComposer shall support wiring ZScript varible
			if (x instanceof Page) {
				final Page pg = (Page) x;
				Object arg = pg.getZScriptVariable(fdname);
				if (arg == null) {
					arg = pg.getVariable(fdname);
				}
				return arg;
			} else {
				final Component cmp = (Component) x;
				Object arg = cmp.getPage().getZScriptVariable(cmp, fdname);
				if (arg == null) {
					arg = cmp.getVariable(fdname, false);
				}
				return arg;
			}
		}
		
		private void injectFellow(Object arg) {
			//try setXxx
			final String fdname = (arg instanceof Page) ? 
					((Page)arg).getId() : ((Component)arg).getId();
			if (!Components.isAutoId(fdname)) {
				injectByName(arg, fdname);
			}
		}
		
		private void injectByName(Object arg, String fdname) {
			//argument to be injected is null; then no need to inject
			if (arg != null) {
				final String mdname = Classes.toMethodName(fdname, "set");
				final Class parmcls = arg.getClass();
				final Class tgtcls = _controller.getClass();
				try {
					final Method md = 
						Classes.getCloseMethod(tgtcls, mdname, new Class[] {parmcls});
					if (!injectByMethod(md, parmcls, parmcls, arg, fdname)) {
						injectFieldByName(arg, tgtcls, parmcls, fdname);
					}
				} catch (NoSuchMethodException ex) {
					//no setXxx() method, try inject into Field
					injectFieldByName(arg, tgtcls, parmcls, fdname);
				} catch (Exception ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
		}
		
		private void injectFieldByName(Object arg, Class tgtcls, Class parmcls, String fdname) {
			try {
				final Field fd = Classes.getAnyField(tgtcls, fdname);
				injectField(arg, parmcls, fd);
			} catch (NoSuchFieldException e) {
				//ignore
			} catch (Exception ex2) {
				throw UiException.Aide.wrap(ex2);
			}
		}
		
		private boolean injectByMethod(Method md, Class parmcls, Class argcls, Object arg, String fdname) {
			if (argcls == null || parmcls.isAssignableFrom(argcls)) {
				final Field fd = (Field) _fldMaps.get(fdname);
				if (fd != null) {
					final boolean old = fd.isAccessible();
					try {
						//check field value
						fd.setAccessible(true);
						final Object value = fd.get(_controller);
						if (value == null) {
							md.invoke(_controller, new Object[] {arg});
							if (fd.get(_controller) != null) { //field is set
								_injected.add(fdname); //mark as injected
								return true;
							}
						}
					} catch (Exception ex) {
						throw UiException.Aide.wrap(ex);
					} finally {
						fd.setAccessible(old);
					}
				} else {
					try {
						md.invoke(_controller, new Object[] {arg});
						_injected.add(fdname); //no field, just mark as injected
						return true;
					} catch (Exception ex) {
						throw UiException.Aide.wrap(ex);
					}
				}
			}
			return false;
		}
		
		private boolean injectField(Object arg, Class argcls, Field fd) {
			final boolean old = fd.isAccessible();
			try {
				fd.setAccessible(true);
				final Class fdcls = fd.getType();
				if (argcls != null && fdcls.isAssignableFrom(argcls)) { //correct type 
					final Object value = fd.get(_controller);
					if (value == null) {
						fd.set(_controller, arg);
						_injected.add(fd.getName());
						return true;
					}
				}
			} catch (Exception e) {
				throw UiException.Aide.wrap(e);
			} finally {
				fd.setAccessible(old);
			}
			return false;
		}
		
		private Object myGetImplicit(Object x, String fdname) {
			return x instanceof Page ?
				getImplicit((Page)x, fdname) : getImplicit((Component)x, fdname);
		}
		private String varname(String id, Class cls) {
			final String clsname = cls.getName();
			int j = clsname.lastIndexOf('.');
			return id + _separator + (j >= 0 ? clsname.substring(j+1) : clsname);
		}
	}
	/** Execution Proxy */
	public static final Exec EXECUTION_PROXY = new Exec();
	
	/** Request Scope Proxy */
	public static final RequestScope REQUEST_SCOPE_PROXY = new RequestScope();
	
	/** Arg Proxy */
	public static final Arg ARG_PROXY = new Arg();
	
	/** Param Proxy */
	public static final Param PARAM_PROXY = new Param();

	//Proxy to read current execution
	private static class Exec implements Execution {
		private static final Execution exec() {
			return Executions.getCurrent();
		}
		
		public void addAuResponse(String key, AuResponse response) {
			exec().addAuResponse(key, response);
		}

		public Component createComponents(PageDefinition pagedef,
				Component parent, Map arg) {
			return exec().createComponents(pagedef, parent, arg);
		}

		public Component createComponents(String uri, Component parent, Map arg) {
			return exec().createComponents(uri, parent, arg);
		}

		public Component[] createComponents(PageDefinition pagedef, Map arg) {
			return exec().createComponents(pagedef, arg);
		}

		public Component[] createComponents(String uri, Map arg) {
			return exec().createComponents(uri, arg);
		}

		public Component createComponentsDirectly(String content,
				String extension, Component parent, Map arg) {
			return exec().createComponentsDirectly(content, extension, parent, arg);
		}

		public Component createComponentsDirectly(Document content,
				String extension, Component parent, Map arg) {
			return exec().createComponentsDirectly(content, extension, parent, arg);
		}

		public Component createComponentsDirectly(Reader reader,
				String extension, Component parent, Map arg) throws IOException {
			return exec().createComponentsDirectly(reader, extension, parent, arg);
		}

		public Component[] createComponentsDirectly(String content,
				String extension, Map arg) {
			return exec().createComponentsDirectly(content, extension, arg);
		}

		public Component[] createComponentsDirectly(Document content,
				String extension, Map arg) {
			return exec().createComponentsDirectly(content, extension, arg);
		}

		public Component[] createComponentsDirectly(Reader reader,
				String extension, Map arg) throws IOException {
			return exec().createComponentsDirectly(reader, extension, arg);
		}

		public String encodeURL(String uri) {
			return exec().encodeURL(uri);
		}

		public Object evaluate(Component comp, String expr, Class expectedType) {
			return exec().evaluate(comp, expr, expectedType);
		}

		public Object evaluate(Page page, String expr, Class expectedType) {
			return exec().evaluate(page, expr, expectedType);
		}

		public void forward(Writer writer, String page, Map params, int mode)
				throws IOException {
			exec().forward(writer, page, params, mode);
			
		}

		public void forward(String page) throws IOException {
			exec().forward(page);
		}

		public Map getArg() {
			return exec().getArg();
		}

		public Object getAttribute(String name) {
			return exec().getAttribute(name);
		}

		public Map getAttributes() {
			return exec().getAttributes();
		}

		public String getContextPath() {
			return exec().getContextPath();
		}

		public Desktop getDesktop() {
			return exec().getDesktop();
		}

		public Evaluator getEvaluator(Page page, Class expfcls) {
			return exec().getEvaluator(page, expfcls);
		}

		public Evaluator getEvaluator(Component comp, Class expfcls) {
			return exec().getEvaluator(comp, expfcls);
		}

		public String getLocalAddr() {
			return exec().getLocalAddr();
		}

		public String getLocalName() {
			return exec().getLocalName();
		}

		public int getLocalPort() {
			return exec().getLocalPort();
		}

		public Object getNativeRequest() {
			return exec().getNativeRequest();
		}

		public Object getNativeResponse() {
			return exec().getNativeResponse();
		}

		public PageDefinition getPageDefinition(String uri) {
			return exec().getPageDefinition(uri);
		}

		public PageDefinition getPageDefinitionDirectly(String content,
				String extension) {
			return exec().getPageDefinitionDirectly(content, extension);
		}

		public PageDefinition getPageDefinitionDirectly(Document content,
				String extension) {
			return exec().getPageDefinitionDirectly(content, extension);
		}

		public PageDefinition getPageDefinitionDirectly(Reader reader,
				String extension) throws IOException {
			return exec().getPageDefinitionDirectly(reader, extension);
		}

		public String getParameter(String name) {
			return exec().getParameter(name);
		}

		public Map getParameterMap() {
			return exec().getParameterMap();
		}

		public String[] getParameterValues(String name) {
			return exec().getParameterValues(name);
		}

		public String getRemoteAddr() {
			return exec().getRemoteAddr();
		}

		public String getRemoteHost() {
			return exec().getRemoteHost();
		}

		/** @deprecated
		 */
		public String getRemoteName() {
			return exec().getRemoteName();
		}

		public String getRemoteUser() {
			return exec().getRemoteUser();
		}

		public String getServerName() {
			return exec().getServerName();
		}

		public int getServerPort() {
			return exec().getServerPort();
		}

		public String getScheme() {
			return exec().getScheme();
		}

		public String getUserAgent() {
			return exec().getUserAgent();
		}

		public Principal getUserPrincipal() {
			return exec().getUserPrincipal();
		}

		public VariableResolver getVariableResolver() {
			return exec().getVariableResolver();
		}

		public void include(Writer writer, String page, Map params, int mode)
				throws IOException {
			exec().include(writer, page, params, mode);
			
		}

		public void include(String page) throws IOException {
			exec().include(page);
		}

		public boolean isAsyncUpdate(Page page) {
			return exec().isAsyncUpdate(page);
		}

		public boolean isBrowser() {
			return exec().isBrowser();
		}
		public boolean isBrowser(String type) {
			return exec().isBrowser(type);
		}

		public boolean isExplorer() {
			return exec().isExplorer();
		}

		public boolean isExplorer7() {
			return exec().isExplorer7();
		}
		
		public boolean isOpera() {
			return exec().isOpera();
		}

		public boolean isForwarded() {
			return exec().isForwarded();
		}

		public boolean isGecko() {
			return exec().isGecko();
		}
		public boolean isGecko3() {
			return exec().isGecko3();
		}

		public boolean isHilDevice() {
			return exec().isHilDevice();
		}

		public boolean isIncluded() {
			return exec().isIncluded();
		}

		public boolean isMilDevice() {
			return exec().isMilDevice();
		}

		public boolean isRobot() {
			return exec().isRobot();
		}

		public boolean isSafari() {
			return exec().isSafari();
		}

		public boolean isUserInRole(String role) {
			return exec().isUserInRole(role);
		}

		public boolean isVoided() {
			return exec().isVoided();
		}

		public void popArg() {
			exec().popArg();
		}

		public void postEvent(Event evt) {
			exec().postEvent(evt);
		}

		public void postEvent(int priority, Event evt) {
			exec().postEvent(priority, evt);
		}

		public void pushArg(Map arg) {
			exec().pushArg(arg);
		}

		public void removeAttribute(String name) {
			exec().removeAttribute(name);
		}

		public void sendRedirect(String uri) {
			exec().sendRedirect(uri);
		}

		public void sendRedirect(String uri, String target) {
			exec().sendRedirect(uri, target);
		}

		public void setAttribute(String name, Object value) {
			exec().setAttribute(name, value);
		}

		public void setVoided(boolean voided) {
			exec().setVoided(voided);
		}

		public String toAbsoluteURI(String uri, boolean skipInclude) {
			return exec().toAbsoluteURI(uri, skipInclude);
		}

		public void addResponseHeader(String name, String value) {
			exec().addResponseHeader(name, value);
		}

		public boolean containsResponseHeader(String name) {
			return exec().containsResponseHeader(name);
		}

		public String getHeader(String name) {
			return exec().getHeader(name);
		}

		public Iterator getHeaderNames() {
			return exec().getHeaderNames();
		}

		public Iterator getHeaders(String name) {
			return exec().getHeaders(name);
		}

		public void setResponseHeader(String name, String value) {
			exec().setResponseHeader(name, value);
		}
	}

	//Proxy to read current requestScope
	private static class RequestScope implements Map {
		protected Map req() {
			return Executions.getCurrent().getAttributes();
		}
		public void clear() {
			req().clear();
		}
		public boolean containsKey(Object key) {
			return req().containsKey(key);
		}
		public boolean containsValue(Object value) {
			return req().containsValue(value);
		}
		public Set entrySet() {
			return req().entrySet();
		}
		public Object get(Object key) {
			return req().get(key);
		}
		public boolean isEmpty() {
			return req().isEmpty();
		}
		public Set keySet() {
			return req().keySet();
		}
		public Object put(Object key, Object value) {
			return req().put(key, value);
		}
		public void putAll(Map arg0) {
			req().putAll(arg0);
		}
		public Object remove(Object key) {
			return req().remove(key);
		}
		public int size() {
			return req().size();
		}
		public Collection values() {
			return req().values();
		}
	}
	
	private static class Arg extends RequestScope {
		protected Map req() {
			return Executions.getCurrent().getArg();
		}
	}
	
	private static class Param extends RequestScope {
		protected Map req() {
			return Executions.getCurrent().getParameterMap();
		}
	}
}
