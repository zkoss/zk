/* ComponentDefinition.java

	Purpose:
		
	Description:
		
	History:
		Tue May 31 17:54:45     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Collection;
import java.util.Map;
import java.net.URL;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.xel.ExValue;

/**
 * A component definition.
 * Like class in Java, a {@link ComponentDefinition} defines the behavior
 * of a component.
 *
 * <p>The implementation need NOT to be thread safe, since the caller
 * has to {@link #clone} first if accessed concurrently.
 *
 * @author tomyeh
 */
public interface ComponentDefinition extends Cloneable {
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

	/** Returns the property name to which the text enclosed within
	 * the element (associated with this component definition) is assigned to.
	 *
	 * <p>Default: null (means to create a Label component as the child)
	 *
	 * <p>For example, if {@link #getTextAs} returns null, then
	 * a Label component is created as a child of <code>comp</code>
	 * with the "Hi Text" value in the following example:
	 *
	 *<pre><code>&lt;comp&gt;
	 *  Hi Text
	 *&lt;/comp&gt;</code></pre>
	 *
	 * <p>In other words, it is equivalent to
	 *
	 *<pre><code>&lt;comp&gt;
	 *  &lt;label value="Hi Text"/&gt;
	 *&lt;/comp&gt;</code></pre>
	 *
	 * <p>On the other hand, if {@link #getTextAs} returns a non-empty string,
	 * say, "content", then
	 * "Hi Text" is assigned to the content property of <code>comp</comp>.
	 * In other words, it is equivalent to
	 *
	 *<pre><code>&lt;comp content="Hi Text"/&gt;
	 *&lt;/comp&gt;</code></pre>
	 *
	 * <p>It is also the same as
	 *
	 *<pre><code>&lt;comp&gt;
	 *  &lt;attribute name="content"/&gt;
	 *  Hi Text
	 *  &lt;/attribute&gt;
	 *&lt;/comp&gt;</code></pre>
	 *
	 * <p>To enable it, you can declare <code>text-as</code> in
	 * the component definition in lang.xml or lang-addon.xml:
	 *
	 * <pre><code>&lt;component&gt;
	 *  &lt;component-name&gt;html&lt;/component-name&gt;
	 *  &lt;text-as&gt;content&lt;/text-as&gt;
	 *...</code></pre>
	 *
	 * @since 3.0.0
	 */
	public String getTextAs();
	/** Returns whether to preserve the blank text.
	 * If false, the blank text (a non-empty string consisting of whitespaces)
	 * are ignored.
	 * If true, they are converted to a label child.
	 * <p>Default: false.
	 * @since 3.5.0
	 */
	public boolean isBlankPreserved();

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

	/** Returns whether this is used for the native namespace.
	 *
	 * @since 3.0.0
	 * @see LanguageDefinition#getNativeDefinition
	 */
	public boolean isNative();
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
	 * @param page the page to check whether the class is defined
	 * in its interpreters. Ignored if null.
	 * This method will search the class loader of the current thread.
	 * If not found, it will search the interpreters of the specifed
	 * page ({@link Page#getLoadedInterpreters}).
	 * Note: this method won't attach the component to the specified page.
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
	/** Creates an component of this definition.
	 * Refer to {@link #newInstance(Page, String)}. They are the same
	 * except this method accepts the class directly,
	 * while {@link #newInstance(Page, String)} invokes
	 * {@link #resolveImplementationClass} to resolve the class first.
	 *
	 * @return the new component (never null)
	 * @since 3.0.2
	 */
	public Component newInstance(Class cls);

	/** Adds a mold.
	 *
	 * @param name the mold name.
	 * @param widgetClass the widget class (aka., name).
	 * Ingored if null.
	 * @since 5.0.0 (the 2nd argument is the class name of the peer widget)
	 */
	public void addMold(String name, String widgetClass);

	/** Returns the widget class assoicated with specified mold,
	 * or the default widget class ({@link #getWidgetClass}) if not available.
	 * The returned widget class includes the package name (JavaScript class).
	 * @param comp the component used to evaluate EL expression, if any,
	 * when retreiving the widget class. Ignored if null.
	 * @param moldName the mold name
	 * @since 5.0.4
	 */
	public String getWidgetClass(Component comp, String moldName);
	/** Returns the default widget class, or null if not available.
	 * @param comp the component used to evaluate EL expression, if any,
	 * when retreiving the widget class. Ignored if null.
	 * @since 5.0.4
	 */
	public String getDefaultWidgetClass(Component comp);
	/** Sets the default widget class.
	 * @param widgetClass the name of the widget class (JavaScript class),
	 * including the package name.
	 * @since 5.0.0
	 */
	public void setDefaultWidgetClass(String widgetClass);
	/** Returns whether the specified mold exists.
	 */
	public boolean hasMold(String name);
	/** Returns a readonly collection of the names of the mold.
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
	 * See also {@link org.zkoss.zk.ui.AbstractComponent#AbstractComponent}.
	 */
	public void applyProperties(Component comp);
	/** Evaluates and retrieves properties to the specified map.
	 *
	 * @param propmap the map to store the retrieved properties.
	 * If null, a HashMap instance is created.
	 * (String name, Object value).
	 * @param owner the owner page; used if parent is null
	 * @param parent the parent
	 */
	public Map evalProperties(Map propmap, Page owner, Component parent);

	/** Returns the annotation map defined in this definition, or null
	 * if no annotation is ever defined.
	 */
	public AnnotationMap getAnnotationMap();

	/** Returns the apply attribute that is a list of
	 *{@link Composer} class
	 * names or EL expressions returning classes, class names or composer
	 * instances, or null if no apply attribute.
	 *
	 * @see #getParsedApply
	 * @since 3.6.0
	 */
	public String getApply();
	/** Return the parsed expressions of the apply attribute.
	 * @see #getApply
	 * @since 3.6.0
	 */
	public ExValue[] getParsedApply();
	/** Sets the apply attribute that is is a list of {@link Composer} class
	 * or EL expressions returning classes, class names or composer instances.
	 *
	 * @param apply the attribute this is a list of {@link Composer} class
	 * or EL expressions
	 * El expressions are allowed, but self means the parent, if available;
	 * or page, if no parent at all. (Note: the component is not created yet
	 * when the apply attribute is evaluated).
	 * @since 3.6.0
	 */
	public void setApply(String apply);

	/** Returns the URL where this component definition is declared, or
	 * null if not available.
	 * @since 3.0.3
	 */
	public URL getDeclarationURL();

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
