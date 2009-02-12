/*	Zombie.java

{{IS_NOTE

	Purpose:
	Description:
	History:
		2002/1/15, Henri Chen: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.lang.reflect;

/**
 * A marker interface to represent a Zombie. A Zombie object is used to
 * represents a non-existent object; or a null object with an identity.
 * To distinguish the difference between an existence object and a Zombie,
 * programer can check wether the object implements this Zombie interface.
 *
 * @author henrichen
 */
public interface Zombie {
	//no methods, it is used as a marker only.
}
