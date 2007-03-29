/* ComponentDefinition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 17:54:45     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Collection;
import java.util.Map;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.impl.ComponentDefinitionImpl;

/**
 * A component definition.
 * Like class in Java, a {@link ComponentDefinition} defines the behavior
 * of a component.
 *
 * <p>The implementation must be thread-safe, since the caller depends on it.
 * 
 * @author tomyeh
 */
public interface ComponentDefinition extends Cloneable {
	/** A special definition representing the zk component. */
	public final static ComponentDefinition ZK =
		new ComponentDefinitionImpl(null, "zk", Component.class);;

	/** Returns the language definition, or null if it is a temporty definition
	 * belonging to a page.
	 */
	public LanguageDefinition getLanguageDefinition();

	/** Returns name of this component definition (never null).
	 * It is unique in the same language, {@link LanguageDefinition},
	 * if it belongs to a language, i.e.,
	 * {@link #getLanguageDefinition} is not null.
	 */
	public String getName();

	/** Returns whether this is a macro component.
	 * @see #getMacroURI
	 */
	public boolean isMacro();
	/** Returns the macro URI, or null if not a macro.
	 */
	public String getMacroURI();
	/** Returns whether this is an inline macro.
	 * If false, you have to examine {@link #isMacro} to see whether it
	 * is a regular macro.
	 */
	public boolean isInlineMacro();

	/** Returns the class (Class) or the class name (String) that
	 * implements the component.
	 *
	 * <p>If a string is returned, the real class may depend on
	 * which page a component will be created to.
	 * Reason: the zscript interpreter is associated with a page and
	 * it may define classes upon evaluating a page.
	 */
	public Object getImplementationClass();
	/** Sets the class to implements the component.
	 *
	 * <p>Note: currently, classes specified in lang.xml or lang-addon.xml
	 * must be resolved when loading the files.
	 * However, classes specified in a page (by use of class or use attributes)
	 * might be resolved later because it might be defined by zscript.
	 */
	public void setImplementationClass(Class cls);
	/** Sets the class name to implements the component.
	 * Unlike {@link #setImplementationClass(Class)}, the class won't
	 * be resolved until {@link ComponentInfo#newInstance} or {@link #getImplementationClass}
	 * is used. In other words, the class can be provided later
	 * (thru, usually, zscript).
	 */
	public void setImplementationClass(String clsnm);
	/** Resolves and returns the class that implements the component.
	 *
	 * <p>Unlike {@link #getImplementationClass},
	 * this method will resolve a class name (String) to a class (Class),
	 * if necessary.
	 * In addition, if the clsnm argument is specified,
	 * it is used instead of {@link #getImplementationClass}.
	 * In other words, it overrides the default class.
	 *
	 * @param clsnm [optional] If specified, clsnm is used instead of
	 * {@link #getImplementationClass}.
	 * In other words, it overrides the default class.
	 * @param page the page that is used to resolve the implementation
	 * class. It is used only this definition is associated
	 * with a class name by {@link #setImplementationClass(String)},
	 * or clsnm is not null.
	 * Note: this method won't attach the component to the specified page.
	 * It can be null if {@link #getImplementationClass} returns a Class
	 * instance, and clsnm is null.
	 * @exception ClassNotFoundException if the class not found
	 */
	public Class resolveImplementationClass(Page page, String clsnm)
	throws ClassNotFoundException;
	/** Returns whether a component belongs to this definition.
	 *
	 * <p>If {@link #resolveImplementationClass} failed to resolve,
	 * true is returned!
	 */
	public boolean isInstance(Component comp);

	/** Creates an component of this definition.
	 *
	 * <p>Note: this method doesn't invoke {@link #applyProperties}.
	 * It is caller's job to apply these properties if necessary.
	 * Since the value of a property might depend on the component tree,
	 * it is better to assign the component with a proper parent
	 * before calling {@link #applyProperties}.
	 *
	 * <p>Similarly, this method doesn't attach the component to the
	 * specified page. Developers may or may not add it to a page or
	 * a parent.
	 *
	 * <p>An application developer can invoke
	 * {@link org.zkoss.zk.ui.sys.UiFactory#newComponent}
	 * instead of {@link #newInstance}, since a deployer might
	 * customize the way to create components by providing
	 * an implementation of {@link org.zkoss.zk.ui.sys.UiFactory}.
	 * In additions, it also invokes {@link #applyProperties}
	 * assigning page/parent.
	 *
	 * <p>On the other hand, this method is 'low-level'. It simply resolves
	 * the implementation class by use of {@link #resolveImplementationClass},
	 * and then uses it to create an instance.
	 *
	 * @param clsnm [optional] If specified, clsnm is used instead of
	 * {@link #getImplementationClass}.
	 * In other words, it overrides the default class.
	 * @param page the page that is used to resolve the implementation
	 * class. It is used only this definition is associated
	 * with a class name by {@link #setImplementationClass(String)},
	 * or clsnm is not null.
	 * Note: this method won't attach the component to the specified page.
	 * It can be null if {@link #getImplementationClass} returns a Class
	 * instance, and clsnm is null.
	 * @return the new component (never null)
	 */
	public Component newInstance(Page page, String clsnm);

	/** Adds a mold
	 *
	 * @param moldURI the URI of the mold; never null nor empty.
	 * It can be an EL expression.
	 */
	public void addMold(String name, String moldURI);
	/** Returns the URI of the mold, or null if no such mold available.
	 * If mold contains an expression, it will be evaluated first
	 * before returning.
	 *
	 * @param name the mold
	 */
	public String getMoldURI(Component comp, String name);
	/** Returns whether the specified mold exists.
	 */
	public boolean hasMold(String name);
	/** Returns a readonly collection of mold names supported by
	 * this definition.
	 */
	public Collection getMoldNames();

	/** Adds a property initializer.
	 * It will initialize a component when created with is definition.
	 *
	 * @param name the member name. The component must have a valid setter
	 * for it.
	 * @param value the value. It might contain expressions (${}).
	 */
	public void addProperty(String name, String value);
	/** Applies the properties and custom attributes defined in
	 * this definition to the specified component.
	 *
	 * <p>Note: annotations are applied to the component when a component
	 * is created. So, this method doesn't and need not to copy them.
	 */
	public void applyProperties(Component comp);
	/** Evaluates and retrieves properties to the specified map from
	 * {@link ComponentDefinition} (and {@link ComponentInfo}).
	 *
	 * @param propmap the map to store the retrieved properties.
	 * If null, a HashMap instance is created.
	 * @param owner the owner page; used if parent is null
	 * @param parent the parent
	 */
	public Map evalProperties(Map propmap, Page owner, Component parent);

	/** Returns the annotation map defined in this definition, or null
	 * if no annotation is ever defined.
	 */
	public AnnotationMap getAnnotationMap();

	/** Clones this definition and assins with the specified language
	 * definition and name.
	 */
	public ComponentDefinition clone(LanguageDefinition langdef, String name);

	/** Clones this component definition.
	 * You rarely invoke this method directly. Rather, use
	 * {@link #clone(LanguageDefinition, String)}.
	 *
	 * <p>Note: the caller usually has to change the component name,
	 * and then assign to a language definition ({@link LanguageDefinition})
	 * or a page definition ({@link PageDefinition}).
	 *
	 * @return the new component definition by cloning from this definition.
	 */
	public Object clone();
}
