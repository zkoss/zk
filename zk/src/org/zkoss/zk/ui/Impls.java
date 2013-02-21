/* Impls.java

	History:
		Wed Feb 22 17:30:05 TST 2012, Created by tomyeh

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.util.Cache;
import org.zkoss.util.FastReadCache;
import org.zkoss.util.resource.Location;

import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.AnnotationMap;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;
import org.zkoss.zk.ui.metainfo.impl.AnnotationHelper;
import org.zkoss.zk.ui.annotation.ComponentAnnotation;
import org.zkoss.zk.ui.sys.ExecutionCtrl;

/**
 * Implementation-only utilities to simplify {@link AbstractComponent}.
 * @author tomyeh
 */
/*package*/ class Impls {
	/*package*/ static final String DEFAULT = "default";

	//--- Component Definition ---//
	/** Returns the component definition of the specified class, or null
	 * if not found.
	 */
	/*package*/ static ComponentDefinition
	getDefinition(Execution exec, Class<? extends Component> cls) {
		if (exec != null) {
			final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
			final PageDefinition pgdef = execCtrl.getCurrentPageDefinition();
			final Page page = execCtrl.getCurrentPage();

			final ComponentDefinition compdef =
				pgdef != null ? pgdef.getComponentDefinition(cls, true):
				page != null ? 	page.getComponentDefinition(cls, true): null;
			if (compdef != null && compdef.getLanguageDefinition() != null)
				return compdef; //already from langdef (not from pgdef)

			final ComponentDefinition compdef2 =
				Components.getDefinitionByDeviceType(exec.getDesktop().getDeviceType(), cls);
			return compdef != null && (compdef2 == null ||
			!Objects.equals(compdef.getImplementationClass(), compdef2.getImplementationClass())) ?
				compdef: compdef2; //Feature 2816083: use compdef2 if same class
		}

		for (String deviceType: LanguageDefinition.getDeviceTypes()) {
			final ComponentDefinition compdef =
				Components.getDefinitionByDeviceType(deviceType, cls);
			if (compdef != null)
				return compdef;
		}
		return null;
	}
	/*package*/ static ComponentDefinition
	getDefinitionByDeviceType(Component comp, String deviceType, String name) {
		for (LanguageDefinition ld: LanguageDefinition.getByDeviceType(deviceType)) {
			try {
				final ComponentDefinition def = ld.getComponentDefinition(name);
				if (def.isInstance(comp))
					return def;
			} catch (DefinitionNotFoundException ex) { //ignore
			}
		}
		return null;
	}

	/*package*/ static boolean duplicateListenerIgnored() {
		if (_dupListenerIgnored == null)
			_dupListenerIgnored = Boolean.valueOf(
				"true".equals(Library.getProperty("org.zkoss.zk.ui.EventListener.duplicateIgnored")));
		return _dupListenerIgnored.booleanValue();
	}
	private static Boolean _dupListenerIgnored;

	/*package*/ static String defaultMold(Class<? extends Component> klass) {
	//To speed up the performance, we store info in FastReadCache (no sync for read)
	//Also, better to use class name as a key since class might be defined in zscript
		final String clsnm = klass.getName();
		String mold = _defMolds.get(clsnm);
		if (mold == null) {
			mold = Library.getProperty(clsnm + ".mold", DEFAULT);
			_defMolds.put(clsnm, mold);
		}
		return mold;
	}
	private static transient Cache<String, String> _defMolds =
		new FastReadCache<String, String>(100, 4 * 60 * 60 * 1000);
		//cache is required since component's class might be defined in zscript

	//--- Class's Annotations ---//
	/** Returns the annotation map defined in Java class, or null if not found.
	 */
	/*package*/ static AnnotationMap getClassAnnotationMap(Class<?> klass) {
		if (klass == null)
			return null;

		final String clsnm = klass.getName();
		Object val = _defAnnots.get(clsnm);
		if (val == null) { //not loaded yet (OK to race)
			final AnnotationMap  annots = new AnnotationMap();
			annots.addAll(getClassAnnotationMap(klass.getSuperclass()));
			loadClassAnnots(annots, klass);
			_defAnnots.put(clsnm, val = annots.isEmpty() ? Objects.UNKNOWN: annots);
		}
		return val instanceof AnnotationMap ? (AnnotationMap)val: null;
	}
	/** Loads the annotation defined
	 */
	private static void loadClassAnnots(AnnotationMap annots, Class<?> klass) {
		if (klass == null)
			return; //nothing to do

		//1. load class's
		ComponentAnnotation jannot = klass.getAnnotation(ComponentAnnotation.class);
		if (jannot != null)
			loadClassAnnots(annots, jannot, null, new Loc(klass.getName()));

		//3. load method's
		final Method[] mtds = klass.getDeclaredMethods();
		for (int j = 0; j < mtds.length; ++j) {
			jannot = mtds[j].getAnnotation(ComponentAnnotation.class);
			if (jannot != null) {
				final Loc loc = new Loc(mtds[j].toString());
				final String prop = getMethodProp(mtds[j]);
				if (prop == null)
					throw new UiException(loc.format("Component annotations allowed only for public getter or setter"));
				loadClassAnnots(annots, jannot, prop, loc);
			}
		}
	}
	private static void loadClassAnnots(AnnotationMap annots,
	ComponentAnnotation jannot, final String prop, Location loc) {
		final AnnotationHelper annHelper = new AnnotationHelper();
		final String[] values = jannot.value();
		for (int j = 0; j < values.length; ++j) {
			String name = prop;
			String value = values[j];
			Matcher m = _rprop.matcher(value);
			if (m.matches()) {
				name = m.group(1);
				value = m.group(2);
			}
			
			if (!AnnotationHelper.isAnnotation(value))
				throw new UiException(loc.format("Invalid annotation: "+value));
			annHelper.addByCompoundValue(value, loc);
			annHelper.applyAnnotations(annots, name, true);
		}
	}
	private static final Pattern _rprop =
		Pattern.compile(" *([a-zA-Z0-9_$]*) *: *(.*) *");

	/** Returns the property name represents by this method, or null
	 * if not a setter/getter.
	 */
	private static String getMethodProp(Method mtd) {
		if (Modifier.isPublic(mtd.getModifiers())) {
			final String nm = mtd.getName();
			final int len = nm.length();
			switch (mtd.getParameterTypes().length) {
			case 0:
				if (len >= 4 && nm.startsWith("get"))
					return Character.toLowerCase(nm.charAt(3)) + nm.substring(4);
				if (len >= 3 && nm.startsWith("is"))
					return Character.toLowerCase(nm.charAt(2)) + nm.substring(3);
				break;
			case 1:
				if (len >= 4 && nm.startsWith("set"))
					return Character.toLowerCase(nm.charAt(3)) + nm.substring(4);
				break;
			}
		}
		return null;
	}
	private static transient Cache<String, Object> _defAnnots =
		new FastReadCache<String, Object>(100, 4 * 60 * 60 * 1000);
		//cache is required since component's class might be defined in zscript

	/** Location's implementation. */
	private static class Loc implements Location, java.io.Serializable {
		final String _path;

		private Loc(String path) {
			_path = path;
		}
		public String getPath() {
			return _path;
		}
		public int getLineNumber() {
			return -1;
		}
		public int getColumnNumber() {
			return -1;
		}
		public String format(String message) {
			return org.zkoss.xml.Locators.format(message, _path, null, -1, -1);
		}
	}
}
