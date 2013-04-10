package org.zkoss.zktest.bind.issue;

public class B01699IncludeMultipleTimesInner2 {

	public int fooCounter = 0;
	public int barCounter = 0;
	private String foo = "FOO";
	private String bar = "BAR";

	public String getFoo() {
		fooCounter++;
		return foo + "_" + fooCounter;
	}

	public String getBar() {
		barCounter++;
		return bar + "_" + barCounter;
	}

}
