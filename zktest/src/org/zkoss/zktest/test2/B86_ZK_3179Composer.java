/* B86_ZK_3179Composer.java

        Purpose:
                
        Description:
                
        History:
                Thu Sep 06 17:33:20 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Toolbarbutton;

public class B86_ZK_3179Composer extends SelectorComposer {

	private int newChildCount = 0;
	@Wire
	private Toolbar toolbar;
	@Wire
	private Combobox combobox;

	@Listen("onClick = #toggleOverflow")
	public void toggleOverflow() {
		toolbar.setOverflowPopup(!toolbar.isOverflowPopup());
	}

	@Listen("onClick = #isOverflow")
	public void isOverflow() {
		Clients.log(toolbar.isOverflowPopup() + "");
	}

	@Listen("onClick = #setWidth200")
	public void setWidth200() {
		toolbar.setWidth("200px");
	}

	@Listen("onClick = #resetWidth")
	public void resetWidth() {
		toolbar.setWidth("");
	}

	@Listen("onClick = #increaseChild5Width")
	public void increaseChild5Width() {
		((HtmlBasedComponent) toolbar.getChildren().get(4)).setWidth("200px");
	}

	@Listen("onClick = #resetChild5Width")
	public void resetChild5Width() {
		((HtmlBasedComponent) toolbar.getChildren().get(4)).setWidth("");
	}

	@Listen("onClick = #insertBefore5")
	public void insertBefore5() {
		toolbar.insertBefore(new Toolbarbutton("new child " + newChildCount++, "img/home.gif"), toolbar.getChildren().get(4));
	}

	@Listen("onClick = #appendChild")
	public void appendChild() {
		toolbar.appendChild(new Toolbarbutton("new child " + newChildCount++, "img/home.gif"));
	}

	@Listen("onClick = #removeSecondChild")
	public void removeSecondChild() {
		toolbar.removeChild(toolbar.getChildren().get(1));
	}

	@Listen("onClick = #removeFirstChild")
	public void removeFirstChild() {
		toolbar.removeChild(toolbar.getFirstChild());
	}

	@Listen("onClick = #removeLastChild")
	public void removeLastChild() {
		toolbar.removeChild(toolbar.getLastChild());
	}

	@Listen("onChange = #combobox")
	public void changeTheme(InputEvent event) {
		Library.setProperty("org.zkoss.theme.preferred", event.getValue());
		Executions.sendRedirect("");
	}
}
