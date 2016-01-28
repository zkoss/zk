package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.util.Clients;

public class B80_ZK_3104VM {

	private List<String> origList = new ArrayList<>(Arrays.asList("\"Peter\"")); 
	private List<String> emptyList = new ArrayList<>(); 
	
	private Pojo origPojo = new Pojo();
	
	@Command
	public void testEmptyList(@BindingParam("list") List<String> list) {
		list.add("\"Monty\"");
		Clients.log(String.valueOf(list.toString().equals(emptyList.toString())));
	}

	@Command
	public void testList(@BindingParam("list") List<String> list) {
		list.add("\"Monty\"");
		Clients.log(String.valueOf(list.toString().equals(origList.toString())));
	}

	@Command
	public void testPojo(@BindingParam("pojo") Pojo pojo) {
		Clients.log(String.valueOf(pojo.toString().equals(origPojo.toString())));
	}

	public List<String> getOrigList() {
		return origList;
	}
	
	public List<String> getEmptyList() {
		return emptyList;
	}

	public Pojo getOrigPojo() {
		return origPojo;
	}

	public class Pojo {
		private String name;
		private String type = "withoutName";
		public String getName() {
			return name;
		}
		public String getType() {
			return type;
		}
		@Override
		public String toString() {
			return getName() + " " + getType();
		}
	}
}
