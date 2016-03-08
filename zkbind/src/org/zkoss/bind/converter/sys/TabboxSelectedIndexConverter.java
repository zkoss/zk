/* TabboxSelectedIndexConverter.java

	Purpose:
		
	Description:
		
	History:
		2013/11/11 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter.sys;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.Tabbox;

/**
 * @author dennis
 * @since 7.0.0
 */
public class TabboxSelectedIndexConverter extends AbstractSelectedIndexConverter<Tabbox> {
	private static final long serialVersionUID = 201108171811L;

	protected ListModel<?> getComponentModel(Tabbox comp) {
		return comp.getModel();
	}

}
