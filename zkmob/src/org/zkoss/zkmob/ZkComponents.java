/* ZkComponents.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 1, 2007 8:08:43 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.impl.Zk;

/**
 * Utitlity class for handling ZkComponent.
 * @author henrichen
 *
 */
public class ZkComponents {
	/**
	 * parse a "width,height" into int[] where int[0] is width, int[1] is height.
	 * @param sizestr The size in string format of "width, height"
	 * @return int[] where int[0] is width, int[1] is height.
	 */
	public static int[] parseSize(String sizestr) {
		int[] size = new int[2]; //[0]: width; [1]: height
		int j = sizestr.indexOf(",");
		if (j < 0) { //not found
			return null;
		} else {
			String wstr = sizestr.substring(0, j).trim();
			String hstr = sizestr.substring(j+1).trim();
			size[0] = Integer.parseInt(wstr);
			size[1] = Integer.parseInt(hstr);
		}
		return size;
	}
	
	/**
	 * Utility to apply common properties to Item component. 
	 */
	public static void applyItemProperties(Form form, Item component, Attributes attrs) {
		setItemAttr(component, "lo", attrs.getValue("lo")); //layout
		setItemAttr(component, "ps", attrs.getValue("ps")); //preferredSize
		if (form != null) {
			((Itemable)component).setForm(form);
			form.append(component);
		}
	}
	
	/**
	 * Given Item, attribute name, and value; then set the attribute of the Item if match. 
	 * @param item The Item to be set
	 * @param attr The attribute name
	 * @param val The value
	 */
	public static void setItemAttr(Item item, String attr, String val) {
		if ("lb".equals(attr)) {
			item.setLabel(val);
		} else if ("lo".equals(attr)) {
			item.setLayout(val == null ? Item.LAYOUT_DEFAULT : Integer.parseInt(val));
		} else if ("ps".equals(attr)) {
			int[] sz = val == null ? new int[] {-1, -1} : parseSize(val);
			item.setPreferredSize(sz[0], sz[1]);
		}
	}
	
	public static void registerCommand(ZkComponent owner, Command cmd, Zk zk) {
		owner.addCommand(cmd); //add into owner
		if (owner instanceof Item) {
			((Item)owner).setItemCommandListener(zk.getItemCommandListener());
		} else if (owner instanceof Displayable) {
			((Displayable)owner).setCommandListener(zk.getCommandListener());
		}
	}
}
