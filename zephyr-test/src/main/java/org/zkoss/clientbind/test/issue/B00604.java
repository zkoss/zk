package org.zkoss.clientbind.test.issue;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

/**
 * @author Dennis Chen
 * 
 */
public class B00604 {
	ListModelList<Option> options = new ListModelList<Option>();
	private String src;

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

	public String getSrc() {
		return src;
	}

	@Command
	@NotifyChange("src")
	public void updateSrc() {
		src = "./B00604-1.zul";
	}
}
