/* Components.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 13 20:55:18     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collection;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;

import org.zkoss.lang.Classes;

/**
 * Utilities to access {@link Component}.
 *
 * @author tomyeh
 */
public class Components {
	protected Components() {}

	/** Sorts the components in the list.
	 *
	 * <p>Note: you cannot use Collections.sort to sort
	 * {@link Component#getChildren} because Collections.sort might cause
	 * some replicated item in the list.
	 */
	public static void sort(List list, Comparator cpr) {
		final Object ary[] = list.toArray();
		Arrays.sort(ary, cpr);

		ListIterator it = list.listIterator();
		int j = 0;
		for (; it.hasNext(); ++j) {
			if (it.next() != ary[j]) {
				it.remove();

				if (it.hasNext()) {
					if (it.next() == ary[j]) continue;
					it.previous();
				}
				break;
			}
		}
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
		for (; j < ary.length; ++j)
			list.add(ary[j]);
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

	/** <p>Wire fellow components of the specified component into a POJO Java
	 * object. This implementation checks the setXxx() methods first then the
	 * field name. If a setXxx() method matches the fellow's id and with correct 
	 * argument type, the method is called with the fellow component as the 
	 * argument. If no proper setXxx() method then search the field of the 
	 * POJO object for a matched field with name equals to the fellow 
	 * component's id and proper type. Then the fellow component 
	 * is assigned as the value of the field if the field value is null 
	 * originally.</p> 
	 * 
	 * <p>This is useful in writing controller code in MVC design practice. You
	 * can wire the page components into controller(the POJO object) per the
	 * component's id and do whatever you like.</p>
	 * 
	 * @param comp the component
	 * @param pojo the POJO Java object to be injected the fellow components.
	 * @see org.zkoss.zk.ui.util.GenericAutowireComposer
	 * @since 3.0.6
	 */
	public static final void wireFellows(Component comp, Object pojo) {
		final Collection fellows = comp.getFellows();
		for(final Iterator it = fellows.iterator(); it.hasNext();) {
			final Component xcomp = (Component) it.next();
			inject(xcomp, pojo);
		}
	}
		
	private static void inject(Component comp, Object pojo) {
		//try setXxx
		final String fdname = comp.getId();
		final String mdname = Classes.toMethodName(fdname, "set");
		final Class compcls = comp.getClass();
		final Class tgtcls = pojo.getClass();
		try {
			final Method md = 
				Classes.getCloseMethod(tgtcls, mdname, new Class[] {compcls});
			md.invoke(pojo, new Object[] {comp});
		} catch (NoSuchMethodException ex) {
			//no setXxx() method, try inject into Field
			try {
				final Field fd = Classes.getAnyField(tgtcls, fdname);
				final boolean old = fd.isAccessible();
				try {
					fd.setAccessible(true);
					final Class fdcls = fd.getType();
					if (fdcls.isAssignableFrom(compcls) 
					&& fd.get(pojo) == null) { //correct type && null
						fd.set(pojo, comp);
					}
				} finally {
					fd.setAccessible(old);
				}
			} catch (NoSuchFieldException e) {
				//ignore
			} catch (Exception ex2) {
				throw UiException.Aide.wrap(ex2);
			}
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
}
