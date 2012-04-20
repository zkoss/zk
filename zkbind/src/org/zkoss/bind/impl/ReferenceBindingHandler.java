/* ReferenceBindingHandler.java

	Purpose:
		
	Description:
		
	History:
		

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.zk.ui.Component;
/**
 * to handle reference binding
 * @author dennis
 *
 */
public interface ReferenceBindingHandler {
	public void setBinder(Binder binder);

	public void addReferenceBinding(Component comp, String attr, ReferenceBinding binding);

	public void removeReferenceBinding(Component comp);

	public void removeReferenceBinding(Component comp, String attr);
}
