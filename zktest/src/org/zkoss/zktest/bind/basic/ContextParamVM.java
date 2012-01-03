package org.zkoss.zktest.bind.basic;

import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.Scope;
import org.zkoss.bind.annotation.ScopeParam;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;

public class ContextParamVM {

	String applicationScope;
	String sessionScope;
	String desktopScope;
	String pageScope;
	String spaceScope;
	String requestScope;
	String componentScope;

	boolean bindContext;
	boolean bindBinder;
	
	String bindComponentId;
	String bindViewId;

	@Init
	public void init(
			@ContextParam(ContextType.EXECUTION) Execution execution,
			@ContextParam(ContextType.COMPONENT) Component component,
			@ContextParam(ContextType.VIEW) Component view,
			@ContextParam(ContextType.SPACE_OWNER) IdSpace spaceOwner, 
			@ContextParam(ContextType.PAGE) Page page,
			@ContextParam(ContextType.DESKTOP) Desktop desktop,
			@ContextParam(ContextType.SESSION) Session session,
			@ContextParam(ContextType.APPLICATION) WebApp application,

			@ContextParam(ContextType.BIND_CONTEXT) BindContext bindContext,
			@ContextParam(ContextType.BINDER) Binder binder) {
		this.applicationScope = (String) application.getAttribute("applicationScopeVar");
		this.sessionScope = (String) session.getAttribute("sessionScopeVar");
		this.desktopScope = (String) desktop.getAttribute("desktopScopeVar");
		this.pageScope = (String) page.getAttribute("pageScopeVar");
		this.spaceScope = (String) spaceOwner.getAttribute("componentScopeVar");
		this.componentScope = (String) component.getAttribute("componentScopeVar");
		this.requestScope = (String) execution.getAttribute("requestScopeVar");


		this.bindContext = bindContext != null;
		this.bindBinder = binder != null;
		this.bindComponentId = component.getId();
		this.bindViewId = view.getId();
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

	public boolean isBindContext() {
		return bindContext;
	}

	public void setBindContext(boolean bindContext) {
		this.bindContext = bindContext;
	}

	public boolean isBindBinder() {
		return bindBinder;
	}

	public void setBindBinder(boolean bindBinder) {
		this.bindBinder = bindBinder;
	}


	@NotifyChange("*")
	@Command
	public void cmd1(
			@ContextParam(ContextType.EXECUTION) Execution execution,
			@ContextParam(ContextType.COMPONENT) Component component,
			@ContextParam(ContextType.SPACE_OWNER) IdSpace spaceOwner, 
			@ContextParam(ContextType.PAGE) Page page,
			@ContextParam(ContextType.DESKTOP) Desktop desktop,
			@ContextParam(ContextType.SESSION) Session session,
			@ContextParam(ContextType.APPLICATION) WebApp application,
			@ContextParam(ContextType.VIEW) Component view,
			@ContextParam(ContextType.BIND_CONTEXT) BindContext bindContext,
			@ContextParam(ContextType.BINDER) Binder binder) {
		this.applicationScope = (String) application.getAttribute("applicationScopeVar");
		this.sessionScope = (String) session.getAttribute("sessionScopeVar");
		this.desktopScope = (String) desktop.getAttribute("desktopScopeVar");
		this.pageScope = (String) page.getAttribute("pageScopeVar");
		this.spaceScope = (String) spaceOwner.getAttribute("componentScopeVar");
		this.componentScope = (String) component.getAttribute("componentScopeVar");
		this.requestScope = (String) execution.getAttribute("requestScopeVar");

		this.bindContext = bindContext == null;
		this.bindBinder = binder == null;

		Executions.getCurrent().getDesktop().setAttribute("sessionScopeVar", "var1 by Desktop");
		Executions.getCurrent().getDesktop().setAttribute("applicationScopeVar", "var2 by Desktop");
		
		this.bindComponentId = component.getId();
		this.bindViewId = view.getId();
		
		spaceOwner.setAttribute("componentScopeVar", "spaceScope-Y");
		
	}

	@NotifyChange("*")
	@Command
	public void cmd2(@ContextParam(ContextType.COMPONENT) Component component,
			@ScopeParam("applicationScopeVar") String applicationScope,
			@ScopeParam("sessionScopeVar") String sessionScope,
			@ContextParam(ContextType.VIEW) Component view,
			@ContextParam(ContextType.SPACE_OWNER) IdSpace spaceOwner) {
		this.applicationScope = applicationScope;
		this.sessionScope = sessionScope;
		this.bindComponentId = component.getId();
		this.bindViewId = view.getId();
		this.spaceScope = (String) spaceOwner.getAttribute("componentScopeVar");
	}

	@NotifyChange("*")
	@Command
	public void cmd3(@ContextParam(ContextType.COMPONENT) Component component,
			@ScopeParam(value = "applicationScopeVar", scopes = Scope.APPLICATION) String applicationScope,
			@ContextParam(ContextType.VIEW) Component view,
			@ScopeParam(value = "sessionScopeVar", scopes = Scope.SESSION) String sessionScope) {
		this.applicationScope = applicationScope;
		this.sessionScope = sessionScope;
		this.bindComponentId = component.getId();
		this.bindViewId = view.getId();
	}

	public String getBindComponentId() {
		return bindComponentId;
	}

	public void setBindComponentId(String bindComponentId) {
		this.bindComponentId = bindComponentId;
	}

	public String getBindViewId() {
		return bindViewId;
	}

	public void setBindViewId(String bindViewId) {
		this.bindViewId = bindViewId;
	}
	
	

	
}
