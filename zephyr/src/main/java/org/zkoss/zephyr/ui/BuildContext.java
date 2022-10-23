/* BuildContext.java

	Purpose:
		
	Description:
		
	History:
		3:21 PM 2021/9/27, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui;

import org.zkoss.zephyr.zpr.IComponent;

/**
 * A build context to hold the necessary meta info when building components.
 * @author jumperchen
 */
public class BuildContext<I extends IComponent> {
	private final I _owner;
	private BuildContext(I owner) {
		_owner = owner;
	}

	/**
	 * Returns the owner component of the build context, if any.
	 * @return null if the build content is not associated into a component.
	 */
	public I getOwner() {
		return _owner;
	}

	/**
	 * Creates a simple build context with the given immutable component.
	 * @param owner the immutable component
	 */
	public static <I extends IComponent> BuildContext<I> newInstance(I owner) {
		return new BuildContext<I>(owner);
	}
}
