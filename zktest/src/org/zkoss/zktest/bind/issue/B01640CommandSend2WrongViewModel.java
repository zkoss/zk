package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;

public class B01640CommandSend2WrongViewModel {

	@Wire("#workContent")
	Div workContent;

	@AfterCompose
	public void init(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Init
	public void init2() {

	}

	@Command("showChild")
	public void cmd() {
		Executions.createComponents("/bind/issue/B01640CommandSend2WrongViewModelChild.zul", workContent, null);
	}
}
