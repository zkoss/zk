/* Composer.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 25 18:24:27     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Component;

/**
 * Represents a composer to initialize a component (or a component of tree)
 * when ZK loader is composing a component.
 * It is the controller in the MVC pattern, while the component is the view.
 *
 * <p>To initialize a component, you can implement this interface
 * and then specify the class or an instance of it with the apply attribute
 * as follows.
 *
 *<pre><code>&lt;window apply="my.MyComposer"/&gt;
 *&lt;window apply="${a_composer}"/&gt;</code></pre>
 *
 * <p>Then, ZK loader will
 * <ol>
 * <li>Invoke {@link ComposerExt#doBeforeCompose}, if the composer
 * also implements {@link ComposerExt}.</li>
 * <li>Create the component (by use of
 * {@link org.zkoss.zk.ui.sys.UiFactory#newComponent}, which creates
 * and initializes the component accordingly).
 * <li>Invokes {@link ComposerExt#doBeforeComposeChildren}, if
 * {@link ComposerExt} is also implemented.</li>
 * <li>Composes all children, if any, of this component defined in
 * the ZUML page.</li>
 * <li>Invokes {@link #doAfterCompose} after all children are, if any,
 * composed.</li>
 * <li>Posts the onCreate event if necessary.</li>
 * </ol>
 *
 * <p>To intercept the lifecycle of the creation of a page,
 * implement {@link Initiator} and specify the class with the init directive.
 *
 * <p>Note: {@link org.zkoss.zk.ui.ext.AfterCompose} has to be implemented
 * as part of a component, while {@link Composer} is a controller used
 * to initialize a component (that might or might not implement
 * {@link org.zkoss.zk.ui.ext.AfterCompose}).
 *
 * <P>Alternatives: in most cases, you don't implement {@link Composer} directly.
 * Rather, you can extend from one of the following skeletons.
 * <dl>
 * <dt>{@link org.zkoss.zk.ui.select.SelectorComposer}</dt>
 * <dd>It supports the autowiring based on Java annoataion and a CSS3-based selector.
 * If you don't know which one to use, use {@link org.zkoss.zk.ui.select.SelectorComposer}.</dd>
 * <dt>{@link GenericForwardComposer}</dt>
 * <dd>It supports the autowiring based on naming convention.
 * You don't need to specify annotations explicitly, but it is error-prone if
 * it is used properly.</dd>
 * </dl>
 *
 * @author tomyeh
 * @since 3.0.0
 * @see org.zkoss.zk.ui.ext.AfterCompose
 * @see ComposerExt
 * @see FullComposer
 * @see Initiator
 */
public interface Composer<T extends Component> {
	/** Invokes after ZK loader creates this component,
	 * initializes it and composes all its children, if any.
	 * @param comp the component has been composed
	 */
	public void doAfterCompose(T comp) throws Exception;
}
