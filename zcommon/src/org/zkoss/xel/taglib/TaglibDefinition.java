/* TaglibDefinition.java

	Purpose:
		
	Description:
		
	History:
		Sun Sep 12 10:54:44 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.xel.taglib;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.xel.Function;

/**
 * Represents the content of a taglib.
 *
 * @author tomyeh
 * @since 6.0.0
 */
public class TaglibDefinition {
	/** A map of functions.
	 * The key is the name associated with the function.
	 */
	public final Map<String, Function> functions = new HashMap<String, Function>();
	/** A map of classes.
	 * The key is the name associated with the impled class.
	 */
	public final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
}
