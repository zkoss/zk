/* ListDataModelWrapper.java
 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
    Jul 25, 2007 10:03:38 AM , Created by Dennis Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.seam;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.jboss.seam.jsf.ListDataModel;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;

/**
 * You should not create this class directly.<br/>
 * This Calss is for wrapper JSF's DataModel to ZK's DataModel.
 * @author Dennis.Chen
 *
 */
public class ListDataModelWrapper extends ListDataModel implements BindingListModel{

    private ListDataModel _ldm;
    
    private HashMap indexCache = new HashMap();
    private List listeners = Collections.synchronizedList(new LinkedList());
    
    
    public int indexOf(Object obj) {
        if(obj==null) return -1;
        Integer i = (Integer)indexCache.get(obj);
        if(i==null) return -1;
        return i.intValue();
    }
    
    public ListDataModelWrapper(ListDataModel ldm){
        _ldm = ldm;
        _ldm.addDataModelListener(new MyDataModelListener());
    }
    
    
    public void addListDataListener(ListDataListener listener) {
        if(listeners.indexOf(listener)==-1){
            listeners.add(listener);
        }
    }

    public Object getElementAt(int i) {
        _ldm.setRowIndex(i);
        Object obj = _ldm.getRowData();
        if(obj!=null){
            indexCache.put(obj,new Integer(i));
        }
        return obj;
    }

    public int getSize() {
        return _ldm.getRowCount();
    }

    public void removeListDataListener(ListDataListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Fire Contents Changed Event to ListDataListener
     * @param start
     * @param end
     */
    public void fireRowChanged(int start,int end){
        new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,start,end);
    }
    /**
     * Fire Contents Added Event to ListDataListener
     * @param start
     * @param end
     */
    public void fireRowAdded(int start,int end){
        new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,start,end);
    }
    /**
     * Fire Contents Removed Event to ListDataListener
     * @param start
     * @param end
     */
    public void fireRowDeleted(int start,int end){
        new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,start,end);
    }

    protected void fireEvent(ListDataEvent event){
        int i = listeners.size();
        for(Iterator iter = listeners.iterator();iter.hasNext();){
            ((ListDataListener)iter.next()).onChange(event);
        }
    }
    
    class MyDataModelListener implements DataModelListener{
        public void rowSelected(DataModelEvent arg0) {
            // TODO , nothing can do now.
        }
    }

    /**
     * @return inner ListDataModel
     */
    public ListDataModel getInner(){
        return _ldm;
    }
    
    //  Delegate org.jboss.seam.jsf.ListDataModel 
    public void addDataModelListener(DataModelListener arg0) {
        _ldm.addDataModelListener(arg0);
    }

    public DataModelListener[] getDataModelListeners() {
        return _ldm.getDataModelListeners();
    }

    public int getRowCount() {
        return _ldm.getRowCount();
    }

    public Object getRowData() {
        return _ldm.getRowData();
    }

    public int getRowIndex() {
        return _ldm.getRowIndex();
    }

    public Object getWrappedData() {
        return _ldm.getWrappedData();
    }

    public int hashCode() {
        return _ldm.hashCode();
    }

    public boolean isRowAvailable() {
        return _ldm.isRowAvailable();
    }

    public void removeDataModelListener(DataModelListener arg0) {
        _ldm.removeDataModelListener(arg0);
    }

    public void setRowIndex(int arg0) {
        _ldm.setRowIndex(arg0);
    }

    public void setWrappedData(Object arg0) {
        _ldm.setWrappedData(arg0);
    }
    
    private void writeObject(ObjectOutputStream oos) throws IOException 
    {
       oos.writeObject( getWrappedData() );
       oos.writeInt( getRowIndex() );
    }
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException 
    {
       this.setWrappedData( ois.readObject() );
       this.setRowIndex( ois.readInt() );
    }
    //end of Delegate
    
    
    
}