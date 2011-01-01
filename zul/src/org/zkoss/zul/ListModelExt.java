/* ListModelExt.java

	Purpose:
		
	Description:
		
	History:
		Fri Mar 30 16:40:50     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Comparator;
import org.zkoss.zul.ext.Sortable;

/**
 * @deprecated As of release 6.0.0, replaced with {@link Sortable}.
 * An extra interface that can be implemented with {@link ListModel}
 * to control the sorting of the data model.
 *
 * @author tomyeh
 */
public interface ListModelExt<T> extends Sortable<T> {
}
