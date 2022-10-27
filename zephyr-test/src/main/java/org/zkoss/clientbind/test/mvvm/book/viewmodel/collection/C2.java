package org.zkoss.clientbind.test.mvvm.book.viewmodel.collection;

import static java.lang.System.out;

import java.util.LinkedList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;


public class C2{
	private List<String> fruitList ;

	public C2(){
		fruitList = new LinkedList<String>();
		fruitList.addAll(ListPool.getFruitList());

	}
	

	public List<String> getFruitList(){
		return new ListModelList(fruitList);
	}

	
	// -----------command -----------------
	@Command @NotifyChange("fruitList")
	public void delete(@BindingParam("index") Integer index){
		out.println(index);
		fruitList.remove(index.intValue());
		out.println("size:"+fruitList.size());
	}
}
