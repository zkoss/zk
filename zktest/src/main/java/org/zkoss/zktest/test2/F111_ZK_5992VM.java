/* F111_ZK_5992VM.java

        Purpose:

        Description:

        History:
                Thu Nov 05 15:20:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Collections;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class F111_ZK_5992VM {

	private List<String> pluginList = Collections.singletonList("colors");
	private String pluginCsv = "fontsize,emoji";

	public List<String> getPluginList() {
		return pluginList;
	}

	public String getPluginCsv() {
		return pluginCsv;
	}

	@Command
	@NotifyChange({"pluginList", "pluginCsv"})
	public void switchPlugins() {
		pluginList = Collections.singletonList("history");
		pluginCsv = "";
	}
}
