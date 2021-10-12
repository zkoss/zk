/* AuHistoryState.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 26 11:13:50 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to push a state to history entry.
 *
 * <p>data[0]: if replace the current history instead of creating a new one<br>
 * data[1]: a state object<br>
 * data[2]: a title for the state<br>
 * data[3]: a new history entry's URL
 *
 * @author rudyhuang
 * @since 8.5.0
 */
public class AuHistoryState extends AuResponse {
	public AuHistoryState(boolean replace, Object state, String title, String url) {
		super("historyState", new Object[]{ replace, state, title, url });
	}
}
