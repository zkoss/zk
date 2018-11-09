/* F86_ZK_4122Composer.java

        Purpose:
                
        Description:
                
        History:
                Thu Nov 08 14:49:06 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.Orgchildren;
import org.zkoss.zkmax.zul.Orgitem;
import org.zkoss.zul.Vlayout;

public class F86_ZK_4122Composer extends SelectorComposer<Vlayout> {

	@Wire
	private Orgitem item1;
	@Wire
	private Orgitem item5;
	private Orgitem newItem;
	private Orgchildren orgchildren;

	@Listen("onClick = #toggleItem1")
	public void toggleItem1() {
		item1.setOpen(!item1.isOpen());
	}

	@Listen("onClick = #appendOrgitem")
	public void appendOrgitem() {
		if (newItem == null)
			newItem = new Orgitem("New item");
		item1.getOrgchildren().appendChild(newItem);
	}

	@Listen("onClick = #removeOrgitem")
	public void removeOrgitem() {
		if (newItem != null)
			item1.getOrgchildren().removeChild(newItem);
	}

	@Listen("onClick = #toggleItem5")
	public void toggleItem5() {
		item5.setOpen(!item5.isOpen());
	}

	@Listen("onClick = #appendOrgchildren")
	public void appendOrgchildren() {
		if (orgchildren == null) {
			orgchildren = new Orgchildren();
			orgchildren.appendChild(new Orgitem("New item"));
			orgchildren.appendChild(new Orgitem("New item"));
		}
		item5.appendChild(orgchildren);
	}

	@Listen("onClick = #removeOrgchildren")
	public void removeOrgchildren() {
		if (orgchildren != null)
			item5.removeChild(orgchildren);
	}
}
