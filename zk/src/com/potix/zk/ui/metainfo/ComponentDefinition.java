/* ComponentDefinition.java

{{IS_NOTE
	$Id: ComponentDefinition.java,v 1.9 2006/05/04 04:36:40 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue May 31 17:54:45     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Condition;
import com.potix.zk.ui.util.Evaluator;
import com.potix.zk.ui.sys.ComponentCtrl;

/**
 * A component definition.
 * Like class in Java, a {@link ComponentDefinition} defines the behavior
 * of a component.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.9 $ $Date: 2006/05/04 04:36:40 $
 * @see LanguageDefinition
 */
public class ComponentDefinition implements Cloneable {
	private final String _name;
	private LanguageDefinition _langdef;
	private Class _cls;
	private Map _molds, _params;
	private List _props;
	private String _macroUri;

	/** A special definition representing the zk component. */
	public final static ComponentDefinition ZK =
		new ComponentDefinition(null, "zk", Component.class);;

	/** Reserved words. */
	/*private static final Set RESERVES = new HashSet(5);
	static {
		String[] resvs = new String[] {"zscript", "attribute", "zk"};
		for (int j = 0; j < resvs.length; ++j)
			RESERVES.add(resvs[j]);
	}*/

	/** Constructs a native component, i.e., a component implemented by
	 * a Java class.
	 *
	 * @param langdef the language definition, or null if this is a temporary
	 * definition doesn't belong to any language.
	 * @param cls the implementation class.
	 */
	public ComponentDefinition(LanguageDefinition langdef, String name,
	Class cls) {
		if (name == null)
			throw new IllegalArgumentException("null name");
		if (!Component.class.isAssignableFrom(cls))
			throw new IllegalArgumentException(cls+" must implement "+Component.class);
		//if (RESERVES.contains(name))
		//	throw new IllegalArgumentException("Reserved word cannot be used: "+name);

		_langdef = langdef;
		_name = name;
		_cls = cls;
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
	public ComponentDefinition(LanguageDefinition langdef, String name,
	String macroUri) {
		if (name == null)
			throw new IllegalArgumentException("null name");
		if (macroUri == null || macroUri.length() == 0)
			throw new IllegalArgumentException("empty macroUri");
		_langdef = langdef;
		_name = name;
		_macroUri = macroUri;
	}
	 

	/** Used by deriving class to contruct a 'virtual' definition that
	 * depends on other definitions.
	 * It is currently used only by {@link InstanceDefinition}.
	 */
	protected ComponentDefinition(LanguageDefinition langdef, String name) {
		if (name == null)
			throw new IllegalArgumentException("null name");

		_langdef =langdef;
		_name = name;
	}

	/** Returns the language definition, or null if it is temporty definition
	 * belonging to a page.
	 */
	public LanguageDefinition getLanguageDefinition() {
		return _langdef;
	}
	/** Sets the language definition.
	 */
	public void setLanguageDefinition(LanguageDefinition langdef) {
		_langdef = langdef;
	}

	/** Returns name of this component definition (never null).
	 * It is unique in the same language, {@link LanguageDefinition}.
	 */
	public String getName() {
		return _name;
	}
	/** Returns whether this is a macro component.
	 * @see #getMacroURI
	 */
	public boolean isMacro() {
		return _macroUri != null;
	}
	/** Returns the macro URI, or null if not a macro.
	 * @see #isMacro
	 */
	public String getMacroURI(Component comp) {
		return (String)evaluate(comp, _macroUri, String.class);
	}

	/** Returns the class that implements the component.
	 */
	public Class getImplementationClass() {
		return _cls;
	}
	/** Sets the class to implements the component.
	 */
	public void setImplementationClass(Class cls) {
		if (!Component.class.isAssignableFrom(cls))
			throw new UiException(Component.class.getName()+" must be implemented by "+cls);
		_cls = cls;
	}

	/** Adds a mold.
	 */
	public void addMold(String name, String moldUri) {
		if (name == null || moldUri == null)
			throw new IllegalArgumentException("null");
		if (name.length() == 0 || moldUri.length() == 0)
			throw new IllegalArgumentException("empty");
		if (_molds == null) {
			synchronized (this) {
				if (_molds == null) {
					final Map molds = new HashMap(3);
					molds.put(name, moldUri);
					_molds = molds;
					return;
				}
			}
		}
		synchronized (_molds) {
			_molds.put(name, moldUri);
		}
	}
	/** Adds a parameter.
	 * It is not public because we don't synchronize the access
	 * (so it is called when booting).
	 */
	/*package*/ void addParam(String name, String value) {
		if (name == null || value == null)
			throw new IllegalArgumentException("null");
		if (name.length() == 0 || value.length() == 0)
			throw new IllegalArgumentException("empty");
		if (_params == null) _params = new HashMap(5);
		_params.put(name, value);
	}

	/** Adds a property initializer.
	 * It will initialize a component when created with is definition.
	 * @param name the member name. The component must have a valid setter
	 * for it.
	 * @param value the value. It might contain expressions (${}).
	 */
	public void addProperty(String name, String value, Condition cond) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("name");
		final Property prop =
			new Property(getImplementationClass(), name, value, cond);
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
	/** Applies the property initializers to the component when the component
	 * is constructed.
	 *
	 * <p>It uses {@link #getLanguageDefinition}, if not null, to evaluate
	 * the EL expressions.
	 */
	public void applyProperties(Component comp) {
		applyProperties(comp, _langdef);
	}
	/** Applies the property initializers to the component by use of
	 * the specified evaluator.
	 *
	 * @param eval the evaluator to evaluate the property value.
	 * If null, Executions.getCurrent() is assumed.
	 */
	public void applyProperties(Component comp, Evaluator eval) {
		if (_props == null) return;
		if (eval == null) eval = Executions.getCurrent();

		//apply members
		synchronized (_props) {
			for (Iterator it = _props.iterator(); it.hasNext();) {
				final Property prop = (Property)it.next();
				prop.assign(comp, eval);
			}
		}
	}

	/** Returns whether the specified mold exists.
	 */
	public boolean hasMold(String name) {
		return _molds != null && _molds.containsKey(name);
	}
	/** Returns a collection of mold names supported by this definition.
	 */
	public Set getMoldNames() {
		return _molds != null ? _molds.keySet(): Collections.EMPTY_SET;
	}
	/** Returns the URI of the mold, or null if no such mold available.
	 * If mold contains an expression, it will be evaluated first
	 * before returning.
	 *
	 * @param name the mold
	 */
	public String getMoldURI(Component comp, String name) {
		if (_molds == null)
			throw new IllegalStateException("No mold is defined for "+this);
		return (String)evaluate(comp, (String)_molds.get(name), String.class);
	}
	/** Returns the value of the specified parameter.
	 * If the parameter contains an expression, it will be evaluated first
	 * before returning.
	 */
	public Object getParameter(Component comp, String name) {
		return _params != null ?
			evaluate(comp, (String)_params.get(name), Object.class):
			null;
	}

	/** Evluates the specified expression.
	 * Note: if {@link #getLanguageDefinition} is not return,
	 * {@link LanguageDefinition#evaluate} is used because it is defined
	 * in the scope of a language.
	 */
	private Object evaluate(Component comp, String expr, Class expectedType) {
		return _langdef != null ? _langdef.evaluate(comp, expr, expectedType):
			Executions.evaluate(comp, expr, expectedType);
	}

	//Object//
	public String toString() {
		return "[ComponentDefinition: "+_name+']';
	}
	public Object clone() {
		try {
			final ComponentDefinition compdef =
				(ComponentDefinition)super.clone();
			if (_props != null) compdef._props = new LinkedList(_props);
			if (_molds != null) compdef._molds = new HashMap(_molds);
			if (_params != null) compdef._params = new HashMap(_params);
			return compdef;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}
}
