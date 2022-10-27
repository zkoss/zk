/* IComponentBuilder.java

	Purpose:
		
	Description:
		
	History:
		11:35 AM 2021/9/28, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import org.zkoss.stateless.zpr.IComponent;

/**
 * An immutable component builder interface to build a component tree to
 * the given view.
 * @author jumperchen
 */
public interface IComponentBuilder<I extends IComponent> {
	/**
	 * Builds an immutable components tree.
	 * @param ctx The build context
	 * @return the new component tree if any.
	 * @see BuildContext
	 */
	I build(BuildContext<I> ctx);
}
