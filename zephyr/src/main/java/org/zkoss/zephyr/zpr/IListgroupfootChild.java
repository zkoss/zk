/* IListgroupfootChild.java

	Purpose:

	Description:

	History:
		3:40 PM 2022/3/10, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

/**
 * An interface to indicate a Listgroupfoot component which its implementation is PE and EE only.
 * @author jumperchen
 */
public interface IListgroupfootChild<I extends IListgroupfootChild>
		extends IListitemBase<I>,
		IChildable<I, IListcell<IAnyGroup>> {
}
