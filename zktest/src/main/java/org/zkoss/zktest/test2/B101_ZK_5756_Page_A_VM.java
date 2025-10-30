/* B101_ZK_5756_Page_A_VM.java

	Purpose:

	Description:

	History:
		5:59 PM 2024/9/19, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.MatchMedia;
import org.zkoss.lang.Threads;

/**
 * @author jumperchen
 */
public class B101_ZK_5756_Page_A_VM {
	@MatchMedia("all and (min-width: 958px)")
	public void beWide(){
		Threads.sleep(500);
	}

	@MatchMedia("all and (max-width: 957px)")
	public void beNarrow(){
		Threads.sleep(500);
	}

}
