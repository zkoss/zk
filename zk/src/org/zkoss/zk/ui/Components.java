/* Components.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 13 20:55:18     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.Principal;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

import org.zkoss.lang.Classes;
import org.zkoss.idom.Document;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.xel.VariableResolver;

import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.xel.Evaluator;

/**
 * Utilities to access {@link Component}.
 *
 * @author tomyeh
 */
public class Components {
	private static final Log log = Log.lookup(Components.class);
	private static final Log _zklog = Log.lookup("org.zkoss.zk.log");

	protected Components() {}

	/** Returns the parent of the ID space, or null if not found.
	 * @since 5.0.0
	 */
	public static IdSpace getParentIdSpace(IdSpace idspace) {
		if (idspace instanceof Component) {
			final Component c = (Component)idspace;
			final Component p = c.getParent();
			return p != null ? p.getSpaceOwner(): c.getPage();
		}
		return null;
	}

	/** Sorts the components in the list.
	 *
	 * <p>Note: you cannot use {@link Collections#sort} to sort
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

	/** Returns the root component of the specified one.
	 * Notice that it could return <code>comp</code>, if it is already
	 * a root component (or it is null).
	 * @since 3.6.3
	 */
	public static Component getRoot(Component comp) {
		if (comp == null)
			return null;
		for (;;) {
			final Component p = comp.getParent();
			if (p == null) return comp;
			comp = p;
		}
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
	 * It is the same as <code>comp.getChildren().clear()</code>.
	 */
	public static void removeAllChildren(Component comp) {
		comp.getChildren().clear();
	}

	/** Returns the component definition of the specified class in all
	 * language of the specified device, or null if not found
	 *
	 * @param deviceType the device type ({@link org.zkoss.zk.device.Device}),
	 * such as ajax. It cannot be null.
	 * @param cls the implementation class of the component.
	 * @since 5.0.0
	 */
	public static final ComponentDefinition
	getDefinitionByDeviceType(String deviceType, Class cls) {
		for (Iterator it = LanguageDefinition.getByDeviceType(deviceType).iterator();
		it.hasNext();) {
			final LanguageDefinition ld = (LanguageDefinition)it.next();
			try {
				return ld.getComponentDefinition(cls);
			} catch (DefinitionNotFoundException ex) { //ignore
			}
		}
		return null;
	}

	/** Returns whether this component is real visible (all its parents
	 * are visible).
	 * <p>Note: true is returned if comp is null.
	 * In other words, it can be used to examine parent's real visibity
	 * even if it is a root component,
	 * such as <code>Components.isRealVisible(getParent())</code>.
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
	 * {@link Component#getAttributeOrFellow} of the specified component, so you 
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
			if (mdname.length() >= 5 && Events.isValid(mdname)) { //onX$Y
				Component xcomp = comp;
				int k = 0;
				do { //handle cascade $. e.g. onClick$btn$win1
					k = mdname.lastIndexOf(separator);
					if (k >= 3) { //found '$'
						final String srcevt = mdname.substring(0, k);
						if ((k+1) < mdname.length()) {
							final String srccompid = mdname.substring(k+1);
							final Object srccomp = xcomp.getAttributeOrFellow(srccompid, true);
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
	
	/** Retuns the implicit object of the specified name, or null
	 * if not found.
	 *
	 * <p>Notice that it does check for the current scope
	 * ({@link org.zkoss.zk.ui.ext.Scopes#getCurrent}).
	 * Rather, {@link org.zkoss.zk.ui.ext.Scopes#getImplicit}
	 * depends on this method.
	 *
	 * @param page the page. If page is null and comp is not,
	 * comp.getPage() is assumed
	 * @see org.zkoss.zk.ui.ext.Scopes#getImplicit
	 * @since 5.0.0
	 */
	public static
	Object getImplicit(Page page, Component comp, String name) {
		if (comp != null && page == null)
			page = getPage(comp);

		if ("log".equals(name))
			return _zklog;
		if ("self".equals(name))
			return comp != null ? comp: (Object)page;
		if ("spaceOwner".equals(name))
			return comp != null ? comp.getSpaceOwner(): (Object)page;
		if ("page".equals(name))
			return page;
		if ("desktop".equals(name))
			return comp != null ? getDesktop(comp): page.getDesktop();
		if ("session".equals(name))
			return comp != null ? getSession(comp): page.getDesktop().getSession();
		if ("application".equals(name))
			return comp != null ? getWebApp(comp): page.getDesktop().getWebApp();
		if ("componentScope".equals(name))
			return comp != null ? comp.getAttributes(): Collections.EMPTY_MAP;
		if ("spaceScope".equals(name)) {
			final Scope scope = comp != null ? (Scope)comp.getSpaceOwner(): (Scope)page;
			return scope != null ? scope.getAttributes(): Collections.EMPTY_MAP;
		}
		if ("pageScope".equals(name))
			return page != null ? page.getAttributes(): Collections.EMPTY_MAP;
		if ("desktopScope".equals(name)) {
			final Desktop dt = comp != null ? getDesktop(comp): page.getDesktop();
			return dt != null ? dt.getAttributes(): Collections.EMPTY_MAP;
		}
		if ("sessionScope".equals(name)) {
			final Session sess = comp != null ? getSession(comp): page.getDesktop().getSession();
			return sess != null ? sess.getAttributes(): Collections.EMPTY_MAP;
		}
		if ("applicationScope".equals(name)) {
			final WebApp app = comp != null ? getWebApp(comp): page.getDesktop().getWebApp();
			return app != null ? app.getAttributes(): Collections.EMPTY_MAP;
		}
		if ("requestScope".equals(name)) {
			final Execution exec = Executions.getCurrent();
			return exec != null ?
				new org.zkoss.web.servlet.xel.RequestScope(
					(javax.servlet.ServletRequest)exec.getNativeRequest()):
				Collections.EMPTY_MAP;
		}
		if ("execution".equals(name))
			return Executions.getCurrent();
		if ("arg".equals(name)) {
			final Execution exec = Executions.getCurrent();
			return exec != null ? exec.getArg(): Collections.EMPTY_MAP;
		}
		if ("param".equals(name)) {
			final Execution exec = Executions.getCurrent();
			return exec != null ?
				new org.zkoss.web.servlet.xel.ParameterMap(
					(javax.servlet.ServletRequest)exec.getNativeRequest()):
				Collections.EMPTY_MAP;
		}
		//20090314, Henri Chen: No way to suppport "event" with an event proxy becuase org.zkoss.zk.Event is not an interface
		return null;
	}
	/** Retuns the implicit object of the specified name, or null
	 * if not found.
	 * <p>It is the same as getImplicit(null, comp, name).
	 * @since 3.6.0
	 */
	public static Object getImplicit(Component comp, String name) {
		return getImplicit(null, comp, name);
	}
	/** Retuns the implicit object of the specified name, or null
	 * if not found.
	 * <p>It is the same as getImplicit(page, null, name).
	 * @since 3.6.0
	 */
	public static Object getImplicit(Page page, String name) {
		return getImplicit(page, null, name);
	}

	private static Desktop getDesktop(Component comp) {
		final Desktop dt = comp.getDesktop();
		if (dt != null) return dt;
		final Execution exec = Executions.getCurrent();
		return exec != null ? exec.getDesktop(): null;
	}
	private static WebApp getWebApp(Component comp) {
		final Desktop dt = getDesktop(comp);
		return dt != null ? dt.getWebApp(): null;
	}
	private static Session getSession(Component comp) {
		final Desktop dt = getDesktop(comp);
		return dt != null ? dt.getSession(): null;
	}
	private static Page getPage(Component comp) {
		Page page = comp.getPage();
		if (page != null) return page;

		final Execution exec = Executions.getCurrent();
		if (exec != null) {
			page = ((ExecutionCtrl)exec).getCurrentPage();
			if (page != null) return page;

			final Desktop dt = exec.getDesktop();
			if (dt != null) { //just in case
				final Collection pgs = dt.getPages();
				if (pgs != null && !pgs.isEmpty())
					return (Page)pgs.iterator().next();
			}
		}
		return null;
	}
	
	/**
	 * Utility class for wiring variables
	 * @author henrichen
	 */
	private static class Wire {
		private final Object _controller;
		private final Set _injected;
		private final Map _fldMaps;
		private final char _separator;
		
		private Wire(Object controller) {
			this(controller, '$');
		}
		private Wire(Object controller, char separator) {
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
		 * Inject controller as variable of the specified component.
		 */
		private void wireController(Component comp, String id) {
			//feature #2778513, support {id}$composer name
			final String composerid =  id + _separator + "composer";
			if (!comp.hasAttributeOrFellow(composerid, false)) {
				comp.setAttribute(composerid, _controller);
			}
			comp.setAttribute(varname(id, _controller.getClass()), _controller);
		}
		
		/**
		 * Inject controller as variable of the specified page.
		 */
		private void wireController(Page page, String id) {
			final String composerid =  id + _separator + "composer";
			if (!page.hasAttributeOrFellow(composerid, false)) {
				page.setAttribute(composerid, _controller);
			}
			page.setAttribute(varname(id, _controller.getClass()), _controller);
		}
		
		private void wireFellows(IdSpace idspace) {
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
						final Page page = ((Component)xidspace).getPage();
						if (page != null) injectFellow(page);
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
		private void wireVariables(Page page) {
			wireController(page, page.getId());
			myWireVariables(page);
		}
		private void wireVariables(Component comp) {
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
				if ((md.getModifiers() & Modifier.STATIC) == 0
				&& mdname.length() > 3 && mdname.startsWith("set") 
				&& Character.isUpperCase(mdname.charAt(3))) {
					final String fdname = Classes.toAttributeName(mdname);
					if (!_injected.contains(fdname)) { //if not injected yet
						final Class[] parmcls = md.getParameterTypes();
						if (parmcls.length == 1) {
							if (containsVariable(x, fdname)) {
								final Object arg = getVariable(x, fdname);
								if (!injectByMethod(md, parmcls[0], arg == null ? null : arg.getClass(), arg, fdname)) {
									final Object arg2 = getFellow(x, fdname);
									if (arg2 != arg && arg2 != null)
										injectByMethod(md, parmcls[0], arg2.getClass(), arg2, fdname);
								}
							} else if ((x instanceof Component || x instanceof Page) &&
							fdname.indexOf(_separator) >= 0) {
								final Object arg = getFellowByPath(x, fdname);
								if (arg != null)
									injectByMethod(md, parmcls[0], arg.getClass(), arg, fdname);
							}
						}
					}
				}
			}

			//check fields
			for (final Iterator it=_fldMaps.entrySet().iterator();it.hasNext();) {
				final Entry entry = (Entry) it.next();
				final String fdname = (String) entry.getKey();
				final Field fd = (Field) entry.getValue();
				if ((fd.getModifiers() & Modifier.STATIC) == 0
				&& !_injected.contains(fdname)) { //if not injected by setXxx yet
					if (containsVariable(x, fdname)) {
						final Object arg = getVariable(x, fdname);
						if (!injectField(arg, arg == null ? null : arg.getClass(), fd)) {
							final Object arg2 = getFellow(x, fdname);
							if (arg2 != arg && arg2 != null)
								injectField(arg2, arg2.getClass(), fd);
						}
					} else if ((x instanceof Component || x instanceof Page) &&
					fdname.indexOf(_separator) >= 0) {
						final Object arg = getFellowByPath(x, fdname);
						if (arg != null)
							injectField(arg, arg.getClass(), fd);
					}
				}
			}
		}

		/** @param x either a page or component. It cannot be null.*/
		private Object getFellowByPath(Object x, String name) {
			return Path.getComponent(
				x instanceof Page ? (Page)x: ((Component)x).getSpaceOwner(),
					name.replace(_separator, '/'));
		}

		private boolean containsVariable(Object x, String fdname) {
			//#feature 2770471 GenericAutowireComposer shall support wiring ZScript varible
			if (x instanceof Page) {
				final Page pg = (Page) x;
				return pg.getZScriptVariable(fdname) != null
					|| pg.hasAttributeOrFellow(fdname, true);
			} else {
				final Component cmp = (Component) x;
				final Page page = getPage(cmp);
				return (page != null && page.getZScriptVariable(cmp, fdname) != null)
					|| cmp.hasAttributeOrFellow(fdname, true);
			}
		}
		
		private Object getVariable(Object x, String fdname) {
			//#feature 2770471 GenericAutowireComposer shall support wiring ZScript varible
			if (x instanceof Page) {
				final Page pg = (Page) x;
				Object arg = pg.getZScriptVariable(fdname);
				if (arg == null) {
					arg = pg.getAttributeOrFellow(fdname, true);
				}
				return arg;
			} else {
				final Component cmp = (Component) x;
				final Page page = getPage(cmp);
				Object arg = page != null ? page.getZScriptVariable(cmp, fdname): null;
				if (arg == null) {
					arg = cmp.getAttributeOrFellow(fdname, true);
				}
				return arg;
			}
		}
		private Object getFellow(Object x, String fdname) {
			return x instanceof Page ? ((Page)x).getFellowIfAny(fdname, true):
				x instanceof Component ? ((Component)x).getFellowIfAny(fdname, true): null;
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
		
		/** Returns false if there is such field but the target class doesn't match.
		 * In other words, false means the caller can try another object (arg).
		 */
		private boolean injectByMethod(Method md, Class parmcls, Class argcls, Object arg, String fdname) {
			if (argcls == null || parmcls.isAssignableFrom(argcls)) {
				final Field fd = (Field) _fldMaps.get(fdname);
				if (fd != null && fd.getType().equals(parmcls)) {
					final boolean old = fd.isAccessible();
					try {
						//check field value
						fd.setAccessible(true);
						final Object value = fd.get(_controller);
						if (value == null) {
							md.invoke(_controller, new Object[] {arg});
							if (fd.get(_controller) == arg) { //field is set
								_injected.add(fdname); //mark as injected
							}
						}
						return true;
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
			return false; //mismatch try again
		}

		/** Returns false if there is such field but the target class doesn't match.
		 * In other words, false means the caller can try another object (arg).
		 */
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
					}
					return true;
				}
				return false; //mismatch (and try other)
			} catch (Exception e) {
				throw UiException.Aide.wrap(e);
			} finally {
				fd.setAccessible(old);
			}
		}
		
		private Object myGetImplicit(Object x, String fdname) {
			return x instanceof Page ?
				getImplicit((Page)x, fdname) :
				getImplicit((Component)x, fdname);
		}
		private String varname(String id, Class cls) {
			final String clsname = cls.getName();
			int j = clsname.lastIndexOf('.');
			return id + _separator + (j >= 0 ? clsname.substring(j+1) : clsname);
		}
	}
}
