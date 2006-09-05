/* InstanceDefinition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 11:27:13     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;

import com.potix.lang.D;
import com.potix.lang.Strings;

import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.metainfo.PageDefinition;
import com.potix.zk.ui.ext.DynamicTag;
import com.potix.zk.ui.sys.ComponentCtrl;
import com.potix.zk.ui.util.Condition;
import com.potix.zk.ui.util.ForEach;
import com.potix.zk.ui.util.impl.ForEachImpl;

/**
 * An instance definition.
 * It extends the behavior of a {@link ComponentDefinition}.
 * It is mainly used to represent an 'instance' in ZUL page --
 * this is how it is named.
 * Notice, it is also a special component definitioin, so user could
 * define a new component by aggregating other components
 * (such as thru another ZUL page).
 *
 * <p>Note: InstanceDefinition is not Serializable, though it is derived
 * from {@link ComponentDefinition}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @see PageDefinition
 */
public class InstanceDefinition extends ComponentDefinition
implements Condition {
	private final PageDefinition _pagedef;
	private final InstanceDefinition _parent;
	private final ComponentDefinition _compdef;
	/** A list of {@link InstanceDefinition} and {@link Script}. */
	private final List _children = new LinkedList(),
		_roChildren = Collections.unmodifiableList(_children);
	/** A list of {@link CustomAttributes}. */
	private List _custAttrs;
	/** A Map of event handler (String name, EventHandler) to handle events. */
	private Map _evthds;
	/** The tag name for the dyanmic tag. Used only if this implements {@link DynamicTag}*/
	private final String _tagnm;
	/** The effectiveness condition (see {@link #isEffective}).
	 * If null, it means effective.
	 */
	private Condition _cond;
	/** The forEach attribute which is used to evaluate this definition
	 * multiple times.
	 */
	private String _forEach;

	/** Constructs an instance definition.
	 * @param parent the parent; never null.
	 */
	public InstanceDefinition(InstanceDefinition parent,
	ComponentDefinition compdef, String tagnm) {
		super(compdef.getLanguageDefinition(), getNextName());
		_parent = parent;
		_pagedef = parent.getPageDefinition();
		_compdef = compdef;
		_tagnm = tagnm;
		_parent.appendChild(this);
	}
	/** Constructs an instance definition.
	 * @param parent the parent; never null.
	 */
	public InstanceDefinition(InstanceDefinition parent,
	ComponentDefinition compdef) {
		this(parent, compdef, null);
	}
	/** Used by {@link PageDefinition} only.
	 */
	protected InstanceDefinition(LanguageDefinition langdef) {
		super(langdef, getNextName());
		_parent = null;
		_pagedef = (PageDefinition)this;
		_compdef = null;
		_tagnm = null;
	}
	private static final String getNextName() {
		return Strings.encode(
			new StringBuffer(12).append("_pid_"), _nextName++).toString();
	}
	private static int _nextName = 0;

	/** Returns the language definition.
	 */
	public PageDefinition getPageDefinition() {
		return _pagedef;
	}
	/** Returns the component definition, or null if it is PageDefinition.
	 */
	public ComponentDefinition getComponentDefinition() {
		return _compdef;
	}

	/** Returns the parent, or null if it is topmost.
	 */
	public InstanceDefinition getParent() {
		return _parent;
	}

	/** Adds a script child. */
	public void appendChild(Script script) {
		if (script == null)
			throw new NullPointerException();
		synchronized (_children) {
			_children.add(script);
		}
	}
	/** Adds an instance-definition child. */
	public void appendChild(InstanceDefinition instdef) {
		if (instdef == null)
			throw new NullPointerException();
		synchronized (_children) {
			_children.add(instdef);
		}
	}

	/** Adds an event handler.
	 * @param name the event name.
	 * @param script the script. It don't support expression.
	 */
	public void addEventHandler(String name, String script, Condition cond) {
		if (name == null || script == null)
			throw new IllegalArgumentException("name and script cannot be null");
		//if (!Events.isValid(name))
		//	throw new IllegalArgumentException("Invalid event name: "+name);
			//AbstractParser has checked it, so no need to check again

		final EventHandler evthd = new EventHandler(script, cond);

		if (_evthds == null) {
			synchronized (this) {
				if (_evthds == null) {
					final Map evthds = new HashMap(5);
					evthds.put(name, evthd);
					_evthds = evthds;
					return;
				}
			}
		}

		synchronized (_evthds) {
			final Object o = _evthds.put(name, evthd);
			if (o != null) {
				_evthds.put(name, o); //recover
				throw new UiException("Replicate event handler: "+name);
			}
		}
	}
	/** Returns a map of event handler ({@link EventHandler}),
	 * or null if no handler at all.
	 */
	/*package*/ Map getEventHandlers() {
		return _evthds;
	}

	/** Adds a map of custom attributes.
	 */
	public void addCustomAttributes(CustomAttributes cattr) {
		if (cattr == null)
			throw new IllegalArgumentException("null");

		if (_custAttrs == null) {
			synchronized (this) {
				if (_custAttrs == null) {
					final List custAttrs = new LinkedList();
					custAttrs.add(cattr);
					_custAttrs = custAttrs;
					return;
				}
			}
		}

		synchronized (_custAttrs) {
			_custAttrs.add(cattr);
		}
	}
	/** Returns the list of custom attributes ({@link CustomAttributes}),
	 * or null if no custom attributes at all.
	 *
	 * <p>Note: to access the returned, you have to use synchronized to
	 * synchronized the returned list (if not null).
	 */
	/*package*/ List getCustomAttributes() {
		return _custAttrs;
	}

	/** Sets the effectiveness condition.
	 */
	public void setCondition(Condition cond) {
		_cond = cond;
	}

	/** Returns the forEach object if the forEach attribute is defined
	 * (or {@link #setForEach} is called).
	 *
	 * <p>If comp is not null, both pagedef and page are ignored.
	 * If comp is null, page must be specified.
	 *
	 * @return the forEach object to iterate this definition multiple times,
	 * or null if this definition shall be interpreted only once.
	 */
	public ForEach getForEach(Page page, Component comp) {
		return comp != null ?
			ForEachImpl.getInstance(comp, _forEach):
			ForEachImpl.getInstance(page, _forEach);
	}
	/** Sets the forEach attribute, which is usually an expression.
	 * @param expr the expression to return a collection of objects, or
	 * null/empty to denote no iteration.
	 */
	public void setForEach(String expr) {
		_forEach = expr != null && expr.length() > 0 ? expr: null;
	}

	/** Returns a readonly list of children.
	 * Children includes another {@link InstanceDefinition} or {@link Script}.
	 */
	public List getChildren() {
		return _roChildren;
	}

	//-- Condition --//
	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(comp);
	}
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(page);
	}

	//-- super --//
	/** Returns an component of this definition (never null).
	 *
	 * <p>Note: {@link Millieu#applyProperties} will NOT be invoked,
	 * if you call this method manually or create a component manually.
	 */
	public Component newInstance(Page page) {
		final Component comp = super.newInstance(page);
		if (_tagnm != null) ((DynamicTag)comp).setTag(_tagnm);
		return comp;
	}

	public Object getImplementationClass() {
		final Object cls = super.getImplementationClass();
		return cls != null ? cls: _compdef.getImplementationClass();
	}
	public boolean isMacro() {
		return _compdef.isMacro();
	}
	/*package*/ String getMacroURI() {
		return _compdef.getMacroURI();
	}	
	public void addMold(String name, String moldUri) {
		throw new UnsupportedOperationException();
		//if we want to allow this, we have to modify Millieu
	}
	/*package*/ Map getMolds() {
		return _compdef.getMolds();
	}
	/*package*/ void addParam(String name, String value) {
		throw new UnsupportedOperationException();
		//if we want to allow this, we have to modify Millieu
	}
	/*package*/ Map getParams() {
		return _compdef.getParams();
	}

	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		throw new java.io.IOException("InstanceDefinition not serializable");
	}

	//Object//
	public String toString() {
		return "[InstanceDefinition:"+_compdef.getName()+'/'+getName()+']';
	}
}
