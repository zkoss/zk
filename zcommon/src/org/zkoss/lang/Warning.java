/* Warning.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct 27 10:20:17     2003, Created by tomyeh

Copyright (C) 2003 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

import org.zkoss.mesg.Messageable;

/**
 * Represents an exception is a warning rather than error.
 * {@link #getCode} is the warnig code.
 *
 * @author tomyeh
 */
public interface Warning extends Expectable, Messageable {
}
