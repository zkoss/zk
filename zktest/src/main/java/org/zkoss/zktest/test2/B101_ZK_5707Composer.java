/* B101_ZK_5707Composer.java

	Purpose:

	Description:

	History:
		12:44 PM 2024/9/19, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.http.SimpleSession;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;

/**
 * @author jumperchen
 */
public class B101_ZK_5707Composer implements Composer {
	static int count = 0;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		Button button = new Button("many users logout");
		button.addEventListener(Events.ON_CLICK, e -> {
			Events.postEvent("onLogout", comp, null);
			IntStream.range(0, 100).forEach(n -> Events.postEvent("onDummy", comp, null));
		});
		comp.addEventListener("onLogout", e -> {
//			HttpSession session = (HttpSession) Sessions.getCurrent().getNativeSession();
			SimpleSession simpleSession = ((SimpleSession)Sessions.getCurrent());
			// simulate session logout/timeout from a different thread (browser tab/background process)
			System.out.println(">> handle onLogout ");
			CompletableFuture.runAsync( () ->{
				System.out.println(">> handle onLogout Now");
				//                session.invalidate();
				simpleSession.invalidateNow();
			});
		});

		comp.addEventListener("onDummy", event -> {
			Thread.sleep(100);
			System.out.println(">> handle onDummy " + ++count);
		});
		comp.appendChild(button);
	}

}