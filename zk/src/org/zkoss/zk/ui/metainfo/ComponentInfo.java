/* ComponentInfo.java

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
import org.zkoss.zk.ui.ext.DynamicTag;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ForEach;
import org.zkoss.zk.ui.util.ForEachImpl;
import org.zkoss.zk.el.Evaluator;

/**
 * Represents a componennt instance defined in a ZUML page.
 *
 * @author tomyeh
 */
public class ComponentInfo extends NodeInfo
implements Cloneable, Condition {
	private final PageDefinition _pagedef;
	private NodeInfo _parent;
	private final ComponentDefinition _compdef;
	/** The implemetation class (use). */
	private String _implcls;
	/** A list of {@link Property}. */
	private List _props;
	/** A Map of event handler to handle events. */
	private EventHandlerMap _evthds;
	/** the annotation map. Note: it doesn't include what are defined in _compdef. */
	private AnnotationMap _annots;
	/** The tag name for the dyanmic tag. Used only if this implements {@link DynamicTag}*/
	private final String _tagnm;
	/** The effectiveness condition (see {@link #isEffective}).
	 * If null, it means effective.
	 */
	private Condition _cond;
	/** The forEach, forEachBegin and forEachEnd attribute,
	 * which are used to evaluate this info multiple times.
	 */
	private String[] _forEach;

	/** Constructs the information about how to create component.
	 * @param parent the parent; never null.
	 * @param compdef the component definition; never null
	 * @param tagnm the tag name; Note: if component implements
	 * {@link DynamicTag}, this argument must be specified.
	 * If {@link DynamicTag} is not implemented, this argument must
	 * be null.
	 */
	public ComponentInfo(NodeInfo parent, ComponentDefinition compdef,
	String tagnm) {
		if (parent == null || compdef == null)
			throw new IllegalArgumentException("parent and compdef required");

		_parent = parent;
		_pagedef = parent.getPageDefinition();
		_compdef = compdef;
		_tagnm = tagnm;
		_parent.appendChild0(this);
	}
	/** Constructs the info about how to create a component.
	 * @param parent the parent; never null.
	 */
	public ComponentInfo(NodeInfo parent, ComponentDefinition compdef) {
		this(parent, compdef, null);
	}

	/** Returns the language definition that {@link #getComponentDefinition}
	 * belongs to, or null if the component definition is temporary.
	 */
	public LanguageDefinition getLanguageDefinition() {
		return _compdef.getLanguageDefinition();
	}
	/** Returns the component definition, or null if it is PageDefinition.
	 */
	public ComponentDefinition getComponentDefinition() {
		return _compdef;
	}

	/** Sets the parent.
	 */
	public void setParent(NodeInfo parent) {
		if (parent != _parent) {
			synchronized (this) {
				if (_parent != null)
					_parent.removeChild0(this);
				_parent = parent;
				if (_parent != null)
					_parent.appendChild0(this);
			}
		}
	}

	/** Returns a readonly list of properties ({@link Property}) (never null).
	 * @since 2.3.2
	 */
	public List getProperties() {
		return _props != null ?
			Collections.unmodifiableList(_props): Collections.EMPTY_LIST;
	}
	/** Adds a property initializer.
	 * It will initialize a component when created with this info.
	 * @param name the member name. The component must have a valid setter
	 * for it.
	 * @param value the value. It might contain expressions (${}).
	 */
	public void addProperty(String name, String value, Condition cond) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("name");

		final Property prop = new Property(name, value, cond);
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
					final EventHandlerMap evthds = new EventHandlerMap();
					evthds.add(name, evthd);
					_evthds = evthds;
					return;
				}
			}
		}

		_evthds.add(name, evthd);
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
	 * @return the forEach object to iterate this info multiple times,
	 * or null if this info shall be interpreted only once.
	 */
	public ForEach getForEach(Page page, Component comp) {
		return _forEach == null ? null:
			comp != null ?
				ForEachImpl.getInstance(
					comp, _forEach[0], _forEach[1], _forEach[2]):
				ForEachImpl.getInstance(
					page, _forEach[0], _forEach[1], _forEach[2]);
	}
	/** Sets the forEach attribute, which is usually an expression.
	 * @param expr the expression to return a collection of objects, or
	 * null/empty to denote no iteration.
	 */
	public void setForEach(String expr, String begin, String end) {
		_forEach = expr != null && expr.length() > 0 ?
			new String[] {expr, begin, end}: null;
	}

	/** Returns the class name (String) that implements the component.
	 */
	public String getImplementationClass() {
		return _implcls;
	}
	/** Sets the class name to implements the component.
	 */
	public void setImplementationClass(String clsnm) {
		_implcls = clsnm;
	}

	/** Creates an component based on this info (never null).
	 *
	 * <p>Like {@link ComponentDefinition#newInstance},
	 * this method doesn't invoke {@link #applyProperties}.
	 * It is caller's job to invoke them if necessary.
	 * Since the value of properties might depend on the component tree,
	 * it is better to assign the component with a proper parent
	 * before calling {@link #applyProperties}.
	 */
	public Component newInstance(Page page) {
		final Component comp = _compdef.newInstance(page, _implcls);
		if (comp instanceof DynamicTag)
			((DynamicTag)comp).setTag(_tagnm);
		return comp;
	}

	/** Applies the event handlers, annotations, properties and
	 * custom attributes to the specified component.
	 *
	 * <p>Unlike {@link ComponentDefinition#applyProperties},
	 * this method copies annotations defined in this info to
	 * the component.
	 *
	 * @param defIncluded whether to call {@link ComponentDefinition#applyProperties}.
	 */
	public void applyProperties(Component comp, boolean defIncluded) {
		if (_annots != null)
			((ComponentCtrl)comp).addSharedAnnotationMap(_annots);

		if (_evthds != null)
			((ComponentCtrl)comp).addSharedEventHandlerMap(_evthds);

		if (defIncluded)
			_compdef.applyProperties(comp);

		if (_props != null) {
			final Evaluator eval = Executions.getCurrent();
			synchronized (_props) {
				for (Iterator it = _props.iterator(); it.hasNext();) {
					final Property prop = (Property)it.next();
					prop.assign(eval, comp);
				}
			}
		}
	}

	/** Evaluates and retrieves properties to the specified map from
	 * {@link ComponentDefinition} (and {@link ComponentInfo}).
	 *
	 * @param propmap the map to store the retrieved properties.
	 * If null, a HashMap instance is created.
	 * @param owner the owner page; used if parent is null
	 * @param parent the parent component (may be null)
	 * @param defIncluded whether to call {@link ComponentDefinition#evalProperties}.
	 */
	public Map evalProperties(Map propmap, Page owner, Component parent,
	boolean defIncluded) {
		if (defIncluded)
			propmap = _compdef.evalProperties(propmap, owner, parent);
		else if (propmap == null)
			propmap = new HashMap();

		if (_props != null) {
			final Evaluator eval = Executions.getCurrent();

			synchronized (_props) {
				for (Iterator it = _props.iterator(); it.hasNext();) {
					final Property prop = (Property)it.next();
					if (parent != null) {
						if (prop.isEffective(parent))
							propmap.put(prop.getName(),
								eval.evaluate(parent, prop.getValue(), Object.class));
					} else {
						if (prop.isEffective(owner))
							propmap.put(prop.getName(),
								eval.evaluate(owner, prop.getValue(), Object.class));
					}
				}
			}
		}
		return propmap;
	}

	/** Associates an annotation to this component info.
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
	 * info.
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

	//Condition//
	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(comp);
	}
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(page);
	}

	//NodeInfo//
	public PageDefinition getPageDefinition() {
		return _pagedef;
	}
	public NodeInfo getParent() {
		return _parent;
	}

	//Cloneable//
	/** Clones this info.
	 * After cloned, {@link #getParent} is null.
	 */
	public Object clone() {
		synchronized (this) {
			try {
				final ComponentInfo info = (ComponentInfo)super.clone();
				info._parent = null;
				if (_annots != null)
					info._annots = (AnnotationMap)_annots.clone();
				if (_props != null)
					info._props = new LinkedList(_props);
				if (_evthds != null)
					info._evthds = (EventHandlerMap)_evthds.clone();
				return info;
			} catch (CloneNotSupportedException ex) {
				throw new InternalError();
			}
		}
	}
}
