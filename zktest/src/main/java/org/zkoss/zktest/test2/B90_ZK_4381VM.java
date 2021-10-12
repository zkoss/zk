/* B86_ZK_4288VM.java

	Purpose:
		
	Description:
		
	History:
		Fri May 17 10:55:28 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.Serializable;

import org.zkoss.bind.annotation.MatchMedia;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author rudyhuang
 */
public class B90_ZK_4381VM implements Serializable {
	private static final long serialVersionUID = 8068544008027154388L;

	@MatchMedia("all and (max-width: 768px)")
	public void matchMedia01() {
		Clients.log("768px");
	}

	@MatchMedia("all and (max-width: 1024px)")
	public void matchMedia02() {
		Clients.log("1024px");
	}
}
