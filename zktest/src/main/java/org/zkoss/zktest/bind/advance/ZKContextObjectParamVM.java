/* ZKContextObjectParamVM.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 16:19:58 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;

public class ZKContextObjectParamVM {
	String bindContextString;
	String binderString;
	String eventName;
	String cmdName;
	String executionString;
	String idSpaceString;
	String bindViewString;
	String bindComponentString;
	String pageString;
	String desktopString;
	String sessionString;
	String webAppName;

	public String getBindContextString() {
		return bindContextString;
	}

	public String getBinderString() {
		return binderString;
	}

	public String getEventName() {
		return eventName;
	}

	public String getCmdName() {
		return cmdName;
	}

	public String getExecutionString() {
		return executionString;
	}

	public String getIdSpaceString() {
		return idSpaceString;
	}

	public String getBindViewString() {
		return bindViewString;
	}

	public String getBindComponentString() {
		return bindComponentString;
	}

	public String getPageString() {
		return pageString;
	}

	public String getDesktopString() {
		return desktopString;
	}

	public String getSessionString() {
		return sessionString;
	}

	public String getWebAppName() {
		return webAppName;
	}

	@Init
	public void init(
		@ContextParam(ContextType.BIND_CONTEXT) BindContext bindContext,
		@ContextParam(ContextType.BINDER) Binder binder,
		@ContextParam(ContextType.TRIGGER_EVENT) Event event,
		@ContextParam(ContextType.COMMAND_NAME) String commandName,
		@ContextParam(ContextType.EXECUTION) Execution execution,
		@ContextParam(ContextType.COMPONENT) Component component,
		@ContextParam(ContextType.SPACE_OWNER) IdSpace idSpace,
		@ContextParam(ContextType.VIEW) Component view,
		@ContextParam(ContextType.PAGE) Page page,
		@ContextParam(ContextType.DESKTOP) Desktop desktop,
		@ContextParam(ContextType.SESSION) Session session,
		@ContextParam(ContextType.APPLICATION) WebApp webApp
	) {
		bindContextString = bindContext.toString();
		binderString = binder.toString();
		eventName = event == null ? "null" : event.getName();
		cmdName = commandName == null ? "null" : commandName;
		executionString = execution.toString();
		idSpaceString = idSpace.toString();
		bindViewString = view.toString();
		bindComponentString = component.toString();
		pageString = page.toString();
		desktopString = desktop.toString();
		sessionString = session.toString();
		webAppName = webApp.getAppName();
	}

	@NotifyChange("*")
	@Command
	public void show(
		@ContextParam(ContextType.BIND_CONTEXT) BindContext bindContext,
		@ContextParam(ContextType.BINDER) Binder binder,
		@ContextParam(ContextType.TRIGGER_EVENT) Event event,
		@ContextParam(ContextType.COMMAND_NAME) String commandName,
		@ContextParam(ContextType.EXECUTION) Execution execution,
		@ContextParam(ContextType.COMPONENT) Component component,
		@ContextParam(ContextType.SPACE_OWNER) IdSpace idSpace,
		@ContextParam(ContextType.VIEW) Component view,
		@ContextParam(ContextType.PAGE) Page page,
		@ContextParam(ContextType.DESKTOP) Desktop desktop,
		@ContextParam(ContextType.SESSION) Session session,
		@ContextParam(ContextType.APPLICATION) WebApp webApp
	) {
		bindContextString = bindContext.toString();
		binderString = binder.toString();
		eventName = event == null ? "null" : event.getName();
		cmdName = commandName == null ? "null" : commandName;
		executionString = execution.toString();
		idSpaceString = idSpace.toString();
		bindViewString = view.toString();
		bindComponentString = component.toString();
		pageString = page.toString();
		desktopString = desktop.toString();
		sessionString = session.toString();
		webAppName = webApp.getAppName();
	}
}
