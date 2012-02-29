/* CollectionIndexComposer.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**
 * @author Dennis Chen
 * 
 */
public class F00889ChildrenBindingConverter {

	String item1;
	List<String> itemList1;
	Set<String> itemSet1;
	String[] itemArray1;
	String item2;
	List<String> itemList2;
	Set<String> itemSet2;
	String[] itemArray2;
	
	

	public F00889ChildrenBindingConverter() {
		item1 = "A";
		itemList1 = new ArrayList<String>();
		itemList1.add("A");
		itemList1.add("B");
		itemList1.add("C");
		itemSet1 = new HashSet<String>(itemList1);
		itemArray1 = new String[]{"A","B","C"};
		
		
		item2 = "D";
		itemList2 = new ArrayList<String>();
		itemList2.add("D");
		itemList2.add("E");
		itemList2.add("F");
		itemSet2 = new HashSet<String>(itemList2);
		itemArray2 = new String[]{"D","E","F"};
	}


	public String getItem1() {
		return item1;
	}

	public List<String> getItemList1() {
		return itemList1;
	}

	public Set<String> getItemSet1() {
		return itemSet1;
	}

	public String[] getItemArray1() {
		return itemArray1;
	}

	public String getItem2() {
		return item2;
	}

	public List<String> getItemList2() {
		return itemList2;
	}

	public Set<String> getItemSet2() {
		return itemSet2;
	}

	public String[] getItemArray2() {
		return itemArray2;
	}

	
	public Class<Type1> getItemEnum1(){
		return Type1.class;
	}
	
	public static enum Type1 {
		A,
		B,
		C
	}
	
	public Class<Type2> getItemEnum2(){
		return Type2.class;
	}
	
	public static enum Type2 {
		D,
		E,
		F
	}

	
}
