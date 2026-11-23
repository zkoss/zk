/* F96_ZK_1209VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 03 12:50:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.concurrent.atomic.AtomicInteger;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.HistoryPopState;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.MatchMedia;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.NotifyCommand;
import org.zkoss.bind.annotation.ToClientCommand;
import org.zkoss.bind.annotation.ToServerCommand;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.HistoryPopStateEvent;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
@NotifyCommand(value = "update", onChange = "_vm_.data")
@ToClientCommand({"update"})
@ToServerCommand({"reload"})
public class F96_ZK_1209VM {
	private int prop;

	@Init
	public void init() {
		prop = 1;
		System.out.println(">>>F96_ZK_1209VM init");
	}

	@AfterCompose
	public void doAfterCompose() {
		System.out.println(">>>F96_ZK_1209VM doAfterCompose");
	}

	@Command
	@NotifyChange("prop")
	public void update() {
		prop++;
	}

	public int getProp() {
		return prop;
	}

	@MatchMedia("all and (max-width : 640px)")
	public void switchMobileTemplate() {
		System.out.println(">>>F96_ZK_1209VM do switchMobileTemplate");
	}

	private final AtomicInteger historyIndex = new AtomicInteger();

	@Command
	public void pushHistoryState(@ContextParam(ContextType.DESKTOP) Desktop desktop) {
		final int index = historyIndex.incrementAndGet();
		desktop.pushHistoryState(String.valueOf(index), "", "#" + index);
	}

	@HistoryPopState
	public void handleHistoryPopState(@ContextParam(ContextType.TRIGGER_EVENT) HistoryPopStateEvent event) {
		Clients.log(event.getState());
	}

	private String word;
	private String reverse;

	@DependsOn("reverse")
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
		reverse = new StringBuilder(word).reverse().toString();
	}

	@DependsOn("word")
	public String getReverse() {
		return reverse;
	}

	public void setReverse(String reverse) {
		this.reverse = reverse;
		word = new StringBuilder(reverse).reverse().toString();
	}

	private String data = "dataString";

	@Command
	@NotifyChange("data")
	public void reload() {
		Clients.log("vm reload");
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
