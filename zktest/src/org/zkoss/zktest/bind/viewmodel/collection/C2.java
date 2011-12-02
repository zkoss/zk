package org.zkoss.zktest.bind.viewmodel.collection;

import static java.lang.System.out;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.Param;
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
	public void delete(@Param("index") Integer index){
		out.println(index);
		fruitList.remove(index.intValue());
		out.println("size:"+fruitList.size());
	}
}
