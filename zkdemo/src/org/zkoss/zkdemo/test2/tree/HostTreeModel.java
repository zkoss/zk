/* HostTreeModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 24, 2008 11:11:19 AM     2008, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2.tree;

import java.util.ArrayList;

import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.event.TreeDataEvent;
/**
 * @author Dennis.Chen
 *
 */
public class HostTreeModel extends AbstractTreeModel{
	
	
	private FakeRoot fakeRoot;
	
	/**
	 * Constructor
	 * @param tree the list is contained all data of nodes.
	 */
	public HostTreeModel(){
		super(new FakeRoot("Room1",5));
		fakeRoot = (FakeRoot)this.getRoot();
	}
	
	//-- TreeModel --//
	public Object getChild(Object parent, int index) {
		return ((TreeNode)parent).getChild(index);
	}
	
	//-- TreeModel --//
	public int getChildCount(Object parent) {
		return ((TreeNode)parent).getChildCount();
	}
	
	//-- TreeModel --//
	public boolean isLeaf(Object node) {
		return ((TreeNode)node).isLeaf();
	}
	
	public void updateProcessType(int hostindex,int processindex,String type){
		FakeHost host = (FakeHost)fakeRoot.getChild(hostindex);
		FakeProcess process = (FakeProcess)host.getChild(processindex);
		process.type = type;
		this.fireEvent(host,processindex, processindex, TreeDataEvent.CONTENTS_CHANGED);
	}
	
	
	static public interface TreeNode{
		public Object getChild(int index);
		public int getChildCount();
		public boolean isLeaf();
	}
	
	static public class FakeRoot implements TreeNode{
		String name;
		int count;
		ArrayList hosts = new ArrayList();
		public FakeRoot(String name,int count){
			this.name = name;
			this.count = count;
			for(int i=0;i<count;i++){
				hosts.add(new FakeHost("host"+i,"host-"+i,i,10));
			}
		}
		
		public String getName(){
			return name;
		}
		
		public FakeHost[] getHosts(){
			return (FakeHost[])hosts.toArray(new FakeHost[0]); 
		}

		public Object getChild(int index) {
			return hosts.get(index);
		}

		public int getChildCount() {
			return hosts.size();
		}

		public boolean isLeaf() {
			return hosts.size()==0?true:false;
		}
		
	}
	
	static public class FakeHost implements TreeNode{
		String name;
		int hostindex;
		int count;
		String id;
		ArrayList processes = new ArrayList();
		public FakeHost(String id,String name,int hostindex,int count){
			this.id = id;
			this.name = name;
			this.hostindex = hostindex;
			this.count = count;
			for(int i=0;i<count;i++){
				processes.add(new FakeProcess("p"+hostindex+"_"+i,"p-"+hostindex+"_"+i,(i%3==0)?"A":"B"));
			}
		}
		
		
		public String getId(){
			return id;
		}
		
		public String getName(){
			return name;
		}
		
		public FakeProcess[] getProcess(){
			return (FakeProcess[])processes.toArray(new FakeProcess[0]); 
		}
		
		public Object getChild(int index) {
			return processes.get(index);
		}

		public int getChildCount() {
			return processes.size();
		}

		public boolean isLeaf() {
			return processes.size()==0?true:false;
		}
		
	}
	
	static public class FakeProcess  implements TreeNode{
		String name;
		String id;
		String type;
		public FakeProcess(String id,String name,String type){
			this.id = id;
			this.name = name;
			this.type = type;
		}
		
		public String getId(){
			return id;
		}
		
		public String getType(){
			return type;
		}
		
		public String getName(){
			return name;
		}
		public Object getChild(int index) {
			return null;
		}

		public int getChildCount() {
			return 0;
		}

		public boolean isLeaf() {
			return true;
		}
	}

}
