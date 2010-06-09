/* SelectedItemConverter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec 12 15:43:28     2006, Created by Henri
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

/**
 * Convert selected item to bean and vice versa.
 *
 * <p>Before ZK version 3.6.2(included), when use data binding with selectedItem 
 * attribute of Listbox and/or Combobox, data binder will fire "onSelect" 
 * event automatically when the page is first loaded or model is changed. 
 * This is not consistent with ZK specification: only user action shall trigger 
 * component event. So since version 3.6.3, no longer the "onSelect" event 
 * will be fired(refer to Bug 2728704). However, some already implemented 
 * application might count on such "side effects". User can specify in 
 * application's WEB-INF/zk.xml following library-property to true to make it 
 * backward compatible (i.e. still fire the "onSelect" event when page is first 
 * loaded or model is changed)</p>
 * 
 * <code><pre>	
 *	<library-property>
 *		<name>org.zkoss.zkplus.databind.onSelectWhenLoad</name>
 *		<value>true</value>
 *	</library-property>
 * </pre></code>
 * @author Henri Chen
 */
public class SelectedItemConverter implements TypeConverter, java.io.Serializable {
	private static final long serialVersionUID = 200808191439L;
	
	public Object coerceToUi(Object val, Component comp) { //load
		Listbox lbx = (Listbox) comp;
	  	if (val != null) {
	  		final ListModel xmodel = lbx.getModel();
	  		if (xmodel instanceof BindingListModel) {
	  			final BindingListModel model = (BindingListModel) xmodel;
	  			int index = model.indexOf(val);
	  			if (index >= 0) {
	    			final Listitem item = (Listitem) lbx.getItemAtIndex(index);
	    			//Bug #2728704: Listbox with databinding generates onSelect w/o user action
	    			//Shall not fire event by spec. For backward compatibility(still want to
	    			//fire onSelect event as usual), user can specifies in zk.xml
	    			//<library-property>
	    			//  <name>org.zkoss.zkplus.databind.onSelectWhenLoad</name>
	    			//  <value>true</value>
	    			//</library-property>
	    			//then data binder will still fire the onSelect event as usual.
	    			if (SelectedItemConverter.isOnSelectWhenLoad()) {
		    			final int selIndex = lbx.getSelectedIndex();
		    			
						//We need this to support load-when:onSelect when first load 
						//the page in (so it is called only once).
		  				if (item != null && selIndex != index) { // bug 1647817, avoid endless-loop
		    				Set items = new HashSet();
		    				items.add(item);
		    				//bug #2140491
		    				Executions.getCurrent().setAttribute("zkoss.zkplus.databind.ON_SELECT"+lbx.getUuid(), Boolean.TRUE);
		    				Events.postEvent(new SelectEvent("onSelect", lbx, items, item));
		    			}
	    			}
	  				return item;
	  			}
	  		} else if (xmodel == null) { //no model case, assume Listitem.value to be used with selectedItem
	  			//iterate to find the selected item assume the value (select mold)
	  			for (final Iterator it = lbx.getItems().iterator(); it.hasNext();) {
	  				final Listitem li = (Listitem) it.next();
	  				if (val.equals(li.getValue())) {
	  					return li;
	  				}
	  			}
	  		} else {
	  			throw new UiException("model of the databind listbox "+lbx+" must be an instanceof of org.zkoss.zkplus.databind.BindingListModel." + xmodel);
	  		}
	  	}
	  	return null;
	}
	private static Boolean _onSelectWhenLoad;
	/*package*/ static boolean isOnSelectWhenLoad() {
		if (_onSelectWhenLoad == null) {
			final String str = Library.getProperty("org.zkoss.zkplus.databind.onSelectWhenLoad", "false");
			_onSelectWhenLoad = Boolean.valueOf("true".equals(str));
		}
		return _onSelectWhenLoad.booleanValue();
	}
	public Object coerceToBean(Object val, Component comp) { //save
	  	final Listbox lbx = (Listbox) comp;
		if (Executions.getCurrent().getAttribute("zkoss.zkplus.databind.ON_SELECT"+lbx.getUuid()) != null) {
			//bug #2140491
			//triggered by coerceToUi(), ignore this
			Executions.getCurrent().removeAttribute("zkoss.zkplus.databind.ON_SELECT"+lbx.getUuid());
			return TypeConverter.IGNORE;
		}
	  	if (val != null) {
	  		final ListModel model = lbx.getModel();
	  		//no model case, assume Listitem.value to be used with selectedItem
	 			return model != null ? model.getElementAt(((Listitem) val).getIndex()) : ((Listitem) val).getValue();
	  	}
	 	return null;
	}
}
