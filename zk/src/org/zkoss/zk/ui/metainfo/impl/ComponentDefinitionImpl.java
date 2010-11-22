/* ComponentDefinitionImpl.java

	Purpose:
		
	Description:
		
	History:
		Tue May 31 17:54:45     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo.impl;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.net.URL;

import org.zkoss.lang.Classes;
import org.zkoss.web.servlet.Servlets;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.*;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.impl.EvaluatorRef;
import org.zkoss.zk.xel.impl.Utils;
import org.zkoss.zk.scripting.Interpreter;

/**
 * An implementation of {@link ComponentDefinition}.
 * 
 * <p>Note: it is not thread safe. Thus, it is better to {@link #clone}
 * and then modifying the cloned instance if you want to change it
 * concurrently.
 *
 * @author tomyeh
 */
public class ComponentDefinitionImpl
implements ComponentDefinition, java.io.Serializable {
	private String _name;
	private transient LanguageDefinition _langdef;
	private transient PageDefinition _pgdef;
	private EvaluatorRef _evalr;
	/** Either String or Class. */
	private Object _implcls;
	/** A map of (String mold, ExValue widgetClass). */
	private Map<String, ExValue> _molds;
	/** The default widget class. */
	private ExValue _defWgtClass;
	/** A map of custom attributs (String name, ExValue value). */
	private Map<String, ExValue> _custAttrs;
	/** A list of {@link Property}. */
	private List<Property> _props;
	/** the current directory. */
	private String _curdir;
	/** the property name to which the text within the element will be assigned. */
	private String _textAs;
	private AnnotationMap _annots;
	private URL _declURL;
	/** The parsed expessions of the apply attribute. */
	private ExValue[] _apply;
	/** Whether to preserve the blank text. */
	private boolean _blankpresv;

	/** Constructs a native component, i.e., a component implemented by
	 * a Java class.
	 *
	 * <p>Note; if both langdef and pgdef are null, it must be a reserved
	 * component.
	 *
	 * @param langdef the language definition. It is null if it is defined
	 * as part of a page definition
	 * @param pgdef the page definition. It is null if it is defined
	 * as part of a language definition.
	 * doesn't belong to any language.
	 * @param cls the implementation class.
	 * @since 3.0.0
	 */
	public ComponentDefinitionImpl(LanguageDefinition langdef,
	PageDefinition pgdef, String name, Class<? extends Component> cls) {
		if (cls != null && !Component.class.isAssignableFrom(cls))
			throw new IllegalArgumentException(cls+" must implement "+Component.class);
		init(langdef, pgdef, name, cls);
	}
	/** Constructs a native component, i.e., a component implemented by
	 * a Java class.
	 *
	 * <p>Note; if both langdef and pgdef are null, it must be a reserved
	 * component.
	 *
	 * @param langdef the language definition. It is null if it is defined
	 * as part of a page definition
	 * @param pgdef the page definition. It is null if it is defined
	 * as part of a language definition.
	 * doesn't belong to any language.
	 * @param clsnm the implementation class.
	 * @since 3.0.8
	 */
	public ComponentDefinitionImpl(LanguageDefinition langdef,
	PageDefinition pgdef, String name, String clsnm) {
		init(langdef, pgdef, name, clsnm);
	}
	private void init(LanguageDefinition langdef,
	PageDefinition pgdef, String name, Object cls) {
		if (name == null)
			throw new IllegalArgumentException();
		if (langdef != null && pgdef != null)
			throw new IllegalArgumentException("langdef and pgdef cannot both null or both non-null");

		_langdef = langdef;
		_pgdef = pgdef;
		_name = name;
		_implcls = cls;

		_evalr = _langdef != null ? _langdef.getEvaluatorRef():
			_pgdef != null ? _pgdef.getEvaluatorRef(): null;
	}
	/** Constructs a macro component definition.
	 * It is the component definition used to implement the macros.
	 *
	 * @param langdef the language definition. It is null if it is defined
	 * as part of a page definition
	 * @param pgdef the page definition. It is null if it is defined
	 * as part of a language definition.
	 * @param macroURI the URI of the ZUML page to representing this macro.
	 * @since 3.0.0
	 */
	public static final ComponentDefinition newMacroDefinition(
	LanguageDefinition langdef, PageDefinition pgdef, String name,
	Class<? extends Component> cls, String macroURI, boolean inline) {
		return new MacroDefinition(langdef, pgdef, name, cls, macroURI, inline);
	}
	/** Constructs a native component definition.
	 * It is the component definition used to implement the native namespace.
	 *
	 * @param langdef the language definition. It cannot be null.
	 * @since 3.0.0
	 */
	public static final ComponentDefinition newNativeDefinition(
	LanguageDefinition langdef, String name, Class<? extends Component> cls) {
		return new NativeDefinition(langdef, name, cls);
	}

	//extra//
	/** Adds a custom attribute.
	 */
	public void addCustomAttribute(String name, String value) {
		if (name == null || value == null
		|| name.length() == 0 || value.length() == 0)
			throw new IllegalArgumentException();

		final ExValue ev = new ExValue(value, Object.class);
		if (_custAttrs == null)
			_custAttrs = new HashMap<String, ExValue>(4);
		_custAttrs.put(name, ev);
	}

	/** Associates an annotation to this component definition.
	 *
	 * @param annotName the annotation name (never null, nor empty).
	 * @param annotAttrs a map of attributes, or null if no attribute at all.
	 * The attribute must be in a pair of strings (String name, String value).
	 */
	public void addAnnotation(String annotName, Map<String, String> annotAttrs) {
		if (_annots == null)
			_annots = new AnnotationMap();
		_annots.addAnnotation(annotName, annotAttrs);
	}
	/** Adds an annotation to the specified proeprty of this component
	 * definition.
	 *
	 * @param propName the property name (never nul, nor empty).
	 * @param annotName the annotation name (never null, nor empty).
	 * @param annotAttrs a map of attributes, or null if no attribute at all.
	 * The attribute must be in a pair of strings (String name, String value).
	 */
	public void addAnnotation(String propName, String annotName, Map<String, String> annotAttrs) {
		if (_annots == null)
			_annots = new AnnotationMap();
		_annots.addAnnotation(propName, annotName, annotAttrs);
	}

	/** Returns the current directory which is used to convert
	 * a relative URI to absolute, or null if not available.
	 */
	public String getCurrentDirectory() {
		return _curdir;
	}
	/** Sets the current directory which is used to convert
	 * a relative URI to absolute.
	 *
	 * @param curdir the current directory; null to ignore.
	 */
	public void setCurrentDirectory(String curdir) {
		if (curdir != null && curdir.length() > 0) {
			_curdir = curdir.charAt(curdir.length() - 1) != '/' ? curdir + '/': curdir;
		} else {
			_curdir = null;
		}
	}

	/** Sets the property name to which the text enclosed within
	 * the element (associated with this component definition) is assigned to.
	 *
	 * <p>Default: null (means to create a Label component)
	 *
	 * @param propnm the property name. If empty (""), null is assumed.
	 * @see #getTextAs
	 * @since 3.0.0
	 */
	public void setTextAs(String propnm) {
		_textAs = propnm != null && propnm.length() > 0 ? propnm: null;
	}
	/** Sets whether to preserve the blank text.
	 * If false, the blank text (a non-empty string consisting of whitespaces)
	 * are ignored.
	 * If true, they are converted to a label child.
	 * <p>Default: false.
	 * @see #isBlankPreserved
	 * @since 3.5.0
	 */
	public void setBlankPreserved(boolean preserve) {
		_blankpresv = preserve;
	}

	/** Sets the URI where this definition is declared.
	 *
	 * @param url the URL. If null, it means not available.
	 * @since 3.0.3
	 */
	public void setDeclarationURL(URL url) {
		_declURL = url;
	}

	//ComponentDefinition//
	public LanguageDefinition getLanguageDefinition() {
		return _langdef;
	}
	public String getName() {
		return _name;
	}

	public String getTextAs() {
		return _textAs;
	}
	public boolean isBlankPreserved() {
		return _blankpresv;
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

	public Object getImplementationClass() {
		return _implcls;
	}
	public void setImplementationClass(Class<? extends Component> cls) {
		if (!Component.class.isAssignableFrom(cls))
			throw new UiException(Component.class.getName()+" must be implemented by "+cls);
		_implcls = cls;
	}
	public void setImplementationClass(String clsnm) {
		if (clsnm == null || clsnm.length() == 0)
			throw new UiException("Non-empty class name is required");
		_implcls = clsnm;
	}
	@SuppressWarnings("unchecked")
	public Component newInstance(Page page, String clsnm) {
		try {
			return newInstance((Class<? extends Component>)
				resolveImplementationClass(page, clsnm));
		} catch (ClassNotFoundException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	public Component newInstance(Class<? extends Component> cls) {
		final Object curInfo = ComponentsCtrl.getCurrentInfo();
		final boolean bSet = !(curInfo instanceof ComponentInfo)
			|| ((ComponentInfo)curInfo).getComponentDefinition() != this;
		if (bSet) ComponentsCtrl.setCurrentInfo(this);
		final Component comp;
		try {
			comp = cls.newInstance();
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			if (bSet) ComponentsCtrl.setCurrentInfo((ComponentDefinition)null);
		}
		return comp;
	}
	public boolean isInstance(Component comp) {
		Class<?> cls;
		if (_implcls instanceof String) {
			final Page page = comp.getPage();
			if (page != null) {
				try {
					cls = resolveImplementationClass(page, null);
				} catch (ClassNotFoundException ex) {
					return true; //consider as true if not resolvable
				}
			} else {
				try {
					cls = Classes.forNameByThread((String)_implcls);
				} catch (ClassNotFoundException ex) {
					return true; //consider as true if not found
				}
			}
		} else {
			cls = (Class<?>)_implcls;
		}
		return cls.isInstance(comp);
	}
	public Class<?> resolveImplementationClass(Page page, String clsnm)
	throws ClassNotFoundException {
		Object cls = clsnm != null ? clsnm: _implcls;
		if (cls instanceof String) {
			clsnm = (String)cls;
			try {
				final Class<?> found = Classes.forNameByThread(clsnm);
				if (clsnm == null) _implcls = found;
					//cache to _implcls (to improve the performance)
				return found;
			} catch (ClassNotFoundException ex) {
				//we don't cache it if it is defined in a interpreter
				if (page != null) {
					for (Iterator it = page.getLoadedInterpreters().iterator();
					it.hasNext();) {
						Class<?> c = ((Interpreter)it.next()).getClass(clsnm);
						if (c != null)
							return c;
					}
				}
				throw ex;
			}
		}
		return (Class)cls;
	}

	public AnnotationMap getAnnotationMap() {
		return _annots;
	}

	public String getApply() {
		if (_apply == null)
			return null;

		final StringBuffer sb = new StringBuffer();
		for (int j = 0; j < _apply.length; ++j) {
			if (j > 0) sb.append(',');
			sb.append(_apply[j].getRawValue());
		}
		return sb.toString();
	}
	public void setApply(String apply) {
		_apply = Utils.parseList(apply, Object.class, true);
	}
	public ExValue[] getParsedApply() {
		return _apply;
	}

	public URL getDeclarationURL() {
		return _declURL;
	}

	public void addProperty(String name, String value) {
	//Implementation Note: the reason not to have condition because
	//isEffective always assumes Executions.getCurrent, which is
	//not true if _langdef != null

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("name");

		final Property prop = new Property(_evalr, name, value, null);
		if (_props == null)
			_props = new LinkedList<Property>();
		_props.add(prop);
	}
	public void applyProperties(Component comp) {
		//Note: it doesn't apply annotations since it is done
		//by AbstractComponent's initial with getAnnotationMap()

		if (_custAttrs != null) {
			for (Map.Entry<String, ExValue> me: _custAttrs.entrySet()) {
				comp.setAttribute(me.getKey(),
					me.getValue().getValue(_evalr, comp));
			}
		}
		if (_props != null) {
			for (Property prop: _props) {
				prop.assign(comp);
			}
		}
	}

	public Map<String, Object> evalProperties(Map<String, Object> propmap, Page owner, Component parent) {
		if (propmap == null)
			propmap = new HashMap<String, Object>();

		if (_props != null) {
			for (Property prop: _props) {
				if (parent != null) {
					if (prop.isEffective(parent))
						propmap.put(prop.getName(), prop.getValue(parent));
				} else {
					if (prop.isEffective(owner))
						propmap.put(prop.getName(), prop.getValue(owner));
				}
			}
		}
		return propmap;
	}

	public void addMold(String name, String widgetClass) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException();

		if (_molds == null)
			_molds = new HashMap<String, ExValue>(2);
		_molds.put(name, new ExValue(widgetClass, String.class));
	}

	public boolean hasMold(String name) {
		return _molds != null && _molds.containsKey(name);
	}
	public Collection<String> getMoldNames() {
		if (_molds != null)
			return _molds.keySet();
		return Collections.emptyList();
	}
	public String getWidgetClass(Component comp, String moldName) {
		if (_molds != null) {
			final ExValue wc = _molds.get(moldName);
			if (wc != null) {
				final String s = (String)wc.getValue(_evalr, comp);
				if (s != null)
					return s;
			}
		}
		return getDefaultWidgetClass(comp);
	}
	public String getDefaultWidgetClass(Component comp) {
		return _defWgtClass != null ?
			(String)_defWgtClass.getValue(_evalr, comp): null;
	}
	public void setDefaultWidgetClass(String widgetClass) {
		final ExValue oldwc = _defWgtClass;
		_defWgtClass = new ExValue(widgetClass, String.class);

		//replace mold's widget class if it is the old default one
		if (oldwc != null && _molds != null)
			for (Map.Entry<String, ExValue> me: _molds.entrySet()) {
				if (oldwc.equals(me.getValue()))
					me.setValue(_defWgtClass);
			}
	}

	private String toAbsoluteURI(String uri) {
		if (_curdir != null && uri != null && uri.length() > 0) {
			final char cc = uri.charAt(0);
			if (cc != '/' && cc != '~' && !Servlets.isUniversalURL(uri)) 
				return _curdir + uri;
		}
		return uri;
	}

	public ComponentDefinition clone(LanguageDefinition langdef, String name) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("empty");

		ComponentDefinitionImpl cd = (ComponentDefinitionImpl)clone();
		cd._name = name;
		cd._langdef = langdef;
		return cd;
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		s.writeObject(_langdef != null ? _langdef.getName(): null);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		final String langnm = (String)s.readObject();
		if (langnm != null)
			_langdef = LanguageDefinition.lookup(langnm);
	}

	//Object//
	public String toString() {
		return "[ComponentDefinition: "+_name+']';
	}

	//Cloneable/
	public Object clone() {
		final ComponentDefinitionImpl compdef;
		try {
			compdef = (ComponentDefinitionImpl)super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}

		if (_annots != null)
			compdef._annots = (AnnotationMap)_annots.clone();
		if (_props != null)
			compdef._props = new LinkedList<Property>(_props);
		if (_molds != null)
			compdef._molds = new HashMap<String, ExValue>(_molds);
		if (_custAttrs != null)
			compdef._custAttrs = new HashMap<String, ExValue>(_custAttrs);
		return compdef;
	}
}
