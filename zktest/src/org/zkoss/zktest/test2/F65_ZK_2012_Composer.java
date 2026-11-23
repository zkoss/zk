package org.zkoss.zktest.test2;

import java.util.Arrays;

import javax.servlet.ServletRequest;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

public class F65_ZK_2012_Composer extends SelectorComposer<Component> {

	@Wire
	Textbox ie;
	@Wire
	Textbox ff;
	@Wire
	Textbox ie11;
	@Wire
	Textbox get;
	@Wire
	Textbox getcomp;
	
	@Listen("onClick=#button")
	public void dbclick(MouseEvent evt) {
		ServletRequest req = (ServletRequest) Executions.getCurrent().getNativeRequest();
		ie.setValue("" + Servlets.isBrowser(req, "ie"));
		ff.setValue("" + Servlets.isBrowser(req, "ff"));
		ie11.setValue("" + Servlets.isBrowser(req, "ie11"));
		get.setValue(Servlets.getBrowser(req));
		getcomp.setValue(Arrays.toString(Servlets.getIECompatibilityInfo(req)));
	}
}
