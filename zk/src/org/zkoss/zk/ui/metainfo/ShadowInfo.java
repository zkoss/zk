/** ShadowInfo.java.

	Purpose:
		
	Description:
		
	History:
		3:04:11 PM Oct 23, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.metainfo;

import static org.zkoss.lang.Generics.cast;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Location;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.DynamicPropertied;
import org.zkoss.zk.ui.ext.DynamicTag;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.impl.Utils;

/**
 * Represent a shadow element.
 * @author jumperchen
 * @since 8.0.0
 */
public class ShadowInfo extends BranchInfo {
	private transient ComponentDefinition _compdef;
	/** A list of {@link Property}, or null if no property at all. */
	private List<Property> _props;
	private final String _tag;
	/** the annotation map. Note: it doesn't include what are defined in _compdef. */
	private AnnotationMap _annots;

	/** Creates a shadow.
	 *
	 * @param parent the parent node (never null)
	 * @param compdef the component definition; never null
	 * @param tag the tag name; Note: if component implements
	 * {@link DynamicTag}, this argument must be specified.
	 * If {@link DynamicTag} is not implemented, this argument is optional.
	 * @param params the map of attribute. Ignored if null.
	 */
	public ShadowInfo(NodeInfo parent, ComponentDefinition compdef, String tag, ConditionImpl cond) {
		super(parent, cond);
		_tag = tag;
		if (compdef == null)
			throw new IllegalArgumentException();
		_compdef = compdef;
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
	 * <p>Note: annotations are applied to the component when a component
	 * is created. So, this method doesn't and need not to copy them.
	 * See also {@link org.zkoss.zk.ui.AbstractComponent#AbstractComponent}.
	 *
	 * <p>Note: the widget class ({@link #setWidgetClass}) is set by this method.
	 *
	 */
	public void applyProperties(Component comp) {
		_compdef.applyProperties(comp);
	}

	/** Adds an annotation to the specified property of this component
	 * info.
	 *
	 * @param propName the property name.
	 * If null, the annotation is associated with the whole component rather than
	 * a particular property.
	 * @param annotName the annotation name (never null, nor empty).
	 * @param annotAttrs a map of attributes, or null if no attribute at all.
	 * The attribute must be in a pair of strings (String name, String value),
	 * or (String name, String[] value).
	 * @param loc the location information of the annotation in
	 * the document, or null if not available.
	 */
	public void addAnnotation(String propName, String annotName,
	Map<String, String[]> annotAttrs, Location loc) {
		if (_annots == null)
			_annots = new AnnotationMap();
		_annots.addAnnotation(propName, annotName, annotAttrs, loc);
	}
	
	/** Applies the custom attributes.
	 * <p>Note: this method does nothing if {@link #isEffective} returns false.
	 */
	public void apply(Component comp) {
		if (_props != null && isEffective(comp)) {
			for (Property prop : _props) {
				prop.assign(comp);
			}
		}
	}

	/** Creates a shadow element based on this info (never null).
	 * It is the same as newInstance(page, null).
	 *
	 * <p>If the implementation class ({@link #getImplementation})
	 * doesn't have any EL expression, or its EL expression doesn't
	 * have reference to the self variable, the result is the same.
	 *
	 * <p>This method is preserved for backward compatibility.
	 * It is better to use {@link #newInstance(Page, Component)}.
	 */
	public Component newInstance(Page page) {
		return newInstance(page, null);
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
	 */
	public Component newInstance(Page page, Component parent) {
		ComponentsCtrl.setCurrentInfo(this);
		final Component comp;
		try {
			comp = _compdef.newInstance(page, null); // use the default one
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			ComponentsCtrl.setCurrentInfo((ComponentInfo)null);
		}
		return comp;
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


	/** Returns a readonly list of properties ({@link Property}) (never null).
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

	/** Adds a child.
	 *
	 * @exception IllegalStateException if this is not an instance of
	 * {@link TemplateInfo} and {@link ShadowInfo}.
	 */
	public void appendChild(NodeInfo child) {
		if (!((child instanceof ShadowInfo) || (child instanceof TemplateInfo)))
			throw new IllegalStateException("Only accept template and shadow element to be a child of "+this);
		super.appendChild(child);
	}

	//Object//
	public String toString() {
		final StringBuffer sb = new StringBuffer(40).append("[shadow element(").append(_tag).append(")");
		if (_props != null)
			for (Property name: _props)
				sb.append(' ').append(name.getName());
		return sb.append(']').toString();
	}
}
