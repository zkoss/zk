/* ISingleChildable.java

	Purpose:

	Description:

	History:
		Tue Oct 12 11:53:23 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import javax.annotation.Nullable;

import org.zkoss.stateless.immutable.StatelessOnly;

/**
 * An interface to indicate only one child is allowed for {@link IComponent}.
 * @author katherine
 */
public interface ISingleChildable<R, T extends IComponent> {
	@Nullable
	@StatelessOnly
	T getChild();

	R withChild(@Nullable T child);
}