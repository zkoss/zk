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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import org.zkoss.lang.Strings;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.AnnotationMap;

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
			return AnnotationMap.EMPTY;
		}
		public ComponentDefinition clone(LanguageDefinition langdef, String name) {
			throw new UnsupportedOperationException();
		}
		public Object clone() {
			throw new UnsupportedOperationException();
		}
	};
}
