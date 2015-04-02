/** WebAppFactoryImpl.java

	Purpose:
		
	Description:
		
	History:
		12:25:10 PM Apr 2, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.SimpleWebApp;
import org.zkoss.zk.ui.util.Configuration;

/**
 * A web app factory implementation.
 * @author jumperchen
 * @since 7.0.6
 */
public class WebAppFactoryImpl implements WebAppFactory {

	public WebApp newWebApp(Object ctx, Configuration config) {
		// the implementation is the same as org.zkoss.zk.ui.http.WebManager
		Class<?> cls = config.getWebAppClass();
		if (cls != null) {
			try {
				return (WebApp)cls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex, "Unable to construct "+cls);
			}
		} else {
			return new SimpleWebApp();
		}
	}

}
