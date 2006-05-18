/*	Zombie.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/m3/pxcommon/src/com/potix/lang/reflect/Zombie.java,v 1.3 2006/02/27 03:42:00 tomyeh Exp $
	Purpose:
	Description:
	History:
		2002/1/15, Henri Chen: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package com.potix.lang.reflect;

/**
 * A marker interface to represent a Zombie. A Zombie object is used to
 * represents a non-existent object; or a null object with an identity.
 * To distinguish the difference between an existence object and a Zombie,
 * programer can check wether the object implements this Zombie interface.
 *
 * @author <a href="mailto:henrichen@potix.com">Henri Chen</a>
 * @version $Revision: 1.3 $ $Date: 2006/02/27 03:42:00 $
 */
public interface Zombie {
	//no methods, it is used as a marker only.
}
