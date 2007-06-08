/* ComponentsCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug  9 19:41:22     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import org.zkoss.lang.Strings;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.util.Pair;
import org.zkoss.util.CacheMap;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.AnnotationMap;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;

/**
 * Utilities for implementing components.
 *
 * @author tomyeh
 */
public class ComponentsCtrl {
	/** The prefix for auto generated ID. */
	private static final String
		AUTO_ID_PREFIX = "z_", ANONYMOUS_ID = "z__i";

	private static final ThreadLocal _compdef = new ThreadLocal();

	/** Returns the automatically generate component's UUID/ID.
	 */
	public static final String toAutoId(String prefix, int id) {
		final StringBuffer sb = new StringBuffer(16)
			.append(AUTO_ID_PREFIX).append(prefix).append('_');
		Strings.encode(sb, id);
		return sb.toString();
	}
	/** Returns the anonymous UUID.
	 */
	public static final String getAnonymousId() {
		return ANONYMOUS_ID;
	}

	/** Returns whether an ID is generated automatically.
	 */
	public static final boolean isAutoId(String id) {
		return id.startsWith(AUTO_ID_PREFIX)
			&& id.indexOf('_', AUTO_ID_PREFIX.length()) > 0;
	}
	/** Returns whether an ID is a valid UUID. */
	public static final boolean isUuid(String id) {
		return isAutoId(id);
	}

	/** Returns the current component definition, which is used only by
	 * {@link org.zkoss.zk.ui.AbstractComponent}.
	 */
	public static ComponentDefinition getCurrentDefinition() {
		return (ComponentDefinition)_compdef.get();
	}
	/** Sets the current component definition, which is used only by
	 * {@link org.zkoss.zk.ui.AbstractComponent}.
	 */
	public static void setCurrentDefinition(ComponentDefinition compdef) {
		_compdef.set(compdef);
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
	 * @return a two element array. The first element is the component,
	 * and the second component is the event name.
	 * @since 2.4.0
	 */
	public static Object[] parseEventExpression(Component comp, String evtexpr)
	throws ComponentNotFoundException {
		final int j = evtexpr.lastIndexOf('.');
		final String evtnm;
		Component target;
		if (j >= 0) {
			evtnm = evtexpr.substring(j + 1).trim();
			String path = evtexpr.substring(0, j);
			if (path.length() > 0) {
				target = null;
				if (path.indexOf("${") >= 0) {
					final Object v =
						Executions.evaluate(comp, path, Object.class);
					if (v instanceof Component) {
						target = (Component)v;
					} else if (v == null) {
						throw new ComponentNotFoundException("EL evaluated to null: "+path);
					} else {
						path = Objects.toString(v);
					}
				}
				if (target == null) {
					path = path.trim();
					target = "self".equals(path) ? comp:
						Path.getComponent(comp.getSpaceOwner(), path);
				}
			} else {
				target = comp;
			}
		} else {
			evtnm = evtexpr.trim();
			target = comp;
		}
		if (!Events.isValid(evtnm))
			throw new UiException("Not an event name: "+evtnm);
		return new Object[] {target, evtnm};
	}

	/** Parses a script by resolving #{xx} to make it executable
	 * at the client.
	 *
	 * @param comp the component used to resolve the EL expression.
	 * @param script the Java script to convert
	 * @since 2.4.0
	 */
	public static String parseClientScript(Component comp, String script) {
		StringBuffer sb = null;
		for (int j = 0, len = script.length();;) {
			final int k = script.indexOf("#{", j);
			if (k < 0)
				return sb != null ?
					sb.append(script.substring(j)).toString(): script;

			final int l = script.indexOf('}', k + 2);
			if (l < 0)
				throw new WrongValueException("Illegal script: unclosed EL expression.\n"+script);

			if (sb == null) sb = new StringBuffer(len);
			sb.append(script.substring(j, k));

			//eval EL
			Object val = Executions.evaluate(comp,
				'$' + script.substring(k + 1, l + 1), Object.class);
			if (val == null || (val instanceof Number)) {
				sb.append(val);
			} else if (val instanceof Component) {
				sb.append(" $e('")
					.append(Strings.escape(((Component)val).getUuid(), "'\\"))
					.append("')");
			} else if (val instanceof Date) {
				sb.append(" new Date(").append(((Date)val).getTime())
					.append(')');
			} else { //FUTURE: regex
				sb.append('\'')
					.append(Strings.escape(val.toString(), "'\\"))
					.append('\'');
			}

			//next
			j = l + 1;
		}
	}

	/** A map of (Pair(Class,String evtnm), Method). */
	private static final CacheMap _evtmtds =
		new CacheMap(131).setMaxSize(1000).setLifetime(60*60000);
	/** Returns the method for handling the specified event, or null
	 * if not available.
	 */
	public static final Method getEventMethod(Class cls, String evtnm) {
		final Pair key = new Pair(cls, evtnm);
		final Object o;
		synchronized (_evtmtds) {
			o = _evtmtds.get(key);
		}
		if (o != null)
			return o == Objects.UNKNOWN ? null: (Method)o;

		Method mtd = null;
		try {
			mtd = Classes.getCloseMethodBySubclass(
					cls, evtnm, new Class[] {Event.class}); //with event arg
		} catch (NoSuchMethodException ex) {
			try {
				mtd = cls.getMethod(evtnm, null); //no argument case
			} catch (NoSuchMethodException e2) {
			}
		}
		synchronized (_evtmtds) {
			_evtmtds.put(key, mtd != null ? mtd: Objects.UNKNOWN);
		}
		return mtd;
	}

	/** Represents a dummy definition. */
	public static final ComponentDefinition DUMMY =
	new ComponentDefinition() {
		public LanguageDefinition getLanguageDefinition() {
			return null;
		}
		public String getName() {
			return "[anonymous]";
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
		public Object getImplementationClass() {
			return Component.class;
		}
		public void setImplementationClass(Class cls) {
			throw new UnsupportedOperationException();
		}
		public void setImplementationClass(String clsnm) {
			throw new UnsupportedOperationException();
		}
		public Class resolveImplementationClass(Page page, String clsnm)
		throws ClassNotFoundException {
			return Component.class;
		}
		public boolean isInstance(org.zkoss.zk.ui.Component comp) {
			return comp != null;
		}
		public Component newInstance(Page page, String clsnm) {
			throw new UnsupportedOperationException();
		}
		public void addMold(String name, String moldURI) {
			throw new UnsupportedOperationException();
		}
		public String getMoldURI(Component comp, String name) {
			return null;
		}
		public boolean hasMold(String name) {
			return false;
		}
		public Collection getMoldNames() {
			return Collections.EMPTY_LIST;
		}
		public void addProperty(String name, String value) {
			throw new UnsupportedOperationException();
		}
		public void applyProperties(Component comp) {
		}
		public Map evalProperties(Map propmap, Page owner, Component parent) {
			return propmap != null ? propmap: new HashMap(3);
		}
		public AnnotationMap getAnnotationMap() {
			return null;
		}
		public ComponentDefinition clone(LanguageDefinition langdef, String name) {
			throw new UnsupportedOperationException();
		}
		public Object clone() {
			throw new UnsupportedOperationException();
		}
	};
}
