package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zuti.zul.Apply;

public class B80_ZK_3546Composer extends SelectorComposer<Component> {


	@Wire("::shadow")
	private Apply listArea;
	private ListModelList model = new ListModelList();
	private ListModelList submodel = new ListModelList();
	private int sequence = 3;
	private String templateName = "t1";

	@Override
	public void doBeforeComposeChildren(Component comp) throws Exception {
		super.doBeforeComposeChildren(comp);
		for (int i = 1; i < sequence; i++) {
			model.add("item " + i);
			submodel.add("inside " + i);
		}
		comp.setAttribute("model", model);
		comp.setAttribute("submodel", submodel);
	}

	@Listen("onClick = #t1")
	public void template1() {
		templateName = "t1";
		listArea.setTemplate(templateName);
		listArea.recreate();
	}

	@Listen("onClick = #add")
	public void add() {
		model.add("item " + sequence);
		submodel.add("inside " + sequence);
		sequence++;
	}

	public ListModelList getModel() {
		return model;
	}

	public void setModel(ListModelList model) {
		this.model = model;
	}

	public ListModelList getSubmodel() {
		return submodel;
	}

	public void setSubmodel(ListModelList submodel) {
		this.submodel = submodel;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
}
