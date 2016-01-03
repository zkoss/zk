/* B80_ZK_3026VM.java

	Purpose:
		
	Description:
		
	History:
		Sun Jan 3 10:00:28 CST 2016, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;

import java.io.*;

/**
 * @author jameschu
 */
public class B80_ZK_3026VM implements Serializable {

	ListModelList<String> list = new ListModelList<>();

	@Init
	public void init(){
		list.add("A");
		list.add("B");
		list.add("C");
		list.add("D");
		list.add("E");
	}

	@Command
	public void add(){
		list.add(0, "A");
	}
	@Command
	public void remove(){
		list.remove(0);
	}

	public ListModelList getList(){
		return list;
	}
}
