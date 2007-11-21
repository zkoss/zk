/* GenericComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 21, 2007 6:22:00 PM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkforge.resort;

import java.lang.reflect.Method;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.GenericEventListener;
import org.zkoss.zk.ui.util.Composer;

/**
 * A generic composer which will register corresponding event listeners 
 * to the supervised component according to those event handlers declared in this composer.
 * 
 * @author robbiecheng
 * @since 3.0.1
 */
public class GenericComposer extends GenericEventListener implements Composer{
    
	/**
	 * Registers event listeners to the supervised component
	 */
	public void doAfterCompose(Component comp) throws Exception {
		final Method [] metds = getClass().getMethods();
		for(int i=0; i < metds.length; i ++){
			String evtnm = metds[i].getName();
			if (evtnm.startsWith("on"))
				comp.addEventListener(evtnm, this);
		}		
	}
}
