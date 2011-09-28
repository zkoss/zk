/* ComponentInfo.java

	Purpose:
		
	Description:
		
	History:
		Tue May 31 11:27:13     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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

import static org.zkoss.lang.Generics.cast;
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
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.ui.impl.MultiComposer;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.EvaluatorRef;
import org.zkoss.zk.xel.ExValue;
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
public class ComponentInfo extends ForEachBranchInfo {
	private transient ComponentDefinition _compdef;
	/** The implemetation class/component (use). */
	private ExValue _impl;
	/** A list of {@link Property}, or null if no property at all. */
	private List<Property> _props;
	/** A Map of event handlers to handle events. */
	private EventHandlerMap _evthds;
	/** A list of event listeners for the peer widget. */
	private List<WidgetListener> _wgtlsns;
	/** A list of method/property overrides for the peer widget. */
	private List<WidgetOverride> _wgtovds;
	/** A list of DOM attributes for the peer widget. */
	private List<WidgetAttribute> _wgtattrs;
	/** the annotation map. Note: it doesn't include what are defined in _compdef. */
	private AnnotationMap _annots;
	/** The tag name for the dyanmic tag. Used only if this implements {@link DynamicTag}*/
	private String _tag;
	/** The fulfill condition.
	 */
	private String _fulfill;
	/** The apply attribute (parsed into an array of ExValue).
	 */
	private ExValue[] _apply;
	/** The forward condition.
	 */
	private String _forward;
	/** The widget class. */
	private ExValue _wgtcls;
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
		super(parent, null);

		if (compdef == null)
			throw new IllegalArgumentException();
		_compdef = compdef;
		_tag = tag;
	}
	/** Constructs the info about how to create a component that is not
	 * a dynamic tag.
	 * @param parent the parent; never null.
	 */
	public ComponentInfo(NodeInfo parent, ComponentDefinition compdef) {
		this(parent, compdef, null);
	}
	/** Constructs the info without a prent.
	 * Used only by {@link NativeInfo}.
	 */
	/*package*/ ComponentInfo(EvaluatorRef evalr, ComponentDefinition compdef,
	String tag) {
		if (compdef == null || evalr == null)
			throw new IllegalArgumentException();
		_evalr = evalr;
		_compdef = compdef;
		_tag = tag;
	}
	/** Used only by {@link DupComponentInfo}.
	 */
	private ComponentInfo(ComponentInfo compInfo) {
		super(compInfo);

		_compdef = compInfo._compdef;
		_impl = compInfo._impl;
		_tag = compInfo._tag;
		_fulfill = compInfo._fulfill;
		_apply = compInfo._apply;
		_forward = compInfo._forward;
		_replaceableText = compInfo._replaceableText;

		dupProps(compInfo);
	}
	/** Duplicates special properties to isolate the dependency. */
	private void dupProps(ComponentInfo compInfo) {
		if (compInfo._annots != null)
			_annots = (AnnotationMap)compInfo._annots.clone();
		if (compInfo._props != null)
			_props = new LinkedList<Property>(compInfo._props);
		if (compInfo._evthds != null)
			_evthds = (EventHandlerMap)compInfo._evthds.clone();
		if (compInfo._wgtlsns != null)
			_wgtlsns = new LinkedList<WidgetListener>(compInfo._wgtlsns);
		if (compInfo._wgtovds != null)
			_wgtovds = new LinkedList<WidgetOverride>(compInfo._wgtovds);
		if (compInfo._wgtattrs != null)
			_wgtattrs = new LinkedList<WidgetAttribute>(compInfo._wgtattrs);
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

	/** Adds a child.
	 *
	 * @exception IllegalStateException if this is not an instance of
	 * {@link NativeInfo} and the child is {@link TextInfo}.
	 */
	public void appendChild(NodeInfo child) {
		if ((child instanceof TextInfo) && !(this instanceof NativeInfo))
			throw new IllegalStateException("TextInfo cannot be a child of "+this);
		super.appendChild(child);
	}

	/** Returns the tag name, or null if no tag name.
	 * @since 3.0.0
	 */
	public String getTag() {
		return _tag;
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
	 * If comp is null, the page is used as the parent component.
	 * If comp is not null, it is used as the self variable.
	 * @see #getApply
	 * @since 3.5.0
	 */
	public Composer resolveComposer(Page page, Component comp) {
		ExValue[] defapply = _compdef.getParsedApply();
		if (_apply == null && defapply == null)
			return null;

		try {
			List<Composer> composers = new LinkedList<Composer>();
			Evaluator eval = getEvaluator();
			toComposers(composers, defapply, eval, page, comp);
			toComposers(composers, _apply, eval, page, comp);

			return MultiComposer.getComposer(page,
				composers.toArray(new Composer[composers.size()]));
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	private static void toComposers(List<Composer> composers, ExValue[] apply,
	Evaluator eval, Page page, Component comp)
	throws Exception {
		if (apply != null)
			for (int j = 0; j < apply.length; ++j)
				toComposer(composers, page,
					comp != null ?
						apply[j].getValue(eval, comp):
						apply[j].getValue(eval, page));
	}
	private static void toComposer(List<Composer> composers, Page page, Object o)
	throws Exception {
		if (o instanceof String) {
			final String s = (String)o;
			if (s.indexOf(',') >= 0)
				o = CollectionsX.parse(null, s, ','); //No EL
		}

		if (o instanceof Collection) {
			for (Iterator it = ((Collection)o).iterator(); it.hasNext();) {
				final Composer cp = org.zkoss.zk.ui.impl.Utils.newComposer(page, it.next());
				if (cp != null)
					composers.add(cp);
			}
			return;
		}

		if (o instanceof Object[]) {
			final Object[] ary = (Object[])o;
			for (int j = 0; j < ary.length; ++j) {
				final Composer cp = org.zkoss.zk.ui.impl.Utils.newComposer(page, ary[j]);
				if (cp != null)
					composers.add(cp);
			}
			return;
		}

		final Composer cp = org.zkoss.zk.ui.impl.Utils.newComposer(page, o);
		if (cp != null)
			composers.add(cp);
	}
	/** Returns the apply attribute that is a list of {@link Composer} class
	 * names or EL expressions returning classes, class names or composer
	 * instances, or null if no apply attribute.
	 *
	 * @since 3.0.0
	 * @see #resolveComposer
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
	 * or EL expressions returning classes, class names or composer instances.
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
	public List<Property> getProperties() {
		if (_props != null)
			return _props;
		return Collections.emptyList();
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
			_props = new LinkedList<Property>();
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
			_props = new LinkedList<Property>();
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
	public Set<String> getEventHandlerNames() {
		if (_evthds != null)
			return _evthds.getEventNames();
		return Collections.emptySet();
	}
	/** Adds an event listener for the peer widget.
	 * @since 5.0.0
	 */
	public void addWidgetListener(String name, String script, ConditionImpl cond) {
		final WidgetListener listener =
			new WidgetListener(_evalr, name, script, cond);
		if (_wgtlsns == null)
			_wgtlsns = new LinkedList<WidgetListener>();
		_wgtlsns.add(listener);
	}
	/** Adds a method or a value to the peer widget.
	 * If there was a method with the same name, it will be renamed to
	 * <code>$<i>name</i></code> so can you access it for callback purpose.
	 * <pre><code>&lt;label w:setValue="function (value) {
	 *  this.$setValue(value); //old method
	 *}"/&gt;</code></pre>
	 * @param script the client side script. EL expressions are allowed.
	 * @see #addWidgetAttribute
	 * @since 5.0.0
	 */
	public void addWidgetOverride(String name, String script, ConditionImpl cond) {
		final WidgetOverride mtd =
			new WidgetOverride(_evalr, name, script, cond);
		if (_wgtovds == null)
			_wgtovds = new LinkedList<WidgetOverride>();
		_wgtovds.add(mtd);
	}
	/** Adds a custom DOM attribute to the peer widget.
	 * <p>Unlike {@link #addWidgetOverride}, the attributes added here are
	 * generated directly as DOM attributes at the client.
	 * In other words, it is not a property or method of the peer widget.
	 * @param name the name of the attribute.
	 * Unlike {@link #addWidgetOverride}, the name might contain
	 * no alphanumeric characters, such as colon and dash.
	 * @since 5.0.3
	 * @see #addWidgetOverride
	 */
	public void addWidgetAttribute(String name, String value, ConditionImpl cond) {
		final WidgetAttribute attr =
			new WidgetAttribute(_evalr, name, value, cond);
		if (_wgtattrs == null)
			_wgtattrs = new LinkedList<WidgetAttribute>();
		_wgtattrs.add(attr);
	}

	/** Sets the widget class.
	 * @param wgtcls the widget class (at the client side).
	 * EL expressions are allowed.
	 * @since 5.0.2
	 */
	public void setWidgetClass(String wgtcls) {
		_wgtcls = wgtcls != null && wgtcls.length() > 0 ?
			new ExValue(wgtcls, String.class): null;
	}
	/** Returns the widget class (might contain EL expressions), or null
	 * if not available.
	 * @since 5.0.2
	 */
	public String getWidgetClass() {
		return _wgtcls != null ? _wgtcls.getRawValue(): null;
	}
	/** Resolves the widget class, or null if the default is expected.
	 * It will evaluate EL expressions if any.
	 * <p>You rarely need to invoke this method since it is called
	 * automatically when {@link #applyProperties} is called.
	 * @param comp the component that the widget class represents at the client.
	 * @since 5.0.2
	 */
	public String resolveWidgetClass(Component comp) {
		return _wgtcls != null ? (String)_wgtcls.getValue(getEvaluator(), comp): null;
	}

	/** Returns the class name or an expression returning a class instance,
	 * a class name, or a component.
	 * It is the same value that {@link #setImplementation} was called.
	 * To resolve the real implementation class, use
	 * {@link #resolveImplementationClass}.
	 *
	 * <p>Notice that, if a component is returned by the expression,
	 * it shall not be assigned to any page.
	 * @since 3.6.0
	 */
	public String getImplementation() {
		return _impl != null ? _impl.getRawValue(): null;
	}
	/** Sets the string that implements the component.
	 *
	 * @param expr the class name, or an expression returning a class instance,
	 * a class name,  or a component instance.
	 * @since 3.6.0
	 */
	public void setImplementation(String expr) {
		_impl = expr != null && expr.length() > 0 ?
			new ExValue(expr, Object.class): null;
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
		Object impl = evalImpl(page, parent);
		ComponentsCtrl.setCurrentInfo(this);
		final Component comp;
		try {
			if (impl instanceof Class) {
				Class<? extends Component> cls = cast((Class)impl);
				comp = _compdef.newInstance(cls);
			} else {
				comp = impl instanceof Component ? (Component)impl:
					_compdef.newInstance(page, (String)impl);
			}
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
	private Object evalImpl(Page page, Component parent) {
		return _impl == null ? null:
			parent != null ?
				_impl.getValue(getEvaluator(), parent):
				_impl.getValue(getEvaluator(), page);
	}
	/** Creates an component based on this info (never null).
	 * It is the same as newInstance(page, null).
	 *
	 * <p>If the implementation class ({@link #getImplementation})
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
	 * <p>Unlike {@link #getImplementation},
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
		Object impl = evalImpl(page, parent);
		return impl instanceof Class ? (Class)impl:
			impl instanceof Component ? impl.getClass():
			_compdef.resolveImplementationClass(page, (String)impl);
	}
	/** Resolves and returns the class for the component represented
	 * by this info (never null).
	 * It is the same as resolveImplementationClass(page, null).
	 *
	 * <p>If the implementation class ({@link #getImplementation})
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
	 * <p>Note: custom attributes are <i>not</i> part of {@link ComponentInfo},
	 * so they won't be applied here.
	 *
	 * <p>Note: annotations are applied to the component when a component
	 * is created. So, this method doesn't and need not to copy them.
	 * See also {@link org.zkoss.zk.ui.AbstractComponent#AbstractComponent}.
	 *
	 * <p>Note: the widget class ({@link #setWidgetClass}) is set by this method.
	 *
	 * @since 3.0.0
	 */
	public void applyProperties(Component comp) {
		_compdef.applyProperties(comp);

		if (_evthds != null)
			((ComponentCtrl)comp).addSharedEventHandlerMap(_evthds);

		if (_props != null)
			for (Property prop: _props)
				prop.assign(comp);

		if (_wgtlsns != null)
			for (WidgetListener wl: _wgtlsns)
				wl.assign(comp);

		if (_wgtovds != null)
			for (WidgetOverride wo: _wgtovds)
				wo.assign(comp);

		if (_wgtattrs != null)
			for (WidgetAttribute wa: _wgtattrs)
				wa.assign(comp);

		comp.setWidgetClass(resolveWidgetClass(comp));
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
	public Map<String, Object> evalProperties(Map<String, Object> propmap, Page owner, Component parent,
	boolean defIncluded) {
		if (defIncluded)
			propmap = _compdef.evalProperties(propmap, owner, parent);
		else if (propmap == null)
			propmap = new HashMap<String, Object>();

		if (_props != null) {
			for (Property prop: _props) {
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
	 * The attribute must be in a pair of strings (String name, String value),
	 * or (String name, String[] value).
	 */
	public void addAnnotation(String annotName, Map<String, Object> annotAttrs) {
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
	 * The attribute must be in a pair of strings (String name, String value),
	 * or (String name, String[] value).
	 */
	public void addAnnotation(String propName, String annotName, Map<String, Object> annotAttrs) {
		if (_annots == null)
			_annots = new AnnotationMap();
		_annots.addAnnotation(propName, annotName, annotAttrs);
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

	//Object//
	public String toString() {
		final StringBuffer sb = new StringBuffer(64)
			.append('[')
			.append(this instanceof NativeInfo ? "NativeInfo": "ComponentInfo");
		if (_compdef != null)
			sb.append(": ").append(_compdef.getName());
		if (_tag != null)
			sb.append(" <").append(_tag).append('>');
		return sb.append(']').toString();
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		final LanguageDefinition langdef = _compdef.getLanguageDefinition();
		if (langdef != null) {
			s.writeObject(langdef.getName());
			s.writeObject(_compdef.getName());
		} else {
			s.writeObject(_compdef);
		}
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		final Object v = s.readObject();
		if (v instanceof String) {
			final LanguageDefinition langdef = LanguageDefinition.lookup((String)v);
			_compdef = langdef.getComponentDefinition((String)s.readObject());
		} else {
			_compdef = (ComponentDefinition)v;
		}
	}

	private static class DupComponentInfo extends ComponentInfo {
		private DupComponentInfo(ComponentInfo compInfo) {
			super(compInfo);
		}

		public void appendChild(NodeInfo child) {
			throw new UnsupportedOperationException();
		}
		public boolean removeChild(NodeInfo child) {
			throw new UnsupportedOperationException();
		}
	}
}
