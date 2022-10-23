/* IMeshComposite.java

	Purpose:
		
	Description:
		
	History:
		2:55 PM 2021/10/26, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

/**
 * Represents a composition of {@link IComponent} onto {@link IMeshElement}.
 * @author jumperchen
 */
public interface IMeshComposite<I extends IMeshComposite> extends IComponent<I> {
}
