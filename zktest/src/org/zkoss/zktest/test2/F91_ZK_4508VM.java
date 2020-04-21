/* F91_ZK_4508VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Feb 18 10:06:21 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.CookieParam;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.ExecutionParam;
import org.zkoss.bind.annotation.HeaderParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.bind.annotation.ScopeParam;
import org.zkoss.bind.annotation.SelectorParam;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;

/**
 * @author rudyhuang
 */
public class F91_ZK_4508VM {
	private String name1;
	private String name2;
	private String param1;
	private String param2;
	private String arg1;
	private String arg2;

	@Init
	public void init(@QueryParam String name1, @QueryParam("name1") String name2,
	                 @ExecutionParam String param1, @ExecutionParam("param1") String param2,
	                 @ExecutionArgParam String arg1, @ExecutionArgParam("arg1") String arg2) {
		this.name1 = name1;
		this.name2 = name2;
		this.param1 = param1;
		this.param2 = param2;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	@Command
	public void bindingParam(@BindingParam String name1, @BindingParam("name1") String name2) {
		Clients.log(String.format("BindingParam, %s (%s)!", name1, name2));
	}

	@Command
	public void queryParam() {
		Clients.log(String.format("QueryParam, %s (%s)!", name1, name2));
	}

	@Command
	public void scopeParam(@ScopeParam String name1, @ScopeParam("name1") String name2) {
		Clients.log(String.format("ScopeParam, %s (%s)!", name1, name2));
	}

	@Command
	public void cookieParam(@CookieParam String jsessionid, @CookieParam("jsessionid") String jsessionid2) {
		Clients.log(String.format("CookieParam, %s (%s)!", jsessionid, jsessionid2));
	}

	@Command
	public void executionArgParam() {
		Clients.log(String.format("ExecutionArgParam, %s (%s)!", arg1, arg2));
	}

	@Command
	public void executionParam() {
		Clients.log(String.format("ExecutionParam, %s (%s)!", param1, param2));
	}

	@Command
	public void headerParam(@HeaderParam String Connection, @HeaderParam("Connection") String conn2) {
		Clients.log(String.format("HeaderParam, %s (%s)!", Connection, conn2));
	}

	@Command
	public void selectorParam(@SelectorParam Div div, @SelectorParam("button[label=\"SelectorParam\"]") Button btn) {
		Clients.log(String.format("SelectorParam, %s (%s)!", div.getId(), btn.getId()));
	}
}
