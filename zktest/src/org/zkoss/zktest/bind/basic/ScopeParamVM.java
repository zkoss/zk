package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.Scope;
import org.zkoss.bind.annotation.ScopeParam;
import org.zkoss.zk.ui.Executions;

public class ScopeParamVM {

	String applicationScope;
	String sessionScope;
	String desktopScope;
	String pageScope;
	String spaceScope;
	String requestScope;
	String componentScope;
	String componentScope1;
	String componentScope2;
	String componentScope3;
	
	
	@Init
	public void init(@ScopeParam("applicationScopeVar") String applicationScope,
			@ScopeParam("sessionScopeVar") String sessionScope,
			@ScopeParam("desktopScopeVar") String desktopScope,
			@ScopeParam("pageScopeVar") String pageScope,
			@ScopeParam("spaceScopeVar") String spaceScope,
			@ExecutionParam("requestScopeVar") String requestScope,
			@ScopeParam("componentScopeVar") String componentScope,
			@ScopeParam("componentScopeVar1") String componentScope1,
			@ScopeParam("componentScopeVar2") String componentScope2,
			@ScopeParam("componentScopeVar3") String componentScope3){
		this.applicationScope = applicationScope;
		this.sessionScope = sessionScope;
		this.desktopScope = desktopScope;
		this.pageScope = pageScope;
		this.spaceScope = spaceScope;
		this.componentScope = componentScope;
		this.componentScope1 = componentScope1;
		this.componentScope2 = componentScope2;
		this.componentScope3 = componentScope3;
		this.requestScope = requestScope;
	}

	

	public String getApplicationScope() {
		return applicationScope;
	}



	public void setApplicationScope(String applicationScope) {
		this.applicationScope = applicationScope;
	}



	public String getSessionScope() {
		return sessionScope;
	}



	public void setSessionScope(String sessionScope) {
		this.sessionScope = sessionScope;
	}



	public String getDesktopScope() {
		return desktopScope;
	}



	public void setDesktopScope(String desktopScope) {
		this.desktopScope = desktopScope;
	}



	public String getPageScope() {
		return pageScope;
	}



	public void setPageScope(String pageScope) {
		this.pageScope = pageScope;
	}



	public String getSpaceScope() {
		return spaceScope;
	}



	public void setSpaceScope(String spaceScope) {
		this.spaceScope = spaceScope;
	}

	


	public String getRequestScope() {
		return requestScope;
	}



	public void setRequestScope(String requestScope) {
		this.requestScope = requestScope;
	}



	public String getComponentScope() {
		return componentScope;
	}



	public void setComponentScope(String componentScope) {
		this.componentScope = componentScope;
	}



	public String getComponentScope1() {
		return componentScope1;
	}



	public void setComponentScope1(String componentScope1) {
		this.componentScope1 = componentScope1;
	}



	public String getComponentScope2() {
		return componentScope2;
	}



	public void setComponentScope2(String componentScope2) {
		this.componentScope2 = componentScope2;
	}



	public String getComponentScope3() {
		return componentScope3;
	}



	public void setComponentScope3(String componentScope3) {
		this.componentScope3 = componentScope3;
	}



	@NotifyChange("*") @Command
	public void cmd1(@ScopeParam("applicationScopeVar") String applicationScope,
			@ScopeParam("sessionScopeVar") String sessionScope,
			@ScopeParam("desktopScopeVar") String desktopScope,
			@ScopeParam(value="pageScopeVar") String pageScope,
			@ScopeParam(value="spaceScopeVar") String spaceScope,
			@ExecutionParam("requestScopeVar") String requestScope,
			@ScopeParam(value="componentScopeVar") String componentScope,
			@ScopeParam(value="componentScopeVar1") String componentScope1,
			@ScopeParam(value="componentScopeVar2") String componentScope2,
			@ScopeParam(value="componentScopeVar3") String componentScope3){
		this.applicationScope = applicationScope;
		this.sessionScope = sessionScope;
		this.desktopScope = desktopScope;
		this.pageScope = pageScope;
		this.spaceScope = spaceScope;
		this.requestScope = requestScope;
		this.componentScope = componentScope;
		this.componentScope1 = componentScope1;
		this.componentScope2 = componentScope2;
		this.componentScope3 = componentScope3;
		
		Executions.getCurrent().getDesktop().setAttribute("sessionScopeVar", "var1 by Desktop");
		Executions.getCurrent().getDesktop().setAttribute("applicationScopeVar", "var2 by Desktop");
	}
	
	@NotifyChange("*") @Command
	public void cmd2(@ScopeParam("applicationScopeVar") String applicationScope,
			@ScopeParam("sessionScopeVar") String sessionScope){
		this.applicationScope = applicationScope;
		this.sessionScope = sessionScope;
	}
	
	@NotifyChange("*") @Command
	public void cmd3(@ScopeParam(value="applicationScopeVar",scopes=Scope.APPLICATION) String applicationScope,
			@ScopeParam(value="sessionScopeVar",scopes=Scope.SESSION) String sessionScope){
		this.applicationScope = applicationScope;
		this.sessionScope = sessionScope;
	}
	
	
}
