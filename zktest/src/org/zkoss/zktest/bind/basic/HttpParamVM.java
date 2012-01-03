package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.CookieParam;
import org.zkoss.bind.annotation.HeaderParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.QueryParam;

public class HttpParamVM {

	String queryParam;
	String headerParam;
	String cookieParam;
	
	@Init
	public void init(@QueryParam("param1") String parm1,
			@HeaderParam("user-agent")String browser, @CookieParam("nosuch") @HeaderParam("user-agent") String guess){
		queryParam = parm1;
		headerParam = browser;
		cookieParam = guess;
	}

	public String getQueryParam() {
		return queryParam;
	}

	public void setQueryParam(String queryParam) {
		this.queryParam = queryParam;
	}

	public String getHeaderParam() {
		return headerParam;
	}

	public void setHeaderParam(String headerParam) {
		this.headerParam = headerParam;
	}

	public String getCookieParam() {
		return cookieParam;
	}

	public void setCookieParam(String cookieParam) {
		this.cookieParam = cookieParam;
	}
	
	@NotifyChange("*") @Command
	public void cmd1(@CookieParam("jsessionid") String cookie, @QueryParam("dtid")String dtid, @HeaderParam("ZK-SID")String sid){
		cookieParam = cookie;
		queryParam = dtid;
		headerParam = sid;
	}
	
	
}
