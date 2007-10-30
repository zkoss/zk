package org;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Button;

public class MyButton extends Button {
	public String test(){
		return Executions.getCurrent().getDesktop().getSession().getWebApp().getRealPath("/");
	}
}
