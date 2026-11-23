/** F80_ZK_2675VM.java.

	Purpose:
		
	Description:
		
	History:
		2:14:03 PM May 20, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ToClientCommand;

/**
 * @author jumperchen
 *
 */
@ToClientCommand({"doEventClicked", "doDayClicked"})
public class F80_ZK_2675VM {

    @Command
    public void doEventClicked() {}

    @Command
    public void doDayClicked() {}
}