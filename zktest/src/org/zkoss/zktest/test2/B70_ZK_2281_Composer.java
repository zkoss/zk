/* B70_ZK_2281_Composer.java

	Purpose:
		
	Description:
		
	History:
		Wed, May 07, 2014 11:39:22 AM, Created by jumperchen

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.util.*;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

/**
 * 
 * @author jumperchen
 */
public class B70_ZK_2281_Composer extends SelectorComposer<Component>{
	
	@Wire
	private Grid gridSaldiGiornalieri;
	@Wire
	private Paging paging;
	
	private int totalSize = 46;
	ListModel listModel = new ListModelList();
	List dataList = new LinkedList();
	private int pageSize = 5;
	private int activePage = 0;
	List<String> columnList = new LinkedList();
	
	public B70_ZK_2281_Composer() {
		for (int i = 0; i < totalSize ; i++){
			dataList.add("row "+i);
		}
		for (int i = 0; i < 20 ; i++){
			columnList.add("col "+i);
		}
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		updateList();
		paging.setPageSize(pageSize);
		paging.setTotalSize(totalSize);
	}
	
	
	@Listen("onPaging = #paging")
    public void paging(){        
		updateList();

		//ï¼�codes below cause issue -------------------
		Columns columns = gridSaldiGiornalieri.getColumns();
		gridSaldiGiornalieri.removeChild(columns);
		columns = new Columns();

		Column employeeColumn = new Column();
		employeeColumn.setWidth("200px");
		employeeColumn.setLabel("stampings.employee");

		columns.appendChild(employeeColumn);

		for (String giornata : columnList) {
			Column newColumn = new Column();
			newColumn.setLabel(giornata);
			newColumn.setWidth("75px");

			columns.appendChild(newColumn);
		}

		gridSaldiGiornalieri.appendChild(columns);
		//end---
    }
	
	
	private void updateList(){
		activePage = paging.getActivePage();
		int start = activePage * pageSize;
		int end = start + pageSize > dataList.size()-1 ? dataList.size() : start + pageSize; 
		listModel = new ListModelList(dataList.subList(start, end));
		gridSaldiGiornalieri.setModel(listModel);
	}

	public List getColumns() {
		return columnList;
	}

	public void setColumns(List columns) {
		this.columnList = columns;
	}
}