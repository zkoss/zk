/* GenericAutowireComposer.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 11, 2008 10:56:06 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.zkoss.idom.Document;
import org.zkoss.lang.Classes;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.xel.Evaluator;

/**
 * <p>An abstract composer that you can extend and write intuitive onXxx 
 * event handler methods with "auto-wired" accessible variable objects such
 * as implicit objects, components, and external resolvable variables in a ZK 
 * zuml page; this class will registers onXxx events to the supervised 
 * component and wire all accessible variable objects to this composer by 
 * calling setXxx() method or set xxx field value directly per the variable 
 * name. Since 3.0.7, this composer has wired all implicit objects 
 * such as self, spaceOwner, page, desktop, session, application, 
 * componentScope, spaceScope, pageScope, desktopScope, sessionScope, 
 * applicationScope, and requestScope, so you can use them directly. Besides 
 * that, it also provides alert(String message) method, so you can call alert() 
 * without problems.</p>
 * 
 * <p>Notice that since this composer kept references to the components, single
 * instance composer object cannot be shared by multiple components.</p>
 *  
 * <p>The following is an example. The onOK event listener is registered into 
 * the target window, and the Textbox component with id name "mytextbox" is
 * injected into the "mytextbox" field automatically (so you can use 
 * mytextbox variable directly in onOK).</p>
 * 
 * <pre><code>
 * MyComposer.java
 * 
 * public class MyComposer extends GenericAutowireComposer {
 *     private Textbox mytextbox;
 *     
 *     public void onOK() {
 *         mytextbox.setValue("Enter Pressed");
 *         alert("Hi!");
 *     }
 * }
 * 
 * test.zul
 * 
 * &lt;window id="mywin" apply="MyComposer">
 *     &lt;textbox id="mytextbox"/>
 * &lt;/window>
 * </code></pre>
 * 
 * @author henrichen
 * @since 3.0.6
 * @see org.zkoss.zk.ui.Components#wireFellows
 */
abstract public class GenericAutowireComposer extends GenericComposer implements ComponentCloneListener {
	private static final String COMPOSER_CLONE = "COMPOSER_CLONE";
	private static final String ON_CLONE_DO_AFTER_COMPOSE = "onCLONE_DO_AFTER_COMPOSE";
	
	/** Implicit Object; the applied component itself. 
	 * @since 3.0.7
	 */ 
	protected Component self;
	/** Implicit Object; the space owner of the applied component.
	 * @since 3.0.7
	 */
	protected IdSpace spaceOwner;
	/** Implicit Object; the page.
	 * @since 3.0.7
	 */
	protected Page page;
	/** Implicit Object; the desktop.
	 * @since 3.0.7
	 */
	protected Desktop desktop;
	/** Implicit Object; the session.
	 * @since 3.0.7
	 */
	protected Session session;
	/** Implicit Object; the web application.
	 * @since 3.0.7
	 */
	protected WebApp application;
	/** Implicit Object; a map of attributes defined in the applied component.
	 * @since 3.0.7
	 */
	protected Map componentScope;
	/** Implicit Object; a map of attributes defined in the ID space contains the applied component.
	 * @since 3.0.7
	 */
	protected Map spaceScope;
	/** Implicit Object; a map of attributes defined in the page.
	 * @since 3.0.7
	 */
	protected Map pageScope;
	/** Implicit Object; a map of attributes defined in the desktop.
	 * @since 3.0.7
	 */
	protected Map desktopScope;
	/** Implicit Object; a map of attributes defined in the session.
	 * @since 3.0.7
	 */
	protected Map sessionScope;
	/** Implicit Object; a map of attributes defined in the web application.
	 * @since 3.0.7
	 */
	protected Map applicationScope;
	/** Implicit Object; a map of attributes defined in the request.
	 * @since 3.0.7
	 */
	protected Map requestScope;
	/** Implicit Object; the current execution.
	 * @since 3.0.7
	 */
	protected Execution execution;
	/** Implicit Object; the arg argument passed to the createComponents method. It is never null.
	 * @since 3.0.8
	 */
	protected Map arg;
	/** The separator. */
	protected final char _separator;

	protected GenericAutowireComposer() {
		_separator = '$';
	}
	/** Constructor with a custom separator.
	 * The separator is used to separate the component ID and event name.
	 * By default, it is '$'. For Grooy and other environment that '$'
	 * is not applicable, you can specify '_'.
	 * @since 3.6.0
	 */
	protected GenericAutowireComposer(char separator) {
		_separator = separator;
	}

	/**
	 * Auto wire accessible variables of the specified component into a 
	 * controller Java object; a subclass that 
	 * override this method should remember to call super.doAfterCompose(comp) 
	 * or it will not work.
	 */
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		//wire variables to reference fields (include implicit objects)
		Components.wireVariables(comp, this, _separator);
	}
	
	/** Shortcut to call Messagebox.show(String).
	 * @since 3.0.7 
	 */
	private static Method SHOW;
	protected void alert(String m) {
		//zk.jar cannot depends on zul.jar; thus we call Messagebox.show() via
		//reflection. kind of weird :-).
		try {
			if (SHOW == null) {
				final Class mboxcls = Classes.forNameByThread("org.zkoss.zul.Messagebox");
				SHOW = mboxcls.getMethod("show", new Class[] {String.class});
			}
			SHOW.invoke(null, new Object[] {m});
		} catch (InvocationTargetException e) {
			throw UiException.Aide.wrap(e);
		} catch (Exception e) {
			//ignore
		}
	}
	
	/** Internal use only. Call-back method of CloneComposerListener. You shall 
	 * not call this method directly. Clone this Composer when its applied 
	 * component is cloned.
	 * @param comp the clone of the applied component
	 * @return A clone of this Composer. 
	 * @since 3.5.2
	 */
	public Object clone(Component comp) {
		try {
			final Execution exec = Executions.getCurrent();
			final int idcode = System.identityHashCode(comp);
			Composer composerClone = (Composer) exec.getAttribute(COMPOSER_CLONE+idcode);
			if (composerClone == null) {
				composerClone = (Composer) Classes.newInstance(getClass(), null);
				exec.setAttribute(COMPOSER_CLONE+idcode, composerClone);
				
				//cannot call doAfterCompose directly because the clone 
				//component might not be attach to Page yet
				comp.addEventListener(ON_CLONE_DO_AFTER_COMPOSE, new CloneDoAfterCompose());
				Events.postEvent(new Event(ON_CLONE_DO_AFTER_COMPOSE, comp, composerClone));
			}
			return composerClone;
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	
	//doAfterCompose, called once after clone
	private static class CloneDoAfterCompose implements EventListener {
		public void onEvent(Event event) throws Exception {
			final Component clone = (Component) event.getTarget();
			final GenericAutowireComposer composerClone = (GenericAutowireComposer) event.getData(); 
			composerClone.doAfterCompose(clone);
			clone.removeEventListener(ON_CLONE_DO_AFTER_COMPOSE, this);
		}
	}
}
