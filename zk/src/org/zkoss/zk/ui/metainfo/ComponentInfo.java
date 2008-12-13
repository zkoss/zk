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
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.impl.EvaluatorRef;
import org.zkoss.zk.xel.impl.Utils;

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
	private transient ComponentDefinition _compdef;
	/** The implemetation class (use). */
	private ExValue _implcls;
	/** A list of {@link Property}, or null if no property at all. */
	private List _props;
	/** A Map of event handlers to handle events. */
	private EventHandlerMap _evthds;
	/** A list of event listeners for the peer widget. */
	private List _wgtlsns;
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
	/** The apply attribute (parsed into an array of ExValue).
	 */
	private ExValue[] _apply;
	/** The forward condition.
	 */
	private String _forward;
	/** The forEach content, i.e., what to iterate.
	 */
	private ExValue[] _forEach;
	/** The forEach info: [forEachBegin, forEachEnd].
	 */
	private ExValue[] _forEachInfo;
	private String _replaceableText;

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
			throw new IllegalArgumentException();

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
	/** Constructs the info that doesn't have a parent.
	 * @since 3.5.0
	 */
	public ComponentInfo(EvaluatorRef evalr, ComponentDefinition compdef,
	String tag) {
		if (compdef == null)
			throw new IllegalArgumentException();
		_compdef = compdef;
		_tag = tag;
		_evalr = evalr;
	}
	/** Used only by {@link DupComponentInfo}.
	 */
	private ComponentInfo(ComponentInfo compInfo) {
		_parent = compInfo._parent; //direct copy since it is 'virtual'
		_children = compInfo._children;
		_evalr = compInfo._evalr;
		_compdef = compInfo._compdef;
		_implcls = compInfo._implcls;
		_tag = compInfo._tag;
		_cond = compInfo._cond;
		_fulfill = compInfo._fulfill;
		_apply = compInfo._apply;
		_forward = compInfo._forward;
		_forEach = compInfo._forEach;
		_forEachInfo = compInfo._forEachInfo;
		_replaceableText = compInfo._replaceableText;

		dupProps(compInfo);
	}
	/** Duplicates special properties to isolate the dependency. */
	private void dupProps(ComponentInfo compInfo) {
		if (compInfo._annots != null)
			_annots = (AnnotationMap)compInfo._annots.clone();
		if (compInfo._props != null)
			_props = new LinkedList(compInfo._props);
		if (compInfo._evthds != null)
			_evthds = (EventHandlerMap)compInfo._evthds.clone();
		if (compInfo._wgtlsns != null)
			_wgtlsns = new LinkedList(compInfo._wgtlsns);
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

	/** Adds a Sting child.
	 * Note: it is callable only if this is an instance of {@link NativeInfo}
	 * or {@link #getComponentDefinition} is {@link ComponentDefinition#ZK}.
	 *
	 * @exception IllegalStateException if this is not an instance of
	 * {@link NativeInfo}, nor {@link #getComponentDefinition} is not
	 * {@link ComponentDefinition#ZK}.
	 * @since 3.5.0
	 */
	public void appendChild(TextInfo text) {
		if (!(this instanceof NativeInfo) && !(this instanceof ZkInfo))
			throw new IllegalStateException("NativeInfo or <zk> required");
		appendChildDirectly(text);
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
	/** Returns whether to preserve the blank text.
	 * If false, the blank text (a non-empty string consisting of whitespaces)
	 * are ignored.
	 * If true, they are converted to a label child.
	 * <p>Default: false.
	 * @since 3.5.0
	 */
	public boolean isBlankPreserved() {
		return _compdef.isBlankPreserved();
	}
	/** Returns the replaceable text, nor null if it cannot be replaced
	 * with a text.
	 *
	 * <p>By replaceable text we mean the component can be replaced by
	 * a text (aka., string).
	 * ZK uses it to optimize the output by generating some content
	 * directly.
	 *
	 * <p>The replaceable text must be a string consisting of whitespaces only.
	 *
	 * @see org.zkoss.zk.ui.ext.render.PrologAllowed
	 * @since 3.5.0 
	 */
	public String getReplaceableText() {
		return _replaceableText;
	}
	/** Sets the replaceable text.
	 *
	 * @param text the text that can be used to replace the component
	 * being generated by this info. If null, it means no way to
	 * replace it with text.
	 * @see org.zkoss.zk.ui.ext.render.PrologAllowed
	 * @since 3.5.0 
	 */
	public void setReplaceableText(String text) {
		_replaceableText = text;
	}

	/** Returns the fulfill condition that controls when to create
	 * the child components, or null if the child components
	 * are created at the time when the page is loaded.
	 *
	 * <p>Default: null.
	 *
	 * <p>There are several forms:<br/>
	 * "eventName", "targetId.evetName", "id1/id2.evetName",
	 * and "${elExpr}.eventName".
	 * <p>Since 3.0.2, you can specify a list of fulfill conditions by
	 * separating them with comma. For example:<br/>
	 * "id1.event1, id2/id3.event2"
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
	 * @param fulfill the fulfill condition. There are several forms:<br/>
	 * "eventName", "targetId.evetName", "id1/id2.evetName",
	 * and "${elExpr}.eventName".
	 * <p>Since 3.0.2, you can specify a list of fulfill conditions by
	 * separating them with comma. For example:<br/>
	 * "id1.event1, id2/id3.event2"
	 * <p>Since 3.5.0, you can specify the URI to fulfill with. For example:<br/>
	 * "id1.event1=/my/super.zul".
	 * @since 2.4.0
	 */
	public void setFulfill(String fulfill) {
		_fulfill = fulfill != null && fulfill.length() > 0 ? fulfill: null;
	}

	/** Returns the composer for this info, or nuull if not available.
	 *
	 * @param comp the component used as the self variable to resolve
	 * EL expressions, if any.
	 * Notice that UI engine uses the parent component for this argument.
	 * If comp is null, it is the same as {@link #getComposer(Page)}.
	 * If comp is not null, it is used as the self variable.
	 * @see #getApply
	 * @since 3.5.0
	 */
	public Composer resolveComposer(Page page, Component comp) {
		if (_apply == null)
			return null;

		try {
			List composers = new LinkedList();
			for (int j = 0; j < _apply.length; ++j)
				toComposer(composers, page,
					comp != null ?
						_apply[j].getValue(getEvaluator(), comp):
						_apply[j].getValue(getEvaluator(), page));

			return MultiComposer.getComposer(page,
				(Composer[])composers.toArray(new Composer[composers.size()]));
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	/** @deprecated As of release 3.5.0, replaced with {@link #resolveComposer}.
	 */
	public Composer getComposer(Page page) {
		return getComposer(page, null);
	}
	/** @deprecated As of release 3.5.0, replaced with {@link #resolveComposer}.
	 */
	public Composer getComposer(Page page, Component comp) {
		return resolveComposer(page, comp);
	}
	private static void toComposer(List composers, Page page, Object o)
	throws Exception {
		if (o instanceof String) {
			final String s = (String)o;
			if (s.indexOf(',') >= 0)
				o = CollectionsX.parse(null, s, ','); //No EL
		}

		if (o instanceof Collection) {
			for (Iterator it = ((Collection)o).iterator(); it.hasNext();) {
				final Composer cp = toComposer(page, it.next());
				if (cp != null)
					composers.add(cp);
			}
			return;
		}

		if (o instanceof Object[]) {
			final Object[] ary = (Object[])o;
			for (int j = 0; j < ary.length; ++j) {
				final Composer cp = toComposer(page, ary[j]);
				if (cp != null)
					composers.add(cp);
			}
			return;
		}

		final Composer cp = toComposer(page, o);
		if (cp != null)
			composers.add(cp);
	}
	private static Composer toComposer(Page page, Object o)
	throws Exception {
		if (o instanceof String) {
			final String clsnm = ((String)o).trim();
			o = page != null ? page.resolveClass(clsnm).newInstance():
				Classes.newInstanceByThread(clsnm);
		} else if (o instanceof Class) {
			o = ((Class)o).newInstance();
		}
		return (Composer)o;
	}
	/** Returns the apply attribute that is a list of {@link Composer} class
	 * names or EL expressions, or null if no apply attribute.
	 *
	 * @since 3.0.0
	 * @see #getComposer
	 */
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
	/** Sets the apply attribute that is is a list of {@link Composer} class
	 * or EL expressions.
	 *
	 * @param apply the attribute this is a list of {@link Composer} class
	 * or EL expressions
	 * El expressions are allowed, but self means the parent, if available;
	 * or page, if no parent at all. (Note: the component is not created yet
	 * when the apply attribute is evaluated).
	 * @since 3.0.0
	 */
	public void setApply(String apply) {
		_apply = Utils.parseList(apply, Object.class, true);
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
			throw new IllegalArgumentException("name cannot be empty");

		if (_props == null)
			_props = new LinkedList();
		_props.add(new Property(_evalr, name, value, cond));
	}
	/** Adds a property initializer based on the native content.
	 * The native content is a XML fragment represented by {@link NativeInfo}.
	 *
	 * @param value the property value represented by {@link NativeInfo}.
	 * @since 3.5.0
	 */
	public void addProperty(String name, NativeInfo value, ConditionImpl cond) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("name cannot be empty");

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
	/** Returns a readonly collection of event names (String),
	 * or an empty collection if no event name is registered.
	 *
	 * <p>To add an event handler, use {@link #addEventHandler} instead.
	 *
	 * @since 3.0.2
	 */
	public Set getEventHandlerNames() {
		return _evthds != null ? _evthds.getEventNames(): Collections.EMPTY_SET;
	}
	/** Adds an event listener for the peer widget.
	 * @since 5.0.0
	 */
	public void addWidgetListener(String name, String script, ConditionImpl cond) {
		final WidgetListener evthd =
			new WidgetListener(_evalr, name, script, cond);
		if (_wgtlsns == null)
			_wgtlsns = new LinkedList();
		_wgtlsns.add(evthd);
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
	 * @since 3.5.0
	 */
	public ForEach resolveForEach(Page page, Component comp) {
		return _forEach == null ? null:
			comp != null ?
				ForEachImpl.getInstance(
					_evalr, comp, _forEach, _forEachInfo[0], _forEachInfo[1]):
				ForEachImpl.getInstance(
					_evalr, page, _forEach, _forEachInfo[0], _forEachInfo[1]);
	}
	/** @deprecated As of release 3.5.0, replaced with {@link #resolveForEach}.
	 */
	public ForEach getForEach(Page page, Component comp) {
		return resolveForEach(page, comp);
	}
	/** Sets the forEach attribute, which is usually an expression.
	 * @param expr the expression to return a collection of objects, or
	 * null/empty to denote no iteration.
	 */
	public void setForEach(String expr, String begin, String end) {
		_forEach = Utils.parseList(expr, Object.class, false);
			//forEach="" means to iterate a single-element array and the value
			//is empty
		_forEachInfo = _forEach == null ? null:
			new ExValue[] {
				begin != null && begin.length() > 0 ?
					new ExValue(begin, Integer.class): null,
				end != null && end.length() > 0 ?
					new ExValue(end, Integer.class): null};
	}
	/** Returns whether the forEach condition is defined.
	 * @since 3.0.0
	 */
	public boolean withForEach() {
		return _forEach != null;
	}

	/** Returns the class name (String) that implements the component.
	 * It is actually the value of the use attribute which might contains
	 * EL expressions.
	 * To resolve the real implementation class, use
	 * {@link #resolveImplementationClass}.
	 */
	public String getImplementationClass() {
		return _implcls != null ? _implcls.getRawValue(): null;
	}
	/** Sets the class name to implements the component.
	 */
	public void setImplementationClass(String clsnm) {
		_implcls = clsnm != null && clsnm.length() > 0 ?
			new ExValue(clsnm, Object.class): null;
	}

	/** Creates an component based on this info (never null).
	 *
	 * <p>Like {@link ComponentDefinition#newInstance},
	 * this method doesn't invoke {@link #applyProperties}.
	 * It is caller's job to invoke them if necessary.
	 * Since the value of properties might depend on the component tree,
	 * it is better to assign the component with a proper parent
	 * before calling {@link #applyProperties}.
	 *
	 * @since 3.0.2
	 */
	public Component newInstance(Page page, Component parent) {
		Object implcls = evalImplClass(page, parent);
		ComponentsCtrl.setCurrentInfo(this);
		final Component comp;
		try {
			comp = implcls instanceof Class ?
				_compdef.newInstance((Class)implcls):
				_compdef.newInstance(page, (String)implcls);
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			ComponentsCtrl.setCurrentInfo((ComponentInfo)null);
		}
		if (comp instanceof DynamicTag)
			((DynamicTag)comp).setTag(_tag);
		return comp;
	}
	/** Evaluates the implementation claas, and rerturn either a class (Class),
	 * a class name (String), or null.
	 */
	private Object evalImplClass(Page page, Component parent) {
		return _implcls == null ? null:
			parent != null ?
				_implcls.getValue(getEvaluator(), parent):
				_implcls.getValue(getEvaluator(), page);
	}
	/** Creates an component based on this info (never null).
	 * It is the same as newInstance(page, null).
	 *
	 * <p>If the implementation class ({@link #getImplementationClass})
	 * doesn't have any EL expression, or its EL expresson doesn't
	 * referece to the self variable, the result is the same.
	 *
	 * <p>This method is preserved for backward compatibility.
	 * It is better to use {@link #newInstance(Page, Component)}.
	 */
	public Component newInstance(Page page) {
		return newInstance(page, null);
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
	 * @since 3.0.2
	 */
	public Class resolveImplementationClass(Page page, Component parent)
	throws ClassNotFoundException {
		Object implcls = evalImplClass(page, parent);
		return implcls instanceof Class ? (Class)implcls:
			_compdef.resolveImplementationClass(page, (String)implcls);
	}
	/** Resolves and returns the class for the component represented
	 * by this info (never null).
	 * It is the same as resolveImplementationClass(page, null).
	 *
	 * <p>If the implementation class ({@link #getImplementationClass})
	 * doesn't have any EL expression, or its EL expresson doesn't
	 * referece to the self variable, the result is the same.
	 *
	 * <p>This method is preserved for backward compatibility.
	 * It is better to use {@link #resolveImplementationClass(Page, Component)}.
	 * @since 3.0.0
	 */
	public Class resolveImplementationClass(Page page)
	throws ClassNotFoundException {
		return resolveImplementationClass(page, null);
	}

	/** Returns the annotation map defined in this info, or null
	 * if no annotation is ever defined.
	 */
	public AnnotationMap getAnnotationMap() {
		return _annots;
	}

	/** Applies the event handlers and properties to the specified component.
	 *
	 * <p>It also invokes {@link ComponentDefinition#applyProperties}.
	 *
	 * <p>Note: custom attributes are not part of {@link ComponentInfo},
	 * so they won't be applied here.
	 *
	 * <p>Note: annotations are applied to the component when a component
	 * is created. So, this method doesn't and need not to copy them.
	 * See also {@link org.zkoss.zk.ui.AbstractComponent#AbstractComponent}.
	 *
	 * @since 3.0.0
	 */
	public void applyProperties(Component comp) {
		_compdef.applyProperties(comp);

		if (_evthds != null)
			((ComponentCtrl)comp).addSharedEventHandlerMap(_evthds);

		if (_wgtlsns != null)
			for (Iterator it = _wgtlsns.iterator(); it.hasNext();)
				((WidgetListener)it.next()).assign(comp);

		if (_props != null)
			for (Iterator it = _props.iterator(); it.hasNext();)
				((Property)it.next()).assign(comp);
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

	/** Returns the evaluator.
	 * @since 3.5.0
	 */
	public Evaluator getEvaluator() {
		return _evalr.getEvaluator();
	}

	/** Duplicates the specified component info but retaining
	 * the same but virtual parent-child relationship.
	 * It is designed to use with
	 * {@link org.zkoss.zk.ui.util.ComposerExt#doBeforeCompose}
	 * to override some properties of the default component info.
	 *
	 * <p>Unlike {@link #clone}, the parent-child relation is duplicated
	 * but it is 'virtual'. By virtual we mean
	 * all the children's parent doesn't reference to 
	 * the duplicated info (the returned instance).
	 * Rather, they reference to the original info being duplicated.
	 *
	 * <p>Since the parent-children relation of the returned info is 'virtual',
	 * you can not call {@link #appendChild} or others to change it.
	 * If you need to change the parent-children relation, use {@link #clone}
	 * instead, and then clone the required children.
	 *
	 * <p>Notice, we actually copy the values to the returned info
	 * so any change to the original one doesn't affect the duplicated info.
	 *
	 * @since 3.0.8
	 * @see #clone
	 */
	public ComponentInfo duplicate() {
		return new DupComponentInfo(this);
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
	 * The children (@{link #getChildren}) is not cloned, either.
	 * Thus, it is better to use {@link #duplicate} with
	 * {@link org.zkoss.zk.ui.util.ComposerExt#doBeforeCompose}
	 * if you want to override some properties.
	 */
	public Object clone() {
		try {
			final ComponentInfo info = (ComponentInfo)super.clone();
			info._parent = null;
			info.dupProps(this);
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

		final LanguageDefinition langdef = _compdef.getLanguageDefinition();
		if (langdef != null) {
			out.writeObject(langdef.getName());
			out.writeObject(_compdef.getName());
		} else {
			out.writeObject(_compdef);
		}

		out.writeObject(_implcls);
		out.writeObject(_props);
		out.writeObject(_evthds);
		out.writeObject(_wgtlsns);
		out.writeObject(_annots);
		out.writeObject(_tag);
		out.writeObject(_cond);
		out.writeObject(_fulfill);
		out.writeObject(_apply);
		out.writeObject(_forward);
		out.writeObject(_forEach);
		out.writeObject(_forEachInfo);
	}
	private void readMembers(java.io.ObjectInput in)
	throws java.io.IOException, ClassNotFoundException {
		_children = (List)in.readObject();
		for (Iterator it = _children.iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof ComponentInfo)
				((ComponentInfo)o).setParentDirectly(this);
		}

		final Object v = in.readObject();
		if (v instanceof String) {
			final LanguageDefinition langdef = LanguageDefinition.lookup((String)v);
			_compdef = langdef.getComponentDefinition((String)in.readObject());
		} else {
			_compdef = (ComponentDefinition)v;
		}

		_implcls = (ExValue)in.readObject();
		_props = (List)in.readObject();
		_evthds = (EventHandlerMap)in.readObject();
		_wgtlsns = (List)in.readObject();
		_annots = (AnnotationMap)in.readObject();
		_tag = (String)in.readObject();
		_cond = (ConditionImpl)in.readObject();
		_fulfill = (String)in.readObject();
		_apply = (ExValue[])in.readObject();
		_forward = (String)in.readObject();
		_forEach = (ExValue[])in.readObject();
		_forEachInfo = (ExValue[])in.readObject();
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

	private static class DupComponentInfo extends ComponentInfo {
		private DupComponentInfo(ComponentInfo compInfo) {
			super(compInfo);
		}

		public void appendChild(ZScript zscript) {
			throw new UnsupportedOperationException();
		}
		public void appendChild(VariablesInfo variables) {
			throw new UnsupportedOperationException();
		}
		public void appendChild(AttributesInfo custAttrs) {
			throw new UnsupportedOperationException();
		}
		public void appendChild(ComponentInfo compInfo) {
			throw new UnsupportedOperationException();
		}
		public boolean removeChild(ZScript zscript) {
			throw new UnsupportedOperationException();
		}
		public boolean removeChild(VariablesInfo variables) {
			throw new UnsupportedOperationException();
		}
		public boolean removeChild(AttributesInfo custAttrs) {
			throw new UnsupportedOperationException();
		}
		public boolean removeChild(ComponentInfo compInfo) {
			throw new UnsupportedOperationException();
		}
		/*pacakge*/ void appendChildDirectly(Object child) {
			throw new UnsupportedOperationException();
		}
		/*package*/ boolean removeChildDirectly(Object child) {
			throw new UnsupportedOperationException();
		}
		public void appendChild(TextInfo text) {
			throw new UnsupportedOperationException();
		}
	}
}
