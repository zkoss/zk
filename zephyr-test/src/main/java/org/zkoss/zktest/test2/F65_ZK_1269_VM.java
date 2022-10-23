package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class F65_ZK_1269_VM {
	private static final String H = "horizontal";
	private static final String V = "vertical";
	private F65_ZK_1269ValueObject[] child;
	private int index = 1;
	private String orient = H;

	public F65_ZK_1269_VM() {
		child = new F65_ZK_1269ValueObject[5];
		for (int i = 0; i < child.length; i++) {
			F65_ZK_1269ValueObject vo = new F65_ZK_1269ValueObject();
			vo.setImage("http://www.gravatar.com/avatar/" + i + "?d=identicon");
			vo.setContent("content " + i);
			vo.setTitle("Title " + i);
			child[i] = vo;
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int value) {
		this.index = value;
	}

	public F65_ZK_1269ValueObject[] getChild() {
		return child;
	}

	public String getOrient() {
		return orient;
	}

	@Command
	@NotifyChange("*")
	public void change(@BindingParam("index") int value) {
		setIndex(value);
	}

	@Command
	@NotifyChange("*")
	public void previous() {
		setIndex(index - 1);
	}

	@Command
	@NotifyChange("*")
	public void next() {
		setIndex(index + 1);
	}

	@Command
	@NotifyChange("*")
	public void changeOrient() {
		orient = H.equals(orient) ? V : H;
	}
}