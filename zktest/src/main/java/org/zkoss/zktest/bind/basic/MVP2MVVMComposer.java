package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.Binder;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Vbox;

public class MVP2MVVMComposer extends GenericForwardComposer<Component> {

	private Vbox mVbox;
	private Component mvvmcomp;

	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);
		mvvmcomp = Executions.createComponents("/bind/basic/mvp2mvvm_mvvm.zul", mVbox, null);
	}

	public void onClick$outerToggle1(Event event) {

		Binder binder = (Binder)mvvmcomp.getAttribute("binder");//it stored in component's attribute with default name 'binder'
		
		binder.postCommand("toggleWriteProtected", null); // post the command , so you don't access viewmodel directly
	}
	
	public void onClick$outerToggle2(Event event) {

		MVP2MVVMViewModel model = (MVP2MVVMViewModel)mvvmcomp.getAttribute("vm");// it stored in component's attribute by the name 'vm' that you assigned in mvvm.zul
		Binder binder = (Binder)mvvmcomp.getAttribute("binder");//it stored in component's attribute with default name 'binder'
		
		model.toggleWriteProtected();
		binder.notifyChange(model, "writeProtected");//notify the viewmodel.writeProtected was changed.
		
	}

}
