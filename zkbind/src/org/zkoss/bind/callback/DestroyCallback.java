/* DestroyCallback.java

        Purpose:
                
        Description:
                
        History:
                Wed Jul 04 17:21:52 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.callback;

import org.zkoss.bind.Binder;
import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Callback;

/**
 * A callback to invoke the destroy method in zkbind.  
 * 
 * @see Binder
 * @see BinderUtil
 * @see Callback
 * @author H.Y.Chen (klyvechen)
 */

public class DestroyCallback implements Callback<Component> {
	// The method to implements the callback interface 
	public void call(Component comp) {
		destroyBinder(comp);
	}

	// A method to invoke @Destroy method in viewModel bound to a component or its children 
	private static void destroyBinder(Component comp) {
		if (!((AbstractComponent) comp).getAnnotations("viewModel").isEmpty()) {
			Binder binder = BinderUtil.getBinder(comp);
			if (binder != null) {
				binder.destroy(comp, binder.getViewModel());
			}
		} 
		for (Component child: comp.getChildren()) {
			destroyBinder(child);
		}	
	}
}
