/* Openable.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Apr 17, 2011 13:39:21 PM, Created by jimmyshiau
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.ext;

import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;


/**
 * @deprecated As of release 6.0.0, replaced with {@link TreeOpenableModel}.
 * Indicate a openable collection or component. Generally used with {@link TreeModel}
 * and {@link Tree}.
 * @author jimmyshiau
 * @see TreeModel
 * @see Tree
 */
public interface Openable<E> extends TreeOpenableModel<E> {
}
