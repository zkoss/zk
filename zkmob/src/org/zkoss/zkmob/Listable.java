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

import javax.microedition.lcdui.Image;

/**
 * A component that can be use to list children; e.g. List and ChoiceGroup.
 * 
 * @author henrichen
 */
public interface Listable {
	/**
	 * set the {@link ListItem} of the specified index with specified label and image.
	 * 
	 * @param index the index of the {@link ListItem} to be set.
	 * @param label the new label of the ListItem.
	 * @param image the new image of the ListItem.
	 */
	public void set(int index, String label, Image image);
	
	/**
	 * get the associated label.
	 */
	public String getString(int index);
	
	/**
	 * append a list item.
	 * @param ls the list item
	 * @param stringPart The label of the list item.
	 * @param imagePart The image of the list item.
	 * @return the assigned index of the list item.
	 */
	public int append(ZkComponent li, String stringPart, Image imagePart);
	
	/**
	 * insert a list item before the given index.
	 * 
	 * @param index the reference index
	 * @param id the list item id
	 * @param stringPart The label of the list item.
	 * @param imagePart The image of the list item.
	 */
	public void insert(int index, ZkComponent li, String stringPart, Image imagePart);
	
	/**
	 * return index of the listitem id.
	 */
	public int indexOf(ZkComponent li);
	
	/**
	 * remove the element at index.
	 */
	public void delete(int index);
	
	/**
	 * The size.
	 */
	public int size();
}
