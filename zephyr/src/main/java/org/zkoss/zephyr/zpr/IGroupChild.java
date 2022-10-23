/* IGroupChild.java

	Purpose:

	Description:

	History:
		2:48 PM 2022/3/10, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

/**
 * An interface to indicate a Group component which its implementation is PE and EE only.
 * @author jumperchen
 */
public interface IGroupChild<I extends IGroupChild>
		extends IRowBase<I> {
}