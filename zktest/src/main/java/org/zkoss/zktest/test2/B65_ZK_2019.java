package org.zkoss.zktest.test2;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModels;


public class B65_ZK_2019 {

	private ListModelList<String> contactsModel;

	
	@Init
	public void init() {
		contactsModel = new ListModelList<String>(Email.getContacts(), false);
	}
	
    public ListModel<String> getModel() {
        return ListModels.toListSubModel(contactsModel, new CustomerSortComparator(), 100);
    }
 
	public class CustomerSortComparator implements Comparator<String> {
		public int compare(String paramT1, String paramT2) {
			if (StringUtils.isEmpty(paramT1)) {
				return -1;
			}
			boolean matching = paramT2.indexOf(paramT1) >= 0;
			if (matching) {
				return 0;
			} else {
				return -1;
			}
		}
	}
	 
	public static class Email{
	 
	    public static List<String> getContacts() {
	        return Arrays.asList("Adam (adam@company.org)",
	                "Chris (chris@company.org)", "Daniel (daniel@company.org)",
	                "Eve(eve@company.org)", "Fritz (fritz@company.org)",
	                "Mary (mary@company.org)", "Max (max@company.org)",
	                "John (john@company.org)", "Peter (peter@company.org)");
	    }
	    
	}
	
}
