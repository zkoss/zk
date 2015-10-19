/* B80_ZK_2923ViewModel.java

	Purpose:
		
	Description:
		
	History:
		12:51 PM 10/19/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

/**
 * @author jumperchen
 */
public class B80_ZK_2923ViewModel  {
	private Account specialAccount;
	private Account account;

	@Init
	public void init() {
		//both account and specialAccount are null
	}

	@Command
	public void selectAccountType() {
		this.setAccount(null);
		BindUtils.postNotifyChange(null, null, this, "account"); //will lead to notify change on a null-value, causing a load on the specialAccount.code as well
	}

	@Command
	public void other(){

	}

	public Account getSpecialAccount() {
		return specialAccount;
	}

	public void setSpecialAccount(Account specialAccount) {
		this.specialAccount = specialAccount;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public static class Account {
		private String code;
		private String name;

		public Account(String code, String name){
			this.setCode(code);
			this.setName(name);
		}

		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
}