package org.zkoss.zktest.test2;

import org.zkoss.zul.ListModel;
import java.util.Comparator;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Sortable;

public class B70_ZK_2377_VM {
	 
    private ListModel<String> hugeList = new FakeListModel(200);
 
    public ListModel<String> getHugeList() {
        return hugeList;
    }
}




class FakeListModel extends AbstractListModel<String> implements Sortable<String> {
    
    private static final long serialVersionUID = -3086046175152725037L;
     
    private int _size;
    private boolean _asc = true;
 
    public FakeListModel() {
        this(1000);
    }
    public FakeListModel(int size) {
        _size = size;
    }
 
    // ListModelExt //
    public void sort(Comparator<String> cmpr, boolean asc) {
        _asc = asc;
        invalidate();
    }
     
    public void invalidate() {
        fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
    }
 
    // AbstractListModel //
    public String getElementAt(int v) {
        String value = "item"+(_asc ? v: _size - v);
        return value;
    }
     
    public int getSize() {
        return _size;
    }
     
    public void setSize(int size){
        _size = size;
    }
    public String getSortDirection(Comparator<String> arg0) {
        return null;
    }
}