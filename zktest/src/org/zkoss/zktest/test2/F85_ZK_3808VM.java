/* F85_ZK_3808VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 16 14:48:34 CST 2018, Created by wenninghsu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Init;

/**
 * 
 * @author wenninghsu
 */
public class F85_ZK_3808VM {

	private List<List<String>> data = new ArrayList<List<String>>();
	private List<String> columns = new ArrayList<String>();
	private List<String> headers = new ArrayList<String>();

	@Init
	public void init() {
		for (int i = 0; i < 8; i++) {
			headers.add("Header " + i);
			for (int j = 0; j < 2; j++) {
				columns.add("Dynamic Col " + i + "/" + j);
			}
		}

		for (int i = 0; i < 2000; i++) {
			ArrayList<String> dataLine = new ArrayList<String>();
			for (int j = 0; j < 16; j++) {
				dataLine.add("Data " + i + "/" + j);
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
