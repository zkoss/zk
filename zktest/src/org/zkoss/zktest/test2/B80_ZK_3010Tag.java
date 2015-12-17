package org.zkoss.zktest.test2;

import java.util.Date;

public class B80_ZK_3010Tag implements Comparable<B80_ZK_3010Tag> {
	
	private String name;

	private String lowerCaseName;
	
	private Date created;

	@SuppressWarnings("unused")
	private B80_ZK_3010Tag() {
	}

	public B80_ZK_3010Tag(String name) {
		this.name = name;
		this.lowerCaseName = name.toLowerCase();
		this.created = new Date();
	}

	public Date getCreated() {
		return created == null ? null : new Date();
	}

	public String getLowerCaseName() {
		return lowerCaseName;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unused")
	private void setLowerCaseName(String lowerCaseName) {
		this.lowerCaseName = lowerCaseName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((lowerCaseName == null) ? 0 : lowerCaseName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		B80_ZK_3010Tag other = (B80_ZK_3010Tag) obj;
		if (lowerCaseName == null) {
			if (other.lowerCaseName != null)
				return false;
		} else if (!lowerCaseName.equals(other.lowerCaseName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(B80_ZK_3010Tag o) {
		return getLowerCaseName().compareTo(o.getLowerCaseName());
	}
}