/* UploadUtils.java

        Purpose:
                
        Description:
                
        History:
                Mon Sep 10 12:25:42 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.util;

import java.util.Iterator;
import java.util.List;

import org.zkoss.util.media.Media;

/**
 * Upload utilities.
 *
 * @since 8.6.0
 */
public class UploadUtils {

	/**
	 * Parse the media list into Array,
	 * if the list is empty or null, the return value will be null.
	 *
	 * @param result the media list
	 * @return the media array
	 */
	public static Media[] parseResult(List<Media> result) {
		if (result != null) {
			//we have to filter items that user doesn't specify any file
			for (Iterator<Media> it = result.iterator(); it.hasNext(); ) {
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
}
