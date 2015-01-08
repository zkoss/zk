/* F02584.java

	Purpose:
		
	Description:
		
	History:
		Thu, Jan 08, 2015  2:28:58 PM, Created by jumperchen

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * 
 * @author jumperchen
 */
public class F02584 {
	private String msg;
	public void setMsg(String msg) {this.msg = msg;}
	public String getMsg() {return this.msg;}
	@Command
	@NotifyChange("msg")
	public void doClick(@BindingParam("label") String msg) {
		this.msg = msg;
	}
}
