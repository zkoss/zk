/* SynchronizedScope.java

	History:
		Fri, Mar 16, 2012  5:37:30 PM, Created by tomyeh

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.impl;

import java.util.Map;
import java.util.Collections;

import org.zkoss.zk.ui.ext.Scope;

/**
 * A synchronized scope.
 * It is extended from {@link SimpleScope}, but it can be accessed
 * concurrently.
 *
 * <p>Thread safe.
 * @author tomyeh
 * @5.0.11
 */
public class SynchronizedScope extends SimpleScope {
	public SynchronizedScope(Scope owner) {
		super(owner);
	}

	//@Override
	Map<String, Object> newInitMap() {
		return Collections.synchronizedMap(super.newInitMap());
	}
}
