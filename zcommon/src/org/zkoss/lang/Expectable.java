/* Expectable.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	2002/04/18 18:24:22, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

/**
 * A specifial interface to denote an exception is expectable -- it means
 * the exception is <i>not</i> caused by programming error.
 * It also implies the exception can be resolved by user by retrying
 * (e.g, for deadlock), correcting (e.g., typro), or fixing (e.g., network
 * broken).
 *
 * <p>It is mainly used to denote no to log the message, if possible,
 * and only shows up a brief description about the exception, and so on.
 *
 * @author tomyeh
 */
public interface Expectable {
}
