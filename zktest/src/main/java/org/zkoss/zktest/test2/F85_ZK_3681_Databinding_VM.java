package org.zkoss.zktest.test2;

import java.math.BigInteger;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.zk.ui.util.Clients;

public class F85_ZK_3681_Databinding_VM {
	private String name = "ZK";
	private int money = 0;
	private BigInteger assets = BigInteger.valueOf(0L);
	private F85_ZK_3681_Issue issue = new F85_ZK_3681_Issue(false, "ZK-1234", "Test");

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public BigInteger getAssets() {
		return assets;
	}

	public void setAssets(BigInteger assets) {
		this.assets = assets;
	}

	public F85_ZK_3681_Issue getIssue() {
		return issue;
	}

	public void setIssue(F85_ZK_3681_Issue issue) {
		this.issue = issue;
	}

	@GlobalCommand
	public void callGlobal(@BindingParam("text") String text,
	                       @BindingParam("num") int num) {
		Clients.log("You called @GlobalCommand. text=" + text + ",num=" + num);
	}
}
