package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;

public class B80_ZK_3107VM {

	private String name = "Peter";
	private String info = "orz";
        
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@DependsOn("name")
    public String getNameUpper() {
		return getName().toUpperCase();
	}
	@Command
	@NotifyChange("name")
	public void change() {
		name = "Hi";
	}

	public int calcNameLength(String name) {
		return name.length();
	}
	public int call(int i) {
		return i;
	}

	public String getInfo() {
		return info;
	}
}
