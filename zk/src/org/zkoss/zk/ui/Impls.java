/* Impls.java

	History:
		Wed Feb 22 17:30:05 TST 2012, Created by tomyeh

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;

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
			AnnotationMap annots = getClassAnnotationMap(klass.getSuperclass());
			if (annots == null)
				annots = new AnnotationMap();
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
		loadClassAnnots(annots, klass.getDeclaredAnnotations(), null,
			new Loc(klass.getName()));

		//3. load method's
		final Method[] mtds = klass.getDeclaredMethods();
		for (int j = 0; j < mtds.length; ++j)
			loadClassAnnots(annots, mtds[j].getDeclaredAnnotations(), mtds[j],
				new Loc(mtds[j].toString()));
	}
	private static void loadClassAnnots(AnnotationMap annots,
	java.lang.annotation.Annotation[] jannots, Method mtd, Location loc) {
		String prop = null;
		for (int j = 0; j < jannots.length; ++j) {
			final java.lang.annotation.Annotation jannot = jannots[j];
			final ComponentAnnotation compAnnot =
				jannot.annotationType().getAnnotation(ComponentAnnotation.class);
			if (compAnnot != null) {
				if (mtd != null) {
					if (prop == null) {
						final String s = mtd.getName();
						prop = s.length() > 3 && (s.startsWith("get") || s.startsWith("get")) ?
							(""+s.charAt(3)).toLowerCase() + s.substring(4): s;
					}
				} else {
					prop = getClassAnnotProp(jannot);
				}

				annots.addAnnotation(prop,
					getClassAnnotName(jannot), getClassAnnotAttrs(jannot), loc);
			}
		}
	}
	private static String getClassAnnotProp(java.lang.annotation.Annotation jannot) {
		final String prop;
		try {
			Method m = jannot.getClass().getMethod("property");
			prop = (String)m.invoke(jannot);
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex, "Failed to invoke property() against "+jannot);
		}
		if (prop == null || prop.length() == 0)
			throw new UiException("property() must return a non-empty string, "+jannot);
		return prop;
	}
	private static String getClassAnnotName(java.lang.annotation.Annotation jannot) {
		final String s = jannot.annotationType().getName();
		final int j = s.lastIndexOf('.');
		return j >= 0 ? s.substring(j + 1): s;
	}
	private static Map<String, String[]>
	getClassAnnotAttrs(java.lang.annotation.Annotation jannot) {
		final Map<String, String[]> attrs = new HashMap<String, String[]>(4);
		final Method[] mtds = jannot.annotationType().getMethods();
		for (int j = 0; j < mtds.length; ++j) {
			final Method mtd = mtds[j];
			final String name = mtd.getName();
			if (!name.equals("property") && !name.equals("annotationType") //keyword
			&& !name.equals("toString") && !name.equals("hashCode") //just in case
			&& mtd.getParameterTypes().length == 0) {
				final String value;
				try {
					value = convertClassAnnotValue(mtd.invoke(jannot));
				} catch (Exception ex) {
					throw UiException.Aide.wrap(ex, "Failed to invoke "+mtd+" against "+jannot);
				}
				if (value != null && value.length() > 0)
					attrs.put(name, new String[] {value});
			}
		}
		return attrs;
	}
	private static String convertClassAnnotValue(Object value) {
		return value instanceof Class ? ((Class)value).getName():
			Objects.toString(value);
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
		public String format(String message) {
			return org.zkoss.xml.Locators.format(message, _path, null, -1, -1);
		}
	}
}
