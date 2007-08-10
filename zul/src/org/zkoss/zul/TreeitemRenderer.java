package org.zkoss.zul;

public interface TreeitemRenderer {
	
	public void render(Treeitem item, Object data) throws Exception;
	
	//TODO: fix this after adding render class to tree, make sure this function is needed
	
	public void render(Treeitem item) throws Exception;
}
