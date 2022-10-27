/* IImageCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 26 12:31:07 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.image.Images;
import org.zkoss.json.JavaScriptValue;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Image;

/**
 * An addition interface to {@link IImage}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IImageCtrl {
	static IImage from(Image instance) {
		return new IImage.Builder()
				.from((IImage) instance)
				.build();
	}

	static Object getEncodedSrc(IImageBase image) {
		org.zkoss.image.Image media = image.getContent();
		if (media != null) {
			return new JavaScriptValue(IMediaBase.JSObjectUrl.generate(media));
		}

		String src = image.getSrc();
		if (src == null) {
			return Images.BASE64SPACERIMAGE;
		} else {
			return Executions.getCurrent().encodeURL(src);
		}
	}
}