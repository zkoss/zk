/* Messageable.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommon/src/com/potix/mesg/Messageable.java,v 1.2 2006/02/27 03:42:00 tomyeh Exp $
	Purpose: Extended exception interface
	Description: 
	History:
	 2001/7/2, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.mesg;

/**
 * Denotes an object that contains a message by use of an integer,
 * called code.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:42:00 $
 */
public interface Messageable extends MessageConst {
	/**
	 * Gets the message code.
	 *
	 * @return the message code
	 */
	public int getCode();
}
