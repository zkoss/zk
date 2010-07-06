/* UploadInfoService.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 23 16:29:21     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.au.http;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.sys.Attributes;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.au.AuRequest;

/**
 * Extends desktop to handle the request of the upload infomation
 * and post events if the upload is completed.
 *
 * @author jumperchen
 * @since 5.0.0
 */
public class UploadInfoService implements AuService {
	private UploadInfoService() {
	}
	
	private static Media[] parseResult(List result) {
		if (result != null) {
			//we have to filter items that user doesn't specify any file
			for (Iterator it = result.iterator(); it.hasNext();) {
				final Media media = (Media)it.next();
				if (media != null && media.inMemory() && media.isBinary()) {
					final String nm = media.getName();
					if (nm == null || nm.length() == 0) {
						final byte[] bs = media.getByteData();
						if (bs == null || bs.length == 0)
							it.remove(); //Upload is pressed without specifying a file
					}
				}
			}

			if (!result.isEmpty())
				return (Media[])result.toArray(new Media[result.size()]);
		}
		return null;
	}
	
	public boolean service(AuRequest request, boolean everError) {
		if ("updateResult".equals(request.getCommand())) {
			final Map data = request.getData();
			Desktop desktop = request.getDesktop();
			final String uuid = (String) request.getData().get("wid");
			final String sid = (String) request.getData().get("sid");
			Events.postEvent(new UploadEvent(Events.ON_UPLOAD,
					desktop.getComponentByUuid(uuid),
					parseResult((List)AuRequests.getUpdateResult(request))));

			Map precent = (Map) desktop.getAttribute(Attributes.UPLOAD_PERCENT);
			Map size = (Map)desktop.getAttribute(Attributes.UPLOAD_SIZE);
			final String key = uuid + '_' + sid;
			precent.remove(key);
			size.remove(key);
			return true;
		}
		return false;
	}
	/** Registers the upload info service when
	 */
	public static class DesktopInit implements org.zkoss.zk.ui.util.DesktopInit {
		public void init(Desktop desktop,  Object request) {
			desktop.addListener(new UploadInfoService());
		}
	}
	
}
