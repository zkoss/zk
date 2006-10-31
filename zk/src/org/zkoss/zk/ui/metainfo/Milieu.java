 /* Milieu.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 19 12:21:08     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.web.servlet.Servlets;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Evaluator;

/**
 * The snapshot of the component definition ({@link ComponentDefinition}).
 *
 * <p>Components created with the same component definition share the
 * same {@link Milieu} instance. However, if a component is serialized and then
 * de-serialized back, it will have an independent Milieu instance
 * not shared with any others ({@link org.zkoss.zk.ui.sys.ComponentCtrl#getMilieu}).
 * Moreover, {@link #getComponentDefinition} returns null in this case.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Milieu implements Serializable {
    private static final long serialVersionUID = 20061017L;

	/** The language definition. */
	private transient LanguageDefinition _langdef;
	/** The component definition.
	 * <p>Note: it becomes null after a component is serialized (and
	 * deserialized back). So we have to make a copy of members that must
	 * remain after a component is serialized.
	 * <p>Note: this implementation use _compdef only in {@link #applyProperties}.
	 */
	private transient ComponentDefinition _compdef; //Yes, transient
	/** either String or Class as the implementation class. */
	private Object _implcls;
	/** The event handlers, molds and parameters.
	 * Note: we have to make a copy from _compdef, since they must remain
	 * after a component is serialized.
	 */
	private final Map _evthds, _molds, _params;
	/** URI. */
	private final String _macroURI;
	/** The current directory. */
	private final String _curdir;
	/** The annotations of properties, (String propName, Map(String annotName, Annotation)).
	 */
	private final Map _annots;

	//static//
	private static final ThreadLocal _mill = new ThreadLocal();
	/** A dummy millieu which assumes nothing is defined at all. */
	public static final Milieu DUMMY = new Milieu();

	/** Returns the current millieu, which is used only by
	 * {@link org.zkoss.zk.ui.AbstractComponent}.
	 * <p>UiEngine use this to communicate with
	 * {@link org.zkoss.zk.ui.AbstractComponent}.
	 */
	public static Milieu getCurrent() {
		return (Milieu)_mill.get();
	}
	/** Sets the current millieu.
	 */
	public static void setCurrent(Milieu mill) {
		_mill.set(mill);
	}

	/** Constructor.
	 */
	/*package*/ Milieu(ComponentDefinition compdef) {
		//Note: we have to make a copy of some members of compdef,
		//since compdef is NOT serializable)
		_implcls = compdef.getImplementationClass();
		_params = compdef.getParams();
		_molds = compdef.getMolds();
		_macroURI = compdef.getMacroURI();
		_langdef = compdef.getLanguageDefinition();
		_compdef = compdef;
		if (compdef instanceof InstanceDefinition) {
			final InstanceDefinition instdef = (InstanceDefinition)compdef;
			_evthds = instdef.getEventHandlers();

			final PageDefinition pgdef = instdef.getPageDefinition();
			String dir = null;
			if (pgdef != null) {
				dir = pgdef.getLocator().getDirectory();
				if (dir != null)
					if (dir.length() == 0) dir = null;
					else if (dir.charAt(dir.length() - 1) != '/')
						dir = dir + '/';
			}
			_curdir = dir;

			_annots = instdef.getAnnotations();
		} else {
			_evthds = _annots = null;
			_curdir = null;
		}
	}
	private Milieu() {
		_implcls = null;
		_evthds = _params = _molds = _annots = null;
		_macroURI = null;
		_curdir = null;
	}

	/** Returns the language definition, or null if it doesn't belong to any
	 * language, such as components defined in a page.
	 */
	public LanguageDefinition getLanguageDefinition() {
		return _langdef;
	}
	/** Returns the component definition, or null if it doesn't belong to
	 * any component definition or the associated component is serialized
	 * (and de-serialized back).
	 *
	 * <p>See also {@link Milieu}.
	 */
	public ComponentDefinition getComponentDefinition() {
		return _compdef;
	}

	/** Returns the annotation associated with the component definition,
	 * or null if not available.
	 *
	 * @param annotName the annotation name
	 */
	public Annotation getAnnotation(String annotName) {
		return getAnnotation(annotName, null);
	}
	/** Returns the annotation associated wit the definition of the specified
	 * property, or null if not available.
	 *
	 *
	 * @param annotName the annotation name
	 * @param propName the property name, e.g., "value".
	 * If null, it is the same as {@link #getAnnotation(String)}.
	 */
	public Annotation getAnnotation(String annotName, String propName) {
		if (_annots != null) {
			final Map ans = (Map)_annots.get(propName);
			if (ans != null) return (Annotation)ans.get(annotName);
		}
		return null;
	}

	/** Resolves and returns the class that implements the component.
	 *
	 * <p>Unlike {@link ComponentDefinition#getImplementationClass},
	 * this method will resolve a class name (String) to a class (Class).
	 *
	 * @param page the page used to resolve the class name from its
	 * namespace ({@link Page#getNamespace}).
	 * @exception UiException if the class not found
	 */
	public Class resolveImplementationClass(Page page) throws UiException {
		if (_implcls instanceof String) {
			final String clsnm = (String)_implcls;
			try {
				final Class cls = page.getClass(clsnm);
				if (Objects.equals(
				cls.getClassLoader(), Milieu.class.getClassLoader()))
					_implcls = cls; //cache only if static
				return cls;
			} catch (ClassNotFoundException ex) {
				throw new UiException("Class not found: "+clsnm, ex);
			}
		}
		return (Class)_implcls;
	}

	/** Applies the properties and custom attributes defined in
	 * {@link ComponentDefinition} (and {@link InstanceDefinition}).
	 *
	 * <p>Note: both properties and custom attributes are applied, if
	 * {@link #getComponentDefinition} returns an instance of {@link InstanceDefinition}.
	 */
	public void applyProperties(Component comp) {
		if (_compdef == null) //not belonging to any def (or been serialized)
			return; //nothing to do

		final Execution exec = Executions.getCurrent();
		final List lprops, pprops, custAttrs;
		if (_compdef instanceof InstanceDefinition) {
			final InstanceDefinition instdef = (InstanceDefinition)_compdef;
			pprops = instdef.getProperties();
			lprops = instdef.getComponentDefinition().getProperties();
			custAttrs = instdef.getCustomAttributes();
		} else {
			lprops = _compdef.getProperties();
			pprops = custAttrs = null;
		}

		if (lprops != null) {
			if (_langdef != null) applyProps(_langdef, comp, lprops);
			else applyProps(exec, comp, lprops);
				//_langdef is null if components are defined in page only
		}

		if (pprops != null)
			applyProps(exec, comp, pprops);

		if (custAttrs != null)
			applyCustAttrs(comp, custAttrs);
	}
	private void applyProps(Evaluator eval, Component comp, List props) {
		synchronized (props) {
			for (Iterator it = props.iterator(); it.hasNext();) {
				final Property prop = (Property)it.next();
				prop.assign(this, comp, eval);
			}
		}
	}
	private void applyCustAttrs(Component comp, List custAttrs) {
		synchronized (custAttrs) {
			for (Iterator it = custAttrs.iterator(); it.hasNext();)
				((CustomAttributes)it.next()).apply(comp);
		}
	}

	/** Returns the script of the event handler.
	 */
	public String getEventHandler(Component comp, String name) {
		if (_evthds != null) {
			final EventHandler ehi;
			synchronized (_evthds) {
				ehi = (EventHandler)_evthds.get(name);
			}
			if (ehi != null && ehi.isEffective(comp))
				return ehi.getScript();
		}
		return null;
	}

	/** Returns the value of the specified parameter.
	 * If the parameter contains an expression, it will be evaluated first
	 * before returning.
	 */
	public Object getParameter(Component comp, String name) {
		if (_params != null) {
			final String param;
			synchronized (_params) {
				param = (String)_params.get(name);
			}
			if (param != null) return evalByLang(comp, param, Object.class);
				//param is part of lang addon if _langdef != null
		}
		return null;
	}

	/** Returns whether the specified mold exists.
	 */
	public boolean hasMold(String name) {
		return _molds != null && _molds.containsKey(name);
	}
	/** Returns a collection of mold names supported by this definition.
	 */
	public Set getMoldNames() {
		if (_molds != null) {
			synchronized (_molds) {
				return new HashSet(_molds.keySet());
			}
		}
		return Collections.EMPTY_SET;
	}
	/** Returns the URI of the mold, or null if no such mold available.
	 * If mold contains an expression, it will be evaluated first
	 * before returning.
	 *
	 * @param name the mold
	 */
	public String getMoldURI(Component comp, String name) {
		if (_molds == null)
			throw new IllegalStateException("No mold is defined for "+comp);

		final String mold;
		synchronized (_molds) {
			mold = (String)_molds.get(name);
		}
		return mold != null ?
			toAbsoluteURI((String)evalByLang(comp, mold, String.class)): null;
			//mold is part of lang addon if _langdef != null
	}
	/** Returns whether this is a macro component.
	 * @see #getMacroURI
	 */
	public boolean isMacro() {
		return _macroURI != null;
	}
	/** Returns the macro URI, or null if not a macro.
	 * It evaluates it before returning if the macro URI is an EL expression.
	 * @see #isMacro
	 */
	public String getMacroURI(Component comp) {
		return toAbsoluteURI((String)evalByLang(comp, _macroURI, String.class));
			//macro-uri is part of lang addon if _langdef != null
	}
	/** Evluates the specified expression with {@link #getLanguageDefinition},
	 * if any. If null, the current execution ({@link Executions#evaluate})
	 * is used.
	 */
	private Object evalByLang(Component comp, String expr, Class expectedType) {
		return _langdef != null ?
			_langdef.evaluate(comp, expr, expectedType):
			Executions.evaluate(comp, expr, expectedType);
	}
	private String toAbsoluteURI(String uri) {
		if (_curdir != null && uri != null && uri.length() > 0) {
			final char cc = uri.charAt(0);
			if (cc != '/' && cc != '~' && !Servlets.isUniversalURL(uri)) 
				return _curdir + uri;
		}
		return uri;
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws IOException {
		s.defaultWriteObject();

		s.writeObject(_langdef != null ? _langdef.getName(): null);
	}
	private synchronized void readObject(ObjectInputStream s)
	throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		
		final String langnm = (String)s.readObject();
		if (langnm != null)
			_langdef = LanguageDefinition.lookup(langnm);
	}

	//Object//
	public String toString() {
		final StringBuffer sb  = new StringBuffer(64).append("[millieu: ");
		if (_implcls != null) {
			if (_implcls instanceof Class)
				sb.append(((Class)_implcls).getName());
			else
				sb.append(_implcls);
			if (_macroURI != null)
				sb.append(", ").append(_macroURI);
		} else {
			sb.append("dummy");
		}
		return sb.append(']').toString();
	}
}
