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
package org.zkoss.zkplus.databind;

import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

/**
 * Convert Tabbox selected Tab to Tab label and vice versa.
 *
 * @author Henri
 * @since 3.0.1
 */
public class TabboxSelectedTabConverter implements TypeConverter, java.io.Serializable {
	private static final long serialVersionUID = 200808190445L;
	public Object coerceToUi(Object val, Component comp) { //load
	  	if (val != null) {
			//iterate to find the selected radio via the value
			for (Iterator it = ((Tabbox)comp).getTabs().getChildren().iterator(); it.hasNext();) {
				final Component child = (Component)it.next();
				if (child instanceof Tab) {
					if (val.equals(((Tab)child).getLabel())) {
						return child;
					}
				}
			}
		}
	  	return null;
	}
  
	public Object coerceToBean(Object val, Component comp) { //save
 		return val != null ? ((Tab)val).getLabel() : null;
	}
}
