package org.zkoss.zktest.test2;

import javax.servlet.ServletRequest;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

public class F65_ZK_2012_Composer extends SelectorComposer<Component> {

	@Wire
	Textbox ff;
	@Wire
	Textbox get;
	@Wire
	Textbox getcomp;
	
	@Listen("onClick=#button")
	public void dbclick(MouseEvent evt) {
		ServletRequest req = (ServletRequest) Executions.getCurrent().getNativeRequest();
		ff.setValue("" + Servlets.isBrowser(req, "ff"));
		get.setValue(Servlets.getBrowser(req));
	}
}
