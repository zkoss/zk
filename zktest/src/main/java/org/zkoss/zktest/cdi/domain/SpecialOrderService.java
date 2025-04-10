package org.zkoss.zktest.cdi.domain;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Named;

@Named
public class SpecialOrderService implements OrderService{

	private List<String> orderList = new LinkedList<String>();
	
	public SpecialOrderService(){
		for (int i=0 ; i<10; i++){
			orderList.add("special-"+i);
		}
	}
	public List<String> findAll(){
		return orderList;
	}
}
