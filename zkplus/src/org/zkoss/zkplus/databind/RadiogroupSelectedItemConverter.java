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

import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Radio;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Convert Radiogroup selected item to radio value and vice versa.
 *
 * @author Henri
 */
public class RadiogroupSelectedItemConverter implements TypeConverter {
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
					return coerceToUi(val, comp);
				}
			}
		}
  	return null;
  }
  
  public Object coerceToBean(Object val, Component comp) { //save
 		return val != null ? ((Radio)val).getValue() : null;
  }
}
