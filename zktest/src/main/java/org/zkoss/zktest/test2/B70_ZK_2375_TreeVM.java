package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.AbstractTreeModel;

public class B70_ZK_2375_TreeVM {
	
    private BinaryTreeModel<String> model;
    private int totalsize = 0;
    
    @Init
    public void init() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 1000; i++) {
            list.add("" + i);
        }
        model = new BinaryTreeModel<String>(list);
    }
    public BinaryTreeModel<String> getModel() {
        return model;
    }

    public void setModel(BinaryTreeModel<String> model) {
        this.model = model;
    }
	
    public int getTotalsize() {
		return totalsize;
	}
	
    public void setTotalsize(int totalsize) {
		this.totalsize = totalsize;
	}
}

class BinaryTreeModel<T> extends AbstractTreeModel<T> {

    private static final long serialVersionUID = 5067310002210333471L;
    private List<T> _tree = null;

    public BinaryTreeModel(List<T> tree) {
        super(tree.get(0));
        _tree = tree;
    }

    public boolean isLeaf(Object node) {
        return (getChildCount(node) == 0);
    }

    public T getChild(Object parent, int index) {
        int i = _tree.indexOf(parent) * 2 + 1 + index;
        if (i >= _tree.size())
            return null;
        else
            return _tree.get(i);
    }

    public int getChildCount(Object parent) {
        int count = 0;
        if (getChild(parent, 0) != null)
            count++;
        if (getChild(parent, 1) != null)
            count++;
        return count;
    }

    public int getIndexOfChild(Object arg0, Object arg1) {
        return 0;
    }
}
