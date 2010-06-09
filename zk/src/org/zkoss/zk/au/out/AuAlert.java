/* AuAlert.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 10:26:35     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask client to show an alert.
 *
 * <p>data[0]: the alert message
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuAlert extends AuResponse {
	public AuAlert(String message) {
		super("alert", message); //component-independent
	}
}
