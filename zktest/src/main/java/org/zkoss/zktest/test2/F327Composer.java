/* F327Composer.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 26 14:38:55 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zktest.test2;

/**
 * The composer for test feature ZK-237.
 * @author tomyeh
 */
public class F327Composer extends org.zkoss.zk.ui.util.GenericForwardComposer {
	public void onClick$hi() {
		alert("Hi!");
	}
}
