/** B70_ZK_2524ViewModel.java.

	Purpose:
		
	Description:
		
	History:
		4:46:27 PM Nov 18, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jumperchen
 *
 */
public class B70_ZK_2524ViewModel {
	 public class Item {
	        private final String name;

	        public Item(String name) {
	            this.name = name;
	        }

	        public String getName() {
	            Clients.log("getName()");
	            return name;
	        }
	    }

	    private List<Item> _model = new ArrayList<Item>();
	    
	    @Init
	    public void init() {
	        _model.add(new Item("test"));
	    }
	    
	    public List<Item> getModel() {
	        return _model;
	    }
}
