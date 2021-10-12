package org.zkoss.zktest.test2;

public class B70_ZK_2460_Contact {
	private final String name;
	private final String category;

	public B70_ZK_2460_Contact(String category) {
		this.category = category;
		this.name = null;
		this.profilepic = null;
	}

	public B70_ZK_2460_Contact(String name, String profilepic) {
		this.name = name;
		this.profilepic = profilepic;
		this.category = null;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public String getProfilepic() {
		return profilepic;
	}

	private final String profilepic;
}
