/* InstanceDefinition.java

{{IS_NOTE
	$Id: InstanceDefinition.java,v 1.17 2006/05/24 13:47:18 tomyeh Exp $
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
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.17 $ $Date: 2006/05/24 13:47:18 $
 * @see PageDefinition
 */
public class InstanceDefinition extends ComponentDefinition
implements Condition {
	private final PageDefinition _pagedef;
	private final InstanceDefinition _parent;
	private final ComponentDefinition _compdef;
	/** A list of {@link InstanceDefiniton} and {@link Script}. */
	private final List _children = new LinkedList(),
		_roChildren = Collections.unmodifiableList(_children);
	/** A list of {@link CustomAttributes}. */
	private final List _custAttrs = new LinkedList();
	/** A Map of event handler (String name, EventHandler) to handle events. */
	private final Map _evthds = new HashMap();
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

		synchronized (_evthds) {
			final Object o = _evthds.put(name, new EventHandler(script, cond));
			if (o != null) {
				_evthds.put(name, o); //recover
				throw new UiException("Replicate event handler: "+name);
			}
		}
	}
	/** Returns the script of the event handler.
	 */
	public String getEventHandler(Component comp, String name) {
		final EventHandler ehi;
		synchronized (_evthds) {
			ehi = (EventHandler)_evthds.get(name);
		}
		if (ehi != null && ehi.isEffective(comp))
			return ehi.getScript();
		return null;
	}

	/** Adds a map of custom attributes.
	 */
	public void addCustomAttributes(CustomAttributes custAttrs) {
		if (custAttrs == null)
			throw new IllegalArgumentException("null");
		synchronized (_custAttrs) {
			_custAttrs.add(custAttrs);
		}
	}
	/** Applies the custom attributes.
	 */
	public void applyCustomAttributes(Component comp) {
		if (!_custAttrs.isEmpty()) //optimize it is rare to have cust. attrs
			synchronized (_custAttrs) {
				for (Iterator it = _custAttrs.iterator(); it.hasNext();)
					((CustomAttributes)it.next()).apply(comp);
			}
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
	public ForEach getForEach(PageDefinition pagedef, Page page, Component comp) {
		return comp != null ?
			ForEachImpl.getInstance(comp, _forEach):
			ForEachImpl.getInstance(pagedef, page, _forEach);
	}
	/** Sets the forEach attribute, which is usually an expression.
	 * @param expr the expression to return a collection of objects, or
	 * null/empty to denote no iteration.
	 */
	public void setForEach(String expr) {
		_forEach = expr != null && expr.length() > 0 ? expr: null;
	}

	/** Returns an component of this definition (never null).
	 *
	 * <p>Note: {@link #applyProperties} will NOT be invoked, if you call
	 * this method manually or create a component manually.
	 * You could invoke it if really necessary.
	 */
	public Component newInstance(Page page) {
		final Component comp;
		try {
			comp = (Component)resolveImplementationClass(page).newInstance();
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
		((ComponentCtrl)comp).setDefinition(this);
		if (_tagnm != null) ((DynamicTag)comp).setTag(_tagnm);
		return comp;
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
	public boolean isEffective(PageDefinition pagedef, Page page) {
		return _cond == null || _cond.isEffective(pagedef, page);
	}

	//-- super --//
	public Object getImplementationClass() {
		final Object cls = super.getImplementationClass();
		return cls != null ? cls: _compdef.getImplementationClass();
	}
	public Class resolveImplementationClass(Page page) {
		final Class cls = super.resolveImplementationClass(page);
		return cls != null ? cls: _compdef.resolveImplementationClass(page);
	}
	public boolean hasMold(String name) {
		return _compdef != null && _compdef.hasMold(name);
	}
	public Set getMoldNames() {
		return _compdef != null ? _compdef.getMoldNames(): Collections.EMPTY_SET;
	}
	public String getMoldURI(Component comp, String name) {
		if (name == null)
			throw new IllegalArgumentException("null");
		if (_compdef == null)
			throw new IllegalStateException("No component definition");
		return _compdef.getMoldURI(comp, name);
	}
	public Object getParameter(Component comp, String name) {
		final Object o = super.getParameter(comp, name);
		return o != null || _compdef == null ? o: _compdef.getParameter(comp, name);
	}
	public boolean isMacro() {
		return _compdef != null && _compdef.isMacro();
	}
	public String getMacroURI(Component comp) {
		return _compdef != null ? _compdef.getMacroURI(comp): null;
	}

	/** Applies the member initials to the component when a component
	 * is created by a ZUML page (instead of by program).
	 */
	public void applyProperties(Component comp) {
		_compdef.applyProperties(comp);
		applyProperties(comp, Executions.getCurrent());
	}

	//Object//
	public String toString() {
		return "[InstanceDefinition:"+_compdef.getName()+'/'+getName()+']';
	}

	private static class EventHandler implements Condition {
		private final String _script;
		private final Condition _cond;
		private EventHandler(String script, Condition cond) {
			_script = script;
			_cond = cond;
		}
		public String getScript() {
			return _script;
		}
		public boolean isEffective(Component comp) {
			return _cond == null || _cond.isEffective(comp);
		}
		public boolean isEffective(PageDefinition pagedef, Page page) {
			return _cond == null || _cond.isEffective(pagedef, page);
		}
	}
}
