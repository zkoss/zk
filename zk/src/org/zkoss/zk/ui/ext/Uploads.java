/* Uploads.java

	Purpose:
		
	Description:
		
	History:
		Fri May 10 12:02:24 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.ext;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.lang.Strings;
import org.zkoss.util.Maps;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.impl.Attributes;

/**
 * Utilities to handle the upload ({@link Uploadable}).
 *
 * @author rudyhuang
 * @since 8.6.2
 */
public class Uploads {
	private Uploads() { }

	/**
	 * Parse the setting and update the upload-related attributes with the component.
	 *
	 * @param comp this component
	 * @param upload upload setting
	 */
	public static void parseUpload(Component comp, String upload) {
		if (!Strings.isEmpty(upload)) {
			Map<String, String> args = new HashMap<String, String>();
			Maps.parse(args, upload, ',', (char) 1);

			String maxsize = args.get("maxsize");
			if (!Strings.isEmpty(maxsize)) {
				try {
					Integer maxsz = Integer.parseInt(maxsize);
					comp.setAttribute(Attributes.UPLOAD_MAX_SIZE, maxsz);
				} catch (NumberFormatException e) {
					throw new UiException("The upload max size should be a positive integer.");
				}
			}

			if (args.containsKey("native")) {
				comp.setAttribute(Attributes.UPLOAD_NATIVE, true);
			} else {
				comp.removeAttribute(Attributes.UPLOAD_NATIVE);
			}
		}
	}

	/**
	 * Gets the real upload setting for client.
	 * It would append the default maxsize in webapp configuration if none was given.
	 *
	 * @param comp this component
	 * @param upload upload setting
	 * @return upload setting (could be altered)
	 */
	public static String getRealUpload(Component comp, String upload) {
		Desktop desktop = comp.getDesktop();
		if (desktop != null && !Strings.isEmpty(upload) && !upload.contains("maxsize="))
			upload = upload.concat(",maxsize=" + desktop.getWebApp().getConfiguration().getMaxUploadSize());
		return upload;
	}
}
