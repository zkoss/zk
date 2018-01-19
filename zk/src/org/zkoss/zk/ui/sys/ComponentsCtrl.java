/* ComponentsCtrl.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug  9 19:41:22     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zkoss.json.JavaScriptValue;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.util.Cache;
import org.zkoss.util.FastReadCache;
import org.zkoss.util.Maps;
import org.zkoss.util.Pair;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.AnnotationMap;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.ShadowInfo;
import org.zkoss.zk.xel.ExValue;

/**
 * Utilities for implementing components.
 *
 * @author tomyeh
 */
public class ComponentsCtrl {
	private static final ThreadLocal<Object> _compdef = new ThreadLocal<Object>();
	private static final ThreadLocal<Component> _rootParent = new ThreadLocal<Component>();

	/** Returns the automatically generate component's UUID/ID.
	 */
	public static final String toAutoId(String prefix, int val) {
		return encodeId(new StringBuffer(16).append(prefix), val);
	}

	/** Returns an ID representing the specified number
	 * The ID consists of 0-9, a-z and _.
	 * @since 5.0.5
	 */
	public static final String encodeId(StringBuffer sb, int val) {
		//Thus, the number will 0, 1... max, 0, 1..., max, 0, 1 (less conflict)
		if (val < 0 && (val += Integer.MIN_VALUE) < 0)
			val = -val; //impossible but just in case

		do {
			//IE6/7's ID case insensitive (safer, though jQuery fixes it)
			int v = val % 37;
			val /= 37;
			if (v-- == 0) {
				sb.append('_');
			} else if (v < 10) {
				sb.append((char) ('0' + v));
				//			} else if (v < 36) {
			} else {
				sb.append((char) (v + ('a' - 10)));
				//			} else {
				//				sb.append((char)(v + ((int)'A' - 36)));
			}
		} while (val != 0);
		return sb.toString();
	}

	/** Returns whether an ID is generated automatically.
	 * Note: true is returned if id is null.
	 * Also notice that this method doesn't check if a custom ID generator
	 * ({@link org.zkoss.zk.ui.sys.IdGenerator}) is assigned.
	 * @since 5.0.3
	 */
	public static final boolean isAutoUuid(String id) {
		if (id == null)
			return true;

		//0: lower, 1: digit or upper, 2: letter or digit, 3: upper
		//See also DesktopImpl.updateUuidPrefix
		if (id.length() < 5)
			return false;
		char cc;
		return isLower(id.charAt(0)) && (isUpper(cc = id.charAt(1)) || isDigit(cc))
				&& (isUpper(cc = id.charAt(2)) || isLower(cc) || isDigit(cc)) && isUpper(id.charAt(3));

	}

	private static boolean isUpper(char cc) {
		return cc >= 'A' && cc <= 'Z';
	}

	private static boolean isLower(char cc) {
		return cc >= 'a' && cc <= 'z';
	}

	private static boolean isDigit(char cc) {
		return cc >= '0' && cc <= '9';
	}

	/** Checks if the given UUID is valid.
	 * UUID cannot be empty and can only have alphanumeric characters or underscore.
	 * @exception UiException if uuid is not valid.
	 */
	public static void checkUuid(String uuid) {
		int j;
		if (uuid == null || (j = uuid.length()) == 0)
			throw new UiException("uuid cannot be null or empty");

		while (--j >= 0) {
			final char cc = uuid.charAt(j);
			if ((cc < 'a' || cc > 'z') && (cc < 'A' || cc > 'Z') && (cc < '0' || cc > '9') && cc != '_')
				throw new UiException("Illegal character, " + cc + ", not allowed in uuid, " + uuid);
		}
	}

	/** Returns if the attribute name is reserved.
	 * If name is null, false is returned.
	 * @since 3.0.0
	 */
	public static final boolean isReservedAttribute(String name) {
		return name != null && !"use".equals(name) && !"if".equals(name) && !"unless".equals(name)
				&& !"apply".equals(name) && !"forEach".equals(name);
	}

	/** Returns the root parent when being called in
	 * {@link org.zkoss.zk.ui.sys.ComponentCtrl#onPageAttached}
	 * and {@link org.zkoss.zk.ui.sys.ComponentCtrl#onPageDetached}
	 *
	 * @since 8.5.1
	 */
	public static final Component getRootParent() {
		return _rootParent.get();
	}

	/** Sets root parent
	 * <p>Used only internally.
	 *
	 * @since 8.5.1
	 */
	public static final void setRootParent(Component comp) {
		_rootParent.set(comp);
	}


	/** Returns the current component info {@link ComponentInfo},
	 * definition ({@link ComponentDefinition} or null, which is used only by
	 * {@link org.zkoss.zk.ui.sys.UiEngine} to communicate with
	 * {@link org.zkoss.zk.ui.AbstractComponent}.
	 * @since 3.0.0
	 */
	public static final Object getCurrentInfo() {
		return _compdef.get();
	}

	/** Sets the current component definition, which is used only by
	 * {@link org.zkoss.zk.ui.sys.UiEngine} to communicate with
	 * {@link org.zkoss.zk.ui.AbstractComponent}.
	 * <p>Used only internally.
	 * @since 3.0.0
	 */
	public static final void setCurrentInfo(ComponentDefinition compdef) {
		_compdef.set(compdef);
	}

	/** Sets the current component definition, which is used only by
	 * {@link org.zkoss.zk.ui.sys.UiEngine} to communicate with
	 * {@link org.zkoss.zk.ui.AbstractComponent}.
	 * <p>Used only internally.
	 * @since 3.0.0
	 */
	public static void setCurrentInfo(ComponentInfo compInfo) {
		_compdef.set(compInfo);
	}

	/** Sets the current shadow definition, which is used only by
	 * {@link org.zkoss.zk.ui.sys.UiEngine} to communicate with
	 * {@link org.zkoss.zk.ui.AbstractComponent}.
	 * <p>Used only internally.
	 * @since 8.0.0
	 */
	public static void setCurrentInfo(ShadowInfo compInfo) {
		_compdef.set(compInfo);
	}

	/** Pares the event expression.
	 *
	 * <p>There are several formats for the event expression:
	 * <ul>
	 * <li>onClick</li>
	 * <li>self.onClick</li>
	 * <li>id.onClick</li>
	 * <li>../id1/id2.onClick</li>
	 * <li>${elexpr}.onClick</li>
	 * </ul>
	 *
	 * @param comp the component that the event expression is referenced to
	 * @param evtexpr the event expression.
	 * @param defaultComp the default component which is used when
	 * evtexpr doesn't specify the component.
	 * @param deferred whether to defer the conversion of the path
	 * to a component. If true and EL not specified or evaluated to a string,
	 * it returns the path directly rather than converting it to a component.
	 * @return a two element array. The first element is the component
	 * if deferred is false or EL is evaluated to a component,
	 * or a path, otherwise.
	 * The second component is the event name.
	 * @since 3.0.0
	 */
	public static Object[] parseEventExpression(Component comp, String evtexpr, Component defaultComp, boolean deferred)
			throws ComponentNotFoundException {
		final int j = evtexpr.lastIndexOf('.');
		final String evtnm;
		Object target;
		if (j >= 0) {
			evtnm = evtexpr.substring(j + 1).trim();
			String path = evtexpr.substring(0, j);
			if (path.length() > 0) {
				target = null;

				// ZK-2598: try to evaluate the path even if its not enclosed by ${}
				String el_path = path.indexOf("${") >= 0 ? path : "${" + path + "}";

				Object v = null;
				try {
					v = Executions.evaluate(comp, el_path, Object.class);
				} catch (Exception e) {
					//eat, path may be . or ..
				}
				if (v instanceof Component) {
					target = v;
				} else if (v != null) {
					path = Objects.toString(v);
				}

				if (target == null) {
					path = path.trim();
					if ("self".equals(path))
						path = ".";

					target = deferred ? path : ".".equals(path) ? comp : Path.getComponent(comp.getSpaceOwner(), path);
					if (target == null && comp instanceof IdSpace && comp.getParent() != null) {
						target = Path.getComponent(comp.getParent().getSpaceOwner(), path);
					}
					//if target is still null
					if (target == null) {
						throw new ComponentNotFoundException("EL evaluated to null: " + path);
					}
				}
			} else {
				target = defaultComp;
			}
		} else {
			evtnm = evtexpr.trim();
			target = defaultComp;
		}

		if (!Events.isValid(evtnm))
			throw new UiException("Not an event name: " + evtnm);
		return new Object[] { target, evtnm };
	}

	/**
	 * Applies the forward condition to the specified component.
	 *
	 * <p>The basic format:<br/>
	 * <code>onEvent1=id1/id2.onEvent2,onEvent3=id3.onEvent4</code>
	 *
	 * <p>See {@link org.zkoss.zk.ui.metainfo.ComponentInfo#setForward}
	 * for more information.
	 *
	 * @since 3.0.0
	 */
	public static final void applyForward(Component comp, String forward) {
		if (forward == null)
			return;

		final Map<String, Collection<String>> fwds = new LinkedHashMap<String, Collection<String>>(); //remain the order
		Maps.parseMultiple(fwds, forward, ',', '\'', true, true);
		for (Map.Entry<String, Collection<String>> me : fwds.entrySet()) {
			final String orgEvent = me.getKey();
			if (orgEvent != null && !Events.isValid(orgEvent))
				throw new UiException("Not an event name: " + orgEvent);

			final Collection<String> conds = me.getValue();
			for (String cond : conds)
				applyForward0(comp, orgEvent, cond);
		}
	}

	private static final void applyForward0(Component comp, String orgEvent, String cond) {
		int len;
		if (cond == null || (len = cond.length()) == 0)
			len = (cond = orgEvent).length();
		//if condition not specified, assume same as orgEvent (to space owenr)

		Object data = null;
		for (int j = 0; j < len; ++j) {
			final char cc = cond.charAt(j);
			if (cc == '\\')
				++j; //skip next
			else if (cc == '{') {
				for (int k = j + 1, depth = 0;; ++k) {
					if (k >= len) {
						j = k;
						break;
					}

					final char c2 = cond.charAt(k);
					if (c2 == '{')
						++depth;
					else if (c2 == '}' && --depth < 0) { //found
						j = k;
						break;
					}
				}
			} else if (cc == '(') { //found
				final int k = cond.lastIndexOf(')');
				if (k > j) {
					data = Executions.evaluate(comp, cond.substring(j + 1, k), Object.class);
					cond = cond.substring(0, j);
					break;
				}
			}
		}

		final Object[] result = parseEventExpression(comp, cond, null, true);

		final Object target = result[0];
		if (target instanceof String)
			comp.addForward(orgEvent, (String) target, (String) result[1], data);
		else
			comp.addForward(orgEvent, (Component) target, (String) result[1], data);
	}

	/** Returns the method for handling the specified event, or null
	 * if not available.
	 */
	public static final Method getEventMethod(Class<?> cls, String evtnm) {
		final Pair<Class<?>, String> key = new Pair<Class<?>, String>(cls, evtnm);
		final Object o = _evtmtds.get(key);
		if (o != null)
			return o == Objects.UNKNOWN ? null : (Method) o;

		Method mtd = null;
		try {
			mtd = Classes.getCloseMethodBySubclass(cls, evtnm, new Class[] { Event.class }); //with event arg
		} catch (NoSuchMethodException ex) {
			try {
				mtd = cls.getMethod(evtnm); //no argument case
			} catch (NoSuchMethodException e2) {
			}
		}
		_evtmtds.put(key, mtd != null ? mtd : Objects.UNKNOWN);
		return mtd;
	}

	/** Sets the cache that stores the information about event handler methods.
	 *
	 * <p>Default: {@link FastReadCache}. In additions, the number of caches is default
	 * to 97 and can be changed by use of the org.zkoss.zk.ui.eventMethods.cache.number
	 * property. The maximal allowed size of each cache, if GC, is default to 30
	 * and can be changed by use of the org.zkoss.zk.ui.eventMethods.cache.maxSize
	 * property.
	 *
	 * @param cache the cache. It cannot be null. It must be thread safe.
	 * Once assigned, the caller shall not access it again.
	 * @since 3.0.0
	 */
	@SuppressWarnings("unchecked")
	public static final void setEventMethodCache(Cache cache) {
		if (cache == null)
			throw new IllegalArgumentException();
		_evtmtds = cache;
	}

	/** A map of (Pair(Class,String evtnm), Method). */
	private static Cache<Pair<Class<?>, String>, Object> _evtmtds = new FastReadCache<Pair<Class<?>, String>, Object>(
			Library.getIntProperty("org.zkoss.zk.ui.event.methods.cache.maxSize", 600), 4 * 60 * 60 * 1000);

	/** An utilities to create an array of JavaScript objects
	 * ({@link JavaScriptValue}) that can be used
	 * to mount the specified widget at the clients.
	 *
	 * @since 5.0.0
	 */
	public static final Collection<JavaScriptValue> redraw(Collection<? extends Component> comps) {
		try {
			final StringWriter out = new StringWriter(1024 * 8);
			final List<JavaScriptValue> js = new LinkedList<JavaScriptValue>();
			for (Component comp : comps) {
				((ComponentCtrl) comp).redraw(out);
				final StringBuffer sb = out.getBuffer();
				js.add(new JavaScriptValue(sb.toString()));
				sb.setLength(0);
			}
			return js;
		} catch (java.io.IOException ex) {
			throw new InternalError();
		}
	}

	/** Represents a dummy definition. */
	public static final ComponentDefinition DUMMY = new ComponentDefinition() {

		public LanguageDefinition getLanguageDefinition() {
			return null;
		}

		public String getName() {
			return "[anonymous]";
		}

		public String getTextAs() {
			return null;
		}

		public boolean isChildAllowedInTextAs() {
			return false;
		}

		public boolean isMacro() {
			return false;
		}

		public String getMacroURI() {
			return null;
		}

		public boolean isInlineMacro() {
			return false;
		}

		public boolean isNative() {
			return false;
		}

		public boolean isBlankPreserved() {
			return false;
		}

		public Object getImplementationClass() {
			return Component.class;
		}

		public void setImplementationClass(Class cls) {
			throw new UnsupportedOperationException();
		}

		public void setImplementationClass(String clsnm) {
			throw new UnsupportedOperationException();
		}

		public Class resolveImplementationClass(Page page, String clsnm) throws ClassNotFoundException {
			return Component.class;
		}

		public boolean isInstance(org.zkoss.zk.ui.Component comp) {
			return comp != null;
		}

		public Component newInstance(Page page, String clsnm) {
			throw new UnsupportedOperationException();
		}

		public Component newInstance(Class cls) {
			throw new UnsupportedOperationException();
		}

		public void addMold(String name, String widgetClass) {
			throw new UnsupportedOperationException();
		}

		public String getWidgetClass(Component comp, String moldName) {
			return null;
		}

		public String getDefaultWidgetClass(Component comp) {
			return null;
		}

		public void setDefaultWidgetClass(String widgetClass) {
			throw new UnsupportedOperationException();
		}

		public boolean hasMold(String name) {
			return false;
		}

		public Collection<String> getMoldNames() {
			return Collections.emptyList();
		}

		public void addProperty(String name, String value) {
			throw new UnsupportedOperationException();
		}

		public void applyProperties(Component comp) {
		}

		public void applyAttributes(Component comp) {
		}

		public Map<String, Object> evalProperties(Map<String, Object> propmap, Page owner, Component parent) {
			return propmap != null ? propmap : new HashMap<String, Object>(2);
		}

		public AnnotationMap getAnnotationMap() {
			return null;
		}

		public String getApply() {
			return null;
		}

		public ExValue[] getParsedApply() {
			return null;
		}

		public void setApply(String apply) {
			throw new UnsupportedOperationException();
		}

		public URL getDeclarationURL() {
			return null;
		}

		public ComponentDefinition clone(LanguageDefinition langdef, String name) {
			throw new UnsupportedOperationException();
		}

		public Object clone() {
			throw new UnsupportedOperationException();
		}

		public boolean isShadowElement() {
			return false;
		}
	};
}
