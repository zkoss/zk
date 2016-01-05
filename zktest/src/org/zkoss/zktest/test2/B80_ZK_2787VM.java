/* B80_ZK_2787VM.java

	Purpose:
		
	Description:
		
	History:
		5:52 PM 1/5/16, Created by jumperchen

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.sql.Timestamp;
import java.util.Date;

import org.zkoss.bind.annotation.ImmutableFields;
import org.zkoss.bind.annotation.Init;

/**
 * @author jumperchen
 */
public class B80_ZK_2787VM {
	private Foo _f;

	@Init
	public void init() {
		_f = new Foo();
		_f.setDate(new Timestamp(new Date().getTime()));
	}
	public void setFoo(Foo f) {
		_f = f;
	}
	@ImmutableFields
	public Foo getFoo() {
		return _f;
	}

	public static class Foo {
		private Timestamp _date;
		public void setDate(Timestamp date) {
			_date = date;
		}
		public Timestamp getDate() {
			return _date;
		}
	}
}
