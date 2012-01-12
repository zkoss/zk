/* LoadDummyBinding.java

	Purpose:
		
	Description:
		
	History:
		Jan 11, 2012 5:43:32 PM, Created by henrichen

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.sys;

import java.util.Map;

import org.zkoss.bind.BindContext;

/**
 * Skeleton implementation for a dummy load binding. 
 * @author henrichen
 *
 */
public abstract class LoadDummyBinding implements DummyBinding, LoadBinding {
	@Override
	public Map<String, Object> getArgs() {
		return null;
	}

	@Override
	public void load(BindContext ctx) {
		//do nothing
	}
}
