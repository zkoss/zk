package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.ListModelList;

/**
 * @author bob peng
 */
public class B85_ZK_3614_ViewModel {

	private ListModelList<String> tagsModel;

	public ListModelList<String> getTagsModel() {
		return tagsModel;
	}

	public void setTagsModel(ListModelList<String> tagsModel) {
		this.tagsModel = tagsModel;
	}

	@Command
	public void newTag(@BindingParam("tagName") String tagName) {
		tagsModel.add(tagName);
		tagsModel.addToSelection(tagName);
	}

	@Init
	public void initSetup() {
		tagsModel = new ListModelList<String>();
	}

	@Command
	public void openDialog(@ContextParam(ContextType.VIEW) Component view) {
		tagsModel.clearSelection();
		Executions.createComponents("B85-ZK-3614-dialog.zul", view, null);
	}
}
