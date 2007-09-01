/* ComponentDefinitionImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 17:54:45     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import org.zkoss.lang.Classes;
import org.zkoss.web.servlet.Servlets;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.*;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.xel.Evaluator;
import org.zkoss.zk.ui.xel.ExValue;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.scripting.Interpreter;

/**
 * An implementation of {@link ComponentDefinition}.
 * 
 * @author tomyeh
 */
public class ComponentDefinitionImpl
implements ComponentDefinition, java.io.Serializable {
	private String _name;
	private transient LanguageDefinition _langdef;
	private transient PageDefinition _pgdef;
	private Evaluator _eval;
	/** Either String or Class. */
	private Object _implcls;
	/** A synchronized map of molds (String name, ExValue moldURI). */
	private Map _molds;
	/** A map of custom attributs (String name, ExValue value). */
	private Map _custAttrs;
	/** A list of {@link Property}. */
	private List _props;
	/** the current directory. */
	private String _curdir;
	/** the property name to which the text within the element will be assigned. */
	private String _textAs;
	private AnnotationMap _annots;

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
	PageDefinition pgdef, String name, Class cls) {
		if (name == null)
			throw new IllegalArgumentException();
		if (cls != null && !Component.class.isAssignableFrom(cls))
			throw new IllegalArgumentException(cls+" must implement "+Component.class);
		if (langdef != null && pgdef != null)
			throw new IllegalArgumentException("langdef and pgdef cannot both null or both non-null");

		_langdef = langdef;
		_pgdef = pgdef;
		_name = name;
		_implcls = cls;
	}
	/** Constructs a macro component definition.
	 * It is the component definition used to implement the macros.
	 *
	 * @param langdef the language definition. It is null if it is defined
	 * as part of a page definition
	 * @param pgdef the page definition. It is null if it is defined
	 * as part of a language definition.
	 * @since 3.0.0
	 */
	public static final ComponentDefinition newMacroDefinition(
	LanguageDefinition langdef, PageDefinition pgdef, String name,
	Class cls, String macroURI, boolean inline) {
		return new MacroDefinition(langdef, pgdef, name, cls, macroURI, inline);
	}
	/** Constructs a native component definition.
	 * It is the component definition used to implement the native namespace.
	 *
	 * @param langdef the language definition. It cannot be null.
	 * @since 3.0.0
	 */
	public static final ComponentDefinition newNativeDefinition(
	LanguageDefinition langdef, String name, Class cls) {
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
		if (_custAttrs == null) {
			synchronized (this) {
				if (_custAttrs == null)
					_custAttrs = new HashMap(4);
			}
		}
		synchronized (_custAttrs) {
			_custAttrs.put(name, ev);
		}
	}

	/** Associates an annotation to this component definition.
	 *
	 * @param annotName the annotation name (never null, nor empty).
	 * @param annotAttrs a map of attributes, or null if no attribute at all.
	 * The attribute must be in a pair of strings (String name, String value).
	 */
	public void addAnnotation(String annotName, Map annotAttrs) {
		if (_annots == null) {
			synchronized (this) {
				if (_annots == null)
					_annots = new AnnotationMap();
			}
		}
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
	public void addAnnotation(String propName, String annotName, Map annotAttrs) {
		if (_annots == null) {
			synchronized (this) {
				if (_annots == null)
					_annots = new AnnotationMap();
			}
		}
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
	public void setImplementationClass(Class cls) {
		if (!Component.class.isAssignableFrom(cls))
			throw new UiException(Component.class.getName()+" must be implemented by "+cls);
		_implcls = cls;
	}
	public void setImplementationClass(String clsnm) {
		if (clsnm == null || clsnm.length() == 0)
			throw new UiException("Non-empty class name is required");
		_implcls = clsnm;
	}
	public Component newInstance(Page page, String clsnm) {
		final Object curInfo = ComponentsCtrl.getCurrentInfo();
		final boolean bSet = !(curInfo instanceof ComponentInfo)
			|| ((ComponentInfo)curInfo).getComponentDefinition() != this;
		if (bSet) ComponentsCtrl.setCurrentInfo(this);
		final Component comp;
		try {
			comp = (Component)
				resolveImplementationClass(page, clsnm).newInstance();
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			if (bSet) ComponentsCtrl.setCurrentInfo((ComponentDefinition)null);
		}
		return comp;
	}
	public boolean isInstance(Component comp) {
		Class cls;
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
			cls = (Class)_implcls;
		}
		return cls.isInstance(comp);
	}
	public Class resolveImplementationClass(Page page, String clsnm)
	throws ClassNotFoundException {
		Object cls = clsnm != null ? clsnm: _implcls;
		if (cls instanceof String) {
			clsnm = (String)cls;
			try {
				final Class found = Classes.forNameByThread(clsnm);
				if (clsnm == null) _implcls = found;
					//cache to _implcls (to improve the performance)
				return found;
			} catch (ClassNotFoundException ex) {
				//we don't cache it if it is defined in a interpreter
				if (page != null) {
					for (Iterator it = page.getLoadedInterpreters().iterator();
					it.hasNext();) {
						Class c = ((Interpreter)it.next()).getClass(clsnm);
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

	public void addProperty(String name, String value) {
	//Implementation Note: the reason not to have condition because
	//isEffective always assumes Executions.getCurrent, which is
	//not true if _langdef != null

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("name");

		final Property prop = new Property(name, value, null);
		if (_props == null) {
			synchronized (this) {
				if (_props == null) {
					final List props = new LinkedList();
					props.add(prop);
					_props = props;
					return;
				}
			}
		}
		synchronized (_props) {
			_props.add(prop);
		}
	}
	public void applyProperties(Component comp) {
		//Note: it doesn't apply annotations since it is done
		//by AbstractComponent's initial with getAnnotationMap()

		if (_custAttrs != null) {
			final Evaluator eval = getEvaluator();
			synchronized (_custAttrs) {
				for (Iterator it = _custAttrs.entrySet().iterator();
				it.hasNext();) {
					final Map.Entry me = (Map.Entry)it.next();
					comp.setAttribute((String)me.getKey(),
						((ExValue)me.getValue()).getValue(eval, comp));
				}
			}
		}
		if (_props != null) {
			final Evaluator eval = getEvaluator();
			synchronized (_props) {
				for (Iterator it = _props.iterator(); it.hasNext();) {
					final Property prop = (Property)it.next();
					prop.assign(eval, comp);
				}
			}
		}
	}
	private Evaluator getEvaluator() {
		return _langdef != null ? _langdef.getEvaluator():
			_pgdef != null ? _pgdef.getEvaluator(): _eval;
	}

	public Map evalProperties(Map propmap, Page owner, Component parent) {
		if (propmap == null)
			propmap = new HashMap();

		if (_props != null) {
			final Evaluator eval = getEvaluator();
			synchronized (_props) {
				for (Iterator it = _props.iterator(); it.hasNext();) {
					final Property prop = (Property)it.next();
					if (parent != null) {
						if (prop.isEffective(parent))
							propmap.put(prop.getName(), prop.getValue(eval, parent));
					} else {
						if (prop.isEffective(owner))
							propmap.put(prop.getName(), prop.getValue(eval, owner));
					}
				}
			}
		}
		return propmap;
	}

	public void addMold(String name, String moldURI) {
		if (name == null || moldURI == null
		|| name.length() == 0 || moldURI.length() == 0)
			throw new IllegalArgumentException();

		final ExValue ev = new ExValue(moldURI, String.class);
		if (_molds == null) {
			synchronized (this) {
				if (_molds == null) {
					final Map molds = new HashMap(3);
					molds.put(name, ev);
					_molds = Collections.synchronizedMap(molds);
					return;
				}
			}
		}

		_molds.put(name, ev);
	}
	public String getMoldURI(Component comp, String name) {
		final ExValue mold = _molds != null ? (ExValue)_molds.get(name): null;
		return mold == null ? null:
			toAbsoluteURI((String)mold.getValue(getEvaluator(), comp));
	}
	public boolean hasMold(String name) {
		return _molds != null && _molds.containsKey(name);
	}
	public Collection getMoldNames() {
		return _molds != null ?
			_molds.keySet(): (Collection)Collections.EMPTY_LIST;
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
		s.writeObject(_pgdef != null ? _pgdef.getEvaluator(): null);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		final String langnm = (String)s.readObject();
		if (langnm != null)
			_langdef = LanguageDefinition.lookup(langnm);
		_eval = (Evaluator)s.readObject();
	}

	//Object//
	public String toString() {
		return "[ComponentDefinition: "+_name+']';
	}
	//Cloneable/
	public Object clone() {
		synchronized (this) {
			try {
				final ComponentDefinitionImpl compdef =
					(ComponentDefinitionImpl)super.clone();
				if (_annots != null)
					compdef._annots = (AnnotationMap)_annots.clone();
				if (_props != null)
					compdef._props = new LinkedList(_props);
				if (_molds != null)
					compdef._molds = Collections.synchronizedMap(new HashMap(_molds));
				if (_custAttrs != null)
					compdef._custAttrs = new HashMap(_custAttrs);
				return compdef;
			} catch (CloneNotSupportedException ex) {
				throw new InternalError();
			}
		}
	}
}
