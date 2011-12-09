/* ConventionWire.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec  8 12:21:34 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.UiException;

/**
 * The utility to wire by name convention.
 * You rarely need to access this class directly.
 * Rather, use {@link ConventionWires} instead.
 * @author tomyeh
 * @since 6.0.0
 */
/*package*/ class ConventionWire {
	private final Object _controller;
	private final Set<String> _injected;
	private final Map<String, Field> _fldMaps;
	private final char _separator;
	private final boolean _ignoreZScript;
	private final boolean _ignoreXel;

	public ConventionWire(Object controller) {
		this(controller, '$', false, false);
	}
	public ConventionWire(Object controller, char separator) {
		this(controller, separator, false, false);
	}
	public ConventionWire(Object controller, char separator,
	boolean ignoreZScript, boolean ignoreXel) {
		_controller = controller;
		_separator = separator;
		_ignoreZScript = ignoreZScript;
		_ignoreXel = ignoreXel;
		_injected = new HashSet<String>();
		_fldMaps = new LinkedHashMap<String, Field>(64);
		
		Class cls = _controller.getClass();
		while (cls != null && !ignoreFromWire(cls)) {
			Field[] flds = cls.getDeclaredFields();
			for (int j = 0; j < flds.length; ++j) {
				final Field fd = flds[j];
				final String fdname = fd.getName();
				if (!_fldMaps.containsKey(fdname))
					_fldMaps.put(fdname, fd);
			}
			cls = cls.getSuperclass();
		}
	}

	/** Injects the fellows in the context of the given IdSpace.
	 */
	public void wireFellows(final IdSpace idspace) {
		//inject fellows
		final Collection<Component> fellows = idspace.getFellows();
		for(Component xcomp: fellows)
			injectFellow(xcomp);

		//inject space owner ancestors
		if (idspace instanceof Component) {
			for (IdSpace xidspace = idspace;;) {
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
			injectFellow((Page) idspace);
		}
	}
	/** Injects the variables in the context of the given page.
	 */
	public void wireVariables(Page page) {
		myWireVariables(page);
	}
	/** Injects the variables in the context of the given component.
	 */
	public void wireVariables(Component comp) {
		myWireVariables(comp);
	}
	private void myWireVariables(Object x) {
		wireImplicit(x);
		wireOthers(x);
	}
	/** Injects the implicit objects in the context of the given object.
	 */
	@SuppressWarnings("unchecked")
	public void wireImplicit(Object x) {
		//Feature #3315689 
		if(ignoreFromWire(_controller.getClass()))
			return;
		
		for (String fdname: Components.getImplicitNames()) {
			//we cannot inject event proxy because it is not an Interface
			if ("event".equals(fdname)) { 
				continue;
			}
			Object arg = myGetImplicit(x, fdname);
			//bug #2945974
			//dirty patch
			if ("param".equals(fdname) && arg != null) {
				arg = new HashMap((Map) arg); 
			}
			injectByName(arg, fdname,
				x instanceof Component && "page".equals(fdname));
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
			&& Character.isUpperCase(mdname.charAt(3))
			&& !ignoreFromWire(md.getDeclaringClass())) {
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
		for (Entry<String, Field> entry: _fldMaps.entrySet()) {
			final String fdname = entry.getKey();
			final Field fd = entry.getValue();
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
			final Page page = (Page) x;
			return (!_ignoreZScript && page.getZScriptVariable(fdname) != null)
				|| page.hasAttributeOrFellow(fdname, true)
				|| (!_ignoreXel && page.getXelVariable(null, null, fdname, true) != null);
		} else {
			final Component cmp = (Component) x;
			final Page page = Components.getCurrentPage(cmp);
			return (!_ignoreZScript && page != null && page.getZScriptVariable(cmp, fdname) != null)
				|| cmp.hasAttributeOrFellow(fdname, true)
				|| (!_ignoreXel && page != null && page.getXelVariable(null, null, fdname, true) != null);
		}
	}
	
	private Object getVariable(Object x, String fdname) {
		//#feature 2770471 GenericAutowireComposer shall support wiring ZScript varible
		if (x instanceof Page) {
			final Page page = (Page) x;
			Object arg = _ignoreZScript ? null: page.getZScriptVariable(fdname);
			if (arg == null) {
				arg = page.getAttributeOrFellow(fdname, true);
				if (!_ignoreXel && arg == null)
					arg = page.getXelVariable(null, null, fdname, true);
			}
			return arg;
		} else {
			final Component cmp = (Component) x;
			final Page page = Components.getCurrentPage(cmp);
			Object arg = !_ignoreZScript && page != null ? page.getZScriptVariable(cmp, fdname): null;
			if (arg == null) {
				arg = cmp.getAttributeOrFellow(fdname, true);
				if (!_ignoreXel && arg == null && page != null)
					arg = page.getXelVariable(null, null, fdname, true);
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
		if (fdname.length() > 0) {
			injectByName(arg, fdname, false);
		}
	}
	
	private void injectByName(Object arg, String fdname, boolean fieldOnly) {
		//argument to be injected is null; then no need to inject
		if (arg != null) {
			final String mdname = Classes.toMethodName(fdname, "set");
			final Class parmcls = arg.getClass();
			final Class tgtcls = _controller.getClass();
			try {
				final Method md = fieldOnly ? null:
					Classes.getCloseMethod(tgtcls, mdname, new Class[] {parmcls});
				if (fieldOnly
				|| !injectByMethod(md, parmcls, parmcls, arg, fdname)) {
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
	private boolean injectByMethod(Method md, Class<?> parmcls, Class<?> argcls, Object arg, String fdname) {
		if (argcls == null || parmcls.isAssignableFrom(argcls)) {
			final Field fd = _fldMaps.get(fdname);
			if (fd != null && fd.getType().equals(parmcls)) {
				final boolean old = fd.isAccessible();
				try {
					//check field value
					fd.setAccessible(true);
					final Object value = fd.get(_controller);
					if (value == null) {
						md.invoke(_controller, arg);
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
					md.invoke(_controller, arg);
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
	private boolean injectField(Object arg, Class<?> argcls, Field fd) {
		final boolean old = fd.isAccessible();
		try {
			fd.setAccessible(true);
			final Class<?> fdcls = fd.getType();
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
				Components.getImplicit((Page)x, fdname) :
				Components.getImplicit((Component)x, fdname);
	}

	private static boolean ignoreFromWire(Class<?> cls) {
		Package pkg;
		return cls != null && (_ignoreWires.contains(cls.getName())
		|| ((pkg = cls.getPackage()) != null && _ignoreWires.contains(pkg.getName())));
	}
	private static final Set<String> _ignoreWires = new HashSet<String>(16);
	static {
		final Class[] clses = new Class[] {
			HtmlBasedComponent.class,
			HtmlMacroComponent.class,
			HtmlNativeComponent.class,
			AbstractComponent.class,
			org.zkoss.zk.ui.util.GenericAutowireComposer.class,
			Object.class
		};
		for (int j = 0; j < clses.length; ++j)
			_ignoreWires.add(clses[j].getName());

		//5.0.5: ignore zul by default (but able to enable for backward compatible)
		if (!"true".equals(Library.getProperty("org.zkoss.zk.ui.wire.zul.enabled"))) {
			//a dirty solution but no better way until we use annotation instead
			_ignoreWires.add("org.zkoss.zul");
			_ignoreWires.add("org.zkoss.zkex.zul");
			_ignoreWires.add("org.zkoss.zkmax.zul");
			_ignoreWires.add("org.zkoss.zhtml");
		}
	}
}
