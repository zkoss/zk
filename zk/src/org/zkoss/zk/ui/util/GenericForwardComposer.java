/* GenericForwardComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 26, 2008 2:30:30 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;

/**
 * <p>An abstract composer that you can extend and write intuitive onXxx$myid 
 * event handler methods with auto event forwarding and "auto-wired" accessible 
 * variable objects such as embedded objects, components, and external 
 * resolvable variables in a ZK zuml page; this class will add forward
 * condition to the myid source component and forward source onXxx 
 * event received by the source myid component to the target onXxx$myid event
 * (as defined in this composer) of the supervised target component; of course
 * it will also registers onXxx$myid events to the supervised 
 * component and wire all accessible variable objects to this composer by 
 * calling setXxx() method or set xxx field value directly per the variable 
 * name.</p>
 * 
 * <p>Notice that since this composer kept references to the components, single
 * instance object cannot be shared by multiple components.</p>
 *  
 * <p>The following is an example. The onChange event received by Textbox 
 * mytextbox will be forwarded to target Window mywin as a new target event 
 * onChange$mytextbox and the Textbox component with id name "mytextbox" and
 * Label with id name mylabel are injected into the "mytextbox" and "mylabel"
 * fields respectively(so you can use mytextbox and mylabel variable directly 
 * in onChange_mytextbox without problem).</p>
 * 
 * <pre><code>
 * MyComposer.java
 * 
 * public class MyComposer extends GenericForwardComposer {
 *     private Textbox mytextbox;
 *     private Window self; //embeded object, the supervised window "mywin"
 *     private Page page; //the ZK zuml page
 *     private Label mylabel;
 *     
 *     public void onChange$mytextbox(Event event) {
 *         mylabel.setValue("You just entered: "+ mytextbox.getValue());
 *     }
 * }
 * 
 * test.zul
 * 
 * &lt;window id="mywin" apply="MyComposer">
 *     &lt;textbox id="mytextbox"/>
 *     &lt;label id="mylabel"/>
 * &lt;/window>
 * </code></pre>
 * 
 * @author henrichen
 * @since 3.0.7
 * @see org.zkoss.zk.ui.Components#addForwards
 */
abstract public class GenericForwardComposer extends GenericAutowireComposer {
	private static final long serialVersionUID = 20091006115726L;

	protected GenericForwardComposer() {
	}
	/** Constructor with a custom separator.
	 * The separator is used to separate the component ID and event name.
	 * By default, it is '$'. For Grooy and other environment that '$'
	 * is not applicable, you can specify '_'.
	 * @since 3.6.0
	 */
	protected GenericForwardComposer(char separator) {
		super(separator);
	}

	/**
	 * Auto forward events and wire accessible variables of the specified 
	 * component into a controller Java object; a subclass that 
	 * override this method should remember to call super.doAfterCompose(comp) 
	 * or it will not work.
	 */
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		//add forward condtions to the components as defined in this composer
		//onXxx$myid
		Components.addForwards(comp, this, _separator);
	}
}
