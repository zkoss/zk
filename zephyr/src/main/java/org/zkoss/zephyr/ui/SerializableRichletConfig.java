/* SerializableRichletConfig.java

	Purpose:
		
	Description:
		
	History:
		11:10 AM 2021/12/9, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.RichletConfig;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;

/**
 * A wrapper to support {@link org.zkoss.zk.ui.RichletConfig} serializable
 * @author jumperchen
 */
public class SerializableRichletConfig implements Serializable, RichletConfig {
	private Map<String, String> _params;
	public SerializableRichletConfig(RichletConfig richletConfig) {
		Iterable<String> initParameterNames = richletConfig.getInitParameterNames();
		_params = new HashMap<String, String>();
		for (String name : initParameterNames) {
			_params.put(name, richletConfig.getInitParameter(name));
		}
	}

	public WebApp getWebApp() {
		return WebApps.getCurrent();
	}

	public String getInitParameter(String name) {
		return _params.get(name);
	}

	public Iterable<String> getInitParameterNames() {
		return _params.keySet();
	}
}
