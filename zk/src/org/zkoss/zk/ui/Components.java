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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Collection;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.zkoss.lang.Classes;
import org.zkoss.util.CollectionsX;
import org.zkoss.zk.ui.event.EventListener;

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
	 * @see #sort(List, int, int, Comparator)
	 */
	public static void sort(List list, Comparator cpr) {
		sort(list, 0, list.size(), cpr);
	}
	
	/**
	 * Sorts the components in the list.
	 * @param list the list to be sorted
	 * @param from the index of the first element (inclusive) to be sorted
	 * @param to the index of the last element (exclusive) to be sorted
	 * @param cpr the comparator to determine the order of the list.
	 * @since 3.1.0
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
	/** <p>Wire fellow components and space owner ancestors of the specified 
	 * Id space into a controller Java object. This implementation checks the 
	 * setXxx() method names first then the
	 * field names. If a setXxx() method name matches the id of the fellow or
	 * space owner ancestors and with correct 
	 * argument type, the method is called with the fellow component as the 
	 * argument. If no proper setXxx() method then search the field of the 
	 * controller object for a matched field with name equals to the fellow 
	 * component's id and proper type. Then the fellow component 
	 * is assigned as the value of the field.</p> 
	 * 
	 * <p>This is useful in writing controller code in MVC design practice. You
	 * can wire the components into the controller object per the
	 * component's id and do whatever you like.</p>
	 * 
	 * @param idspace the id space to be bound
	 * @param controller the controller Java object to be injected the fellow components.
	 * @since 3.0.6
	 */
	public static final void wireFellows(IdSpace idspace, Object controller) {
		//inject space owner ancestors
		IdSpace xidspace = idspace;
		if (xidspace instanceof Component) {
			while (true) {
				final Component parent = ((Component)xidspace).getParent();
				if (parent == null) {//hit page
					inject(((Component)xidspace).getPage(), controller);
					break;
				}
				xidspace = parent.getSpaceOwner();
				inject(xidspace, controller);
			}
		} else {
			inject((Page) idspace, controller);
		}

		//inject fellows
		final Collection fellows = idspace.getFellows();
		for(final Iterator it = fellows.iterator(); it.hasNext();) {
			final Component xcomp = (Component) it.next();
			inject(xcomp, controller);
		}
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
	 * <p>This is useful in writing controller code in MVC design practice. You
	 * can wire the embedded objects, components, and accessible variables into 
	 * the controller object per the components' id and variables' name and do 
	 * whatever you like.
	 * </p>
	 * 
	 * @param comp the reference component to wire variables
	 * @param controller the controller Java object to be injected the 
	 * accessible variable objects.
	 * @see org.zkoss.zk.ui.util.GenericAutowireComposer
	 * @since 3.0.6
	 */
	public static final void wireVariables(Component comp, Object controller) {
		Class cls = controller.getClass();
		Class xcls = cls;
		
		final Map fldMaps = new LinkedHashMap(64);
		do {
			Field[] flds = xcls.getDeclaredFields();
			for (int j = 0; j < flds.length; ++j) {
				final Field fd = flds[j];
				final String fdname = fd.getName();
				if (!fldMaps.containsKey(fdname))
					fldMaps.put(fdname, fd);
			}
			xcls = xcls.getSuperclass();
		} while (xcls != null && !Object.class.equals(xcls));

		//check methods
		final Set injected = new HashSet();
		Method[] mtds = cls.getMethods();
		for (int j = 0; j < mtds.length; ++j) {
			final Method md = mtds[j];
			final String mdname = md.getName();
			if (mdname.length() > 3 && mdname.startsWith("set") 
			&& Character.isUpperCase(mdname.charAt(3))) {
				final String fdname = Classes.toAttributeName(mdname);
				final Class[] parmcls = md.getParameterTypes();
				if (parmcls.length == 1) {
					final boolean isself = "self".equals(fdname);
					if (comp.containsVariable(fdname, false) || isself) {
						final Object arg = isself ? comp : comp.getVariable(fdname, false);
						final Class argcls = arg == null ? null : arg.getClass();
						final Class fdcls = parmcls[0]; 
						if (argcls == null || fdcls.isAssignableFrom(argcls)) {
							final Field fd = (Field) fldMaps.get(fdname);
							if (fd != null) {
								final boolean old = fd.isAccessible();
								try {
									//check field value
									fd.setAccessible(true);
									final Object value = fd.get(controller);
									if (value == null) {
										md.invoke(controller, new Object[] {arg});
										injected.add(fdname); //mark as injected
									}
								} catch (Exception ex) {
									throw UiException.Aide.wrap(ex);
								} finally {
									fd.setAccessible(old);
								}
							} else {
								try {
									md.invoke(controller, new Object[] {arg});
									injected.add(fdname); //mark as injected
								} catch (Exception ex) {
									throw UiException.Aide.wrap(ex);
								}
							}
						}
					}
				}
			}
		}

		//check fields
		for (final Iterator it=fldMaps.entrySet().iterator();it.hasNext();) {
			final Entry entry = (Entry) it.next();
			final String fdname = (String) entry.getKey();
			if (!injected.contains(fdname)) { //if not injected by setXxx
				final boolean isself = "self".equals(fdname);
				if (comp.containsVariable(fdname, false) || isself) {
					final Field fd = (Field) entry.getValue();
					final Object arg = isself ? comp : comp.getVariable(fdname, false);
					final Class argcls = arg == null ? null : arg.getClass();
					injectField(arg, argcls, controller, fd);
				}
			}
		}
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
	 * <p>This is useful in writing controller code in MVC design practice. You
	 * can wire the embedded objects, components, and accessible variables into 
	 * the controller object per the component's id and variable name and do 
	 * whatever you like.
	 * </p>
	 * 
	 * @param page the reference page to wire variables
	 * @param controller the controller Java object to be injected the fellow components.
	 * @since 3.0.6
	 */
	public static final void wireVariables(Page page, Object controller) {
		Class cls = controller.getClass();
		Class xcls = cls;
		
		final Map fldMaps = new LinkedHashMap(64);
		do {
			Field[] flds = xcls.getDeclaredFields();
			for (int j = 0; j < flds.length; ++j) {
				final Field fd = flds[j];
				final String fdname = fd.getName();
				if (!fldMaps.containsKey(fdname))
					fldMaps.put(fdname, fd);
			}
			xcls = xcls.getSuperclass();
		} while (xcls != null && !Object.class.equals(xcls));

		//check methods
		final Set injected = new HashSet();
		Method[] mtds = cls.getMethods();
		for (int j = 0; j < mtds.length; ++j) {
			final Method md = mtds[j];
			final String mdname = md.getName();
			if (mdname.length() > 3 && mdname.startsWith("set") 
			&& Character.isUpperCase(mdname.charAt(3))) {
				final String fdname = Classes.toAttributeName(mdname);
				final Class[] parmcls = md.getParameterTypes();
				if (parmcls.length == 1) {
					final boolean isself = "self".equals(fdname);
					if (page.containsVariable(fdname) || isself) {
						final Object arg = isself ? page : page.getVariable(fdname);
						final Class argcls = arg == null ? null : arg.getClass();
						final Class fdcls = parmcls[0]; 
						if (argcls == null || fdcls.isAssignableFrom(argcls)) {
							final Field fd = (Field) fldMaps.get(fdname);
							if (fd != null) {
								final boolean old = fd.isAccessible();
								try {
									//check field value
									fd.setAccessible(true);
									final Object value = fd.get(controller);
									if (value == null) {
										md.invoke(controller, new Object[] {arg});
										injected.add(fdname); //mark as injected
									}
								} catch (Exception ex) {
									throw UiException.Aide.wrap(ex);
								} finally {
									fd.setAccessible(old);
								}
							} else {
								try {
									md.invoke(controller, new Object[] {arg});
									injected.add(fdname); //mark as injected
								} catch (Exception ex) {
									throw UiException.Aide.wrap(ex);
								}
							}
						}
					}
				}
			}
		}

		//check fields
		for (final Iterator it=fldMaps.entrySet().iterator();it.hasNext();) {
			final Entry entry = (Entry) it.next();
			final String fdname = (String) entry.getKey();
			if (!injected.contains(fdname)) { //if not injected by setXxx
				final boolean isself = "self".equals(fdname);
				if (page.containsVariable(fdname) || isself) {
					final Field fd = (Field) entry.getValue();
					final Object arg = isself ? page : page.getVariable(fdname);
					final Class argcls = arg == null ? null : arg.getClass();
					injectField(arg, argcls, controller, fd);
				}
			}
		}
	}
	
	private static void inject(Object comp, Object injectee) {
		//try setXxx
		final String fdname = (comp instanceof Page) ? 
				((Page)comp).getId() : ((Component)comp).getId();
		if (!isAutoId(fdname)) {
			final String mdname = Classes.toMethodName(fdname, "set");
			final Class compcls = comp.getClass();
			final Class tgtcls = injectee.getClass();
			try {
				final Method md = 
					Classes.getCloseMethod(tgtcls, mdname, new Class[] {compcls});
				md.invoke(injectee, new Object[] {comp});
			} catch (NoSuchMethodException ex) {
				//no setXxx() method, try inject into Field
				try {
					final Field fd = Classes.getAnyField(tgtcls, fdname);
					injectField(comp, compcls, injectee, fd);
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
	
	private static void injectField(Object comp, Class compcls, Object injectee, Field fd) {
		final boolean old = fd.isAccessible();
		try {
			fd.setAccessible(true);
			final Class fdcls = fd.getType();
			if (compcls != null && fdcls.isAssignableFrom(compcls)) { //correct type 
				final Object value = fd.get(injectee);
				if (value == null) 
					fd.set(injectee, comp);
			}
		} catch (Exception e) {
			throw UiException.Aide.wrap(e);
		} finally {
			fd.setAccessible(old);
		}
	}
	
	/** <p>Adds forward conditions to myid source component so onXxx source 
	 * event received by 
	 * myid component can be forwarded to the specified target 
	 * component with the target event name onXxx$myid.</p> 
	 * <p>The controller is a POJO file with onXxx$myid methods (the event handler 
	 * codes). This utility method search such onXxx$myid methods and adds 
	 * forward condition to the source myid component looked up by  
	 * {@link org.zkoss.zk.ui.Component#getVariable} of the specified component, 
	 * so you don't have to specify in zul file the "forward" attribute one by one. 
	 * If the source component cannot be looked up or the object looked up is 
	 * not a component, we will throw an UiException.
	 * </p>
	 * 
	 * @param comp the targetComponent
	 * @param controller the controller code with onXxx$myid event handler methods
	 * @since 3.0.7
	 */
	public static void addForwards(Component comp, Object controller) {
		final Class cls = controller.getClass();
		final Method[] mtds = cls.getMethods();
		for (int j = 0; j < mtds.length; ++j) {
			final Method md = mtds[j];
			final String mdname = md.getName();
			if (mdname.length() > 5 && mdname.startsWith("on") 
			&& Character.isUpperCase(mdname.charAt(2))) {
				final int k = mdname.indexOf('$', 3);
				if (k >= 0) { //found '$'
					final String srcevt = mdname.substring(0, k);
					if ((k+1) < mdname.length()) {
						final String srccompid = mdname.substring(k+1);
						final Object srccomp = comp.getVariable(srccompid, false);
						if (srccomp == null || !(srccomp instanceof Component)) {
							throw new UiException("Cannot find the associated component: "+mdname);
						}
						((Component)srccomp).addForward(srcevt, comp, mdname);
					}
				}
			}
		}
	}
}
