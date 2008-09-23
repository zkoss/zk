/* AutoComplete.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Dec 13 12:04:33     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import java.util.Iterator;
import java.util.Arrays;

import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.*;

/**
 * An example of auto-complete with combobox.
 *
 * @author tomyeh
 */
public class AutoComplete extends Combobox {
	private static String[] _dict = { //alphabetic order
		"abacus", "accuracy", "acuity", "adage", "afar", "after", "apple",
		"bible", "bird", "bingle", "blog",
		"cabane", "cape", "cease", "cedar",
		"dacron", "defacto", "definable", "deluxe",
		"each", "eager", "effect", "efficacy",
		"far", "far from",
		"girl", "gigantean", "giant",
		"home", "honest", "huge",
		"information", "inner",
		"jump", "jungle", "jungle fever",
		"kaka", "kale", "kame",
		"lamella", "lane", "lemma",
		"master", "maxima", "music",
		"nerve", "new", "number",
		"omega", "opera",
		"pea", "peace", "peaceful",
		"rock",
		"sound", "spread", "student", "super",
		"tea", "teacher",
		"unit", "universe",
		"vector", "victory",
		"wake", "wee", "weak",
		"xeme",
		"yea", "yellow",
		"zebra", "zk",
	};
	static {
		Arrays.sort(_dict);
	}

	public AutoComplete() {
		refresh("");
	}
	public AutoComplete(String value) {
		super(value); //it invokes setValue
	}

	public void setValue(String value) {
		super.setValue(value);
		refresh(value);
	}
	public void onChanging(InputEvent evt) {
		if (!evt.isChangingBySelectBack())
			refresh(evt.getValue());
	}

	/** Refresh comboitem based on the specified value.
	 */
	private void refresh(String val) {
		int j = Arrays.binarySearch(_dict, val);
		if (j < 0) j = -j-1;

		Iterator it = getItems().iterator();
		for (int cnt = 10; --cnt >= 0 && j < _dict.length && _dict[j].startsWith(val); ++j) {
			if (it != null && it.hasNext()) {
				((Comboitem)it.next()).setLabel(_dict[j]);
			} else {
				it = null;
				new Comboitem(_dict[j]).setParent(this);
			}
		}

		while (it != null && it.hasNext()) {
			it.next();
			it.remove();
		}
	}
}
