/* FunctionDefinition.java

	Purpose:
		
	Description:
		
	History:
		Sun Sep 12 00:30:46 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.xel.taglib;

import org.zkoss.xel.Function;

/**
 * Represents a function defined in a taglib.
 * It consists of prefix, name and function.
 * @author tomyeh
 * @since 6.0.0
 */
public class FunctionDefinition {
	/** Method's prefix. */
	public final String prefix;
	/** Method's name. */
	public final String name;
	/** Method's function. */
	public final Function function;

	public FunctionDefinition(String prefix, String name, Function function) {
		this.prefix = prefix;
		this.name = name;
		this.function = function;
	}
}
