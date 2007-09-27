/* Listbox.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 17, 2007 12:14:47 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkforge.yuiext.grid;

import org.zkoss.zk.ui.ext.render.ZidRequired;

/**
 * This class is used by {@link Column} to implement the combobox function for <code>Ext JS</code>.
 * <p>Default: Uses the <code>select</code> mold and sets rows to be 1 .</p>
 * @author jumperchen
 *
 */
public class Listbox extends org.zkoss.zul.Listbox {
	public Listbox(){
		setMold("select");
		setRows(1);
	}
	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	protected class ExtraCtrl extends  org.zkoss.zul.Listbox.ExtraCtrl implements ZidRequired{
		public boolean isZidRequired() {
			return true;
		}		
	}
	
}
