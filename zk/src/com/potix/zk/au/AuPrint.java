/* AuPrint.java

{{IS_NOTE
	$Id: AuPrint.java,v 1.1 2006/04/10 03:02:02 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Sat Apr  8 21:08:05     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au;

/**
 * A response to ask the client to print the desktop.
 *  <p>no data.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.1 $ $Date: 2006/04/10 03:02:02 $
 */
public class AuPrint extends AuResponse {
	public AuPrint() {
		super("print");
	}
}
