package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Init;

public class B86_ZK_4248VM {
	private List<List<String>> data = new ArrayList<List<String>>();
	private List<String> columns = new ArrayList<String>();
	private List<String> headers = new ArrayList<String>();

	@Init
	public void init(){
		int cols = 5;
		int rows = 2000;

		for (int i = 0; i< 1; i++) {
			headers.add("Header "+i);
			for (int j = 0; j< cols; j++) {
				columns.add("Dynamic Col "+i+"/"+j);
			}
		}

		for (int i = 0; i< rows; i++) {
			ArrayList<String> dataLine = new ArrayList<String>();
			for (int j = 0; j< cols; j++) {
				dataLine.add("Data "+i+"/"+j);
			}
			data.add(dataLine);
		}

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
