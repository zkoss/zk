/* B96_ZK_5181Composer.java

	Purpose:
		
	Description:
		
	History:
		2:17 PM 2022/10/5, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jumperchen
 */
public class B96_ZK_5181Composer extends
		SelectorComposer<Component> {

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		long total = 0;
		for (int i = 0; i < 100; i++) {
			total += test(i+"test");
		}
		Clients.log("Average length (nano): " + (total/100));
	}

	private long test(String prefix) {
		long startTimeNano = System.nanoTime();
		List<EventQueue> queueList = new ArrayList<EventQueue>();
		for (int i = 0; i < 1000; i++) {
			queueList.add(EventQueues.lookup(prefix+i, EventQueues.DESKTOP, true));
		}
		long endTimeNano = System.nanoTime();
		long length = endTimeNano - startTimeNano;
		Clients.log("elapased time: " + length);
		return length;
	}

}
