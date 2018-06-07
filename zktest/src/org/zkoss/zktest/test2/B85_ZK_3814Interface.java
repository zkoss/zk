package org.zkoss.zktest.test2;

/**
 * @author rudyhuang
 */
public interface B85_ZK_3814Interface {
	String getFirstName();

	String getLastName();

	default String getName() {
		return getFirstName() + " " + getLastName();
	}
}
