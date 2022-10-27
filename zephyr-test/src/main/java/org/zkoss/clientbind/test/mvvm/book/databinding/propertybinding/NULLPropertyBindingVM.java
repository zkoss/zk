package org.zkoss.clientbind.test.mvvm.book.databinding.propertybinding;

/**
 * @author jameschu
 */
public class NULLPropertyBindingVM {
	private String testStr = "test";
	private String testNULLStr;
	private User user;

	public String getTestStr() {
		return testStr;
	}

	public String getTestNULLStr() {
		return testNULLStr;
	}

	public User getUser() {
		return user;
	}

	public static class User {
		private String account;

		public String getAccount() {
			return account;
		}
	}
}
