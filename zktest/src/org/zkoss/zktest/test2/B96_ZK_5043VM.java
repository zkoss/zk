/* B96_ZK_5043VM.java

	Purpose:
		
	Description:
		
	History:
		5:56 PM 2021/11/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;

/**
 * @author jumperchen
 */
public class B96_ZK_5043VM {
	Map<String, Field> map = new LinkedHashMap<>();

	public B96_ZK_5043VM() {
		map.put("key1", new Field());
	}

	@Command
	public void replace() {
		Field newField = new Field();
		newField.setValue(Long.toString(System.currentTimeMillis()));
		map.put("key1", newField);
		BindUtils.postNotifyChange(this, "map");
	}

	@Command
	public void change() {
		Field field = map.get("key1");
		field.setValue(Long.toString(System.currentTimeMillis()));
		field.notifyChange();
	}

	public Map<String, Field> getMap() {
		return map;
	}

	public static class Field {
		private String value = "1";

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public void notifyChange() {
			BindUtils.postNotifyChange(null, null, this, "*");
		}
	}
}