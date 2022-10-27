/* ISelectors.java

	Purpose:
		
	Description:
		
	History:
		11:07 AM 2022/8/18, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import org.zkoss.stateless.sul.IComponent;

/**
 * A util API to seek {@link IComponent} by a selector,
 * by its {@code id}, or by a path.
 *
 * @author jumperchen
 */
public class ISelectors {

	/**
	 * Returns the icomponent that seeking from the root icomponent to match its id.
	 * @param root The root icomponent for seeking
	 * @param id The id of the icomponent to match
	 */
	public static <T extends IComponent> T findById(IComponent root, String id) {
		return new ISelectorImpl(root).get(id);
	}

	/**
	 * Returns the iselector fot the given root context.
	 * @param root The root icomponent for seeking
	 */
	public static ISelector select(IComponent root) {
		return new ISelectorImpl(root);
	}
}