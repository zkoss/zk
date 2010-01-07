/* wirer.js

	Purpose:
		
	Description:
		
	History:
		Dec 15, 2009 4:43:47 PM , Created by joy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
/** @class zk.Wirer
 * Used to manipulate the wires.
 * By default, all methods do nothing. It is designed to be overriden
 * with a real implementation.
 */
zk.Wirer = {	
	add: zk.$void,
	remove: zk.$void,
	/** Synchronized the wires of the given controller.
	 * @param Object controller the controller of the wires. The controller
	 * is usually a {@link zk.Widget}.
	 * @param zk.Event evt the event.
	 */
	sync: zk.$void
};