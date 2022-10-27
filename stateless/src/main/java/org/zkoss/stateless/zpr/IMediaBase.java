/* IMediaBase.java

	Purpose:

	Description:

	History:
		Tue Nov 23 14:28:40 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.zkoss.zk.ui.UiException;

public class IMediaBase {
	public static class JSObjectUrl {
		public static String generate(org.zkoss.util.media.Media media) {
			try {
				byte[] byteData = media.isBinary() ? media.inMemory() ? media.getByteData() : IOUtils.toByteArray(media.getStreamData()) :
						media.getStringData().getBytes("UTF-8");
				return "(function () {\n"
						+ "var bytes = new Uint8Array(" + Arrays.toString(byteData) + ");\n"
						+ "var blob = new Blob([bytes], {type: '" + media.getContentType() + "'});\n"
						+ "return URL.createObjectURL(blob);\n" + "})()";
			} catch (IOException e) {
				throw UiException.Aide.wrap(e);
			}
		}
	}
}