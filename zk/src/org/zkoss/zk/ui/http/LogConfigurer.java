/* LogConfigurer.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec 16 23:14:07 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.http;

import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

import org.zkoss.lang.Library;
import org.zkoss.mesg.MCommon;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.UiException;

/**
 * Utilties for configure the logg.
 * @author tomyeh
 * @since 6.0.0
 */
/*package*/ class LogConfigurer {
	private static final Log log = Log.lookup(LogConfigurer.class);

	/*package*/ static void configure() {
		final String LIBPROP = "org.zkoss.util.logging.config.file";
		final String path = Library.getProperty(LIBPROP);
		if (path == null || path.length() == 0)
			return;

		InputStream is = LogConfigurer.class.getResourceAsStream(path);
		try {
			if (is == null) {
				File file = new File(path);
				if (!file.isAbsolute())
					file = new File(System.getProperty("user.dir", "."), path);
				if (file.exists())
					try {
						is = new FileInputStream(file);
					} catch (java.io.FileNotFoundException ex) {
						//silent
					}
				if (is == null) {
					log.error(MCommon.FILE_NOT_FOUND, path);
					return;
				}
			}

			log.info(MCommon.FILE_OPENING, path);
			final Properties props = new Properties();
			props.load(is);
			Log.configure(props);
		} catch (Throwable ex) {
			log.realCauseBriefly("Failed to load "+path, ex);
		} finally {
			if (is != null)
				try {is.close();} catch (Throwable ex) {}
		}
	}
}

