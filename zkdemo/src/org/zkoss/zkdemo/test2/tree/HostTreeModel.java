/* HostTreeModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 24, 2008 11:11:19 AM     2008, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
	 */
	public HostTreeModel(){
		super(new FakeRoot("Root",5));
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
	
	public void updateProcessType(int groupindex,int hostindex,int processindex,String type){
		FakeGroup group = (FakeGroup)fakeRoot.getChild(groupindex);
		FakeHost host = (FakeHost)group.getChild(hostindex);
		FakeProcess process = (FakeProcess)host.getChild(processindex);
		process.type = type;
		process.name ="AAAAA";		
		this.fireEvent(host,processindex, processindex, TreeDataEvent.CONTENTS_CHANGED);
	}

	private static int _cnt;
	public void addProcessType(int groupindex,int hostindex,int processindex,String type){
		FakeGroup group = (FakeGroup)fakeRoot.getChild(groupindex);
		FakeHost host = (FakeHost)group.getChild(hostindex);
		String id = "p1000_10000_" + ++_cnt;
		FakeProcess process = new FakeProcess(id, id, type);
		host.addProcess(processindex, process);
		this.fireEvent(host,processindex, processindex, TreeDataEvent.INTERVAL_ADDED);
	}
	
	public void removeProcessType(int groupindex,int hostindex,int processindex,String type){
		FakeGroup group = (FakeGroup)fakeRoot.getChild(groupindex);
		FakeHost host = (FakeHost)group.getChild(hostindex);
		FakeProcess process = (FakeProcess)host.removeChild(processindex);
		process.type = type;	
		this.fireEvent(host,processindex, processindex, TreeDataEvent.INTERVAL_REMOVED);

	}
	
	
	static public interface TreeNode{
		public Object getChild(int index);
		public int getChildCount();
		public boolean isLeaf();
	}
	
	static public class FakeRoot implements TreeNode{
		String name;
		int count;
		ArrayList groups = new ArrayList();
		public FakeRoot(String name,int count){
			this.name = name;
			this.count = count;
			for(int i=0;i<count;i++){
				groups.add(new FakeGroup("Group"+i,i,4));
			}
		}
		
		public String getName(){
			return name;
		}
		
		public FakeGroup[] getGroups(){
			return (FakeGroup[])groups.toArray(new FakeGroup[0]); 
		}

		public Object getChild(int index) {
			return groups.get(index);
		}

		public int getChildCount() {
			return groups.size();
		}

		public boolean isLeaf() {
			return groups.size()==0?true:false;
		}
		
	}
	
	
	static public class FakeGroup implements TreeNode{
		String name;
		int count;
		ArrayList hosts = new ArrayList();
		public FakeGroup(String name,int index,int count){
			this.name = name;
			this.count = count;
			for(int i=0;i<count;i++){
				hosts.add(new FakeHost("host"+index+"_"+i,"host-"+i,i,index,10));
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
		public FakeHost(String id,String name,int hostindex,int groupindex,int count){
			this.id = id;
			this.name = name;
			this.hostindex = hostindex;
			this.count = count;
			for(int i=0;i<count;i++){
				processes.add(new FakeProcess("p"+hostindex+groupindex+"_"+i,"p-"+hostindex+"_"+i,(i%3==0)?"A":"B"));
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
		public Object removeChild(int index) {
			return processes.remove(index);
		}

		public int getChildCount() {
			return processes.size();
		}

		public boolean isLeaf() {
			return processes.size()==0?true:false;
		}
		
		public void addProcess(FakeProcess fp){
			processes.add(fp);
		}
		public void addProcess(int j, FakeProcess fp){
			processes.add(j, fp);
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
