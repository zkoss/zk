package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Listbox;

public class B70_ZK_2217_ViewModel {

	private List<List<String>> data = new ArrayList<List<String>>();
	private List<String> columns = new ArrayList<String>();
	private List<String> headers = new ArrayList<String>();
	@Wire
	private Auxhead auxh;
	@Wire
	private Listbox LB;

	@AfterCompose
	public void init(@ContextParam(ContextType.VIEW) Component view) {
		// enlaza los componentes
		Selectors.wireComponents(view, this, false);
		// enlaza eventos
		Selectors.wireEventListeners(view, this);
		Selectors.wireVariables(view, this, null);
		for (int i = 0; i < 4; i++) {
			headers.add("Header " + i);
			for (int j = 0; j < 2; j++) {
				columns.add("Dynamic Col " + i + "/" + j);
			}
		}

		LB.getPagingChild().setAutohide(false);

	}

	@Command("loadData")
	@NotifyChange("data")
	public void loadData() {
		data.clear();
		for (int i = 0; i < 20; i++) {
			ArrayList<String> dataLine = new ArrayList<String>();
			for (int j = 0; j < 8; j++) {
				dataLine.add("Data " + i + "/" + j);
			}
			data.add(dataLine);
		}
	}

	@Command("switchAuxhead")
	public void switchAuxhead() {
		auxh.setVisible(!auxh.isVisible());
		LB.invalidate();
	}

	public List<String> getHeaders() {
		return headers;
	}

	public void setHeadList(List<String> headers) {
		this.headers = headers;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<List<String>> getData() {
		return data;
	}

	public void setData(List<List<String>> dataList) {
		this.data = dataList;
	}
}