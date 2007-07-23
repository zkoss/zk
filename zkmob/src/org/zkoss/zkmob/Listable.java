/* Listable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 22, 2007 9:29:32 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import javax.microedition.lcdui.Choice;

/**
 * A component that can be use to list children; e.g. List and ChoiceGroup.
 * 
 * @author henrichen
 */
public interface Listable extends Choice {
	/**
	 * append a list item.
	 * @param ls the list item
	 */
	public void appendChild(ZkComponent li);
	
	/**
	 * insert a list item before the given index.
	 * 
	 * @param index the reference index
	 * @param id the list item id
	 * @param stringPart The label of the list item.
	 * @param imagePart The image of the list item.
	 */
	public void insertChild(int index, ZkComponent li);
	
	/**
	 * return index of the listitem id.
	 */
	public int indexOf(ZkComponent li);
}
