/* F104_ZK_6097_AvatargroupComposer.java

		Purpose:

		Description:

		History:
				Wed May 13 13:17:28 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Avatar;
import org.zkoss.zul.Avatargroup;
import org.zkoss.zul.Label;

public class F104_ZK_6097_AvatargroupComposer extends SelectorComposer<org.zkoss.zk.ui.Component> {

	@Wire Avatargroup agMvc;
	@Wire Label resultLabel;

	@Listen("onClick = #btnRemoveLimit")
	public void onRemoveLimit() {
		agMvc.setMaxCount(0);
		resultLabel.setValue("maxCount removed — all avatars visible (" + agMvc.getChildren().size() + ")");
	}

	@Listen("onClick = #btnAddAvatar")
	public void onAddAvatar() {
		Avatar av = new Avatar();
		// Derive the next letter from the LIVE child count instead of a
		// pre-seeded counter — the ZUL controls the initial set, so any
		// edit to that side stays in sync without the composer needing to
		// know how many avatars the page started with. Wrap around [A-Z]
		// after 26 clicks.
		int idx = agMvc.getChildren().size();
		av.setLabel(String.valueOf((char) ('A' + idx % 26)));
		agMvc.appendChild(av);
		resultLabel.setValue("added avatar, total=" + agMvc.getChildren().size());
	}

	@Listen("onClick = #btnSetSmall")
	public void onSetSmall() {
		agMvc.setSize("small");
		agMvc.setShape("circle");
		resultLabel.setValue("size=small shape=circle applied");
	}

	@Listen("onClick = #btnReset")
	public void onReset() {
		agMvc.setMaxCount(3);
		agMvc.setSize(null);
		agMvc.setShape(null);
		resultLabel.setValue("reset: maxCount=3");
	}
}
