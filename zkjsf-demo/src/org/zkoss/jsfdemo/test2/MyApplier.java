package org.zkoss.jsfdemo.test2;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;
import org.zkoss.zul.Window;
import org.zkoss.zul.Checkbox;

public class MyApplier implements Composer, ComposerExt {
	
	Component comp;
	
	public void doAfterCompose(Component arg0) throws Exception {
		Window win = (Window)arg0;
		((Checkbox)win.getFellow("dac")).setChecked(true);
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("doAfterCompose..!"));
		this.comp = arg0;
	}

	public ComponentInfo doBeforeCompose(Page arg0, Component arg1,
			ComponentInfo arg2) {
		this.comp = arg1;
		return null;
	}

	public void doBeforeComposeChildren(Component arg0) throws Exception {
		this.comp = arg0;
	}

	public boolean doCatch(Throwable arg0) throws Exception {
	
		return false;
	}

	public void doFinally() throws Exception {
		
	}

}
