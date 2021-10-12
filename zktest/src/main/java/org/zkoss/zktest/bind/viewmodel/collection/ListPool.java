package org.zkoss.zktest.bind.viewmodel.collection;

import org.zkoss.zul.ListModelList;

public class ListPool {

	static public String [] listName = {"Fruit", "Car Mark"};
	static public String [] fruits ={"Apple", "Orange", "Strawberry","Bananna", "Watermalon"};
	static public String [] carMarks ={"TOYOTA", "Nissan", "Luxgen","Honda", "Ford","Mitsubishi","Lexus","Audi","Benz"};

	static public ListModelList<String> getListNameList(){
		return new ListModelList<String>(listName);
	}
	
	static public ListModelList<String> getFruitList(){
		return new ListModelList<String>(fruits);
	}

	static public ListModelList<String> getCarMarkList(){
		return new ListModelList<String>(carMarks);
	}
}
