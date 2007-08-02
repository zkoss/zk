/* DataModelUtil.java
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

import javax.faces.model.DataModel;

import org.jboss.seam.Component;
/**
 * This class helps developers to do thing with Seam's DataModel
 * @author Dennis.Chen
 */
public class DataModelUtil {

    
    /**
     * Put selectedItem into Seam's context for DataModelSelection.<br/>
     * JSF use row index to identify the selected row in action.However, if data was changed between first request and secondary request (ex, delete or insert). 
     * when secondary request, that's say a selection clicked, then rowIndex may not equals to actual selectedItem's index.
     * 
     * @param dataModelName name of DataModel
     * @param selectionName name of DataModelSelection
     * @param selectedItem new or updated instance of bean. 
     * @param selectedIndex selected index of selection
     * @return true if selection is consistent.
     */
    static public boolean select(String dataModelName,String selectionName,Object selectedItem,int selectedIndex){
        //prepare data model (cause datamode outject by seam)
        Object obj = Component.getInstance(dataModelName);
        if(obj!=null){
            boolean setsel = checkSelection(obj,selectedItem,selectedIndex);
            if(!setsel) return false;
            //put selected item in to context, then seam can inject to some bean 
            ContextUtil.updateToContext(selectionName,selectedItem);
            return true;
        }
        return false;
        
    }
    
    /**
     * Put selectedItem into Seam's context for DataModelSelection.
     * This method will find index of selectedItem in DataModel which named dataModelName)<br/>
     * JSF use row index to identify the selected row in action.However, if data was changed between first request and secondary request (ex, delete or insert). 
     * when secondary request, that's say a selection clicked, then rowIndex may not equals to actual selectedItem's index.
     * 
     * @param dataModelName name of DataModel
     * @param selectionName name of DataModelSelection
     * @param selectedItem new or updated instance of bean. 
     * @return true if selection is consistent.
     */
    static public boolean select(String dataModelName,String selectionName,Object selectedItem){
        
        //prepare data model (cause datamode outject by seam)
        Object obj = Component.getInstance(dataModelName);
        if(obj!=null){
            boolean findsel = findSelection(obj,selectedItem);
            if(!findsel) return false;
            //put selected item in to context,then seam can inject to some bean 
            ContextUtil.updateToContext(selectionName,selectedItem);
            return true;
        }
        return false;
    }
    
    static private boolean findSelection(Object model,Object selectedItem){
        
        //We Can support fast finding method in some Implement if it have faster index search mechanism 
        if(model instanceof DataModel){
            DataModel m = ((DataModel)model);
            int size = m.getRowCount();
            for(int i=0;i<size;i++){
                m.setRowIndex(i);
                if(m.isRowAvailable()){
                    if(m.getRowData().equals(selectedItem)){
                        m.setRowIndex(i);//why set again?
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    static private boolean checkSelection(Object model,Object selectedItem,int rowIndex){
        if(model instanceof DataModel){
            DataModel m = ((DataModel)model);
            int oindex = m.getRowIndex();
            m.setRowIndex(rowIndex);
            if(m.isRowAvailable()){
                Object data = m.getRowData();
                if(data.equals(selectedItem)){
                    return true;
                }
            }
            m.setRowIndex(oindex);
        }
        return false;
    }
    
    
}
