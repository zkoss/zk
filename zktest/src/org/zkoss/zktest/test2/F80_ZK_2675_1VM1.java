/** F80_ZK_2675_1VM1.java.

	Purpose:
		
	Description:
		
	History:
		2:14:03 PM May 20, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ToClientCommand;
import org.zkoss.bind.annotation.ToServerCommand;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jumperchen
 *
 */
@ToServerCommand({"doEventClicked"})
public class F80_ZK_2675_1VM1 {

    @Command
    public void doEventClicked() {
    	Clients.log("clicked 2");
    }

    @Command
    public void doDayClicked() {}
}