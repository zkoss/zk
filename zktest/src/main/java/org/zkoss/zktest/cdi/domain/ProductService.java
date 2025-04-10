package org.zkoss.zktest.cdi.domain;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Named;

@Named
public class ProductService {

	private List<String> productList = new LinkedList<String>();
	
	public List<String> findAll(){
		return productList;
	}
}
