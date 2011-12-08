/* Components.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun 13 20:55:18     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.security.Principal;
import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.zkoss.idom.Document;
import org.zkoss.lang.Objects;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.ExpressionFactory;

import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.util.ConventionWires;
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
	public static void sort(List<? extends Component> list, Comparator<? super Component> cpr) {
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
	void replaceChildren(Component parent, Collection<Component> newChildren) {
		final Collection<Component> children = parent.getChildren();
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
	public static void sort(List<? extends Component> list, int from, int to, Comparator<? super Component> cpr) {
		final Component ary[] = CollectionsX.toArray(list, new Component[0], from, to);
		Arrays.sort(ary, cpr);

		ListIterator<? extends Component> it = list.listIterator(from);
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
			add(list, from + j, ary[j]);
	}
	@SuppressWarnings("unchecked")
	private static void add(List list, int index, Object o) { //to minimize the unchecked range
		list.add(index, o);
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
		for (LanguageDefinition ld: LanguageDefinition.getByDeviceType(deviceType)) {
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
	public static Collection<Component> getVisibleChildren(Component comp) {
		final Collection<Component> children = comp.getChildren();
		return new AbstractCollection<Component>() {
			public int size() {
				int size = 0;
				for (Component c: children) {
					if (c.isVisible())
						++size;
				}
				return size;
			}
			public Iterator<Component> iterator() {
				return new Iterator<Component>() {
					final Iterator<Component> _it = children.iterator();
					Component _next;
					public boolean hasNext() {
						if (_next != null) return true;
						_next = getNextVisible(false);
						return _next != null;
					}
					public Component next() {
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
			if (!(comp instanceof IdSpace) && id.length() == 0)
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
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final void wireFellows(IdSpace idspace, Object controller) {
		ConventionWires.wireFellows(idspace, controller);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final
	void wireFellows(IdSpace idspace, Object controller, char separator) {
		ConventionWires.wireFellows(idspace, controller, separator);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final
	void wireFellows(IdSpace idspace, Object controller, char separator,
	boolean ignoreZScript, boolean ignoreXel) {
		ConventionWires.wireFellows(idspace, controller, separator, ignoreZScript, ignoreXel);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final void wireVariables(Component comp, Object controller) {
		ConventionWires.wireVariables(comp, controller);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final
	void wireVariables(Component comp, Object controller, char separator) {
		ConventionWires.wireVariables(comp, controller, separator);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final
	void wireVariables(Component comp, Object controller, char separator,
	boolean ignoreZScript, boolean ignoreXel) {
		ConventionWires.wireVariables(comp, controller, separator, ignoreZScript, ignoreXel);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final void wireVariables(Page page, Object controller) {
		ConventionWires.wireVariables(page, controller);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final
	void wireVariables(Page page, Object controller, char separator) {
		ConventionWires.wireVariables(page, controller, separator);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final
	void wireVariables(Page page, Object controller, char separator,
	boolean ignoreZScript, boolean ignoreXel) {
		ConventionWires.wireVariables(page, controller, separator, ignoreZScript, ignoreXel);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final
	void wireController(Component comp, Object controller) {
		ConventionWires.wireController(comp, controller);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final
	void wireController(Component comp, Object controller, char separator) {
		ConventionWires.wireController(comp, controller, separator);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final
	void wireController(Component comp, Object controller, char separator,
	boolean ignoreZScript, boolean ignoreXel) {
		ConventionWires.wireController(comp, controller, separator, ignoreZScript, ignoreXel);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static final
	void wireImplicit(Component comp, Object controller) {
		ConventionWires.wireImplicit(comp, controller);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static void addForwards(Component comp, Object controller) {
		ConventionWires.addForwards(comp, controller);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link ConventionWires}.
	 */
	public static
	void addForwards(Component comp, Object controller, char separator) {
		ConventionWires.addForwards(comp, controller, separator);
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
	/** Retuns a readonly collection of the names of the implicit objects.
	 * @since 6.0.0
	 */
	public static Collection<String> getImplicitNames() {
		return IMPLICIT_NAMES;
	}

	private static final Set<String> IMPLICIT_NAMES = new HashSet<String>();
	static { 
		final String[] names = {
			"application", "applicationScope", "arg",
			"componentScope",
			"desktop", "desktopScope",
			"execution",
			"event", //since 3.6.1, #bug 2681819: normal page throws exception after installed zkspring
			"self",
			"session", "sessionScope",
			"spaceOwner", "spaceScope",
			"page", "pageScope",
			"requestScope", "param"};
		for (int j = 0; j < names.length; ++j)
			IMPLICIT_NAMES.add(names[j]);
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
			page = getCurrentPage(comp);

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
		if ("requestScope".equals(name))
			return REQUEST_SCOPE_PROXY;
		if ("execution".equals(name))
			return EXECUTION_PROXY;
		if ("arg".equals(name)) {
			final Execution exec = Executions.getCurrent(); 
			return exec != null ? exec.getArg() : null;
			//bug 2937096: composer.arg shall be statically wired 
			//arg is a Map prepared by application developer, so can be wired statically 
		}
		if ("param".equals(name)) {
			final Execution exec = Executions.getCurrent(); 
			return exec != null ? exec.getParameterMap() : null;
			//bug 2945974: composer.param shall be statically wired
			//Note that request parameter is prepared by servlet container, you shall not
			//copy the reference to this map; rather, you shall clone the key-value pair one-by-one.
		}
		//20090314, Henri Chen: No way to suppport "event" with an event proxy because org.zkoss.zk.Event is not an interface
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
	/** Returns the page of the give component, or the current page if the
	 * component is null or it doesn't belong to any page.
	 * The current page is retrieved by {@link ExecutionCtrl#getCurrentPage}
	 * or the current execution. This method returns null if no execution
	 * or no current page at all.
	 * @param comp the component to retrieve the page. Ignored if null.
	 * @since 6.0.0
	 */
	public static Page getCurrentPage(Component comp) {
		if (comp != null) {
			Page page = comp.getPage();
			if (page != null) return page;
		}

		final Execution exec = Executions.getCurrent();
		return exec != null ? ((ExecutionCtrl)exec).getCurrentPage(): null;
	}

	/** Execution Proxy */
	public static final Exec EXECUTION_PROXY = new Exec();
	
	/** Request Scope Proxy */
	public static final RequestScope REQUEST_SCOPE_PROXY = new RequestScope();
	
	//Proxy to read current execution
	private static class Exec implements Execution {
		private static final Execution exec() {
			return Executions.getCurrent();
		}
		
		public void addAuResponse(AuResponse response) {
			exec().addAuResponse(response);
		}
		public void addAuResponse(String key, AuResponse response) {
			exec().addAuResponse(key, response);
		}

		public Component createComponents(PageDefinition pagedef,
				Component parent, Map<?, ?> arg) {
			return exec().createComponents(pagedef, parent, arg);
		}
		public Component createComponents(String uri, Component parent, Map<?, ?> arg) {
			return exec().createComponents(uri, parent, arg);
		}
		public Component createComponentsDirectly(String content,
				String extension, Component parent, Map<?, ?> arg) {
			return exec().createComponentsDirectly(content, extension, parent, arg);
		}
		public Component createComponentsDirectly(Document content,
				String extension, Component parent, Map<?, ?> arg) {
			return exec().createComponentsDirectly(content, extension, parent, arg);
		}
		public Component createComponentsDirectly(Reader reader,
				String extension, Component parent, Map<?, ?> arg) throws IOException {
			return exec().createComponentsDirectly(reader, extension, parent, arg);
		}

		public Component createComponents(PageDefinition pagedef,
				Component parent, Component insertBefore, VariableResolver resolver) {
			return exec().createComponents(pagedef, parent, insertBefore, resolver);
		}
		public Component createComponents(String uri, Component parent, Component insertBefore, VariableResolver resolver) {
			return exec().createComponents(uri, parent, insertBefore, resolver);
		}
		public Component createComponentsDirectly(String content,
				String extension, Component parent, Component insertBefore, VariableResolver resolver) {
			return exec().createComponentsDirectly(content, extension, parent, insertBefore, resolver);
		}
		public Component createComponentsDirectly(Document content,
				String extension, Component parent, Component insertBefore, VariableResolver resolver) {
			return exec().createComponentsDirectly(content, extension, parent, insertBefore, resolver);
		}
		public Component createComponentsDirectly(Reader reader,
				String extension, Component parent, Component insertBefore, VariableResolver resolver) throws IOException {
			return exec().createComponentsDirectly(reader, extension, parent, insertBefore, resolver);
		}

		public Component[] createComponents(PageDefinition pagedef, Map<?, ?> arg) {
			return exec().createComponents(pagedef, arg);
		}
		public Component[] createComponents(String uri, Map<?, ?> arg) {
			return exec().createComponents(uri, arg);
		}

		public Component[] createComponentsDirectly(String content,
				String extension, Map<?, ?> arg) {
			return exec().createComponentsDirectly(content, extension, arg);
		}
		public Component[] createComponentsDirectly(Document content,
				String extension, Map<?, ?> arg) {
			return exec().createComponentsDirectly(content, extension, arg);
		}
		public Component[] createComponentsDirectly(Reader reader,
				String extension, Map<?, ?> arg) throws IOException {
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

		public void forward(Writer writer, String page, Map<String, ?> params, int mode)
				throws IOException {
			exec().forward(writer, page, params, mode);
			
		}

		public void forward(String page) throws IOException {
			exec().forward(page);
		}

		public Map<?, ?> getArg() {
			return exec().getArg();
		}

		public Object getAttribute(String name) {
			return exec().getAttribute(name);
		}
		public boolean hasAttribute(String name) {
			return exec().hasAttribute(name);
		}
		public Object getAttribute(String name, boolean recurse) {
			return exec().getAttribute(name, recurse);
		}
		public boolean hasAttribute(String name, boolean recurse) {
			return exec().hasAttribute(name, recurse);
		}
		public Object setAttribute(String name, Object value, boolean recurse) {
			return exec().setAttribute(name, value, recurse);
		}
		public Object removeAttribute(String name, boolean recurse) {
			return exec().removeAttribute(name, recurse);
		}
		public boolean addScopeListener(ScopeListener listener) {
			return exec().addScopeListener(listener);
		}
		public boolean removeScopeListener(ScopeListener listener) {
			return exec().removeScopeListener(listener);
		}

		public Map<String, Object> getAttributes() {
			return exec().getAttributes();
		}

		public String getContextPath() {
			return exec().getContextPath();
		}

		public Desktop getDesktop() {
			return exec().getDesktop();
		}

		public Evaluator getEvaluator(Page page, Class<? extends ExpressionFactory> expfcls) {
			return exec().getEvaluator(page, expfcls);
		}

		public Evaluator getEvaluator(Component comp, Class<? extends ExpressionFactory> expfcls) {
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

		public Map<String, String[]> getParameterMap() {
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

		public void include(Writer writer, String page, Map<String, ?> params, int mode)
				throws IOException {
			exec().include(writer, page, params, mode);
			
		}

		public void include(String page) throws IOException {
			exec().include(page);
		}

		public boolean isAsyncUpdate(Page page) {
			return exec().isAsyncUpdate(page);
		}

		@Override
		public Double getBrowser(String name) {
			return exec().getBrowser(name);
		}
		@Override
		public String getBrowser() {
			return exec().getBrowser();
		}

		/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
		public boolean isBrowser() {
			return exec().isBrowser();
		}
		/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
		public boolean isBrowser(String type) {
			return exec().isBrowser(type);
		}
		/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
		public boolean isExplorer() {
			return exec().isExplorer();
		}
		/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
		public boolean isExplorer7() {
			return exec().isExplorer7();
		}
		/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
		public boolean isOpera() {
			return exec().isOpera();
		}
		/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
		public boolean isGecko() {
			return exec().isGecko();
		}
		/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
		public boolean isGecko3() {
			return exec().isGecko3();
		}
		/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
		public boolean isHilDevice() {
			return exec().isHilDevice();
		}
		/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
		public boolean isSafari() {
			return exec().isSafari();
		}
		/** @deprecated As of release 6.0.0, replaced with {@link #getBrowser(String)}. */
		public boolean isRobot() {
			return exec().isRobot();
		}

		public boolean isForwarded() {
			return exec().isForwarded();
		}
		public boolean isIncluded() {
			return exec().isIncluded();
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

		public void postEvent(int priority, Component realTarget, Event evt) {
			exec().postEvent(priority, realTarget, evt);
		}

		public void pushArg(Map<?, ?> arg) {
			exec().pushArg(arg);
		}

		public Object removeAttribute(String name) {
			return exec().removeAttribute(name);
		}

		public void sendRedirect(String uri) {
			exec().sendRedirect(uri);
		}

		public void sendRedirect(String uri, String target) {
			exec().sendRedirect(uri, target);
		}

		public Object setAttribute(String name, Object value) {
			return exec().setAttribute(name, value);
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

		public void addResponseHeader(String name, Date value) {
			exec().addResponseHeader(name, value);
		}

		public boolean containsResponseHeader(String name) {
			return exec().containsResponseHeader(name);
		}

		public String getHeader(String name) {
			return exec().getHeader(name);
		}

		public Iterable<String> getHeaderNames() {
			return exec().getHeaderNames();
		}

		public Iterable<String> getHeaders(String name) {
			return exec().getHeaders(name);
		}

		public void setResponseHeader(String name, String value) {
			exec().setResponseHeader(name, value);
		}
		public void setResponseHeader(String name, Date value) {
			exec().setResponseHeader(name, value);
		}

		public Session getSession() {
			return exec().getSession();
		}

		public String locate(String path) {
			return exec().locate(path);
		}

		public boolean addVariableResolver(VariableResolver resolver) {
			return exec().addVariableResolver(resolver);
		}
		public boolean removeVariableResolver(VariableResolver resolver) {
			return exec().removeVariableResolver(resolver);
		}
		public boolean hasVariableResolver(VariableResolver resolver) {
			return exec().hasVariableResolver(resolver);
		}

		public String toString() {
			return Objects.toString(exec());
		}
		public int hashCode() {
			return Objects.hashCode(exec());
		}
		public boolean equals(Object o) {
			if (o instanceof Exec)
				return Objects.equals(exec(), ((Exec)o).exec());
			return Objects.equals(exec(), o);
		}
		@Override
		public void log(String msg) {
			exec().log(msg);
		}
		@Override
		public void log(String msg, Throwable ex) {
			exec().log(msg, ex);
		}
	}
	
	//Proxy to read current requestScope
	private static class RequestScope implements Map<String, Object> {
		protected Map<String, Object> req() {
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
		public Set<Map.Entry<String, Object>> entrySet() {
			return req().entrySet();
		}
		public Object get(Object key) {
			return req().get(key);
		}
		public boolean isEmpty() {
			return req().isEmpty();
		}
		public Set<String> keySet() {
			return req().keySet();
		}
		public Object put(String key, Object value) {
			return req().put(key, value);
		}
		public void putAll(Map<? extends String, ? extends Object> arg0) {
			req().putAll(arg0);
		}
		public Object remove(Object key) {
			return req().remove(key);
		}
		public int size() {
			return req().size();
		}
		public Collection<Object> values() {
			return req().values();
		}

		public String toString() {
			return Objects.toString(req());
		}
		public int hashCode() {
			return Objects.hashCode(req());
		}
		public boolean equals(Object o) {
			if (o instanceof RequestScope)
				return Objects.equals(req(), ((RequestScope)o).req());
			return Objects.equals(req(), o);
		}
	}
}
