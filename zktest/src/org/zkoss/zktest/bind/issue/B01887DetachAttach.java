package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.impl.AnnotateBinderHelper;
import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;

public class B01887DetachAttach {

	private VM entityVM;
	private VM listVM;
	
	@Wire("#container")
	private Div container;
	
	@Wire("#dyn1")
	private Div dyn1;
	
	@Wire("#dyn2")
	private Div dyn2;
	private Component view;
	
	@Init
	public void init() {
		entityVM = new VM();
		listVM = new VM();
		
		entityVM.setInc("/bind/issue/B01887DetachAttachInner1.zul");
		listVM.setInc("/bind/issue/B01887DetachAttachInner2.zul");
	}
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		this.view = view;
		Selectors.wireComponents(view, this, false);

		enableComponent(dyn1);
	}
	
	@Command("enableDyn1") 
	public void enableDyn1() {
		enableComponent(dyn1);
	}
	
	@Command("enableDyn2") 
	public void enableDyn2() {
		enableComponent(dyn2);
	}
	
	public void enableComponent(Component comp) {
		//detach all components first, as described by matthias
		dyn1.detach();
		dyn2.detach();
		//when called from afterCompose works in 6.5.2 but not in 6.5.3/.4 
		container.appendChild(comp);
		
		container.invalidate(); //resize and re-flex - re flex after visibility change is the actual issue !!!
	}
	
	public VM getEntityVM() {
		return entityVM;
	}

	public VM getListVM() {
		return listVM;
	}

	public static class VM {
		
		private String inc;

		public String getInc() {
			return inc;
		}

		public void setInc(String inc) {
			this.inc = inc;
		}
	}
}
