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
import java.util.Collection;
import java.util.Collections;

import org.zkoss.lang.D;
import org.zkoss.lang.Strings;
import org.zkoss.lang.Classes;
import org.zkoss.util.CollectionsX;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.DynamicTag;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.ui.util.ForEach;
import org.zkoss.zk.ui.util.ForEachImpl;
import org.zkoss.zk.ui.metainfo.impl.MultiComposer;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * Represents a componennt instance defined in a ZUML page.
 *
 * <p>Though serializable, we can restore {@link #getPageDefinition}
 * correctly after deserialized.
 *
 * <p>Note:it is not thread-safe.
 *
 * <p>It is serializable.
 *
 * @author tomyeh
 */
public class ComponentInfo extends NodeInfo
implements Cloneable, Condition, java.io.Externalizable {
	/** Note: it is NodeInfo's job to serialize _evalr. */
	private transient EvaluatorRef _evalr;
	private transient NodeInfo _parent; //it is restored by its parent
	private ComponentDefinition _compdef;
	/** The implemetation class (use). */
	private String _implcls;
	/** A list of {@link Property}, or null if no property at all. */
	private List _props;
	/** A Map of event handler to handle events. */
	private EventHandlerMap _evthds;
	/** the annotation map. Note: it doesn't include what are defined in _compdef. */
	private AnnotationMap _annots;
	/** The tag name for the dyanmic tag. Used only if this implements {@link DynamicTag}*/
	private String _tag;
	/** The effectiveness condition (see {@link #isEffective}).
	 * If null, it means effective.
	 */
	private ConditionImpl _cond;
	/** The fulfill condition.
	 */
	private String _fulfill;
	/** The apply attribute.
	 */
	private ExValue _apply;
	/** The forward condition.
	 */
	private String _forward;
	/** The forEach, forEachBegin and forEachEnd attribute,
	 * which are used to evaluate this info multiple times.
	 */
	private String[] _forEach;

	/** Constructs the information about how to create component.
	 * @param parent the parent; never null.
	 * @param compdef the component definition; never null
	 * @param tag the tag name; Note: if component implements
	 * {@link DynamicTag}, this argument must be specified.
	 * If {@link DynamicTag} is not implemented, this argument must
	 * be null.
	 */
	public ComponentInfo(NodeInfo parent, ComponentDefinition compdef,
	String tag) {
		if (parent == null || compdef == null)
			throw new IllegalArgumentException("parent and compdef required");

		_parent = parent;
		_compdef = compdef;
		_tag = tag;
		_parent.appendChildDirectly(this);
		_evalr = parent.getEvaluatorRef();
	}
	/** Constructs the info about how to create a component that is not
	 * a dynamic tag.
	 * @param parent the parent; never null.
	 */
	public ComponentInfo(NodeInfo parent, ComponentDefinition compdef) {
		this(parent, compdef, null);
	}
	/** This constructor is used only for {@link java.io.Externalizable}.
	 * Don't call it, otherwise.
	 * @since 3.0.0
	 */
	public ComponentInfo() {
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

	/** Returns the tag name, or null if no tag name.
	 * @since 3.0.0
	 */
	public String getTag() {
		return _tag;
	}

	/** Sets the parent.
	 */
	public void setParent(NodeInfo parent) {
		//we don't check if parent is changed (since we have to move it
		//to the end)
		if (_parent != null)
			_parent.removeChildDirectly(this);

		_parent = parent;

		if (_parent != null)
			_parent.appendChildDirectly(this);
	}
	/** Used for implementation only. */
	/*package*/ void setParentDirectly(NodeInfo parent) {
		_parent = parent;
	}

	/** Returns the property name to which the text enclosed within
	 * the element (associated with this component definition) is assigned to.
	 *
	 * <p>Default: null (means to create a Label component as the child)
	 *
	 * @see ComponentDefinition#getTextAs
	 * @since 3.0.0
	 */
	public String getTextAs() {
		return _compdef.getTextAs();
	}

	/** Returns the fulfill condition that controls when to create
	 * the child components, or null if the child components
	 * are created at the time when the page is loaded.
	 *
	 * <p>Default: null.
	 *
	 * <p>If not null, the child components specified in
	 * {@link #getChildren} are created, when the event sepcified in
	 * the fulfill condition is received at the first time.
	 *
	 * <p>It is the value specified in the fulfill attribute.
	 *
	 * @since 2.4.0
	 */
	public String getFulfill() {
		return _fulfill;
	}
	/** Sets the fulfill condition that controls when to create
	 * the child components.
	 *
	 * <p>If not null, the child components specified in
	 * {@link #getChildren} are created, when the event sepcified in
	 * the fulfill condition is received at the first time.
	 *
	 * @param fulfill the fulfill condition. There are several forms:
	 * "eventName", "targetId.evetName", "id1/id2.evetName",
	 * and "${elExpr}.eventName"
	 * @since 2.4.0
	 */
	public void setFulfill(String fulfill) {
		_fulfill = fulfill != null && fulfill.length() > 0 ? fulfill: null;
	}

	/** Returns the composer for this info, or null if not available.
	 * It evaluates the value returned by {@link #getApply}.
	 *
	 * @see #getApply
	 * @since 3.0.0
	 */
	public Composer getComposer(Page page) {
		return getComposer(page, null);
	}
	/** Returns the composer for this info, or nuull if not available.
	 * If comp is null, it is the same as {@link #getComposer(Page)}.
	 * If comp is not null, it is used as the self variable.
	 *
	 * @see #getApply
	 * @since 3.0.1
	 */
	public Composer getComposer(Page page, Component comp) {
		if (_apply != null) {
			Object o = comp != null ?
				_apply.getValue(_evalr.getEvaluator(), comp):
				_apply.getValue(_evalr.getEvaluator(), page);;
			try {
				if (o instanceof String) {
					final String s = (String)o;
					if (s.indexOf(',') >= 0)
						o = CollectionsX.parse(null, s, ',');
				}

				if (o instanceof Collection) {
					final Collection c = (Collection)o;
					int sz = c.size();
					switch (sz) {
					case 0: return null;
					case 1: o = c.iterator().next(); break;
					default: o = c.toArray(new Object[sz]); break;
					}
				}

				if (o instanceof Object[])
					return MultiComposer.getComposer(page, (Object[])o);

				if (o instanceof String) {
					final String clsnm = ((String)o).trim();
					o = page != null ? page.resolveClass(clsnm).newInstance():
						Classes.newInstanceByThread(clsnm);
				} else if (o instanceof Class)
					o = ((Class)o).newInstance();

				if (o instanceof Composer)
					return (Composer)o;
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex);
			}
			if (o != null)
				throw new UiException(Composer.class + " not implemented by "+o);
		}
		return null;
	}
	/** Returns the apply attribute that is the class that implements
	 * {@link Composer}, an instance of it or null.
	 *
	 * @since 3.0.0
	 * @see #getComposer
	 */
	public String getApply() {
		return _apply != null ? _apply.getRawValue(): null;
	}
	/** Sets the apply attribute that is used to initialize
	 * the component.
	 *
	 * @param apply the attribute which must be the class that implements
	 * {@link org.zkoss.zk.ui.util.Composer}, an instance of it, or null.
	 * El expressions are allowed, but self means the parent, if not null,
	 * or page, if root, (after all, the component is not created yet).
	 * @since 3.0.0
	 */
	public void setApply(String apply) {
		_apply = apply != null && apply.length() > 0 ?
			new ExValue(apply, Object.class): null;
	}

	/** Returns the forward condition that controls how to forward
	 * an event, that is received by the component created
	 * by this info, to another component.
	 *
	 * <p>Default: null.
	 *
	 * <p>If not null, when the component created by this
	 * info receives the event specified in the forward condition,
	 * it will forward it to the target component, which is also
	 * specified in the forward condition.
	 *
	 * @since 3.0.0
	 * @see #setForward
	 */
	public String getForward() {
		return _forward;
	}
	/** Sets the forward condition that controls when to forward
	 * an event receiving by this component to another component.
	 *
	 * <p>The basic format:<br/>
	 * <code>onEvent1=id1/id2.onEvent2</code>
	 *
	 * <p>It means when onEvent1 is received, onEvent2 will be posted
	 * to the component with the specified path (id1/id2).
	 *
	 * <p>If onEvent1 is omitted, it is assumed to be onClick (and
	 * the equal sign need not to be specified.
	 * If the path is omitted, it is assumed to be the space owner
	 * {@link Component#getSpaceOwner}.
	 *
	 * <p>For example, "onOK" means "onClick=onOK".
	 *
	 * <p>You can specify several forward conditions by separating
	 * them with comma as follows:
	 *
	 * <p><code>onChanging=onChanging,onChange=onUpdate,onOK</code>
	 *
	 * @param forward the forward condition. There are several forms:
	 * "onEvent1", "target.onEvent1" and "onEvent1(target.onEvent2)",
	 * where target could be "id", "id1/id2" or "${elExpr}".
	 * The EL expression must return either a path or a reference to
	 * a component.
	 * @since 3.0.0
	 */
	public void setForward(String forward) {
		_forward = forward != null && forward.length() > 0 ? forward: null;
	}

	/** Returns a readonly list of properties ({@link Property}) (never null).
	 * @since 2.4.0
	 */
	public List getProperties() {
		return _props != null ? _props: Collections.EMPTY_LIST;
			//it is better to protect with Collections.unmodifiableList
			//but for better performance...
	}
	/** Adds a property initializer.
	 * It will initialize a component when created with this info.
	 * @param name the member name. The component must have a valid setter
	 * for it.
	 * @param value the value. It might contain expressions (${}).
	 */
	public void addProperty(String name, String value, ConditionImpl cond) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("name");

		if (_props == null)
			_props = new LinkedList();
		_props.add(new Property(_evalr, name, value, cond));
	}

	/** Adds an event handler.
	 *
	 * @param name the event name.
	 * @param zscript the script.
	 */
	public void addEventHandler(String name, ZScript zscript, ConditionImpl cond) {
		if (name == null || zscript == null)
			throw new IllegalArgumentException("name and zscript cannot be null");
		//if (!Events.isValid(name))
		//	throw new IllegalArgumentException("Invalid event name: "+name);
			//AbstractParser has checked it, so no need to check again

		final EventHandler evthd = new EventHandler(_evalr, zscript, cond);
		if (_evthds == null)
			_evthds = new EventHandlerMap();
		_evthds.add(name, evthd);
	}

	/** Sets the effectiveness condition.
	 */
	public void setCondition(ConditionImpl cond) {
		_cond = cond;
	}

	/** Returns the forEach object if the forEach attribute is defined
	 * (or {@link #setForEach} is called).
	 *
	 * <p>If comp is not null, both pagedef and page are ignored.
	 * If comp is null, page must be specified.
	 *
	 * @param page the page. It is used only if comp is null.
	 * @param comp the component.
	 * @return the forEach object to iterate this info multiple times,
	 * or null if this info shall be interpreted only once.
	 */
	public ForEach getForEach(Page page, Component comp) {
		return _forEach == null ? null:
			comp != null ?
				ForEachImpl.getInstance(
					_evalr, comp, _forEach[0], _forEach[1], _forEach[2]):
				ForEachImpl.getInstance(
					_evalr, page, _forEach[0], _forEach[1], _forEach[2]);
	}
	/** Sets the forEach attribute, which is usually an expression.
	 * @param expr the expression to return a collection of objects, or
	 * null/empty to denote no iteration.
	 */
	public void setForEach(String expr, String begin, String end) {
		_forEach = expr != null && expr.length() > 0 ?
			new String[] {expr, begin, end}: null;
	}
	/** Returns whether the forEach condition is defined.
	 * @since 3.0.0
	 */
	public boolean withForEach() {
		return _forEach != null;
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
		ComponentsCtrl.setCurrentInfo(this);
		final Component comp;
		try {
			comp = _compdef.newInstance(page, _implcls);
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			ComponentsCtrl.setCurrentInfo((ComponentInfo)null);
		}
		if (comp instanceof DynamicTag)
			((DynamicTag)comp).setTag(_tag);
		return comp;
	}
	/** Resolves and returns the class for the component represented
	 * by this info (never null).
	 *
	 * <p>Unlike {@link #getImplementationClass},
	 * this method will resolve a class name (String) to a class (Class),
	 * if necessary.
	 *
	 * @param page the page to check whether the class is defined
	 * in its interpreters. Ignored if null.
	 * This method will search the class loader of the current thread.
	 * If not found, it will search the interpreters of the specifed
	 * page ({@link Page#getLoadedInterpreters}).
	 * Note: this method won't attach the component to the specified page.
	 * @exception ClassNotFoundException if the class not found
	 * @since 3.0.0
	 */
	public Class resolveImplementationClass(Page page)
	throws ClassNotFoundException {
		return _compdef.resolveImplementationClass(page, _implcls);
	}

	/** Returns the annotation map defined in this info, or null
	 * if no annotation is ever defined.
	 */
	public AnnotationMap getAnnotationMap() {
		return _annots;
	}

	/** Applies the event handlers, annotations, properties and
	 * custom attributes to the specified component.
	 *
	 * <p>It also invokes {@link ComponentDefinition#applyProperties}.
	 *
	 * @since 3.0.0
	 */
	public void applyProperties(Component comp) {
		_compdef.applyProperties(comp);

		if (_evthds != null)
			((ComponentCtrl)comp).addSharedEventHandlerMap(_evthds);

		if (_props != null) {
			for (Iterator it = _props.iterator(); it.hasNext();) {
				final Property prop = (Property)it.next();
				prop.assign(comp);
			}
		}
	}

	/** Evaluates and retrieves properties to the specified map from
	 * {@link ComponentDefinition} (and {@link ComponentInfo}).
	 *
	 * @param propmap the map to store the retrieved properties
	 * (String name, Object value).
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
			for (Iterator it = _props.iterator(); it.hasNext();) {
				final Property prop = (Property)it.next();
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

	/** Associates an annotation to this component info.
	 *
	 * @param annotName the annotation name (never null, nor empty).
	 * @param annotAttrs a map of attributes, or null if no attribute at all.
	 * The attribute must be in a pair of strings (String name, String value).
	 */
	public void addAnnotation(String annotName, Map annotAttrs) {
		if (_annots == null)
			_annots = new AnnotationMap();
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
		if (_annots == null)
			_annots = new AnnotationMap();
		_annots.addAnnotation(propName, annotName, annotAttrs);
	}

	//Condition//
	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(_evalr, comp);
	}
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(_evalr, page);
	}

	//NodeInfo//
	public PageDefinition getPageDefinition() {
		return _evalr.getPageDefinition();
	}
	public NodeInfo getParent() {
		return _parent;
	}
	protected EvaluatorRef getEvaluatorRef() {
		return _evalr;
	}

	//Cloneable//
	/** Clones this info.
	 * After cloned, {@link #getParent} is null.
	 */
	public Object clone() {
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
	//Object//
	public String toString() {
		final StringBuffer sb = new StringBuffer(64)
			.append("[ComponentInfo: ")
			.append(_compdef.getName());
		if (_tag != null)
			sb.append(" <").append(_tag).append('>');
		return sb.append(']').toString();
	}

	//Externalizable//
	public final void writeExternal(java.io.ObjectOutput out)
	throws java.io.IOException {
		if (getSerializingEvalRef() != _evalr) {
			pushSerializingEvalRef(_evalr);
			try {
				out.writeObject(_evalr);
				writeMembers(out);
			} finally {
				popSerializingEvalRef();
			}
		} else {
			out.writeObject(null); //to save space, don't need to write evalr
			writeMembers(out);
		}
	}
	/** Don't override this method. Rather, override {@link #readMembers}.
	 * @since 3.0.0
	 */
	public final void readExternal(java.io.ObjectInput in)
	throws java.io.IOException, ClassNotFoundException {
		_evalr = (EvaluatorRef)in.readObject();
		final EvaluatorRef top = getSerializingEvalRef();
		if (_evalr == null)
			_evalr = top;

		if (_evalr != null &&  top != _evalr) {
			pushSerializingEvalRef(_evalr);
			try {
				readMembers(in);
			} finally {
				popSerializingEvalRef();
			}
		} else {
			readMembers(in);
		}
	}
	private void writeMembers(java.io.ObjectOutput out)
	throws java.io.IOException {
		out.writeObject(_children);

		out.writeObject(_compdef);
		out.writeObject(_implcls);
		out.writeObject(_props);
		out.writeObject(_evthds);
		out.writeObject(_annots);
		out.writeObject(_tag);
		out.writeObject(_cond);
		out.writeObject(_fulfill);
		out.writeObject(_apply);
		out.writeObject(_forward);
		out.writeObject(_forEach);
	}
	private void readMembers(java.io.ObjectInput in)
	throws java.io.IOException, ClassNotFoundException {
		_children = (List)in.readObject();
		for (Iterator it = _children.iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof ComponentInfo)
				((ComponentInfo)o).setParentDirectly(this);
		}

		_compdef = (ComponentDefinition)in.readObject();
		_implcls = (String)in.readObject();
		_props = (List)in.readObject();
		_evthds = (EventHandlerMap)in.readObject();
		_annots = (AnnotationMap)in.readObject();
		_tag = (String)in.readObject();
		_cond = (ConditionImpl)in.readObject();
		_fulfill = (String)in.readObject();
		_apply = (ExValue)in.readObject();
		_forward = (String)in.readObject();
		_forEach = (String[])in.readObject();
	}

	/** Writes the evaluator reference.
	 * It is called by {@link EvalRefStub} to serialize
	 * the evaluator reference, in order to minimize the number of bytes
	 * to serialize.
	 */
	/*package*/ static final
	void writeEvalRef(java.io.ObjectOutputStream s, EvaluatorRef evalr)
	throws java.io.IOException {
		s.writeObject(getSerializingEvalRef() != evalr ? evalr: null);
	}
	/*package*/ static final
	EvaluatorRef readEvalRef(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		final EvaluatorRef evalr = (EvaluatorRef)s.readObject();
		return evalr != null ? evalr: getSerializingEvalRef();
	}

	/** Returns the evaluator reference of the info that is being serialized.
	 * It is used to minimize the bytes to write when serialized.
	 */
	private static final EvaluatorRef getSerializingEvalRef() {
		final List stack = (List)_evalRefStack.get();
		return stack == null || stack.isEmpty() ? null: (EvaluatorRef)stack.get(0);
	}
	/** Pushes the sepcified evaluator referene to be the current one.
	 */
	private static final void pushSerializingEvalRef(EvaluatorRef evalr) {
		List stack = (List)_evalRefStack.get();
		if (stack == null)
			_evalRefStack.set(stack = new LinkedList());
		stack.add(0, evalr);
	}
	/** Pops out the current evaluator reference.
	 */
	private static final void popSerializingEvalRef() {
		((List)_evalRefStack.get()).remove(0);
	}
	private static final ThreadLocal _evalRefStack = new ThreadLocal();
}
