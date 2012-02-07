package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class Function {

	String foo = "foo";
	int i = 0;

	public String foo() {
		return foo;
	}

	public String bar(String bar) {
		return foo + ":" + bar;
	}

	public String cat(String a, String b) {
		return foo + ":" + a + ":" + b;
	}
	
	public String getFoo(){
		return foo;
	}

	@Command
	@NotifyChange("foo")
	public void cmd1() {
		foo = "foo" + i++;
	}

	@Command
	@NotifyChange({ "foo", "bar" })
	public void cmd2() {
		foo = "foo" + i++;
	}
	
	@Command
	@NotifyChange({ "cat"})
	public void cmd3() {
		foo = "foo" + i++;
	}
}
