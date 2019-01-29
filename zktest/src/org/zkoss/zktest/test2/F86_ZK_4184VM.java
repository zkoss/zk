/* F86_ZK_4184VM.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 07 15:40:15 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Destroy;
import org.zkoss.bind.annotation.Init;
import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.Desktop;

/**
 * @author rudyhuang
 */
public class F86_ZK_4184VM {
	private int counter;

	private List<String> contacts = new ArrayList<>(); // better to use CopyOnWriteArrayList
	private volatile boolean running = true;

	@Init
	public void init(@ContextParam(ContextType.DESKTOP) Desktop desktop) {
		for (int i = 0; i < 100; i++) {
			contacts.add(newContact());
		}
		new Thread(() -> {
			try {
				Thread.sleep(1000);
				while (running) {
					Thread.sleep(1);
					if (Math.random() < 0.5) {
						int size = contacts.size();
						if (size > 0) {
							int index = (int) (Math.random() * size);
							contacts.remove(index);
						}
					} else {
						contacts.add(newContact());
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Command
	public void update() {
		BindUtils.postNotifyChange(null, null, this, "contacts");
	}

	@Command
	public void indexLoop() {
		int size = contacts.size();
		for (int i = 0; i < size; i++) {
			Threads.sleep(1);
			contacts.get(i);
		}
	}

	@Command
	public void forLoop() {
		for (String contact : contacts) {
			Threads.sleep(1);
		}
	}

	@Command
	public void iteratorLoop() {
		final Iterator<String> iterator = contacts.iterator();
		while (iterator.hasNext()) {
			Threads.sleep(1);
			iterator.next();
		}
	}

	@Destroy
	public void cleanup() {
		running = false;
	}

	private String newContact() {
		return "Contact-" + counter++;
	}

	public List<String> getContacts() {
		return contacts;
	}
}
