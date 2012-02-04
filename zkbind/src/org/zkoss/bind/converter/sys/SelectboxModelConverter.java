/* SelectboxModelConverter.java

	Purpose:
		
	Description:
		
	History:
		2011/12/12 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter.sys;

import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Selectbox;

/**
 * The {@link Converter} implementation of the selectbox for converting collection to ListModel and vice versa.
 * @author dennis
 * @since 6.0.0
 */
public class SelectboxModelConverter extends AbstractListModelConverter{
	private static final long serialVersionUID = 1463169907348730644L;
	
	@Override
	protected ListModel<?> getComponentModel(Component comp) {
		return ((Selectbox)comp).getModel();
	}
}
