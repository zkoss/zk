/* TabboxSelectedTabConverter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Nov 13 14:39:14     2007, Created by Henri
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.bind.converter.sys;

import java.util.Iterator;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

/**
 * Convert Tabbox selected Tab to Tab label and vice versa.
 *
 * @author Dennis
 */
public class SelectedTabConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 200808190445L;

	@Override
	public Object coerceToUi(Object val, Component component, BindContext ctx) {
		if (val != null) {
			//iterate to find the selected radio via the value
			for (Iterator<Component> it = ((Tabbox)component).getTabs().getChildren().iterator(); it.hasNext();) {
				final Component child = it.next();
				if (child instanceof Tab) {
					if (val.equals(((Tab)child).getLabel())) {
						return child;
					}
				}
			}
		}
	  	return null;
	}

	@Override
	public Object coerceToBean(Object val, Component component, BindContext ctx) {
		return val != null ? ((Tab)val).getLabel() : null;
	}
}
