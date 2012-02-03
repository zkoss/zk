/* ComboboxSelectedIndexConverter.java

	Purpose:
		
	Description:
		
	History:
		Feb 3, 2012 6:10:20 PM, Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.converter.sys;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;

/**
 * Convert selected index to bean and vice versa.
 * @author dennis
 * @since 6.0.0
 */
public class ListboxSelectedIndexConverter extends AbstractSelectedIndexConverter {
	private static final long serialVersionUID = 201108171811L;

	protected ListModel<?> getComponentModel(Component comp) {
		return ((Listbox)comp).getModel();
	}
}
