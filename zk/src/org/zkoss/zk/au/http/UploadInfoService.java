/* UploadInfoService.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 23 16:29:21     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.au.http;

import static org.zkoss.lang.Generics.cast;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.util.media.Media;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.impl.Attributes;

/**
 * Extends desktop to handle the request of the upload information
 * and post events if the upload is completed.
 *
 * @author jumperchen
 * @since 5.0.0
 */
public class UploadInfoService implements AuService, Serializable {
	private UploadInfoService() {
	}

	private static Media[] parseResult(List<Media> result) {
		if (result != null) {
			//we have to filter items that user doesn't specify any file
			for (Iterator<Media> it = result.iterator(); it.hasNext();) {
				final Media media = it.next();
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
				return result.toArray(new Media[result.size()]);
		}
		return null;
	}

	public boolean service(AuRequest request, boolean everError) {
		if ("updateResult".equals(request.getCommand())) {
			final Map<String, Object> data = request.getData();
			Desktop desktop = request.getDesktop();
			final String uuid = (String) request.getData().get("wid");
			final Component comp = desktop.getComponentByUuidIfAny(uuid);
			final String sid = (String) request.getData().get("sid");
			if (comp == null) {
				Map<String, Integer> percent = cast((Map) desktop.getAttribute(Attributes.UPLOAD_PERCENT));
				Map<String, Object> size = cast((Map) desktop.getAttribute(Attributes.UPLOAD_SIZE));
				String key = uuid + '_' + sid;
				if (percent != null) {
					percent.remove(key);
					size.put(key, "Upload Aborted");
				}
				return false;
			}
			final List<Media> result = cast((List) AuRequests.getUpdateResult(request));
			Events.postEvent(new UploadEvent(Events.ON_UPLOAD, comp, parseResult(result)));
			Map percent = (Map) desktop.getAttribute(Attributes.UPLOAD_PERCENT);
			Map size = (Map) desktop.getAttribute(Attributes.UPLOAD_SIZE);
			final String key = uuid + '_' + sid;
			percent.remove(key);
			size.remove(key);
			return true;
		}
		return false;
	}

	/** Registers the upload info service when
	 */
	public static class DesktopInit implements org.zkoss.zk.ui.util.DesktopInit {
		public void init(Desktop desktop, Object request) {
			desktop.addListener(new UploadInfoService());
		}
	}

}
