/* IAuxheadCtrl.java

	Purpose:

	Description:

	History:
		Thu Oct 07 17:50:45 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Auxhead;

/**
 * An addition interface to {@link Auxhead}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IAuxheadCtrl {

	static IAuxhead from(Auxhead instance) {
		ImmutableIAuxhead.Builder builder = new IAuxhead.Builder();
		builder.setChildren((Iterable<? extends IAuxheader>) Immutables.proxyIChildren(instance));
		return builder.from((IAuxhead) instance).build();
	}
}