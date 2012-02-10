package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

//@Wire("vmex")
public class F00638 extends F00638Base3{

	String value2;
	public String getValue2() {
		return value2;
	}
	@NotifyChange
	public void setValue2(String value2) {
		this.value2 = value2;
	}

	//component
	@Wire("#l31")
	Label label;
	@Wire("#t31")
	Textbox textbox;
	
	 
	@Init(superclass=true)
	public void init(@ContextParam(ContextType.BINDER) Binder binder){
		this.value2 = "B";
//		Component r = ctx.getComponent();
		Component r = binder.getView();
		Selectors.wireComponents(r, this,false);
//		Selectors.wireVariables(r, this, null);
		Selectors.wireEventListeners(r, this);
		//to wire vm as vmex
//		Selectors.wireController(r, this);
		
		label.setValue("C");
		textbox.setValue("D");
	}
	
	@Listen("onClick = #btn1")
	public void onClick(){
		label.setValue("E");
		textbox.setValue("F");
	}
}
