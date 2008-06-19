/* GenericAutowireComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 11, 2008 10:56:06 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.UiException;

/**
 * <p>An abstract composer that you can extend and write intuitive onXxx 
 * event handler methods with "auto-wired" accessible variable objects such
 * as embedded objects, components, and external resolvable variables in a ZK 
 * zuml page; this class will registers onXxx events to the supervised 
 * component and wire all accessible variable objects to this composer by 
 * calling setXxx() method or set xxx field value directly per the variable 
 * name.</p>
 * 
 * <p>Notice that since this composer kept references to the components, single
 * instance object cannot be shared by multiple components.</p>
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
 *     private Window self; //embeded object, the supervised window "mywin"
 *     private Page page; //the ZK zuml page
 *     
 *     public void onOK() {
 *         mytextbox.setValue("Enter Pressed");
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
abstract public class GenericAutowireComposer extends GenericComposer {
	/**
	 * Auto wire accessible variables of the specified component into a 
	 * controller Java object; a subclass that 
	 * override this method should remember to call super.doAfterCompose(comp) 
	 * or it will not work.
	 */
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		//wire variables to reference fields
		Components.wireVariables(comp, this);
	}
}
