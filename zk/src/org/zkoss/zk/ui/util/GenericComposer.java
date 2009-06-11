/* GenericComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 21, 2007 6:22:00 PM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.lang.reflect.Method;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.GenericEventListener;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.NamespaceActivationListener;

/**
 * <p>An abstract composer that you can extend and write intuitive onXxx event handler methods;
 * this class will registers onXxx events to the supervised component automatically.</p>
 * <p>The following is an example. The onOK and onCancel event listener is registered into 
 * the target main window automatically.</p>
 * 
 * <pre><code>
 * &lt;zscript>&lt;!-- both OK in zscript or a compiled Java class -->
 * public class MyComposer extends GenericComposer {
 *    public void onOK() {
 *        //doOK!
 *        //...
 *    }
 *    public void onCancel() {
 *        //doCancel
 *        //...
 *    } 
 * }
 * &lt;/zscript>
 *
 * &lt;window id="main" apply="MyComposer">
 *     ...
 * &lt;/window>
 * </code></pre>
 * <p>since 3.6.1, this composer would be assigned as a variable of the given component 
 * per the naming convention composed of the component id and composer Class name. e.g.
 * If the applied component id is "xwin" and this composer class is 
 * org.zkoss.MyComposer, then the variable name would be "xwin$MyComposer". You can
 * reference this composer with {@link Component#getVariable} or via EL as ${xwin$MyComposer}
 * of via annotate data binder as @{xwin$MyComposer}, etc. If this composer is the 
 * first composer applied to the component, a shorter variable name
 * composed of the component id and a String "composer" would be also available for use. 
 * Per the above example, you can also reference this composer with the name "xwin$composer"</p>
 *
 * <p>Since 3.6.2, this composer becomes serializable, so it is better to
 * declare members to be wired as transient, since it will re-write automatically
 * after the session has been activated.
 * 
 * @author robbiecheng
 * @since 3.0.1
 */
abstract public class GenericComposer extends GenericEventListener
implements Composer, ComposerExt, NamespaceActivationListener,
java.io.Serializable {
	/**
	 * Registers onXxx events to the supervised component; a subclass that override
	 * this method should remember to call super.doAfterCompose(comp) or it will not 
	 * work.
	 */
	public void doAfterCompose(Component comp) throws Exception {
		//bind this GenericEventListener to the supervised component
		bindComponent(comp);
	}

	//since 3.6.1
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		//do nothing
		return compInfo;
	}

	//since 3.6.1
	public void doBeforeComposeChildren(Component comp) throws Exception {
		//assign this composer as a variable
		//feature #2778508
		Components.wireController(comp, this);
	}

	//since 3.6.1
	public boolean doCatch(Throwable ex) throws Exception {
		//do nothing
		return false;
	}

	//since 3.6.1
	public void doFinally() throws Exception {
		//do nothing
	}

	//NamespaceActivationListener//
	/** Called when a namespace is going to passivate this object.
	 * Default: does nothing.
	 */
	public void willPassivate(Namespace ns) {
	}
	/** Called when a namespace has activated this object back.
	 * Default: invokes {@link #doAfterCompose}.
	 * @since 3.6.2
	 */
	public void didActivate(Namespace ns) {
		try {
			doAfterCompose(ns.getOwner());
		} catch (Throwable ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
}
