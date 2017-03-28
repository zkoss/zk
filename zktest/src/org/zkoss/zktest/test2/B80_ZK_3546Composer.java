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
	private int sequence = 5;
	private String templateName = "t1";

	@Override
	public void doBeforeComposeChildren(Component comp) throws Exception {
		super.doBeforeComposeChildren(comp);
		for (int i = 1; i < sequence; i++) {
			model.add("item " + i);
		}
		comp.setAttribute("model", model);
	}

	@Listen("onClick = #t1")
	public void template1() {
		templateName = "t1";
		listArea.setTemplate(templateName);
		listArea.recreate();
	}

	@Listen("onClick = #t2")
	public void template2() {
		templateName = "t2";
		listArea.setTemplate(templateName);
		listArea.recreate();
	}

	@Listen("onClick = #add")
	public void add() {
		model.add("item " + sequence);
		sequence++;
	}

	public ListModelList getModel() {
		return model;
	}

	public void setModel(ListModelList model) {
		this.model = model;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
}
