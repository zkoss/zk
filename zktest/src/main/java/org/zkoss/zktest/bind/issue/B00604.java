package org.zkoss.zktest.bind.issue;

import org.zkoss.zul.ListModelList;

/**
 * @author Dennis Chen
 * 
 */
public class B00604 {
	ListModelList<Option> options = new ListModelList<Option>();

	public B00604() {
		options = new ListModelList<Option>();
		options.add(new Option("A","X"));
		options.add(new Option("B","Y"));
		options.add(new Option("C","Z"));
	}

	public ListModelList<Option> getOptions() {
		return options;
	}
	
	static public class Option{
		String name;
		String value;
		public Option(String name,String value){
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
}
