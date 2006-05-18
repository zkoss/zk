/* Initial.java

{{IS_NOTE
	$Id: Initial.java,v 1.2 2006/02/27 03:41:51 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Sep 20 16:08:09     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.comp;

import java.util.Map;

/**
 * An optional interface that a component <em>might</em> implement.
 * A component could be anything -- whether it implement
 * {@link Initial} or not.
 *
 * <p>There are two reasons that a component might implements it:
 * <ol>
 * <li>If it, during initialing, can be configured based on
 * the parameter map specified in i3-comp.xml.</li>
 * <li>If it, during initialing, invokes another component which invokes
 * back and then cause dead loop. And, if this interface is implemented
 * such dead loop could be prevented
 * (by allowing {@link ComponentManager#get} return the instance even
 * {@link #init} is still executing).</li>
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:41:51 $
 */
public interface Initial {
	/** Initializes the component implementing {@link Initial}.
	 *
	 * @param params the map of parameters specified in i3-comp.xml.
	 */
	public void init(Map params) throws Exception;
}
