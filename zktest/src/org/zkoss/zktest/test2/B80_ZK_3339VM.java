/* B80_ZK_3339VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 15 15:15:47 CST 2016, Created by wenninghsu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.MatchMedia;
import org.zkoss.zk.ui.util.Clients;

/**
 * 
 * @author wenninghsu
 */
public class B80_ZK_3339VM {

	@MatchMedia("all and (max-width : 640px)")
	private void m0(){
		Clients.log("invoked m0");
	}

	@MatchMedia("all and (max-width : 700px)")
	private void m1(){
		Clients.log("invoked m1");
	}

}
