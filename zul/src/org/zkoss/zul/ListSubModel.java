/* ListSubModel.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jan 2, 2008 11:28:13 AM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zul;


/**
 * An extra interface that can be implemented with {@link ListModel} to control
 * the extract of the combobox.
 * 
 * @author jumperchen
 * 
 */
public interface ListSubModel{
	
	/** Returns the subset of list model data that the subset data is extract 
	 * from combobox's list model data. It is ususally used for implmentation of
	 * auto-complete.
	 *
	 * @param value the object is used to find that the content is consistent 
	 * within list model data.
	 * @param nRows the nRows is reserved for future use. By default, -1 is 
	 * passed and it means the maximal allowed rows is decided by the 
	 * implementation of ListSubmodel. It's a suggestion for developer to follow
	 * it.
	 */
	public ListModel getSubModel(Object value, int nRows);
}
