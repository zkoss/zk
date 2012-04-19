/* ComboboxSelectedIndexConverter.java

	Purpose:
		
	Description:
		
	History:
		Feb 3, 2012 6:10:20 PM, Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.converter.sys;

import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModel;

/**
 * Convert selected index to bean and vice versa.
 * @author dennis
 * @since 6.0.0
 */
public class ComboboxSelectedIndexConverter extends AbstractSelectedIndexConverter<Combobox> {
	private static final long serialVersionUID = 201108171811L;

	@Override
	protected ListModel<?> getComponentModel(Combobox comp) {
		return comp.getModel();
	}
}
