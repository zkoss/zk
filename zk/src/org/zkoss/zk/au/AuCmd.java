/* AuCmd.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul  6 10:49:43     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import org.zkoss.zk.ui.Component;

/**
 * A response to ask the client to execute the specified client function.
 * Unlike {@link AuScript},
 *
 * <p>data[0]: the component UUID
 * data[1]: the client function name (i.e., JavaScript function)<br/>
 * data[2]: the first argument<br/>
 * data[3]: the second argument...
 * 
 * @author tomyeh
 * @since 2.4.1
 */
public class AuCmd extends AuResponse {
	/** Construct AuCmd to call a client function without argument.
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 */
	public AuCmd(Component comp, String function) {
		super("cmd", comp, new String[] {comp.getUuid(), function});
	}
	/** Construct AuCmd to call a client function with one argument.
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 * @param arg
	 */
	public AuCmd(Component comp, String function, String arg) {
		super("cmd", comp,
			new String[] {comp.getUuid(), function, arg});
	}
	/** Construct AuCmd to call a client function with two arguments.
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 */
	public AuCmd(Component comp, String function, String arg0, String arg1) {
		super("cmd", comp, new String[] {comp.getUuid(), function,
			arg0, arg1});
	}
	/** Construct AuCmd to call a client function with three arguments.
	 * @param comp the component that this script depends on.
	 * It cannot be null.
	 * @param function the function name
	 */
	public AuCmd(Component comp, String function, String arg0, String arg1,
	String arg2) {
		super("cmd", comp, new String[] {comp.getUuid(), function,
			arg0, arg1, arg2});
	}
}
