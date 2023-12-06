package org.zkoss.zktest.test2.B100_ZK_5535;

public class BugFieldLayout {
private String stringVal;

public String getStringVal() {
	return stringVal;
}

public void setStringVal(String stringVal) {
	this.stringVal = stringVal;
}
public void detach()
{
	this.stringVal=null;
}
}
