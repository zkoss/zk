/* EventDemoController.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Feb 2, 2012 11:07:23 AM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.select;

import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 *
 * @author simonpai
 */
public class Z60EventWiringController extends SelectorComposer<Component> {
	
	private static final long serialVersionUID = 1562037930089176968L;
	
	@WireVariable
	private Page _page;
	@WireVariable
	private Desktop _desktop;
	@WireVariable
	private Session _sess;
	@WireVariable
	private WebApp _wapp;
	@WireVariable("self")
	private Component _self;
	@WireVariable("desktopScope")
	private Map<String, Object> _desktopScope;
	
	@Wire(":root")
	private Window _win;
	
	@Wire("grid")
	private Grid _grid;
	
	@Listen("onCreate = :root")
	public void onCreate(Event event) {
		_grid.renderAll();
		_win.appendChild(new Button("Go-3"));
	}
	
	@Listen("onClick = row")
	public void showRow(Event event) {
		alert(((Label) event.getTarget().getFirstChild()).getValue());
	}
	
	@Listen("onClick = button")
	public void showLabel(Event event) {
		alert(((Button) event.getTarget()).getLabel());
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		_win.appendChild(new Button("Go-2"));
		System.out.println("Page: " + _page);
		System.out.println("Desktop: " + _desktop);
		System.out.println("Session: " + _sess);
		System.out.println("WebApp: " + _wapp);
		System.out.println("Self: " + _self);
		System.out.println("Desktop Scope: " + _desktopScope);
	}
	
}
