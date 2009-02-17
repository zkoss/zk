/* RadiogroupSelectedItemConverter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar 12 11:05:43     2007, Created by Henri
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;

/**
 * Convert Radiogroup selected item to radio value and vice versa.
 *
 * @author Henri
 */
public class RadiogroupSelectedItemConverter implements TypeConverter, java.io.Serializable {
  	private static final long serialVersionUID = 200808191534L;
	public Object coerceToUi(Object val, Component comp) { //load
		if (val != null) {
			//iterate to find the selected radio via the value
			for (Iterator it = comp.getChildren().iterator(); it.hasNext();) {
				final Component child = (Component)it.next();
				if (child instanceof Radio) {
					if (val.equals(((Radio)child).getValue())) {
						return child;
					}
				} else if (!(child instanceof Radiogroup)) { //skip nested radiogroup
					//bug 2464484
					final Object value = coerceToUi(val, child); //recursive
					if (value != null) {
						return value;
					}
				}
			}
		}
		return null;
	}
  
	public Object coerceToBean(Object val, Component comp) { //save
 		return val != null ? ((Radio)val).getValue() : null;
	}
}
