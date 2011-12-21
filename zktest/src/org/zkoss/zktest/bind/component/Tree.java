package org.zkoss.zktest.bind.component;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.AbstractTreeModel;



public class Tree{

	public class MyModel extends AbstractTreeModel {
	    public MyModel() {
	        super("Root");
	    }
	    public boolean isLeaf(Object node) {
	        return getLevel((String)node) >= 2; //at most 4 levels
	    }
	    public Object getChild(Object parent, int index) {
	        return parent + "." + index;
	    }
	    public int getChildCount(Object parent) {
	        return isLeaf(parent) ? 0: 2; //each node has 5 children
	    }
	    public int getIndexOfChild(Object parent, Object child) {
	        String data = (String)child;
	        int i = data.lastIndexOf('.');
	        return Integer.parseInt(data.substring(i + 1));
	    }
	    private int getLevel(String data) {
	        for (int i = -1, level = 0;; ++level)
	            if ((i = data.indexOf('.', i + 1)) < 0)
	                return level;
	    }
		
	}
	private String selected="init";
	private boolean open =false;
	
	private MyModel model;
	
	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public AbstractTreeModel<String> getModel(){
		model = new MyModel();
		return model;
	}
	// -----------command -----------------
	@Command @NotifyChange("*")
	public void open(){
		open = true;
	}

}
