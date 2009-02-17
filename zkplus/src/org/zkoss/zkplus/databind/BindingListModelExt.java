/* BindingListModelEx.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Apr 16, 2008 1:48:31 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

/**
 * <p>Support same object in multiple items of a ListModel.</p> 
 * <p>To speed up performance, DataBinder used to find only the first item of a given bean (see
 * {@link BindingListModel#indexOf}). However, in some cases, there might be one object in multiple items which 
 * will cause "unexpected" results of data binding (i.e. While one change should cause multiple
 * items to be updated in Grid, we saw only one item is updated.). Therefore, 
 * since version 3.1, we now support a "distinct" annotation that you can specify in model attribute
 * of collection components (i.e. Grid, Listbox, Combobox, etc.). So the data binder knows how to handle 
 * your ListModel. The default value for "distinct" is true so the Data Binder behaves as usual. If you 
 * specify distinct=false, the Data Binder will scan the whole ListModel to find all items that matches
 * and thus will be some performance issue if processing a big ListModel.</p>
 * 
 * <p>Example:</p>
 * <pre><code>
 * &lt;grid model="@{persons,distinct=false}">
 *    ...
 * &lt;/grid>
 * </code></pre>
 * @author henrichen
 * @since 3.5.0
 */
public interface BindingListModelExt extends BindingListModel {
	/** Returns indexes of the given object inside a ListModel.
	 * @param obj the specified object
	 * @return indexes that contains the given object; return empty array if none found.
	 */
	public int[] indexesOf(Object obj);
	
	/**
	 * Returns true if all objects inside this ListModel is distinct. 
	 * This is for {@link DataBinder} performance so there is no need 
	 * to scan the whole ListModel as long as find one.  
	 * @return true if all objects inside this ListModel is distinct; otherwise, return false.
	 * @since 3.5.0
	 */
	public boolean isDistinct();

}
