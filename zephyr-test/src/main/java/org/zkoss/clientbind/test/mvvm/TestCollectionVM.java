package org.zkoss.clientbind.test.mvvm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.zkoss.bind.annotation.Init;

/**
 * @author jameschu
 */
public class TestCollectionVM {
	private static List<Integer> list = new ArrayList<>();

	private List<List<String>> nestedList = new ArrayList<>();
	@Init
	public void init() {
		for (int i = 0; i < 3000; i++) {
			list.add(i);
		}
		List<String> innerList = new ArrayList<>();
		innerList.add("1-1");
		innerList.add("1-2");
		innerList.add("1-3");
		nestedList.add(innerList);

		innerList = new ArrayList<>();
		innerList.add("2-1");
		innerList.add("2-2");
		innerList.add("2-3");
		nestedList.add(innerList);
	}
	public List<Integer> getList() {
		return list;
	}

	public List<List<String>> getNestedList() {
		return nestedList;
	}
}
