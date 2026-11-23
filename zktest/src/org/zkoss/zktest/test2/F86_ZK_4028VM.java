/* F86_ZK_4028VM.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 31 11:01:27 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.Serializable;
import java.util.Collections;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zuti.zul.NavigationLevel;
import org.zkoss.zuti.zul.NavigationModel;

/**
 * @author rudyhuang
 */
public class F86_ZK_4028VM implements Serializable {
	private NavigationModel<F86_ZK_4028Item> navModel = new NavigationModel<>();

	public F86_ZK_4028VM() {
		navModel.put("Dashboard", new F86_ZK_4028Item("Dashboard", "z-icon-dashboard", "/test2/F86-ZK-4028-lvl1.zul"));
		navModel.put("Configuration", new F86_ZK_4028Item("Configuration", "z-icon-gear", "/test2/F86-ZK-4028-lvl1.zul"));
		navModel.put("Diagnostics", new F86_ZK_4028Item("Diagnostics", "z-icon-hand-o-right", "/test2/F86-ZK-4028-lvl1.zul"));
		navModel.put("Maintenance", new F86_ZK_4028Item("Maintenance", "z-icon-laptop", "/test2/F86-ZK-4028-lvl1.zul"));

		navModel.put("Configuration/WLANs", new F86_ZK_4028Item("WLANs", "z-icon-wifi", "/test2/F86-ZK-4028-lvl2.zul"));
		navModel.put("Configuration/Roles & Policies", new F86_ZK_4028Item("Roles & Policies", "z-icon-user", "/test2/F86-ZK-4028-lvl2.zul"));
		navModel.put("Configuration/Access Point", new F86_ZK_4028Item("Access Point", "z-icon-wifi", "/test2/F86-ZK-4028-lvl2.zul"));
		navModel.put("Configuration/AP Groups", new F86_ZK_4028Item("AP Groups", "z-icon-wifi", "/test2/F86-ZK-4028-lvl2.zul"));
		navModel.put("Configuration/Authentication", new F86_ZK_4028Item("Authentication", "z-icon-globe", "/test2/F86-ZK-4028-lvl2.zul"));

		navModel.put("Configuration/Access Point/List", new F86_ZK_4028Item("List", "z-icon-list", "/test2/F86-ZK-4028-lvl3.zul"));
		navModel.put("Configuration/Authentication/Auth Servers", new F86_ZK_4028Item("Auth Servers", "z-icon-globe", "/test2/F86-ZK-4028-lvl3.zul"));
		navModel.put("Configuration/Authentication/AAA Profiles", new F86_ZK_4028Item("AAA Profiles", "z-icon-globe", "/test2/F86-ZK-4028-lvl3.zul"));
	}

	public NavigationModel<F86_ZK_4028Item> getNavModel() {
		return navModel;
	}

	@Command
	public void navTo(@BindingParam("path") String path) {
		navModel.navigateToByPath(path);
	}

	@Command
	public void insert() {
		navModel.insertBefore("Diagnostics", "New Item", new F86_ZK_4028Item("New Item", "z-icon-new", "/test2/F86-ZK-4028-lvl1.zul"));
	}

	@Command
	public void remove(@BindingParam("path") String path) {
		navModel.remove(path);
	}

	@Command
	public void levelNavTo(@BindingParam("level") NavigationLevel level,
	                       @BindingParam("key") String key) {
		level.navigateTo(key).setContext(Collections.singletonMap("time", System.currentTimeMillis()));
	}
}
