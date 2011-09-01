/* ListSubModel.java


 Purpose:
 
 Description:
 
 History:
 Jan 2, 2008 11:28:13 AM , Created by jumperchen


 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under LGPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.zul;


/**
 * An extra interface that can be implemented with {@link ListModel} to control
 * the extract of the combobox.
 * 
 * @author jumperchen
 * @since 3.0.2
 */
public interface ListSubModel{
	
	/** Returns the subset of list model data that the subset data is extract 
	 * from combobox's list model data. It is ususally used for implmentation of
	 * auto-complete.
	 *
	 * @param value the object is used to find that the content is consistent 
	 * within list model data.
	 * @param nRows the number of rows suggested to return (as the
	 * returned ListModel instance). It's a suggestion for developer to
	 * follow.
	 * If nonpositive, it means the maximal allowed rows is decided by the 
	 * implementation of ListSubmodel.
	 * @since 3.0.2
	 */
	public ListModel getSubModel(Object value, int nRows);
}
