/** F80_ZK_2650.java.

	Purpose:
		
	Description:
		
	History:
		11:42:34 AM Mar 12, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ToServerCommand;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jumperchen
 *
 */
@ToServerCommand("dataChange")
public class F80_ZK_2650 {
	public static class DataObject {
		private String title;
		public void setTitle(String title) {
			this.title = title;
		}
		public String getTitle() {return title;}
		public String toString() {
			return title;
		}
	}
	@Command
	public void dataChange(@BindingParam("data") DataObject data) {
		Clients.log(data.toString());
	}
}
