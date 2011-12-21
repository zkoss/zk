/* BindingParam.java

	Purpose:
		
	Description:
		
	History:
		2011/12/21 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import org.zkoss.util.Pair;
import org.zkoss.zk.ui.Component;
/**
 * 
 * @author dennis
 *
 */
public class BindingKey extends Pair<Component, String> {

	private static final long serialVersionUID = 1L;

	public BindingKey(Component x, String y) {
		super(x, y);
	}
}
