/* TreeClickComposer.java

	Purpose:
		
	Description:
		
	History:
		Oct 28, 2010 10:46:41 AM, Created by henrichen

Copyright (C) 2010 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.test2.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.MaximizeEvent;
import org.zkoss.zk.ui.event.SizeEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Chart;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Panel;
import org.zkoss.zul.SimpleTreeModel;
import org.zkoss.zul.SimpleTreeNode;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

public class TreeClickComposer extends GenericForwardComposer
{
	private Tree tree;
	private Button btn2;
	
	SimpleTreeModel stm;
	SimpleTreeModel stm2;
	
	private void initModel(){
		ListModelList lm = new ListModelList(Arrays.asList(new String[]{"David", "Thomas","Steven"}));
		stm = new SimpleTreeModel(new SimpleTreeNode("ROOT",
				Arrays.asList(new SimpleTreeNode[]{
						new SimpleTreeNode("David",new ArrayList()), 
						new SimpleTreeNode("Thomas",new ArrayList()),
						new SimpleTreeNode("Steven",new ArrayList())})));
		
		
		ArrayList list = new ArrayList();
		ArrayList list2 = new ArrayList();
		int len = 10;
		for(int i = 0; i < len; i++) {
			list.add("item " + i);
			list2.add(new SimpleTreeNode("item " + i,new ArrayList()));
		}
		ListModelList lm2 = new ListModelList(list);
		
		stm2 = new SimpleTreeModel(
				new SimpleTreeNode("ROOT", 
						Arrays.asList(new SimpleTreeNode[]{new SimpleTreeNode("root",list2)})));			
	}
	
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		initModel();
//		tree.setModel(stm2);
	}
	
	public void onClick$btn2(Event event){
		tree.setModel(stm2);
	}
}

