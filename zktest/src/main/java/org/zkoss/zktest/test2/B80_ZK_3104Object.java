package org.zkoss.zktest.test2;

public class B80_ZK_3104Object {
	private String name;

	public B80_ZK_3104Object() {
	}
	public B80_ZK_3104Object(String type) {
			this.type = type;
		}

	public void setType(String type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

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
