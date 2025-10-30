package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

public class B80_ZK_3196Object {
	private String name;
	private List<B80_ZK_3196Object> allFriends = new ArrayList<B80_ZK_3196Object>();
	private B80_ZK_3196Object favoriteBuddy;

	public B80_ZK_3196Object() {}

	public B80_ZK_3196Object(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<B80_ZK_3196Object> getAllFriends() {
		return allFriends;
	}

	public B80_ZK_3196Object getFavoriteBuddy() {
		return favoriteBuddy;
	}

	public void setFavoriteBuddy(B80_ZK_3196Object favoriteBuddy) {
		this.favoriteBuddy = favoriteBuddy;
	}

	@Override
	public String toString() {
			return "B80_ZK_3196Object [name=" + this.getName() + "]";
		}
}
