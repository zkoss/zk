/* InstanceDefinition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 11:27:13     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;

import org.zkoss.lang.D;
import org.zkoss.lang.Strings;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.ext.DynamicTag;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ForEach;
import org.zkoss.zk.ui.util.ForEachImpl;

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
 * @author tomyeh
 * @see PageDefinition
 */
public class InstanceDefinition extends ComponentDefinition
implements Condition {
	private final PageDefinition _pagedef;
	private final InstanceDefinition _parent;
	private final ComponentDefinition _compdef;
	/** A list of {@link InstanceDefinition} and {@link ZScript}. */
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
	/** The forEach, forEachBegin and forEachEnd attribute,
	 * which are used to evaluate this definition multiple times.
	 */
	private String[] _forEach;

	/** Constructs an instance definition.
	 * @param parent the parent; never null.
	 * @param compdef the component definition; never null
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
			new StringBuffer(12).append("z__id"), _nextName++).toString();
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
	public void appendChild(ZScript script) {
		if (script == null)
			throw new IllegalArgumentException("script");
		synchronized (_children) {
			_children.add(script);
		}
	}
	/** Adds a variable child. */
	public void appendChild(Variables variables) {
		if (variables == null)
			throw new IllegalArgumentException("variables");
		synchronized (_children) {
			_children.add(variables);
		}
	}
	/** Adds an instance-definition child. */
	public void appendChild(InstanceDefinition instdef) {
		if (instdef == null)
			throw new IllegalArgumentException("instdef");
		synchronized (_children) {
			_children.add(instdef);
		}
	}

	/** Adds an event handler.
	 *
	 * @param name the event name.
	 * @param zscript the script.
	 */
	public void addEventHandler(String name, ZScript zscript, Condition cond) {
		if (name == null || zscript == null)
			throw new IllegalArgumentException("name and zscript cannot be null");
		//if (!Events.isValid(name))
		//	throw new IllegalArgumentException("Invalid event name: "+name);
			//AbstractParser has checked it, so no need to check again

		final EventHandler evthd = new EventHandler(zscript, cond);

		if (_evthds == null) {
			synchronized (this) {
				if (_evthds == null) {
					final Map evthds = new HashMap();
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
		return _forEach == null ? null:
			comp != null ?
				ForEachImpl.getInstance(comp, _forEach[0], _forEach[1], _forEach[2]):
				ForEachImpl.getInstance(page, _forEach[0], _forEach[1], _forEach[2]);
	}
	/** Sets the forEach attribute, which is usually an expression.
	 * @param expr the expression to return a collection of objects, or
	 * null/empty to denote no iteration.
	 */
	public void setForEach(String expr, String begin, String end) {
		_forEach = expr != null && expr.length() > 0 ?
			new String[] {expr, begin, end}: null;
	}

	/** Returns a readonly list of children.
	 * Children includes another {@link InstanceDefinition} or {@link ZScript}.
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
	/** Creates an component of this definition (never null).
	 *
	 * <p>Note: this method doesn't invoke {@link Milieu#applyProperties}.
	 * It is caller's job to invoke them if necessary.
	 * Since the value of properties might depend on the component tree,
	 * it is better to assign the component with a proper parent
	 * before calling {@link Milieu#applyProperties}.
	 *
	 * <p>This method is used to implement {@link org.zkoss.zk.ui.sys.UiFactory#newComponent}.
	 * Others shall invoke {@link org.zkoss.zk.ui.sys.UiFactory#newComponent}
	 * instead of this method, such that an application developer have a chance
	 * to customize the way to instantiate a component.
	 *
	 * <p>In addition to instantiating a component with {@link #newInstance},
	 * {@link org.zkoss.zk.ui.sys.UiFactory#newComponent} will initialize the
	 * component with {@link Milieu#applyProperties}.
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
		//if we want to allow this, we have to modify Milieu
	}
	/*package*/ Map getMolds() {
		return _compdef.getMolds();
	}
	public void addParam(String name, String value) {
		throw new UnsupportedOperationException();
		//if we want to allow this, we have to modify Milieu
	}
	/*package*/ Map getParams() {
		return _compdef.getParams();
	}

	/** Returns the map of annotations associated with this definition
	 * (never null).
	 */
	public AnnotationMap getAnnotationMap() {
		return _annots != null ? _annots:
			_compdef != null ? _compdef.getAnnotationMap(): AnnotationMap.EMPTY;
	}
	public void addAnnotation(String annotName, Map annotAttrs) {
		if (_annots == null) {
			synchronized (this) {
				if (_annots == null) {
					_annots = _compdef != null ?
						(AnnotationMapImpl)_compdef.getAnnotationMap().clone():
						new AnnotationMapImpl();
					_annots.addAnnotation(annotName, annotAttrs);
					return;
				}
			}
		}
		synchronized (_annots) {
			_annots.addAnnotation(annotName, annotAttrs);
		}
	}
	public void addAnnotation(String propName, String annotName, Map annotAttrs) {
		if (_annots == null) {
			synchronized (this) {
				if (_annots == null) {
					_annots = _compdef != null ?
						(AnnotationMapImpl)_compdef.getAnnotationMap().clone():
						new AnnotationMapImpl();
					_annots.addAnnotation(propName, annotName, annotAttrs);
					return;
				}
			}
		}
		synchronized (_annots) {
			_annots.addAnnotation(propName, annotName, annotAttrs);
		}
	}

	//Serializable//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		throw new java.io.IOException("InstanceDefinition not serializable");
	}

	//Object//
	public String toString() {
		return "[InstanceDefinition:"+_compdef.getName()+'/'+getName()+']';
	}

	/** Not clonable.
	 * @exception CloneNotSupportedException always thrown.
	 */
	public Object clone() {
		throw new UnsupportedOperationException("InstanceDefinition not clonable");
	}
}
