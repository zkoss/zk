package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;

public class B02022ComponentRemoval {
	@Wire("#container")
	private Div container;
	
	int count;

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}
	
	public int getCount(){
		return count;
	}
	
	public String cat(String arg1,String arg2){
		return arg1+arg2;
	}

	@Command @NotifyChange("count")
	public void doRemoveAppend() {
		Component c = container.getChildren().get(0);
		container.removeChild(c);
		container.appendChild(c);
		count++;
	}

	@Command @NotifyChange("count")
	public void doNotify() {
		count++;
	}
}