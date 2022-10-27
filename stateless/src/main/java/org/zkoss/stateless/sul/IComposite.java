/* IComposite.java

	Purpose:

	Description:

	History:
		10:48 AM 2021/10/26, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.List;

/**
 * An addition interface to {@link IChildable} and {@link ISingleChildable}
 * to retrieve all {@link IComponent}s which belong to the implementation class.
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface IComposite<I extends IComponent> {

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	List<I> getAllComponents();
}