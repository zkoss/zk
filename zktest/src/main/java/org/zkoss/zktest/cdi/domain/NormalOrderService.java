package org.zkoss.zktest.cdi.domain;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Named;

@Named
public class NormalOrderService implements OrderService{

	private List<String> orderList = new LinkedList<String>();
	
	public NormalOrderService(){
		for (int i=0 ; i<10; i++){
			orderList.add("normal-order-"+i);
		}
	}
	public List<String> findAll(){
		return orderList;
	}
}
