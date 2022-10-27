/* IListgroupChild.java

	Purpose:

	Description:

	History:
		3:38 PM 2022/3/10, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

/**
 * An interface to indicate a Listgroup component which its implementation is PE and EE only.
 * @author jumperchen
 */
public interface IListgroupChild<I extends IListgroupChild>
		extends IListitemBase<I>,
		IChildable<I, IListcell<IAnyGroup>> {
}
