package org.zkoss.zul;

import java.util.ArrayList;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.event.TreeDataListener;
import org.zkoss.zul.event.TreeDataEvent;

public class SimpleTreeModel extends AbstractTreeModel{
	
	
	
	public SimpleTreeModel(Object obj)
	{
		super.setRoot(obj);
	}
	
	public int getChildCount(Object parent) {
		
		if(isLeaf(parent))
			return -1;
		else
		{
			ArrayList al = (ArrayList)parent;
			return al.size();
		}
	}

	public boolean isLeaf(Object node) {
		boolean isLeaf =!(node instanceof ArrayList);
		if(!isLeaf){
			return (((ArrayList)node).size() == 0);
		}
		return isLeaf;
	}

	public Object getChild(Object parent, int index) {
		
		ArrayList al = (ArrayList)parent;
		return al.get(index);
	}

	public Object getRoot() {
		return super.getRoot();
	}
	
	//TODO 
	public void set(Object parent, int index, Object value)
	{
		ArrayList al = (ArrayList)parent;
		al.set(index, value);
		fireEvent(parent,index,TreeDataEvent.CONTENTS_CHANGED);
	}
	
	public void remove(Object parent, int index){
		ArrayList al = (ArrayList)parent;	
		try
		{
			al.remove(index);
			fireEvent(parent,index,TreeDataEvent.NODE_REMOVED);
		}
		catch(Exception exp)
		{
			throw new IndexOutOfBoundsException("Out of bound: "+index+" while size="+al.size());
		}
		
	}
	
	public void add(Object parent, Object newNode)
	{
		ArrayList al = (ArrayList)parent;
		al.add(newNode);
		fireEvent(parent,al.size()-1,TreeDataEvent.NODE_ADDED);
	}
	
	public void insert(Object parent, int index, Object newNode){
		ArrayList al = (ArrayList)parent;
		al.add(index, newNode);
		System.out.println(al);
		fireEvent(parent,index,TreeDataEvent.NODE_ADDED);
	}
}

