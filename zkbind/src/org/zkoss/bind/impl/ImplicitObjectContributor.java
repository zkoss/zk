/* CommandImplicitContributor.java

	Purpose:
		
	Description:
		
	History:
		2012/4/20 Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.Map;

import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.CommandBinding;
import org.zkoss.zk.ui.event.Event;

/**
 * to contribute implicit object
 * @author dennis
 *
 */
public interface ImplicitObjectContributor {

	public Map<String,Object> contirbuteCommandObject(Binder binder,CommandBinding binding, Event event);
}
