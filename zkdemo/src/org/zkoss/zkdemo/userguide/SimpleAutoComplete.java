/* SimpleAutoComplete.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 4, 2008 5:57:30 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import java.util.Arrays;

import org.zkoss.zul.Combobox;
import org.zkoss.zul.SimpleListModel;

/**
 * This class is an example of auto-complete with combobox via SimpleListModel to achieve.
 * @author jumperchen
 *
 */
public class SimpleAutoComplete extends Combobox {
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
	public SimpleAutoComplete() {
		setModel(new SimpleListModel(_dict));
	}
}
