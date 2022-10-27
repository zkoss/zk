/* IAudioCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 08 18:07:15 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.json.JavaScriptValue;
import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Audio;

/**
 * An addition interface to {@link IAudio}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IAudioCtrl {
	static IAudio from(Audio instance) {
		IAudio.Builder builder = new IAudio.Builder()
				.from((IAudio) instance);
		List<ITrack> children = (List<ITrack>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}

	static List<Object> getEncodedSrc(IAudio audio) {
		List list = new ArrayList<String>();
		org.zkoss.sound.Audio media = audio.getContent();
		if (media != null) {
			list.add(new JavaScriptValue(IMediaBase.JSObjectUrl.generate(media)));
		} else {
			for (String src : audio.getSrc()) {
				list.add(Executions.getCurrent().encodeURL(src));
			}
		}
		return list;
	}
}