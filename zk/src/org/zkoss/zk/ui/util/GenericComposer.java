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
import org.zkoss.zk.ui.event.GenericEventListener;
import org.zkoss.zk.ui.util.Composer;

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
 * 
 * @author robbiecheng
 * @since 3.0.1
 */
abstract public class GenericComposer extends GenericEventListener implements Composer{
    
	/**
	 * Registers onXxx events to the supervised component; a subclass that override
	 * this method should remember to call super.doAfterCompose(comp) or it will not 
	 * work.
	 */
	public void doAfterCompose(Component comp) throws Exception {
		//bind this GenericEventListener to the supervised component
		bindComponent(comp);
	}
}
