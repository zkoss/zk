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
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.el.Evaluator;

/**
 * An implementation of {@link ComponentDefinition}.
 * 
 * @author tomyeh
 */
public class ComponentDefinitionImpl
implements ComponentDefinition, java.io.Serializable {
	private String _name;
	private transient LanguageDefinition _langdef;
	/** Either String or Class. */
	private Object _implcls;
	/** A synchronized map of molds (String name, String moldURI. */
	private Map _molds;
	/** A map of custom attributs (String name, String value). */
	private Map _custAttrs;
	/** A list of {@link Property}. */
	private List _props;
	private String _macroURI;
	/** the current directory. */
	private String _curdir;
	/** the property name to which the text within the element will be assigned. */
	private String _textAs;
	/** inline or regular macro. Used if _macroURI is not null. */
	private boolean _inline;
	private AnnotationMap _annots;

	/** Constructs a native component, i.e., a component implemented by
	 * a Java class.
	 *
	 * @param langdef the language definition, or null if this is a temporary
	 * definition, such as components defined in a page,
	 * doesn't belong to any language.
	 * @param cls the implementation class.
	 */
	public ComponentDefinitionImpl(LanguageDefinition langdef, String name,
	Class cls) {
		if (name == null)
			throw new IllegalArgumentException("null name");
		if (cls != null && !Component.class.isAssignableFrom(cls))
			throw new IllegalArgumentException(cls+" must implement "+Component.class);
			//cls might be assigned later

		_langdef = langdef;
		_name = name;
		_implcls = cls;
	}
	/** Constructs a macro component, i.e., a component implemented by
	 * a macro.
	 *
	 * <p>After calling this method, the caller MUST invoke
	 * {@link LanguageDefinition#initMacroDefinition}.
	 *
	 * @param langdef the language definition, or null if this is a temporary
	 * definition doesn't belong to any language.
	 */
	public ComponentDefinitionImpl(LanguageDefinition langdef, String name,
	String macroURI, boolean inline) {
		if (name == null)
			throw new IllegalArgumentException("null name");
		if (macroURI == null || macroURI.length() == 0)
			throw new IllegalArgumentException("empty macroURI");

		_langdef = langdef;
		_name = name;
		_macroURI = macroURI;
		_inline = inline;
	}

	//extra//
	/** Adds a custom attribute.
	 */
	public void addCustomAttribute(String name, String value) {
		if (name == null || value == null)
			throw new IllegalArgumentException("null");
		if (name.length() == 0 || value.length() == 0)
			throw new IllegalArgumentException("empty");

		if (_custAttrs == null) {
			synchronized (this) {
				if (_custAttrs == null) {
					final Map attrs = new HashMap(3);
					attrs.put(name, value);
					_custAttrs = attrs;
					return;
				}
			}
		}
		synchronized (_custAttrs) {
			_custAttrs.put(name, value);
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
				if (_annots == null) {
					final AnnotationMap annots = new AnnotationMap();
					annots.addAnnotation(annotName, annotAttrs);
					_annots = annots;
					return;
				}
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
				if (_annots == null) {
					final AnnotationMap annots = new AnnotationMap();
					annots.addAnnotation(propName, annotName, annotAttrs);
					_annots = annots;
					return;
				}
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
	 * @since 2.5.0
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
		return _macroURI != null;
	}
	public String getMacroURI() {
		return _macroURI;
	}
	public boolean isInlineMacro() {
		return _inline;
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
			synchronized (_custAttrs) {
				for (Iterator it = _custAttrs.entrySet().iterator();
				it.hasNext();) {
					final Map.Entry me = (Map.Entry)it.next();
					comp.setAttribute((String)me.getKey(),
						eval(comp, (String)me.getValue(), Object.class));
				}
			}
		}
		if (_props != null) {
			final Evaluator eval = _langdef != null ?
				_langdef.getEvaluator(): Executions.getCurrent();

			synchronized (_props) {
				for (Iterator it = _props.iterator(); it.hasNext();) {
					final Property prop = (Property)it.next();
					prop.assign(eval, comp);
				}
			}
		}
	}

	public Map evalProperties(Map propmap, Page owner, Component parent) {
		if (propmap == null)
			propmap = new HashMap();

		if (_props != null) {
			final Evaluator eval = _langdef != null ?
				_langdef.getEvaluator(): Executions.getCurrent();

			synchronized (_props) {
				for (Iterator it = _props.iterator(); it.hasNext();) {
					final Property prop = (Property)it.next();
					propmap.put(prop.getName(),
						parent != null ?
							eval(parent, prop.getValue(), Object.class):
							eval(owner, prop.getValue(), Object.class));
					//Note: we don't invoke isEffective since addProperty
					//doesn't support it.
					//Moreover, prop.isEffective supports only
					//Executions.getCurrent.
					//It is not applicable if _langdef != null
				}
			}
		}
		return propmap;
	}

	/** Evluates the specified expression with {@link #getLanguageDefinition},
	 * if any. If no language definition,
	 * the current execution ({@link Executions#evaluate}) is used.
	 */
	private Object eval(Component comp, String expr, Class expectedType) {
		return _langdef != null ?
			_langdef.getEvaluator().evaluate(comp, expr, expectedType):
			Executions.evaluate(comp, expr, expectedType);
	}
	/** Evluates the specified expression with {@link #getLanguageDefinition},
	 * if any. If no language definition,
	 * the current execution ({@link Executions#evaluate}) is used.
	 */
	private Object eval(Page page, String expr, Class expectedType) {
		return _langdef != null ?
			_langdef.getEvaluator().evaluate(page, expr, expectedType):
			Executions.evaluate(page, expr, expectedType);
	}

	public void addMold(String name, String moldURI) {
		if (name == null || moldURI == null)
			throw new IllegalArgumentException("null");
		if (name.length() == 0 || moldURI.length() == 0)
			throw new IllegalArgumentException("empty");

		if (_molds == null) {
			synchronized (this) {
				if (_molds == null) {
					final Map molds = new HashMap(3);
					molds.put(name, moldURI);
					_molds = Collections.synchronizedMap(molds);
					return;
				}
			}
		}

		_molds.put(name, moldURI);
	}
	public String getMoldURI(Component comp, String name) {
		final String mold = _molds != null ? (String)_molds.get(name): null;
		return mold != null ?
			toAbsoluteURI((String)eval(comp, mold, String.class)): null;
			//mold is part of lang addon if _langdef != null
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
